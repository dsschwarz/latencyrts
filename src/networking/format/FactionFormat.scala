package networking.format

import java.util.UUID

import core.Faction
import play.api.libs.json.{Format, JsResult, JsValue, Json}

/**
  * Created by dan-s on 17/07/2016.
  */
object FactionFormat extends Format[Faction] {
  override def writes(o: Faction): JsValue =
    Json.writes[FactionDTO].writes(FactionDTO(o.name, o.id.toString, o.color))

  override def reads(json: JsValue): JsResult[Faction] =
    Json.reads[FactionDTO].reads(json).map(factionDTO =>
      new Faction(factionDTO.name, UUID.fromString(factionDTO.id), factionDTO.color)
    )

  private case class FactionDTO(name: String, id: String, color: String)
}
