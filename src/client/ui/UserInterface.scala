package client.ui

import org.newdawn.slick.{GameContainer, Graphics}

/**
  * Created by dan-s on 12/07/2016.
  */
trait UserInterface {
  def render(gameContainer: GameContainer, graphics: Graphics): Unit
}
