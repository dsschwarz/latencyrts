define(["jquery", "networking/format/reportFormat"], function ($, ReportFormat) {

var FETCH_REPORT_INTERVAL = 1000;
class InGameCommunication {
    constructor(gameId, factionId, starSystems) {
        var that = this;
        that.basicRequestData = {
            gameId: gameId,
            factionId: factionId
        };
        that.starSystems = starSystems;
        that.allReports = [];
        that.reportsSeen = {}; // Map of report id to report

        // called when new reports are received
        // TODO only call when reports have not been seen before
        that.onReportReceivedCallbacks = [];

        setInterval(function () {
            // get all reports

            $.get("/getReports", that.basicRequestData)
                .then(function (reports) {
                    that.allReports = reports.map(report => ReportFormat.fromDTO(report, that.starSystems));
                    console.log("Got " + reports.length + " reports");
                    that.onReportReceivedCallbacks.forEach(callback => callback());
                })

        }, FETCH_REPORT_INTERVAL);
    }

    getStarSystems() {
        return this.starSystems;
    }

    // Returns all the reports since the last time this was called,
    // in the chronological order the reports were received
    getNewReports() {
        return []; // TODO
    }

    getAllReports() {
        return this.allReports;
    }

    sendCommand(command) {
        $.ajax({
            type: "POST",
            url: "/processCommand?" + $.param(this.basicRequestData),
            data: command.toDto(),
            contentType: "application/json; charset=utf-8"
        });
    }

    createFleet() {
        $.get("/createFleet", this.basicRequestData)
            .done(r => console.log("Fleet created"))
            .fail(e => console.error(e));
    }

    addReportReceivedCallback(callback) {
        this.onReportReceivedCallbacks.push(callback);
    }
}


return {
    InGameCommunication: InGameCommunication
}
});