package client.ui

import java.util.UUID

import client.PlayerEngine
import core.{Constants, StarSystem}
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.{GameContainer, Image}

import scala.collection.mutable

/**
  * Created by dan-s on 12/07/2016.
  */
class ButtonContainer(isSelected: IsSelected) {
  // callback that accepts the button that was clicked(0 for left, 1 for right)
  type SystemClickCallback = (Int) => Unit
  private val starSystemButtons: mutable.HashMap[UUID, SystemButton] = mutable.HashMap.empty
  def systemButton(container: GameContainer, system: StarSystem, onClick: => SystemClickCallback): SystemButton = {
    val button = starSystemButtons.getOrElseUpdate(system.id, {
      val scalingFactor = RenderingHelpers.scalingFactor(container)
      val x = system.x * scalingFactor
      val y = Constants.HEADER_TILE_SIZE + system.y*scalingFactor
      new SystemButton(
        container, x toInt, y toInt, onClick
      )
    })

    button.setIsSelected(isSelected.systemIsSelected(system.id))
    button
  }

  private var addFleetButton: SquareButton = _
  def getAddFleetButton(container: GameContainer, playerEngine: PlayerEngine): SquareButton = {
    if (addFleetButton == null) {
      addFleetButton = new SquareButton(
        container,
        new Image("images/plus.png", false, Image.FILTER_NEAREST).getScaledCopy(Constants.HEADER_TILE_SIZE, Constants.HEADER_TILE_SIZE),
        new Rectangle(0, 0, Constants.HEADER_TILE_SIZE, Constants.HEADER_TILE_SIZE),
        () => { playerEngine.addFleet()}
      )
    }
    addFleetButton
  }

  private var fleetsDisplay: FleetsDisplay = _
  // the onFleetSelect function is not created unless the fleetsDisplay is null
  def getFleetsDisplay(container: GameContainer, onFleetSelect: => (UUID) => Unit) = {
    if (fleetsDisplay == null) {
      fleetsDisplay = new FleetsDisplay(container, isSelected, onFleetSelect)
    }
    fleetsDisplay
  }
}
