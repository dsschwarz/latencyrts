package core

import java.util.UUID

import core.StatusReportEvents.StatusReportEvent

/**
  * Created by dan-s on 28/05/2016.
  */
trait Report {
  def fleetId: UUID
  def factionId: UUID // the faction of the ship that generated the report
  def time: Long
  def origin: StarSystem
  def fleetSize: Int
}

trait StatusReport extends Report {
  def locationSummary: String
}

case class GenericStatusReport(
  fleetId: UUID,
  factionId: UUID,
  event: StatusReportEvent,
  time: Long,
  origin: StarSystem,
  fleetSize: Int) extends StatusReport {
  override def locationSummary: String = {
    s"In ${origin.name}"
  }
}

object StatusReportEvents extends Enumeration {
  type StatusReportEvent = Value
  val Arrival = Value("arrival")
  val Update = Value("update")
  val Creation = Value("creation")
}

case class DepartureReport(
  fleetId: UUID,
  factionId: UUID,
  time: Long,
  origin: StarSystem,
  destination: StarSystem,
  fleetSize: Int) extends StatusReport {
  override def locationSummary: String = {
    s"Travelling to ${destination.name}"
  }
}

case class CommandAcknowledgement(commandId: UUID,
                                  fleetId: UUID,
                                  factionId: UUID,
                                  time: Long,
                                  origin: StarSystem,
                                  fleetSize: Int) extends Report

case class BattleReport(fleetId: UUID,
                        factionId: UUID,
                        time: Long,
                        origin: StarSystem,
                        fleetSize: Int,
                        losses: Int) extends Report

case class FleetDestruction(fleetId: UUID,
                            factionId: UUID,
                            time: Long,
                            origin: StarSystem) extends Report {
  val fleetSize = 0
}