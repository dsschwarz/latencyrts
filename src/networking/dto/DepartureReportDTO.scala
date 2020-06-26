package networking.dto

import java.util.UUID

import core.{DepartureReport, StarSystem}


case class DepartureReportDTO(fleetId: UUID,
                              factionId: UUID,
                              time: Long,
                              originId: UUID,
                              destinationId: UUID,
                              fleetSize: Int
                             ) {
  def toModel(starSystems: Seq[StarSystem]) = {
    DepartureReport(
      fleetId,
      factionId,
      time,
      starSystems.find(_.id == originId).get,
      starSystems.find(_.id == destinationId).get,
      fleetSize
    )
  }
}
object DepartureReportDTO {
  def fromModel(report: DepartureReport): DepartureReportDTO = {
    DepartureReportDTO(
      report.fleetId,
      report.factionId,
      report.time,
      report.origin.id,
      report.destination.id,
      report.fleetSize
    )
  }
}