package networking.format

import core.{Command, GoTo}
import play.api.libs.json.{Format, JsResult, JsValue, Json}

/**
  * Created by dan-s on 17/07/2016.
  */
object CommandFormat extends Format[Command] {
  override def reads(json: JsValue): JsResult[Command] = {
    Json.reads[TypedPayload].reads(json).flatMap(dto =>
      dto.name match {
        case "GoTo" => Json.reads[GoTo].reads(dto.payload)
      }
    )
  }

  override def writes(o: Command): JsValue = {
    val commandDTO: TypedPayload = o match {
      case c: GoTo => TypedPayload("GoTo", Json.writes[GoTo].writes(c))
    }
    Json.writes[TypedPayload].writes(commandDTO)
  }
}