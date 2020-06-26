package networking.dto

import java.util.UUID

import core.Faction

case class PreGameResponse(gameId: UUID, simpleId: String, faction: Faction)
case class BeginResponse(gameId: UUID, factions: Seq[Faction], systems: Seq[StarSystemDTO])
