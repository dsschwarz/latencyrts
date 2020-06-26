package client.ui

import java.util.UUID

import client.PlayerEngine
import core.StarSystem

/**
  * Created by dan-s on 14/07/2016.
  */
// Remembers the currently selected UI component, and will send commands to the player engine when things are selected
class SelectionManager(playerEngine: PlayerEngine) extends IsSelected {
  private var _selectedValue: Option[SelectedValue] = None

  def selectedFleet: Option[UUID] = _selectedValue collect {
    case SelectedFleet(fleetId) => fleetId
  }
  def selectedSystem: Option[UUID] = _selectedValue collect {
    case SelectedSystem(systemId) => systemId
  }

  def deselect(): Unit = {
    _selectedValue = None
  }

  def selectFleet(fleetId: UUID): Unit = {
    _selectedValue = _selectedValue match {
      case _ => Some(SelectedFleet(fleetId))
    }
  }

  def selectStarSystem(systemId: UUID, button: Int): Unit = {
    _selectedValue = _selectedValue match {
      case Some(SelectedFleet(fleetId)) if button == 1=>
        playerEngine.goTo(systemId, fleetId)
        None // deselect after sending go to command
      case _ => Some(SelectedSystem(systemId))
    }
  }

  private sealed trait SelectedValue
  private case class SelectedFleet(fleetId: UUID) extends SelectedValue
  private case class SelectedSystem(starSystemId: UUID) extends SelectedValue

  override def fleetIsSelected(fleetId: UUID): Boolean = {
    _selectedValue match {
      case None => false
      case Some(SelectedFleet(id)) => id == fleetId
      case Some(SelectedSystem(starSystemId)) =>
        val latestReport: Option[StarSystem] = playerEngine.getSystem(fleetId)
        latestReport.fold(false)(_.id == starSystemId)
    }
  }

  override def systemIsSelected(starSystemId: UUID): Boolean = {
    _selectedValue match {
      case Some(SelectedSystem(id)) => id == starSystemId
      case Some(SelectedFleet(fleetId)) =>
        val latestReport: Option[StarSystem] = playerEngine.getSystem(fleetId)
        latestReport.fold(false)(_.id == starSystemId)
      case _ => false
    }
  }
}

// Contains methods that Indicate whether something is selected, without exposing the other methods on the SelectionManager
trait IsSelected {
  def fleetIsSelected(fleetId: UUID): Boolean
  def systemIsSelected(starSystemId: UUID): Boolean
}