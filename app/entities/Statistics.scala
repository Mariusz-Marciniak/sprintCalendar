package entities

import config.Configuration
import config.JsonImplicits._
import org.joda.time.LocalDate
import play.api.libs.json.{JsObject, JsValue, JsArray}

import scala.math.BigDecimal.RoundingMode


object Statistics {
  private val Zero = BigDecimal("0.00")

  def apply()(implicit config:Configuration): Statistics = new Statistics(DateRange(new LocalDate(2000,1,1), new LocalDate(1999,1,1)))(config)
  def apply(range:DateRange)(implicit config:Configuration): Statistics = new Statistics(range)(config)
}

class Statistics(range: DateRange)(implicit config: Configuration) {

  val fromDate = range.fromDate
  val toDate = range.toDate

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


  lazy val calculateVelocities: Seq[VelocityEntry] = calculateVelocitiesFor(sprints)

  private def calculateVelocitiesFor(sprints: Seq[JsValue]): Seq[VelocityEntry] = {
    sprints map (sprint => calculateVelocitiesFor(castToJsString(sprint \ "label").value)) flatten
  }

  private def calculateVelocitiesFor(sprintName: String): Option[VelocityEntry] = {
    val sprintDataOption = config.sprintsDao.loadSprintData(sprintName)
    if(sprintDataOption.isSuccess) {
      val sprintData: JsObject = config.sprintsDao.loadSprintData(sprintName).get
      val unitsInSprint: BigDecimal = totalUnitsInSprint(sprintData)
      val storyPoints: BigDecimal = castToJsNumber(sprintData \ "storyPoints").value
      if("hours".equals(precisionType)) {
        val perHour = (storyPoints / unitsInSprint).setScale(2,RoundingMode.HALF_UP)
        val defaults = config.settingsDao.loadDayAndPrecision.getOrElse(dao.SettingsDao.DefaultDaysAndPrecisionOptions)
        val perDay = (perHour * castToJsNumber(defaults \ "precision" \ "perDay").value).setScale(2,RoundingMode.HALF_UP)
        val perWeek = (perDay * BigDecimal(amountOfWorkdaysInWeek)).setScale(2,RoundingMode.HALF_UP)
        Some(VelocityEntry(sprintName, Some(perHour), perDay, perWeek))
      } else {
        val perDay = (storyPoints / unitsInSprint).setScale(2,RoundingMode.HALF_UP)
        val perWeek = (perDay * BigDecimal(amountOfWorkdaysInWeek)).setScale(2,RoundingMode.HALF_UP)
        Some(VelocityEntry(sprintName, None, perDay, perWeek))
      }
    } else None
  }

  lazy val totalVelocity: VelocityEntry = avgVelocities(calculateVelocities, "Total velocity")

  lazy val globalVelocity: VelocityEntry =
    avgVelocities(calculateVelocitiesFor(castToJsArray(config.settingsDao.loadSprints.getOrElse(JsArray())).map(v => v)), "Global velocity")

  lazy val precisionType = {
    val defaults = config.settingsDao.loadDayAndPrecision.getOrElse(dao.SettingsDao.DefaultDaysAndPrecisionOptions)
    castToJsString(defaults \ "precision" \ "type").value
  }

  private def avgVelocities(velocities: Seq[VelocityEntry], label: String): VelocityEntry = {
    import Statistics.Zero
    if(velocities.size > 0) {
      val perHourSum: Option[BigDecimal] = velocities.map(_.perHour).reduceLeft(
        (sum, opt) => opt match {
          case Some(v) => Some(sum.getOrElse(Zero) + v)
          case None => None
        }
      )
      VelocityEntry(label,
        perHourSum map (_ / velocities.size setScale(2, RoundingMode.HALF_UP)),
        (velocities.map(_.perDay).sum / velocities.size).setScale(2, RoundingMode.HALF_UP),
        (velocities.map(_.perWeek).sum / velocities.size).setScale(2, RoundingMode.HALF_UP))
    } else {
      if("hours".equals(precisionType))
        VelocityEntry(label, Some(Zero), Zero, Zero)
      else
        VelocityEntry(label, None, Zero, Zero)
    }
  }

  private[entities] def totalUnitsInSprint(sprintData: JsObject): BigDecimal = {
      castToJsArray(sprintData \ "workload").map(v => castToJsNumber(v \ "availability").value).sum
  }

  def dataOfConfirmedSprint(sprintName: String) : Option[JsObject] = {
    val sprintData = config.sprintsDao.loadSprintData(sprintName)
    if(sprintData.isSuccess && castToJsBoolean(sprintData.get \ "confirmed").value)
      Some(sprintData.get)
    else
      None
  }

  def employeeVelocity(employeeName: String) : VelocityEntry = {
    avgVelocities(
      calculateVelocitiesFor(
        castToJsArray(config.settingsDao.loadSprints.getOrElse(JsArray())).filter(s => {
          val sprintName: String = castToJsString(s \ "label").value
          val sprintDataOption = dataOfConfirmedSprint(sprintName)
          if(sprintDataOption.isDefined) {
            val workload: JsArray = sprintDataOption.get \ "workload"
            workload.findRow("employee", employeeName) match {
              case Some(row) => castToJsNumber(row \ "availability").value > 0
              case None => false
            }
          } else false
        })
      ), s"$employeeName velocity")
  }

  def employeesVelocities= config.settingsDao.loadEmployeesNames map (employeeVelocity(_))

}

/**
 *
 * @param name velocity name
 * @param perHour is <ul><li>
 *                  <code>Some(v)</code> if <code>settings.dayAndPrecision.precision.type</code> set to <code>hours</code>
 *                  (<code>v</code> is calculated as: <code>storyPointsInSprint / sum(users availabilities)</code>)
 *                </li>
 *                <li><code>None</code> otherwise</li></ul>
 * @param perDay is calculated as <ul><li><code>perHour * settings.dayAndPrecision.precision.perDay</code>
 *               when <code>settings.dayAndPrecision.precision.type</code>
 *               set to <code>hours</code></li><li><code>storyPointsInSprint / sum(users availabilities)</code> otherwise</li></ul>
 * @param perWeek is calculated as <code>perDay * amountOfWorkDays from settings.dayAndPrecision.workdays</code>
 */
case class VelocityEntry(name: String, perHour: Option[BigDecimal], perDay: BigDecimal, perWeek: BigDecimal)
