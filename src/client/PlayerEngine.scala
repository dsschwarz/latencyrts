package client

import java.util.UUID

import core._
import networking.InGameCommunication

/**
  * Created by dan-s on 26/05/2016.
  */

// knows only about reports, and maybe predicted positions for fleets in transit
class PlayerEngine(commLink: InGameCommunication) {
  def getNewReports = commLink.getNewReports

  // The most recent status report for each fleet the player controls
  def latestFleetStatuses: Seq[StatusReport] =
  // TODO handle fleet destruction - should not get a status report?
    commLink.getAllReports
      .collect({
        case report: StatusReport => report
      })
      .groupBy(_.fleetId).values  // group the reports by fleet
      .map(reports =>
        // For each list of reports for a single fleet, find the latest one
        reports.maxBy(_.time)
      )
      .toList

  /**
    * Get the last report received from a fleet
    */
  def getLatestReport(fleetId: UUID): Report = {
    commLink.getAllReports.filter(_.fleetId == fleetId).maxBy(_.time)
  }
  /**
    * Get the last status report received from a fleet
    */
  def getLatestStatusReport(fleetId: UUID): StatusReport = {
    latestFleetStatuses.find(_.fleetId == fleetId).getOrElse(throw new Exception(s"No report for fleet $fleetId"))
  }

  /**
    * Get the last known star system for a fleet
    * @return Some if in system, None if fleet is in transit
    */
  def getSystem(fleetId: UUID): Option[StarSystem] = {
    val latestReport = getLatestReport(fleetId)
    latestReport match {
      case r: DepartureReport => None
      case _ => Some(latestReport.origin)
    }
  }

  // todo this is wrong
  def getFleetsInSystem(systemId: UUID): Seq[Report] = {
    latestFleetStatuses.filterNot(_.isInstanceOf[DepartureReport])
  }

  var systems: Seq[StarSystem] = commLink.getStarSystems


  def addFleet(): Unit = {
    commLink.createFleet()
  }

  def goTo(systemId: UUID, fleetId: UUID): Unit = {
    commLink.sendAction(GoTo(systemId, fleetId))
  }
}
