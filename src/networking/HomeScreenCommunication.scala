package networking
import metagame.LocalGame
import networking.dto.PreGameResponse
import networking.format.FactionFormat
import play.api.libs.json.Json
import play.api.libs.ws._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by dan-s on 17/07/2016.
  */
trait HomeScreenCommunication {
  def createGame(): Future[PreGameResponse]
  def joinGame(simpleId: String): Future[PreGameResponse]
}

class HomeScreenCommImpl(ws: WSClient) extends HomeScreenCommunication {
  override def createGame(): Future[PreGameResponse] = {
    implicit val factionFormat = FactionFormat
    ws.url("http://localhost:9000/createGame")
      .get()
      .map { response =>
        Json.reads[PreGameResponse].reads(response.json)
          .getOrElse(throw new Exception("Could not parse response"))
      }
  }

  override def joinGame(simpleId: String): Future[PreGameResponse] = {
    implicit val factionFormat = FactionFormat
    ws.url("http://localhost:9000/joinGame")
      .withQueryString("simpleId" -> simpleId)
      .get()
      .map { response =>
        Json.reads[PreGameResponse].reads(response.json)
          .getOrElse(throw new Exception("Could not parse response"))
      }
  }
}

class LocalHomeScreenComm(mainGame: LocalGame) extends HomeScreenCommunication {
  override def createGame(): Future[PreGameResponse] = Future.successful(mainGame.createGame())

  override def joinGame(simpleId: String): Future[PreGameResponse] = Future.successful(mainGame.joinGame(simpleId))
}