define(["jquery"], function ($) {

    // Creates a lobby, using html elements
    class PreGameUserInterface {
        constructor(preGameCommunication, simpleId, isGameCreator, game, container) {
            this.communication = preGameCommunication;
            this.simpleId = simpleId;
            this.isGameCreator = isGameCreator;
            this.game = game;

            this.container = container;
            this._initiateElements(container);
        }

        onClickBeginGame() {
            var that = this;
            that.communication.beginGame()
                .done(function (response) {
                    that.game.initiateGame(response);
                })
                .fail(function (error) {
                    console.error(error);
                });
        }

        _initiateElements(container) {
            $(container)
                .empty()
                .append(
                    $("<button>")
                        .text("Begin")
                        .addClass("begin-game-btn")
                        .click(this.onClickBeginGame.bind(this))
                )
        }

        destroy() {
            this.container.empty();
        }
    }

    return {
        PreGameUserInterface: PreGameUserInterface
    };

});