package dao.memory

import dao.SettingsDao
import play.api.libs.json.{JsObject, JsValue, JsArray, JsString}

import scala.util.{Success, Try}

class InMemorySettingsDao extends SettingsDao {
  import config.JsonImplicits._

  private var employees: JsArray = JsArray(Seq(
    JsObject(Seq(("label",JsString("Thomas")))),
    JsObject(Seq(("label",JsString("Mary"))))
  ))
  private var holidays: JsArray = JsArray(Seq(
    JsObject(Seq(
      ("label",JsString("Christmas Day 12-25")),
      ("name",JsString("Christmas Day")),
      ("date",JsString("12-25"))
    )),
    JsObject(Seq(
      ("label",JsString("Boxing Day 12-26")),
      ("name",JsString("Boxing Day")),
      ("date",JsString("12-26"))
    ))
  ))
  private var sprints: JsArray = JsArray(Seq(
    JsObject(Seq(
      ("label",JsString("Sprint 1 2015-12-14::2016-01-01")),
      ("name",JsString("Sprint 1")),
      ("from",JsString("2015-12-14")),
      ("to",JsString("2016-01-01"))
    )),
    JsObject(Seq(
      ("label",JsString("Sprint 2 2016-01-02::2016-01-18")),
      ("name",JsString("Sprint 2")),
      ("from",JsString("2016-01-02")),
      ("to",JsString("2016-01-18"))
    ))
  ))

  private var dayAndPrecision: JsObject = SettingsDao.DefaultDaysAndPrecisionOptions

  override def saveEmployees(employees: JsValue): Try[JsValue] = {
    println(s"saving employees: $employees")
    this.employees = employees
    Success(employees)
  }

  override def loadEmployees: Try[JsValue] = Success(employees)

  override def loadEmployeesNames: Seq[String] = {
    (employees \\ "label").map {
      case s:JsString  =>  s.value
      case v:JsValue  => "Unknown"
    }
  }

  override def saveHolidays(holidays: JsValue): Try[JsValue] = {
    println(s"saving holidays: $holidays")
    this.holidays = holidays
    Success(holidays)
  }

  override def loadHolidays: Try[JsValue] = Success(holidays)

  override def saveSprints(sprints: JsValue): Try[JsValue] = {
    println(s"saving sprints: $sprints")
    this.sprints = sprints
    Success(sprints)
  }

  override def loadSprints: Try[JsValue] = Success(sprints)

  override def saveDayAndPrecision(daysAndPrecisionOptions: JsValue): Try[JsValue] =  {
    println(s"saving days and precisions options: $daysAndPrecisionOptions")
    this.dayAndPrecision = daysAndPrecisionOptions
    Success(daysAndPrecisionOptions)
  }

  override def loadDayAndPrecision: Try[JsValue] = Success(dayAndPrecision)
}
