define([], function () {
    /**
     * Determines what happens when a fleet or system is selected
     */
    class SelectionManager {
        constructor(engine) {
            this.engine = engine;
            this._selectedFleet = undefined;
            this._selectedSystem = undefined;
        }

        getSelectedFleet() {
            return this._selectedFleet;
        }

        getSelectedSystem() {
            return this._selectedSystem;
        }

        deselect() {
            this._selectedFleet = this._selectedSystem = undefined;
        }

        selectFleet(fleetId) {
            this._selectedFleet = fleetId;
            this._selectedSystem = undefined;
        }

        goToSystem(systemId) {
            if (this._selectedFleet) {
                this.engine.sendFleetToSystem(this._selectedFleet, systemId);
            } else if (this._selectedSystem) {
                this.engine.sendFleetsInSystem(this._selectedSystem, systemId);
            } else {
                console.warn("Nothing selected");
            }
        }

        selectSystem(systemId) {
            this._selectedSystem = systemId;
            this._selectedFleet = undefined;
        }

        fleetIsSelected(fleetId) {
            return fleetId && fleetId == this._selectedFleet;
        }

        systemIsSelected(systemId) {
            return systemId && systemId == this._selectedSystem;
        }
    }

    return {
        SelectionManager: SelectionManager
    }
});