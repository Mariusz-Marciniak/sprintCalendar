package dao.memory

import dao.SettingsDao
import play.api.libs.json
import play.api.libs.json.{JsObject, JsValue, JsArray, JsString}

class InMemorySettingsDao extends SettingsDao {
  private var employees: JsArray = JsArray(Seq(
    JsObject(Seq(("label",JsString("Thomas")))),
    JsObject(Seq(("label",JsString("Mary"))))
  ))
  private var holidays: JsArray = JsArray(Seq(
    JsObject(Seq(
      ("label",JsString("Christmas Day 25-12")),
      ("name",JsString("Christmas Day")),
      ("date",JsString("25-12"))
    )),
    JsObject(Seq(
      ("label",JsString("Boxing Day 26-12")),
      ("name",JsString("Boxing Day")),
      ("date",JsString("26-12"))
    ))
  ))
  private var sprints: JsArray = JsArray(Seq(
    JsObject(Seq(
      ("label",JsString("Sprint 1 2015-12-14::2016-1-1")),
      ("name",JsString("Sprint 1")),
      ("from",JsString("2015-12-14")),
      ("to",JsString("2016-1-1"))
    ))
  ))

  override def saveEmployees(employees: JsArray): Unit = {
    println(s"saving employees: $employees")
    this.employees = employees
  }

  override def loadEmployees: JsArray = employees

  override def loadEmployeesNames: Seq[String] = {
    (employees \\ "label").map {
      case s:JsString  =>  s.value
      case v:JsValue  => "Unknown"
    }
  }

  override def saveHolidays(holidays: JsArray): Unit = {
    println(s"saving holidays: $holidays")
    this.holidays = holidays
  }

  override def loadHolidays: JsArray = holidays

  override def saveSprints(sprints: JsArray): Unit = {
    println(s"saving sprints: $sprints")
    this.sprints = sprints
  }

  override def loadSprints: JsArray = sprints

}
