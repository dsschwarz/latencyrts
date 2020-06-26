define(["jquery", "knockout", "text!templates/home.ko.html"], function ($, ko, template) {

    // Creates a lobby, using html elements
    class HomeScreenUserInterface {
        constructor(homeScreenCommunication, game, container) {
            this.communication = homeScreenCommunication;
            this.game = game;

            this.container = container;
            this._initiateElements(container);
        }

        onClickCreateGame() {
            var that = this;
            that.communication.createGame()
                .done(function (response) {
                    that.game.initiatePreGame(response, true);
                })
                .fail(function (error) {
                    console.error(error);
                });
        }

        onClickJoinGame() {
            var that = this;
            that.communication.joinGame($("#join-game-id-input").val())
                .done(function (response) {
                    that.game.initiatePreGame(response, false);
                })
                .fail(function (error) {
                    console.error(error);
                });
        }

        _initiateElements(container) {
            var elements = $(template).appendTo($(container).empty());

            ko.applyBindings({
                onClickCreateGame: this.onClickCreateGame.bind(this),
                onClickJoinGame: this.onClickJoinGame.bind(this)
            }, elements[0])
        }

        destroy() {
            this.container.empty();
        }
    }

    return {
        HomeScreenUserInterface: HomeScreenUserInterface
    }
});