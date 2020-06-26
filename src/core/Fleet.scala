package core

import java.util.UUID

/**
  * Created by dan-s on 24/05/2016.
  */
sealed abstract class Fleet(val id: UUID, val faction: Faction, initialShipCount: Int, initialPartialDamage: Double) {
  private var _ships: Int = initialShipCount
  private var _partialDamage: Double = initialPartialDamage
  def ships: Int = _ships
  protected def partialDamage = _partialDamage

  def update(ms: Int): Fleet

  def takeDamage(damage: Double): Unit = {
    _ships -= Math.floor(_partialDamage + damage).toInt
    _partialDamage = (_partialDamage+damage)%1
  }

  // the damage dealt by this ship per millisecond
  def damage: Double = 0.002*_ships * damageMultiplier

  private def damageMultiplier = math.random*0.3 + 1
}

class InSystemFleet(id: UUID, faction: Faction, val location: StarSystem, initialShipCount: Int,
                    initialPartialDamage: Double, reportManager: ReportManager, clock: EngineClock)
  extends Fleet(id, faction, initialShipCount, initialPartialDamage) {

  def update(ms: Int): Fleet = {
    this
  }

  def processCommand(command: ParsedCommand): Fleet = {
    command match {
      case goTo: ParsedGoTo =>
        println(s"<${clock.time}> ${goTo.fleetId} going to ${goTo.starSystem.name}")
        reportManager.addReport(DepartureReport(id, faction.id, clock.time, location, goTo.starSystem, ships))
        new InterstellarFleet(id, faction, Interstellar(location, goTo.starSystem), ships, partialDamage, reportManager, clock)
    }
  }
}

class InterstellarFleet(id: UUID, faction: Faction, val location: Interstellar, initialShipCount: Int,
                        initialPartialDamage: Double, reportManager: ReportManager, clock: EngineClock)
  extends Fleet(id, faction, initialShipCount, initialPartialDamage) {

  def update(ms: Int): Fleet = {
    location.update(ms)
    if (location.progress > 1) {
      println(s"<${clock.time}> Arrived in system")
      reportManager.addReport(GenericStatusReport(id, faction.id, StatusReportEvents.Arrival, clock.time, location.to, ships))
      new InSystemFleet(id, faction, location.to, ships, partialDamage, reportManager, clock)
    } else {
      this
    }
  }
}