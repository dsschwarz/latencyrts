package networking.dto

import java.util.UUID

import core.{GenericStatusReport, StarSystem, StatusReportEvents}

case class GenericStatusReportDTO(fleetId: UUID,
                                  factionId: UUID, // faction the fleet the created the report belongs to
                                  event: String,
                                  time: Long,
                                  starSystemId: UUID,
                                  fleetSize: Int
                                 ) {
  @deprecated
  def toModel(starSystems: Seq[StarSystem]) = {
    GenericStatusReport(
      fleetId,
      factionId,
      StatusReportEvents.withName(event),
      time,
      starSystems.find(_.id == starSystemId).get,
      fleetSize
    )
  }
}

object GenericStatusReportDTO {
  def fromModel(report: GenericStatusReport): GenericStatusReportDTO = {
    GenericStatusReportDTO(
      report.fleetId,
      report.factionId,
      report.event.toString,
      report.time,
      report.origin.id,
      report.fleetSize
    )
  }
}