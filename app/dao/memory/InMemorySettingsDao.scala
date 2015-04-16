package dao.memory

import dao.SettingsDao
import play.api.libs.json
import play.api.libs.json.{JsObject, JsValue, JsArray, JsString}

class InMemorySettingsDao extends SettingsDao {
  private var employees: JsArray = JsArray(Seq(JsObject(Seq(("label",JsString("Tomasz")))),JsObject(Seq(("label",JsString("Zosia"))))))
  private var holidays: JsArray = JsArray(Seq())
  private var sprints: JsArray = JsArray(Seq())

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
    println(s"saving holidays: $sprints")
    this.sprints = sprints
  }

  override def loadSprints: JsArray = sprints

}
