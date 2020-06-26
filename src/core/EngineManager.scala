package core

import java.util.UUID

import networking.dto.{BeginResponse, PreGameResponse, StarSystemDTO}

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer

/**
  * Created by dan-s on 17/07/2016.
  */
class EngineManager {
  val engines: ArrayBuffer[Engine] = ArrayBuffer.empty
  val preGames: ArrayBuffer[PreGame] = ArrayBuffer.empty

  def update(ms: Int): Unit = {
    engines.foreach(_.update(ms))
  }

  def createGame(): PreGameResponse = {
    val gameCreator = new Faction("Schwarz Empire", UUID.randomUUID(), Faction.getUniqueColor(List()))
    @tailrec
    def createUniqueGame: PreGame = {
      val preGame = new PreGame(gameCreator)
      if (findPreGameBySimpleId(preGame.simpleId).isDefined) {
        createUniqueGame
      } else {
        preGame
      }
    }
    val preGame = createUniqueGame
    preGames += preGame
    PreGameResponse(preGame.id, preGame.simpleId, gameCreator)
  }

  def joinGame(simpleId: String): PreGameResponse = {
    val preGame = findPreGameBySimpleId(simpleId)
      .getOrElse(throw new Exception(s"Could not find pre game with simple id $simpleId"))

    val color = Faction.getUniqueColor(preGame.getFactions)

    val faction = new Faction("New Faction", UUID.randomUUID(), color)
    preGame.addFaction(faction)
    PreGameResponse(preGame.id, preGame.simpleId, faction)
  }

  def beginGame(id: UUID): BeginResponse = {
    val preGame = getPreGame(id)
    val engine = preGame.createEngine()
    engines += engine
    preGames -= preGame
    BeginResponse(engine.id, engine.getFactions, engine.systems.map(StarSystemDTO.fromStarSystem))
  }

  def getGame(engineId: UUID): Engine = {
    engines.find(_.id == engineId)
      .getOrElse(throw new Exception(s"Could not find engine with id $engineId"))
  }

  def getPreGame(id: UUID): PreGame = {
    preGames.find(_.id == id)
      .getOrElse(throw new Exception(s"Could not find pre game with id $id"))
  }

  def findPreGameBySimpleId(simpleId: String): Option[PreGame] = {
    preGames.find(_.simpleId == simpleId)
  }
}
