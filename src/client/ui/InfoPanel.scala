package client.ui

import client.PlayerEngine
import core.{Constants, StatusReport}
import org.newdawn.slick.geom.Rectangle
import org.newdawn.slick.{Color, GameContainer, Graphics}

/**
  * Created by dan-s on 17/07/2016.
  */
class InfoPanel(selectionManager: SelectionManager, playerEngine: PlayerEngine) {
  def render(container: GameContainer, graphics: Graphics): Unit = {
    val panelArea = new Rectangle(container.getWidth - Constants.INFO_PANEL_WIDTH, 0, Constants.INFO_PANEL_WIDTH, container.getHeight)
    graphics.setColor(Color.darkGray)
    graphics.fill(panelArea)
    graphics.setColor(Color.lightGray)
    graphics.draw(panelArea)

    selectionManager.selectedFleet match {
      case Some(fleetId) =>
        val latestReport: StatusReport = playerEngine.getLatestStatusReport(fleetId)
        // TODO dschwarz give random names to fleets
        val shortId = fleetId.toString.take(3)

        val displayItems = Seq(
          DisplayItem("Identifier", shortId),
          DisplayItem("Ships", latestReport.fleetSize.toString),
          DisplayItem("Status", latestReport.locationSummary)
        )
        renderDisplayItems(container, graphics, displayItems)

      case None =>
        selectionManager.selectedSystem match {
          case Some(systemId) =>
            val system = playerEngine.systems.find(_.id == systemId)
              .getOrElse(throw new Exception(s"Could not find system $systemId"))
            val fleets = playerEngine.getFleetsInSystem(systemId)
            val displayItems = Seq(
              DisplayItem("Name", system.name),
              DisplayItem("Owner", system.owner.name),
              DisplayItem("Income", system.income.toString),
              DisplayItem("Fleets", fleets.length.toString),
              DisplayItem("Ships", fleets.map(_.fleetSize).sum.toString)  // the total number of ships in system
            )
            renderDisplayItems(container, graphics, displayItems)
          case None =>
            // do nothing
        }
    }
  }

  private def renderDisplayItems(container: GameContainer, graphics: Graphics, displayItems: Seq[DisplayItem]): Unit = {
    val margin = 10
    RenderingHelpers.renderStrings(
      displayItems.map(displayItem => s"${displayItem.key}: ${displayItem.value}"),
      container.getWidth - Constants.INFO_PANEL_WIDTH + margin,
      margin,
      Constants.INFO_PANEL_WIDTH - margin*2,
      graphics
    )
  }
}

case class DisplayItem(key: String, value: String)