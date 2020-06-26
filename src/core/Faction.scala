package core

import java.util.UUID

import core.Faction.Color

/**
  * Created by dan-s on 24/05/2016.
  */
class Faction(val name: String, val id: UUID, val color: Color) {
  var resources: Double = 0
  private val TEN_SECONDS = 10000d  // in milliseconds
  def update(ms: Int, engine: Engine): Unit = {
    val resourcesPer10Seconds = engine.systems.foldLeft(0d) { case (sum: Double, system: StarSystem) =>
      sum + (if (system.owner == this) system.income else 0)
    }
    resources += resourcesPer10Seconds * ms/TEN_SECONDS
  }
}
object Faction {
  type Color = String
  def createUnknownFaction() = new Faction("Unknown", UUID.randomUUID(), "#555") // id doesn't matter
  def createNeutralFaction() = new Faction("Neutral", UUID.randomUUID(), "#243")

  def getUniqueColor(otherFactions: Seq[Faction]): Color = {
    (Faction.colors -- otherFactions.map(_.color)).head
  }

  val colors = Set(
    "#98abc5", "#8a89a6", "#7b6888", "#6b486b", "#a05d56", "#d0743c", "#ff8c00"
  )
}
