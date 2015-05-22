package entities

import config.{InMemoryConfiguration, Configuration}
import config.JsonImplicits._
import org.joda.time.LocalDate
import play.api.libs.json.{JsValue, JsArray}


object Statistics {
  def apply(range:DateRange)(implicit config:Configuration): Statistics = new Statistics(range)(config)
}

class Statistics(range: DateRange)(implicit config: Configuration) {
  lazy val sprints: Seq[JsValue] = castToJsArray(config.settingsDao.loadSprints.getOrElse(JsArray())).filter(
    jsValue=> DateRange(LocalDate.parse(castToJsString(jsValue \ "from").value),LocalDate.parse(castToJsString(jsValue \ "to").value)).in(range)
  )

  lazy val sprintsNames: Seq[String] = sprints.map(
    v => castToJsString(v \ "label").value
  )

  lazy val calculateVelocities: Seq[VelocityEntry] = {
    ???
  }

  def totalUnitsInSprint: Int = {
    def sumAvailabilities(sprintData: JsArray) : Int = {
      sprintData map (v => castToJsNumber(v \ "availability").value.toInt) sum
    }

    sprints.foldLeft(0) { (sum, jsValue) =>
      sum + sumAvailabilities(castToJsArray(config.sprintsDao.loadSprintData(castToJsString(jsValue \ "label").value).get))
    }
  }
}

case class VelocityEntry(name: String, perHour: Double, perDay: Double, perWeek: Double)
