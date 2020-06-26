package networking

import java.util.{Timer, TimerTask, UUID}

import core._
import networking.format.{CommandFormat, ReportReader}
import play.api.libs.json.Reads
import play.api.libs.ws.{WSClient, WSResponse}

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by dan-s on 28/05/2016.
  */
trait InGameCommunication {
  def getStarSystems: Seq[StarSystem]
  // All the reports the user has not seen yet
  def getNewReports: Seq[Report]
  def getAllReports: Seq[Report]  // returns every report the player has seen
  def sendAction(action: Command): Unit
  def createFleet(): Unit
}

class InGameCommunicationImpl(gameId: UUID, factionId: UUID, starSystems: Seq[StarSystem], wsClient: WSClient) extends InGameCommunication {
  // Reports the user has seen. Not used yet
  private val reportsSeen = mutable.HashSet.empty[Report]
  private var allReports = Seq.empty[Report]
  val timer = new Timer()
  timer.schedule(new FetchReports(), 0, 2000)

  class FetchReports extends TimerTask {
    override def run(): Unit = {
      wsClient.url(Constants.SERVER_ROOT + "/getReports")
        .withQueryString("gameId" -> gameId.toString)
        .withQueryString("factionId" -> factionId.toString)
        .get()
        .onSuccess {
          case response: WSResponse =>
            val reports = Reads.seq(new ReportReader(starSystems)).reads(response.json).get
            allReports = reports
            println(s"Got ${reports.length} reports")
        }
    }
  }

  override def getStarSystems: Seq[StarSystem] = starSystems

  // All the reports the user has not seen yet
  override def getNewReports: Seq[Report] = List()

  override def getAllReports: Seq[Report] = allReports

  // returns every report the player has seen
  override def sendAction(action: Command): Unit = {
    wsClient.url(Constants.SERVER_ROOT + "/processCommand")
      .withQueryString("gameId" -> gameId.toString)
      .withQueryString("factionId" -> factionId.toString)
      .post(CommandFormat.writes(action))
  }

  override def createFleet(): Unit = {
    val request = wsClient.url(Constants.SERVER_ROOT + "/createFleet")
      .withQueryString("gameId" -> gameId.toString)
      .withQueryString("factionId" -> factionId.toString)
      .get()

    request.onSuccess {
      case _ => println("Fleet created")
    }
    request.onFailure {
      case e: Throwable => throw e
    }
  }
}

class LocalInGameComm(engine: Engine, faction: Faction) extends InGameCommunication {
  private val reportsSeen = mutable.HashSet.empty[Report]
  def getStarSystems: Seq[StarSystem] = {
    engine.systems
  }

  def getAllReports: Seq[Report] = engine.getReports(faction.id)

  def getNewReports: Seq[Report] = {
    val reports = engine.getReports(faction.id).filterNot(report => reportsSeen.contains(report))
    reportsSeen ++= reports

    reports
  }

  def sendAction(action: Command): Unit = {
    engine.processCommand(action, faction.id)
  }

  def createFleet(): Unit = {
    engine.createFleet(faction.id)
  }
}
