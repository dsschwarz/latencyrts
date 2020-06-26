package networking

import java.util.UUID

import core.{Faction, PreGame}
import metagame.{LocalGame, PreGameEvent}
import networking.dto.BeginResponse
import networking.format.BeginResponseFormat
import play.api.libs.ws.WSClient

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * After creating or joining a game, waiting for it to start
  */
trait PreGameCommunication {
  def leaveGame(): Unit
  def beginGame(): Future[BeginResponse]
  def getNewEvents(): Seq[PreGameEvent]
}

class PreGameCommImpl(ws: WSClient, preGameId: UUID, factionId: UUID) extends PreGameCommunication {
  override def leaveGame(): Unit = ???

  override def beginGame(): Future[BeginResponse] = {
    ws.url("http://localhost:9000/beginGame")
      .withQueryString("id" -> preGameId.toString)
      .get()
      .map { response =>
        BeginResponseFormat.reads(response.json)
          .getOrElse(throw new Exception("Could not parse response"))
      }
  }

  override def getNewEvents(): Seq[PreGameEvent] = {
    List()
  }
}

class LocalPreGameComm(mainGame: LocalGame, preGame: PreGame, faction: Faction) extends PreGameCommunication {
  override def leaveGame(): Unit = ???

  override def beginGame(): Future[BeginResponse] = {
    Future.successful(mainGame.beginGame(preGame.id))
  }

  override def getNewEvents(): Seq[PreGameEvent] = preGame.getEvents(faction.id)
}