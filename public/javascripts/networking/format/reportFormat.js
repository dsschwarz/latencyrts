define(["report"], function (_report) {
    return {

        fromDTO: function (reportDTO, starSystems) {
            var payload = reportDTO.payload;
            if (reportDTO.name == _report.ReportTypes.Status) {
                return _report.GenericStatusReport.fromDTO(payload, starSystems);
            } else if (reportDTO.name == _report.ReportTypes.Departure) {
                return _report.DepartureReport.fromDTO(payload, starSystems);
            } else {
                throw new Error("Unrecognized report type")
            }
        }
    }
});