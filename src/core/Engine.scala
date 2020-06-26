package core

import java.util.UUID

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import scala.util.control.NonFatal

/**
  * Created by dan-s on 24/05/2016.
  */
class Engine(val id: UUID, initialFactions: Seq[Faction], initialSystems: Seq[StarSystem],
             initialCenters: Map[Faction, StarSystem]) {
  // When sending system information to the client, the user only knows the owners of systems their ships have visited
  private val unknownFaction = Faction.createUnknownFaction()
  val systems: Seq[StarSystem] = initialSystems
  var fleets: List[Fleet] = Nil
  // map of faction id to faction home system
  private val factionCenters: mutable.HashMap[UUID, StarSystem] = mutable.HashMap.empty
  initialCenters.foreach {case (faction, center) => factionCenters.put(faction.id, center) }

  private val factions = initialFactions :+ unknownFaction

  private val systemDamage = 0.05
  private val timeDelayFactor = 5d
  private var hadError = false

  val reportManager = new ReportManager()
  var commands: ArrayBuffer[ParsedCommand] = ArrayBuffer.empty

  private val clock = new EngineClock()

  println("Engine created")
  println("Faction centers:")
  factionCenters.foreach{ case (factionId, starSystem) =>
      println(s"${getFaction(factionId).name} in ${starSystem.name}")
  }

  def update(ms: Int): Unit = {
    if (!hadError) {
      try {
        clock.update(ms)
        handleCommands()
        fleets = fleets.map(_.update(ms))
        performBattles(ms)
        reassignSystems()
        factions.foreach(_.update(ms, this))
      } catch {
        case NonFatal(e) =>
          println(e.getMessage)
          e.printStackTrace()
          hadError = true
      }
    }
  }

  def processCommand(command: Command, factionId: UUID): Unit = {
    val faction = getFaction(factionId)
    command match {
      case GoTo(systemId, fleetId) =>
        val system = systems.find(_.id == systemId).getOrElse(throw new Exception("System not found for id " + systemId))
        commands += ParsedGoTo(system, fleetId, getFactionCenter(factionId), faction, clock.time)
    }
  }

  def createFleet(factionId: UUID): Unit = {
    val faction = getFaction(factionId)
    val homeSystem = getFactionCenter(factionId)

    // use all of the faction's resources to create a new fleet
    val shipsToCreate = math.floor(faction.resources).toInt

    // Don't create an empty fleet
    if (shipsToCreate > 0) {
      val newFleet = new InSystemFleet(UUID.randomUUID(), faction, homeSystem, shipsToCreate, 0d, reportManager, clock)

      reportManager.addReport(
        GenericStatusReport(newFleet.id, newFleet.faction.id, StatusReportEvents.Creation, clock.time, homeSystem, newFleet.ships)
      )

      fleets = fleets :+ newFleet
      faction.resources -= shipsToCreate
    }
  }

  /**
    * Returns the reports for a faction, accounting for information delay
    *
    * @param factionId The id of the faction to get reports for
    * @return
    */
  def getReports(factionId: UUID): Seq[Report] = {
    val factionCenter = getFactionCenter(factionId)
    reportManager.reports.filter((report) => {
      report.factionId == factionId && informationIsAvailable(report.origin, factionCenter, report.time)
    })
  }

  def getFactions = factions

  private def getFactionCenter(factionId: UUID): StarSystem = factionCenters(factionId)

  private def handleCommands(): Unit = {
    commands = commands.filter((command) => {
      val commandProcessed: Boolean = fleets.find(_.id == command.fleetId) match {
        case Some(fleet: InSystemFleet) =>
          assert(fleet.faction == command.faction, "Fleet must belong to faction")
          if (informationIsAvailable(command.commandOrigin, fleet.location, command.time)) {
            println(s"<${clock.time}> ${fleet.id} processing command ${command.getClass.getSimpleName}")
            updateFleetInList(fleet.processCommand(command))
            true
          } else {
            false
          }
        case Some(fleet: InterstellarFleet) => false // fleet is in transit, do nothing
        case None =>
          println("Fleet does not exist, dropping command", command.fleetId)
          true
      }
      !commandProcessed
    })
  }

  private def updateFleetInList(fleet: Fleet) = {
    fleets = (fleets.map(fleet => (fleet.id, fleet)).toMap + ((fleet.id, fleet))).values.toList
  }

  private def performBattles(ms: Int): Unit = {
    fleetsInSystem.groupBy(_.location).foreach { case (system, allFleetsInSystem) =>
      val factions = allFleetsInSystem.map(_.faction).toSet
      val damageDealtPerShip = factions.map(faction =>
        (faction, calculateDamageDealtPerShip(faction, system.owner == faction, allFleetsInSystem) * ms)
      ).toMap

      allFleetsInSystem.groupBy(_.faction).foreach { case (faction, alliedFleets) =>
        // take damage from all the other fleets
        val damageTaken = damageDealtPerShip.filterKeys(_ != faction).values.sum
        alliedFleets.foreach(fleet => fleet.takeDamage(damageTaken*fleet.ships))
      }
    }

    fleets = fleets.filter(_.ships > 0)
  }

  private def calculateDamageDealtPerShip(faction: Faction, addSystemDamage: Boolean, allShipsInSystem: Seq[Fleet]): Double = {
    val (alliedFleets, allEnemies) = allShipsInSystem.partition(_.faction == faction)
    val fleetDamage = alliedFleets.map(_.damage).sum
    val enemyShipCount = allEnemies.map(_.ships).sum
    val totalDamagePerShip = if (addSystemDamage) {
      systemDamage + fleetDamage/enemyShipCount // system contributes damage that scales with enemy numbers
    } else {
      fleetDamage/enemyShipCount
    }
    totalDamagePerShip
  }

  private def reassignSystems(): Unit = {
    fleetsInSystem.groupBy(_.location).foreach { case (system, allFleetsInSystem) =>
      var systemHasFriendlyFleet = false
      var homogeneousFleet = true
      val iterator = allFleetsInSystem.toIterator

      while (!systemHasFriendlyFleet && iterator.hasNext) {
        val fleet = iterator.next()
        if (fleet.faction == system.owner) {
          // if system has a single fleet belonging to the owner, then system cannto be reassigned
          systemHasFriendlyFleet = true
        }
        if (fleet.faction != allFleetsInSystem.head.faction) {
          homogeneousFleet = false
        }
      }

      if (!systemHasFriendlyFleet && homogeneousFleet) {
        system.setOwner(allFleetsInSystem.head.faction)
      }
    }
  }

  private def calculateInformationDelay(origin: StarSystem, destination: StarSystem): Double = {
    Helpers.distance(origin.x, destination.x, origin.y, destination.y) * timeDelayFactor
  }

  private def informationIsAvailable(origin: StarSystem, destination: StarSystem, timeSent: Long): Boolean = {
    val delay = calculateInformationDelay(origin, destination)
    timeSent + delay < clock.time
  }

  private def fleetsInSystem: Seq[InSystemFleet] = {
    fleets.collect {
      case fleet: InSystemFleet => fleet
    }
  }

  private def fleetsTravelling: Seq[InterstellarFleet] = {
    fleets.collect {
      case fleet: InterstellarFleet => fleet
    }
  }

  def getFaction(factionId: UUID): Faction = {
    factions.find(_.id == factionId).getOrElse(throw new Exception("No faction for id " + factionId))
  }
}
