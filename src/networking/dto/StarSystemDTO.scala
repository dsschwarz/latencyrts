package networking.dto

import java.util.UUID

import core.{Faction, StarSystem}

case class StarSystemDTO(ownerId: String,
                                 x: Int,
                                 y: Int,
                                 income: Int,
                                 name: String,
                                 id: String) {
  def toStarSystem(factions: Seq[Faction]) = {
    new StarSystem(
      factions.find(_.id.toString == ownerId)
        .getOrElse(throw new Exception(s"Could not find faction $ownerId")),
      x,
      y,
      income,
      name,
      UUID.fromString(id)
    )
  }
}

object StarSystemDTO {
  def fromStarSystem(o: StarSystem) = {
    StarSystemDTO(o.owner.id.toString, o.x, o.y, o.income, o.name, o.id.toString)
  }
}
