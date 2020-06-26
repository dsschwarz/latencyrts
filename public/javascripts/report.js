define([], function () {

    var ReportTypes = {
        Status: "GenericStatusReport",
        Departure: "DepartureReport"
    };

    class GenericStatusReport {
        constructor(fleetId, factionId, event, time, fleetSize, origin, destination) {
            this.type = ReportTypes.Status;
            this.fleetId = fleetId;
            this.factionId = factionId;
            this.event = event;
            this.time = time;
            this.fleetSize = fleetSize;
            this.origin = origin;
        }

        getFaction() {
            return this.factionId; // TODO special handling when sighting other fleets
        }

        static fromDTO(payload, starSystems) {
            return new GenericStatusReport(
                payload.fleetId,
                payload.factionId,
                payload.event,
                payload.time,
                payload.fleetSize,
                _.findWhere(starSystems, {
                    id: payload.starSystemId
                })
            )
        }
    }


    class DepartureReport {
        constructor(fleetId, factionId, time, fleetSize, origin, destination) {
            this.type = ReportTypes.Departure;
            this.fleetId = fleetId;
            this.factionId = factionId;
            this.time = time;
            this.fleetSize = fleetSize;
            this.origin = origin;
            this.destination = destination;
        }

        // Get the faction of the ships this report is about
        getFaction() {
            // this is not expected to be called in the current implementation
            debugger;
            return this.factionId;
        }

        static fromDTO(payload, starSystems) {
            return new DepartureReport(
                payload.fleetId,
                payload.factionId,
                payload.time,
                payload.fleetSize,
                _.findWhere(starSystems, {
                    id: payload.originId
                }),
                _.findWhere(starSystems, {
                    id: payload.destinationId
                })
            )
        }
    }

    return {
        ReportTypes: ReportTypes,
        GenericStatusReport: GenericStatusReport,
        DepartureReport: DepartureReport
    };
});