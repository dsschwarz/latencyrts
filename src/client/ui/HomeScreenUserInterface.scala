package client.ui

import client.ClientGame
import networking.HomeScreenCommunication
import networking.dto.PreGameResponse
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.gui.TextField
import org.newdawn.slick.{Font, GameContainer, Graphics}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by dan-s on 12/07/2016.
  */
class HomeScreenUserInterface(clientGame: ClientGame, communication: HomeScreenCommunication, container: GameContainer) extends UserInterface {
  val createGameBtn: SquareButton = {
    val image = RenderingHelpers.imageWithText("Create a new game")
    new SquareButton(
      container,
      image,
      new Rectangle(container.getWidth/2, container.getHeight/2 - image.getHeight*2, image.getWidth, image.getHeight),
      () => {
        val promise = communication.createGame()
        promise.onSuccess {
          case response: PreGameResponse =>
            clientGame.initiatePreGame(response, isOwner = true)
            createGameBtn.setAcceptingInput(false)
        }
        promise.onFailure {
          case e: Throwable => throw e
        }
      }
    )
  }

  var joinGameIdInput: TextField = _
  def getJoinGameIdInput(font: Font): TextField = {
    if (joinGameIdInput == null) {
      joinGameIdInput = new TextField(container, font, container.getWidth/2, container.getHeight/3*2, 50, font.getLineHeight)
    }
    joinGameIdInput
  }

  val joinGameButton: SquareButton = {
    val image = RenderingHelpers.imageWithText("Join a game")
    new SquareButton(
      container,
      image,
      new Rectangle(container.getWidth/2, container.getHeight/2 + image.getHeight*2, image.getWidth, image.getHeight),
      () => {
        val promise = communication.joinGame(joinGameIdInput.getText)
        promise.onSuccess {
          case response: PreGameResponse =>
            clientGame.initiatePreGame(response, isOwner = false)
            createGameBtn.setAcceptingInput(false)
        }
        promise.onFailure {
          case e: Throwable => throw e
        }
      }
    )
  }

  override def render(gameContainer: GameContainer, graphics: Graphics): Unit = {
    createGameBtn.render(container, graphics)
    joinGameButton.render(container, graphics)
    graphics.drawString("Game Id:", container.getWidth/2 - 80, container.getHeight/3*2)
    getJoinGameIdInput(graphics.getFont).render(container, graphics)
  }
}
