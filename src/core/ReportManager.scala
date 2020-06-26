package core

import scala.collection.mutable.ArrayBuffer

/**
  * Created by dan-s on 13/07/2016.
  */
class ReportManager {
  private val _reports: ArrayBuffer[Report] = ArrayBuffer.empty

  def addReport(report: Report): Unit = {
    _reports += report
  }

  def reports: Seq[Report] = _reports
}
