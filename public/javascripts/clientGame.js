define(["jquery", "ui/homeScreen", "ui/preGame", "ui/inGame", "playerEngine", "networking/homeScreen", "networking/preGame", "networking/inGame"],
function ($, _homeScreen, _preGame, _inGame, _playerEngine, _homeScreenComm, _preGameComm, _inGameComm) {
    class Game {
        constructor(container) {
            this.mainContainer = $(container);
            this.userInterface = new _homeScreen.HomeScreenUserInterface(
                Game.createHomeScreenComm(),
                this,
                this.mainContainer
            )
        }

        initiatePreGame(response, isGameCreator) {
            this.userInterface.destroy();

            this.userInterface = new _preGame.PreGameUserInterface(
                Game.createPreGameComm(response),
                response.simpleId, isGameCreator,
                this,
                this.mainContainer
            );
        }

        initiateGame(response) {
            this.userInterface.destroy();
            var playerEngine = new _playerEngine.PlayerEngine(Game.createInGameComm(response), response.factions);
            this.userInterface = new _inGame.InGameUserInterface(
                playerEngine,
                this.mainContainer
            )
        }

        // create networking services
        static createHomeScreenComm() {
            return new _homeScreenComm.HomeScreenCommunication();
        }

        static createPreGameComm(response) {
            return new _preGameComm.PreGameCommunication(response.gameId, response.faction.id);
        }

        static createInGameComm(response) {
            return new _inGameComm.InGameCommunication(response.gameId, response.factions[0].id, response.systems);
        }
    }

    return {
        Game: Game
    };
});
