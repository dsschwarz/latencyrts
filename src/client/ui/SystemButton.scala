package client.ui

import core.Constants
import org.newdawn.slick.Color
import org.newdawn.slick.geom.Circle
import org.newdawn.slick.gui.{GUIContext, MouseOverArea}

/**
  * Created by dan-s on 12/07/2016.
  */
class SystemButton(container: GUIContext, x: Int, y: Int, onClick: (Int) => Unit)
  extends MouseOverArea(container, RenderingHelpers.simpleCircle(Color.blue), new Circle(x, y, Constants.SYSTEM_SIZE)) {

  def setIsSelected(flag: Boolean): Unit = {
    val image = if (flag) {
      RenderingHelpers.simpleCircle(Color.green)
    } else {
      RenderingHelpers.simpleCircle(Color.blue)
    }
    this.setNormalImage(image)
    this.setMouseDownImage(image)
    this.setMouseOverImage(image)
  }

  override def mousePressed(button: Int, mx: Int, my: Int): Unit = {
    super.mousePressed(button, mx, my)
    if (this.isMouseOver) {
      onClick(button)
    }
  }
}
