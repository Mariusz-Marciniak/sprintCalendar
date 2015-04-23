package config

import java.text.DateFormat

import dao.file.{FileVacationsDao, FileSettingsDao}
import dao.{SettingsDao, VacationsDao}
import dao.memory.{InMemorySettingsDao, InMemoryVacationsDao}

trait Configuration {
  val AppDateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
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

