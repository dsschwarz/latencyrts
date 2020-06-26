package metagame

import java.util.UUID

import client._
import client.ui.HomeScreenUserInterface
import core._
import networking._
import networking.dto.{BeginResponse, PreGameResponse}
import org.newdawn.slick.{BasicGame, GameContainer, Graphics}

/**
  * The game object used for testing locally. Holds all the games, and the user interface
  */
class LocalGame() extends BasicGame("Latency RTS") with ClientGame {

  val engineManager = new EngineManager()

  override def update(gameContainer: GameContainer, ms: Int): Unit = {
    engineManager.update(ms)
  }

  // mock functions to let this behave like a server
  def createGame(): PreGameResponse = {
    engineManager.createGame()
  }

  def joinGame(simpleId: String): PreGameResponse = {
    engineManager.joinGame(simpleId)
  }

  def beginGame(id: UUID): BeginResponse = {
    println(s"Beginning game $id")
    engineManager.beginGame(id)
  }

  override protected def createHomeScreenComm(): HomeScreenCommunication = {
    new LocalHomeScreenComm(this)
  }

  override def createPreGameComm(response: PreGameResponse): PreGameCommunication = {
    println("Entering pregame UI")
    new LocalPreGameComm(this, engineManager.getPreGame(response.gameId), response.faction)
  }

  override def createInGameComm(response: BeginResponse): InGameCommunication = {
    println("Entering game UI")
    val engine = engineManager.getGame(response.gameId)
    new LocalInGameComm(
      engine,
      engine.getFaction(response.factions.head.id)
    )
  }
}



