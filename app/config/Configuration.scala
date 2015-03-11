package config

import java.text.DateFormat

import dao.SettingsDao
import dao.memory.InMemorySettingsDao

trait Configuration {
  val AppDateFormat = DateFormat.getDateInstance(DateFormat.SHORT)
  val dao: SettingsDao
}

object Configuration {
  implicit val config = InMemoryConfiguration
}

object InMemoryConfiguration extends Configuration {
  val dao = new InMemorySettingsDao
}


