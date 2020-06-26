package client.ui

import client.ClientGame
import networking.PreGameCommunication
import networking.dto.BeginResponse
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.{GameContainer, Graphics, Image}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by dan-s on 18/07/2016.
  */
class PreGameUserInterface(communication: PreGameCommunication, simpleId: String, isOwner: Boolean, clientGame: ClientGame) extends UserInterface {
  private var beginGameButton: SquareButton = _
  private def getButtonImage: Image = {
    val image = new Image(50, 50)
    val graphics = image.getGraphics
    graphics.drawString("Begin", 5, image.getHeight/2)
    graphics.flush()
    image
  }
  private def getButton(container: GameContainer) = {
    if (beginGameButton == null) {
      val size = 100
      beginGameButton = new SquareButton(
        container,
        getButtonImage,
        new Rectangle(container.getWidth/2 - size/2, container.getHeight/2 - size/2, size, size),
        () => {
          val promise = communication.beginGame()
          promise.onSuccess {
            case response: BeginResponse =>
              clientGame.initiateGame(response)
              beginGameButton.setAcceptingInput(false)
          }
          promise.onFailure {
            case e: Throwable => throw e
          }
        }
      )
    }
    beginGameButton
  }

  override def render(gameContainer: GameContainer, graphics: Graphics): Unit = {
    // This works because isOwner does not change. The button is never initialized if isOwner=false
    if (isOwner) {
      getButton(gameContainer).render(gameContainer, graphics)
    }
    // draw the id 1/3 down the screen
    graphics.drawString(s"Game ID: $simpleId", gameContainer.getWidth/2, gameContainer.getHeight/3)
  }
}
