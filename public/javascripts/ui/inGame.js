define(["jquery", "knockout", "lib/d3", "selectionManager", "text!templates/inGame.ko.html"],
    function ($, ko, d3, _sm, template) {

    const Constants = {
        HEADER_TILE_SIZE: 70,
        INFO_PANEL_WIDTH: 150,
        UNIVERSE_HEIGHT: 800,
        UNIVERSE_WIDTH: 1000,
        minSystemSize: 5,
        maxSystemSize: 30,
        fleetSliceWidth: 3,
        sizeConsistency: 60 // higher value = less difference in size between systems of similar income
    };

    const RenderingHelpers = {
        scalingFactor: function (height, width) {
            return 0.9 * Math.min(
                (height) / Constants.UNIVERSE_HEIGHT,
                (width) / Constants.UNIVERSE_WIDTH
            )
        },
        getSystemSize: function getSystemSize(systemData) {
            return Constants.minSystemSize + (Constants.maxSystemSize - Constants.minSystemSize) * systemData.income / Constants.sizeConsistency;
        }
    };

    const CLASS = {
        systemContainer: "system-container",
        allSystemContainer: "all-system-container",
        planet: "star-system-planet"
    };

    // Creates a lobby, using html elements
    class InGameUserInterface {
        /**
         *
         * @param playerEngine {PlayerEngine}
         * @param container {d3.selection}
         */
        constructor(playerEngine, container) {
            const ui = this;
            this.playerEngine = playerEngine;

            this.view = $(template).appendTo(container);
            this.viewModel = this.createViewModel();
            ko.applyBindings(this.viewModel, this.view[0]);

            this.svgContainer = d3.select(this.getSvgContainer()[0]);

            this.selectionManager = new _sm.SelectionManager(playerEngine);

            this.systemContainers = this.svgContainer
                .append("g")
                  .attr("class", CLASS.allSystemContainer);

            this.playerEngine.addReportReceivedCallback(function () {
                ui.render();
            });

            this.render();

        }

        createViewModel() {
            const ui = this;
            const viewModel = {
                createFleet: function () {
                    ui.playerEngine.createFleet();
                },
                playerFleets: ko.observableArray()
            };

            return viewModel;
        }

        render() {
            this._renderStarSystems(this.playerEngine.getStarSystems());
        }

        getSvgContainer() {
            return $(this.view).find("#svg-root-element");
        }

        _renderStarSystems(starSystems) {
            const updatedSystemContainers = this.systemContainers.selectAll("." + CLASS.systemContainer)
                .data(starSystems);

            const newContainers = updatedSystemContainers.enter().append('g')
                .attr("class", CLASS.systemContainer);

            this._fillSystemContainers(newContainers);

            const bbox = this.svgContainer.node().getBoundingClientRect();
            const scalingFactor = RenderingHelpers.scalingFactor(bbox.height, bbox.width);

            updatedSystemContainers
                .attr("transform", d => "translate(" + d.x*scalingFactor + "," + d.y*scalingFactor + ")")
                .select("." + CLASS.planet)
                .attr("r", function (d) {
                    return RenderingHelpers.getSystemSize(d);
                });

            this.renderFleetsInSystem(updatedSystemContainers, starSystems);

            updatedSystemContainers.exit().remove();
        }

        renderFleetsInSystem(systemContainers, starSystems) {
            const ui = this;
            // render fleets in a donut chart, where each faction is given a
            // slice proportional to the total size or their fleets
            const systemToFactionToFleets = {};

            starSystems.forEach(function (system) {
                const fleetReports = ui.playerEngine.getFleetsInSystem(system.id);
                systemToFactionToFleets[system.id] = _.groupBy(fleetReports, function (report) { return report.getFaction() });
            });

            const pie = d3.layout.pie()
                .sort(d => d.faction.name)
                .value(function (mixedData) {
                    let totalFleetSize = 0;
                    mixedData.fleetReports.forEach(report => totalFleetSize += report.fleetSize);
                    return totalFleetSize;
                });

            const fleetDisplayContainer = systemContainers
                .select(".fleet-display");
            // container for the part of the pie graph for a given faction
            const factionFleetElements = fleetDisplayContainer
                .selectAll(".faction-fleet")
                .data(systemData => {
                    const fleetsGroupedByFaction = systemToFactionToFleets[systemData.id];
                    // list of factions
                    const orderedFactions = _.keys(fleetsGroupedByFaction)
                        .map(factionId => ui.playerEngine.getFaction(factionId));
                    // Create data to use with pie function
                    const fleetReports = orderedFactions.map(faction => {
                        return {
                            faction: faction,
                            fleetReports: fleetsGroupedByFaction[faction.id]
                        };
                    });
                    const pieChartData = pie(fleetReports);

                    return orderedFactions.map((faction, index) => {
                        return {
                            faction: ui.playerEngine.getFaction(faction.id),
                            fleetReports: fleetReports[index],
                            pieChartData: pieChartData[index],
                            system: systemData
                        }
                    });
                });

            const newFactionFleetElements = factionFleetElements.enter().append("g")
                .attr("class", "faction-fleet");

            newFactionFleetElements
                .append("path")
                .attr("class", "arc");

            factionFleetElements.selectAll(".arc")
                .style("fill", function(d) { return d.faction.color; })
                .attr("d", d => {
                    const systemSize = RenderingHelpers.getSystemSize(d.system);
                    return d3.svg.arc()
                        .innerRadius(systemSize)
                        .outerRadius(systemSize + Constants.fleetSliceWidth)
                        (d.pieChartData);
                });

            // todo add floating text showing total fleet size

            factionFleetElements.exit().remove();
        }

        _fillSystemContainers(newContainers) {
            newContainers.append("g")
                .attr("class", "fleet-display");

            newContainers
                .on("click", this._onClickSystem.bind(this))
                .on("contextmenu", system => {
                    d3.event.preventDefault();
                    this._goToSystem(system);
                });
            newContainers.append("circle")
                .attr("class", CLASS.planet)
                .style("fill", "steelblue")
        }

        _onClickSystem(system) {
            console.log("selected system " + system.id);
            this.selectionManager.selectSystem(system.id);
        }

        _goToSystem(system) {
            console.log("goto system " + system.id);
            this.selectionManager.goToSystem(system.id);
        }

        destroy() {
            throw new Error("not implemented")
        }
    }

    return {
        InGameUserInterface: InGameUserInterface
    }
});