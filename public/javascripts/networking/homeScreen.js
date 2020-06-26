define(["jquery"], function ($) {
class HomeScreenCommunication {
    createGame() {
        return $.get("/createGame");
    }

    joinGame(simpleId) {
        return $.get("/joinGame", {simpleId: simpleId});
    }
}

return {
    HomeScreenCommunication: HomeScreenCommunication
}

});