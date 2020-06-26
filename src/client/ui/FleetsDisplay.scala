package client.ui

import java.util.UUID

import core.{Constants, StatusReport}
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.{GameContainer, Graphics}

import scala.collection.mutable

/**
  * Created by dan-s on 12/07/2016.
  */
class FleetsDisplay(gameContainer: GameContainer, isSelected: IsSelected, onFleetSelect: (UUID) => Unit) {
  def left = Constants.HEADER_TILE_SIZE // make room for the add fleet button
  def top = 0
  def height = Constants.HEADER_TILE_SIZE
  def width = gameContainer.getWidth - left

  private val fleetButtonManager = new FleetButtonManager(onFleetSelect, isSelected)

  /**
    * Render the reports as a horizontal row of selectable tiles
    *
    * @param reports The latest report for each fleet
    */
  def render(reports: Seq[StatusReport], graphics: Graphics): Unit = {
    fleetButtonManager.removeUnnecessaryButtons(reports.map(_.fleetId))
    reports.zipWithIndex.foreach{ case (report, index) =>
      val buttonXPosition = left + index*Constants.HEADER_TILE_SIZE
      fleetButtonManager.getFleetButton(report.fleetId, gameContainer, buttonXPosition, top, graphics)
        .render(gameContainer, graphics)
      val shortId = report.fleetId.toString.take(3)
      RenderingHelpers.renderStrings(
        List(shortId, report.locationSummary), buttonXPosition, top, Constants.HEADER_TILE_SIZE, graphics
      )
    }
  }
}

private class FleetButtonManager(onFleetSelect: (UUID) => Unit, isSelected: IsSelected) {
  private var fleetButtons: mutable.HashMap[UUID, FleetButton] = mutable.HashMap.empty
  def getFleetButton(fleetId: UUID, gameContainer: GameContainer, x: Int, y: Int, graphics: Graphics): SquareButton = {
    val button = fleetButtons.getOrElseUpdate(
      fleetId,
      new FleetButton(
        gameContainer,
        new Rectangle(x, y, Constants.HEADER_TILE_SIZE, Constants.HEADER_TILE_SIZE),
        () => {
          onFleetSelect(fleetId)
        }
      )
    )
    button.setIsSelected(isSelected.fleetIsSelected(fleetId))
    button.setX(x)
    button.setY(y)
    button
  }

  def removeUnnecessaryButtons(fleetIds: Seq[UUID]): Unit = {
    val toDelete = fleetButtons.keys.toList.diff(fleetIds)
    toDelete.foreach(key =>
      fleetButtons(key).setAcceptingInput(false)
    )
    fleetButtons --= toDelete
  }
}