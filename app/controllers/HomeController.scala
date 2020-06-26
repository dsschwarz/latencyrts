package controllers

import java.util.{Timer, TimerTask, UUID}
import javax.inject._

import core._
import networking.dto.{BeginResponse, PreGameResponse}
import networking.format.{BeginResponseFormat, CommandFormat, FactionFormat, ReportWriter}
import play.api.libs.json._
import play.api.mvc._

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject() extends Controller {
  def index = Action {
    Ok(views.html.index())
  }

  val engineManager = new EngineManager

  {
    var lastUpdated = System.currentTimeMillis()
    val timer = new Timer()
    timer.schedule(new UpdateEngine(), 0, 10)

    class UpdateEngine extends TimerTask {
      override def run(): Unit = {
        val newTime = System.currentTimeMillis()
        engineManager.update((newTime - lastUpdated).toInt) // the difference between these should be quite small
        lastUpdated = newTime
      }
    }
  }

  def createGame() = Action {
    implicit val factionFormat = FactionFormat
    val creationResponse: PreGameResponse = engineManager.createGame()
    Ok(Json.writes[PreGameResponse].writes(creationResponse))
  }

  def joinGame(simpleId: String) = Action {
    implicit val factionFormat = FactionFormat
    val creationResponse: PreGameResponse = engineManager.joinGame(simpleId)
    Ok(Json.writes[PreGameResponse].writes(creationResponse))
  }

  def beginGame(id: String) = Action {
    val beginResponse: BeginResponse = engineManager.beginGame(UUID.fromString(id))
    Ok(BeginResponseFormat.writes(beginResponse))
  }

  def createFleet(engineId: String, factionId: String) = Action {
    println("Creating fleet")
    engineManager.getGame(UUID.fromString(engineId)).createFleet(UUID.fromString(factionId))
    Ok("test")
  }

  def processCommand(gameId: String, factionId: String) = Action { request =>
    val requestBody = request.body.asJson
    val command = requestBody.flatMap(json =>
      CommandFormat.reads(json).asOpt
    ).getOrElse(throw new Exception("Could not process command"))
    engineManager.getGame(UUID.fromString(gameId)).processCommand(command, UUID.fromString(factionId))
    Ok("test")
  }

  def getFleetReports(gameId: String, factionId: String) = Action {
    val game = engineManager.getGame(UUID.fromString(gameId))
    val reports = game.getReports(UUID.fromString(factionId))
    Ok(Writes.seq(ReportWriter).writes(reports))
  }
}
