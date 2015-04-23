package config

import dao.file.{FileSettingsDao, FileVacationsDao}
import dao.memory.{InMemorySettingsDao, InMemoryVacationsDao}
import dao.{SettingsDao, VacationsDao}
import org.joda.time.format.DateTimeFormat

trait Configuration {
  val AppDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
  val settingsDao: SettingsDao
  val vacationsDao: VacationsDao
}

object Configuration {
  implicit val configuration = FileConfiguration
}

object InMemoryConfiguration extends Configuration {
  val settingsDao = new InMemorySettingsDao
  val vacationsDao = new InMemoryVacationsDao
}

object FileConfiguration extends Configuration {
  val settingsDao = new FileSettingsDao
  val vacationsDao = new FileVacationsDao
}

