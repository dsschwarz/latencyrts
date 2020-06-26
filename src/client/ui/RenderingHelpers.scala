package client.ui

import core.Constants
import org.newdawn.slick.geom.{Circle, Rectangle}
import org.newdawn.slick.{Color, GameContainer, Graphics, Image}

import scala.collection.mutable

/**
  * Created by dan-s on 12/07/2016.
  */
object RenderingHelpers {
  // Splits a string so that each substring is less than the given width
  private def splitString(string: String, width: Int, g: Graphics): Seq[String] = {
    val font = g.getFont
    var i = 0
    while (i < string.length - 1) {
      if (font.getWidth(string.substring(0, i+1)) > width) {
        return List(string.substring(0, i)) ++ splitString(string.substring(i), width, g)
      }
      i+=1
    }

    List(string)
  }

  /**
    * Renders each string on a new line. Long strings will be wrapped into multiline
    *
    * @param strings The strings to render
    * @param x Left side of each string
    * @param y Starting y position
    * @param width The maximum length of any line
    */
  def renderStrings(strings: Seq[String], x: Int, y: Int, width: Int, graphics: Graphics): Unit = {
    // all the lines
    val lines = strings.flatMap(string =>
      RenderingHelpers.splitString(string, width, graphics)
    )
    lines.zipWithIndex.foreach { case (namePart, yIndex) =>
      graphics.drawString(namePart, x, y + yIndex * graphics.getFont.getLineHeight)
    }
  }

  private val cachedCircle: mutable.HashMap[Color, Image] = mutable.HashMap.empty
  // Returns the same circle for all
  def simpleCircle(color: Color): Image = {
    cachedCircle.getOrElseUpdate(color, {
      val image = new Image(30, 30)
      val graphics = image.getGraphics
      graphics.setColor(color)
      graphics.draw(new Circle(Constants.SYSTEM_SIZE, Constants.SYSTEM_SIZE, Constants.SYSTEM_SIZE))
      graphics.flush()
      image
    })
  }

  private val cachedSquares: mutable.HashMap[Color, Image] = mutable.HashMap.empty
  // Returns the same circle for all
  def simpleSquare(color: Color, size: Int): Image = {
    cachedSquares.getOrElseUpdate(color, {
      val image = new Image(10, 10)
      val graphics = image.getGraphics
      graphics.setColor(color)
      graphics.draw(new Rectangle(0, 0, 10, 10))
      graphics.flush()
      image
    }).getScaledCopy(size, size)
  }

  def scalingFactor(container: GameContainer): Double = {
    0.9 * Math.min(
      (container.getHeight.toDouble- Constants.HEADER_TILE_SIZE)/Constants.UNIVERSE_HEIGHT,
      (container.getWidth.toDouble - Constants.INFO_PANEL_WIDTH)/Constants.UNIVERSE_WIDTH
    )
  }

  def imageWithText(text: String) = {
    val font = new Image(0, 0).getGraphics.getFont
    val width = font.getWidth(text)
    val height = font.getHeight(text)
    val image = new Image(width, height)
    val graphics = image.getGraphics
    graphics.drawString(text, 0, 0)
    graphics.flush()
    image
  }
}
