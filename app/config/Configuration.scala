package config

import dao.file.{FileUserDefaultsDao, FileSettingsDao, FileVacationsDao}
import dao.memory.{InMemoryUserDefaultsDao, InMemorySettingsDao, InMemoryVacationsDao}
import dao.{UserDefaultsDao, SettingsDao, VacationsDao}
import org.joda.time.format.DateTimeFormat

trait Configuration {
  val AppDateFormatter = DateTimeFormat.forPattern("yyyy-MM-dd")
  val settingsDao: SettingsDao
  val vacationsDao: VacationsDao
  val userDefaultsDao: UserDefaultsDao
}

object Configuration {
  implicit val configuration = FileConfiguration
}

object InMemoryConfiguration extends Configuration {
  lazy val settingsDao = new InMemorySettingsDao
  lazy val vacationsDao = new InMemoryVacationsDao
  lazy val userDefaultsDao = new InMemoryUserDefaultsDao
}

object FileConfiguration extends Configuration {
  lazy val settingsDao = new FileSettingsDao
  lazy val vacationsDao = new FileVacationsDao
  lazy val userDefaultsDao = new FileUserDefaultsDao
}

