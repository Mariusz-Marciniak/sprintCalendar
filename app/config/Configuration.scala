package config

import java.text.DateFormat

import dao.{SettingsDao, VacationsDao}
import dao.memory.{InMemorySettingsDao, InMemoryVacationsDao}

trait Configuration {
  val AppDateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
  val settingsDao: SettingsDao
  val vacationsDao: VacationsDao
}

object Configuration {
  implicit val config = InMemoryConfiguration
}

object InMemoryConfiguration extends Configuration {
  val settingsDao = new InMemorySettingsDao
  val vacationsDao = new InMemoryVacationsDao
}


