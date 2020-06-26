define([], function () {
    return {
        GoTo: function (fleetId, toSystemId) {
            var toDto = function () {
                return JSON.stringify({
                    name: "GoTo",
                    payload: {
                        systemId: toSystemId,
                        fleetId: fleetId
                    }
                });
            };

            return {
                systemId: toSystemId,
                fleetId: fleetId,
                toDto: toDto
            }
        }
    }
});