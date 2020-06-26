package networking.format

import networking.dto.{BeginResponse, StarSystemDTO}
import play.api.libs.json._

/**
  * Created by dan-s on 17/07/2016.
  */
object BeginResponseFormat extends Format[BeginResponse] {
  implicit val factionReads = Reads.seq(FactionFormat)
  implicit val factionWrites = Writes.seq(FactionFormat)
  implicit val starSystemReads = Reads.seq(Json.format[StarSystemDTO])
  implicit val starSystemWrites = Writes.seq(Json.format[StarSystemDTO])
  override def writes(o: BeginResponse): JsValue = {
    Json.writes[BeginResponse].writes(o)
  }

  override def reads(json: JsValue): JsResult[BeginResponse] = {
    Json.reads[BeginResponse].reads(json)
  }

  private case class CreationResponseDTO(

                                        )
}
