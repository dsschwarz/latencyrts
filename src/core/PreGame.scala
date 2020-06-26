package core

import java.util.UUID

import metagame.PreGameEvent

import scala.collection.mutable.ArrayBuffer
import scala.util.Random

/**
  * Holds information about a game before it begins
  */
class PreGame(owner: Faction) {
  private val neutralFaction = Faction.createNeutralFaction()
  private val factions: ArrayBuffer[Faction] = ArrayBuffer(owner)

  val id = UUID.randomUUID()
  val simpleId = id.toString.take(4)
  val starSystems = {
    val desiredSystems = 10
    val initialCoordinates = ArrayBuffer.empty[(Int, Int)]
    val rand = new Random()

    val shuffledStarNames: Iterator[String] = Random.shuffle(Constants.starNames.iterator)

    while(initialCoordinates.size < desiredSystems) {
      initialCoordinates.append((
        rand.nextInt(Constants.UNIVERSE_WIDTH),
        rand.nextInt(Constants.UNIVERSE_HEIGHT)
        ))
    }

    initialCoordinates.map { case (x, y) =>
      new StarSystem(neutralFaction, x, y, Constants.randomIncome(), shuffledStarNames.next(), UUID.randomUUID())
    }
  }

  def addFaction(faction: Faction): Unit = {
    factions += faction
  }

  def getEvents(factionId: UUID): Seq[PreGameEvent] = {
    List()  // TODO dschwarz
  }

  def getFactions: Seq[Faction] = factions

  def createEngine(): Engine = {
    val factionCenters = factions.zip(Random.shuffle(starSystems)).toMap
    factionCenters.foreach({case (faction, starSystem) =>
        starSystem.setOwner(faction)
        starSystem.setIncome(Constants.homeSystemIncome())
    })
    new Engine(UUID.randomUUID(), factions :+ neutralFaction, starSystems, factionCenters)
  }
}
