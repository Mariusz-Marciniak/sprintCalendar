package controllers

import dao.SettingsDao
import entities.{DateRange, WorkingDays}
import play.api.libs.json.JsArray

object CustomUtil {
  import config.Configuration._
  import config.JsonImplicits._

  private val settingsDao = configuration.settingsDao

  private[controllers] def workingDaysWithoutHolidays(range: DateRange): WorkingDays = {
    import entities.WorkingDays._
    workdaysInRange(
      range,
      workdaysFromJsObject(settingsDao.loadDayAndPrecision.getOrElse(SettingsDao.DefaultDaysAndPrecisionOptions))
    ) filterHolidays(
      holidaysInRange(holidaysFromJsArray(settingsDao.loadHolidays.getOrElse(JsArray())),range)
      )
  }

}
