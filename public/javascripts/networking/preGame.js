define(["jquery"], function ($) {

class PreGameCommunication {
    constructor(preGameId, factionId) {
        this.preGameId = preGameId;
        this.factionId = factionId;
    }
    // leaveGame() {}
    // Start fetching new events i.e. joins, leaves, begin game etc.

    beginGame() {
        return $.get("/beginGame", {id: this.preGameId});
    }
}

return {
    PreGameCommunication: PreGameCommunication
}
});