package client.ui

import core.Constants
import org.newdawn.slick.geom.Shape
import org.newdawn.slick.gui.{GUIContext, MouseOverArea}
import org.newdawn.slick.{Color, Image}

/**
  * Created by dan-s on 12/07/2016.
  */
class SquareButton(container: GUIContext, image: Image, shape: Shape, callback: () => Unit)
  extends MouseOverArea(container, image, shape) {

  override def mousePressed(button: Int, mx: Int, my: Int): Unit = {
    super.mousePressed(button, mx, my)
    if (this.isMouseOver) {
      callback()
    }
  }
}

class FleetButton(container: GUIContext, shape: Shape, callback: () => Unit)
  extends SquareButton(container, RenderingHelpers.simpleSquare(Color.blue, Constants.HEADER_TILE_SIZE), shape, callback) {


  def setIsSelected(flag: Boolean): Unit = {
    val image = if (flag) {
      RenderingHelpers.simpleSquare(Color.green, Constants.HEADER_TILE_SIZE)
    } else {
      RenderingHelpers.simpleSquare(Color.blue, Constants.HEADER_TILE_SIZE)
    }
    this.setNormalImage(image)
    this.setMouseDownImage(image)
    this.setMouseOverImage(image)
  }
}