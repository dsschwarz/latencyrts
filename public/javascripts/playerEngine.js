define(["lib/monet", "report", "command"], function (monet, _report, _command) {

class PlayerEngine {
    constructor(commLink, factions) {
        this.commLink = commLink;
        this.factions = factions;
    }

    getFaction(factionId) {
        return _.findWhere(this.factions, {id: factionId});
    }

    getNewReports() {
        return this.commLink.getNewReports();
    }

    getLatestFleetStatuses() {
        return _.chain(this.commLink.getAllReports())
            .filter(report => isStatusReport(report) || isDepartureReport(report)) // status, and any report that could remove a fleet from a system
            .groupBy(report => report.fleetId)
            .map(reports => _.max(reports, report => report.time)) // get the latest report
            .filter(report => isStatusReport(report)) // now, only return the status reports
            .value();
    }


    /**
     * Get the last report received from a fleet
     * @return A report object
     */
    getLatestReport(fleetId) {
        return this._genericGetFleetReport(fleetId, report => true);
    }

    /**
     * Get the last status report received from a fleet
     */
    getLatestStatusReport(fleetId) {
        return this._genericGetFleetReport(fleetId, isStatusReport);
    }

    /**
     * Get the last known star system that a given fleet was in
     * @param fleetId
     * @return An option of a system. None if fleet is in transit
     */
    getSystem(fleetId) {
        var latestReport = this.getLatestReport(fleetId);
        if (isDepartureReport(latestReport)) {
            return monet.None();
        } else {
            return monet.Some(latestReport.origin);
        }
    }

    /**
     * The latest reports about each fleet in a given system
     * @param systemId
     * @returns {*} A list of reports
     */
    getFleetsInSystem(systemId) {
        return _.chain(this.getLatestFleetStatuses())
            .filter(report => !isDepartureReport(report))
            .filter(report => report.origin.id == systemId)
            .value();
    }

    getStarSystems() {
        return this.commLink.getStarSystems();
    }

    // Actions
    createFleet() {
        this.commLink.createFleet();
    }

    sendFleetToSystem(fleetId, systemId) {
        this.commLink.sendCommand(_command.GoTo(fleetId, systemId));
    }

    /**
     * Send all the fleets currently known to be in a system to another system
     * @param fromSystemId
     * @param systemId
     */
    sendFleetsInSystem(fromSystemId, systemId) {
        var fleetReports = this.getFleetsInSystem(fromSystemId);
        fleetReports.forEach(fleetReport => this.commLink.sendCommand(_command.GoTo(fleetReport.fleetId, systemId)));
    }

    addReportReceivedCallback(callback) {
        this.commLink.addReportReceivedCallback(callback);
    }

    /**
     * Helper method to get the latest report from a fleet
     */
    _genericGetFleetReport(fleetId, reportFilter) {
        return _.chain(this.commLink.getAllReports())
            .filter(reportFilter)
            .filter(report => report.fleetId == fleetId)
            .max(report => report.time)
            .value();
    }
}

function isStatusReport(report) {
    return report.type == _report.ReportTypes.Status;
}

function isDepartureReport(report) {
    return report.type == _report.ReportTypes.Departure;
}

return {
    PlayerEngine: PlayerEngine
}
});