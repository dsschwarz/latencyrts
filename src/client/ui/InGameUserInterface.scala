package client.ui

import java.util.UUID

import client.PlayerEngine
import org.newdawn.slick.{GameContainer, Graphics}

/**
  * Created by dan-s on 12/07/2016.
  */
class InGameUserInterface(val playerEngine: PlayerEngine) extends UserInterface {
  val selectionManager = new SelectionManager(playerEngine)
  val buttons = new ButtonContainer(selectionManager)
  val infoPanel = new InfoPanel(selectionManager, playerEngine)

  def render(gameContainer: GameContainer, graphics: Graphics): Unit = {
    // TODO add them to a list of reports, then render them
    playerEngine.getNewReports.foreach(report => {
      println(s"New report: ${report.getClass.getSimpleName}")
    })
    playerEngine.systems.map(system =>
      buttons.systemButton(gameContainer, system, (button: Int) =>
        selectionManager.selectStarSystem(system.id, button)
      )
    ).foreach(_.render(gameContainer, graphics))

    buttons.getFleetsDisplay(
      gameContainer,
      (fleetId: UUID) => selectionManager.selectFleet(fleetId)
    ).render(playerEngine.latestFleetStatuses, graphics)

    buttons.getAddFleetButton(
      gameContainer, playerEngine
    ).render(gameContainer, graphics)

    infoPanel.render(gameContainer, graphics)
  }
}


