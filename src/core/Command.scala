package core

import java.util.UUID

/**
  * Created by dan-s on 28/05/2016.
  */
sealed trait Command
case class GoTo(systemId: UUID, fleetId: UUID) extends Command

trait ParsedCommand {
  def commandOrigin: StarSystem // system command was sent from
  def faction: Faction
  def fleetId: UUID
  def time: Long // time that command was sent
}
case class ParsedGoTo(starSystem: StarSystem, fleetId: UUID, commandOrigin: StarSystem, faction: Faction, time: Long) extends ParsedCommand