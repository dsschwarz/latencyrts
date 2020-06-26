package networking.format

import core._
import networking.dto.{DepartureReportDTO, GenericStatusReportDTO}
import play.api.libs.json._

/**
  * Created by dan-s on 17/07/2016.
  */
class ReportReader(starSystems: Seq[StarSystem]) extends Reads[Report] {
  override def reads(json: JsValue): JsResult[Report] = {
    Json.reads[TypedPayload].reads(json).flatMap { dto =>
      dto.name match {
        case "GenericStatusReport" =>
          Json.reads[GenericStatusReportDTO].reads(dto.payload).map(dto => dto.toModel(starSystems))
        case "DepartureReport" =>
          Json.reads[DepartureReportDTO].reads(dto.payload).map(dto => dto.toModel(starSystems))
        case _ => JsError.apply(s"Unknown payload type ${dto.name}")
      }
    }
  }
}

object ReportWriter extends Writes[Report] {
  override def writes(report: Report): JsValue = {
    val dto = report match {
      case o: GenericStatusReport =>
        TypedPayload(
          "GenericStatusReport",
          Json.writes[GenericStatusReportDTO].writes(GenericStatusReportDTO.fromModel(o))
        )
      case o: DepartureReport =>
        TypedPayload(
          "DepartureReport",
          Json.writes[DepartureReportDTO].writes(DepartureReportDTO.fromModel(o))
        )
      case _ =>
        println(s"WARNING: Unknown report type ${report.getClass.getSimpleName}")
        TypedPayload(
          "Unknown",
          JsNull
        )
    }
    Json.writes[TypedPayload].writes(dto)
  }
}