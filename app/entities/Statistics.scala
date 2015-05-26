package entities

import config.Configuration
import config.JsonImplicits._
import org.joda.time.LocalDate
import play.api.libs.json.{JsObject, JsValue, JsArray}

import scala.math.BigDecimal.RoundingMode


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

  lazy val amountOfWorkdaysInWeek : Int = {
    val workdays: JsObject = config.settingsDao.loadDayAndPrecision.getOrElse(dao.SettingsDao.DefaultDaysAndPrecisionOptions) \ "workdays"
    (for {
      day <- WorkingDays.DaysNames
      if(castToJsBoolean(workdays \ day).value)
    } yield 1).sum
  }

  lazy val calculateVelocities: Seq[VelocityEntry] = {
    sprints map (jsValue => {
      val sprintName: String = castToJsString(jsValue \ "label").value
      val sprintData: JsObject = config.sprintsDao.loadSprintData(sprintName).get
      val storyPoints: BigDecimal = castToJsNumber(sprintData \ "storyPoints").value
      val unitsInSprint: BigDecimal = BigDecimal(totalUnitsInSprint(sprintName))
      if("hours".equals(castToJsString(config.settingsDao.loadDayAndPrecision.get \ "precision" \ "type").value)) {
        val perHour = (storyPoints / unitsInSprint).setScale(2,RoundingMode.HALF_UP)
        val perDay = (perHour * castToJsNumber(config.settingsDao.loadDayAndPrecision.get \ "precision" \ "perDay").value).setScale(2,RoundingMode.HALF_UP)
        val perWeek = (perDay * BigDecimal(amountOfWorkdaysInWeek)).setScale(2,RoundingMode.HALF_UP)
        VelocityEntry(sprintName, Some(perHour), perDay, perWeek)
      } else {
        val perDay = (storyPoints / unitsInSprint).setScale(2,RoundingMode.HALF_UP)
        val perWeek = (perDay * BigDecimal(amountOfWorkdaysInWeek)).setScale(2,RoundingMode.HALF_UP)
        VelocityEntry(sprintName, None, perDay, perWeek)
      }
    })
  }


  def totalUnitsInSprint(sprintId: String): Int = {
    if(sprintsNames.contains(sprintId))
      castToJsArray(castToJsObject(config.sprintsDao.loadSprintData(sprintId).get) \ "workload").map(v => castToJsNumber(v \ "availability").value.toInt).sum
    else
      0
  }

}

case class VelocityEntry(name: String, perHour: Option[BigDecimal], perDay: BigDecimal, perWeek: BigDecimal)
