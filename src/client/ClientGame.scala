package client

import client.ui.{HomeScreenUserInterface, InGameUserInterface, PreGameUserInterface, UserInterface}
import networking.dto.{BeginResponse, PreGameResponse}
import networking.{HomeScreenCommunication, InGameCommunication, PreGameCommunication}
import org.newdawn.slick.{BasicGame, GameContainer, Graphics}

/**
  * Created by dan-s on 24/05/2016.
  */

trait ClientGame { self: BasicGame =>
  private var userInterface: UserInterface = _
  override def init(gameContainer: GameContainer) = {
    userInterface = new HomeScreenUserInterface(this, createHomeScreenComm(), gameContainer)
  }

  override def render(gameContainer: GameContainer, graphics: Graphics): Unit = {
    if (userInterface != null) {
      userInterface.render(gameContainer, graphics)
    } else {
      throw new Exception("User interface failed to be initialized")
    }
  }
  def initiatePreGame(response: PreGameResponse, isOwner: Boolean): Unit = {
    userInterface = new PreGameUserInterface(
      createPreGameComm(response),
      response.simpleId,
      isOwner,
      this
    )
  }
  def initiateGame(response: BeginResponse): Unit = {
    userInterface = new InGameUserInterface(
      new PlayerEngine(
        createInGameComm(response)
      )
    )
  }

  protected def createHomeScreenComm(): HomeScreenCommunication
  protected def createPreGameComm(response: PreGameResponse): PreGameCommunication
  protected def createInGameComm(response: BeginResponse): InGameCommunication
}
