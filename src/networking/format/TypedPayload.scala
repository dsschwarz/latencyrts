package networking.format

import play.api.libs.json.JsValue

/**
  * Created by dan-s on 17/07/2016.
  */
case class TypedPayload(name: String, payload: JsValue)
