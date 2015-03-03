package util

import java.util.Date
import config.Configuration
import entities.{Employee, Sprint}

object Validator {
  def verifySprintDates(from: String, to: String, others: Set[Sprint]): Boolean = {
    val dFrom = parseToDate(from)
    val dTo = parseToDate(to)

    if (dFrom.before(dTo)) { 
      val collision = others.find(s => afterOrTheSame(dTo, s.from) && beforeOrTheSame(dFrom, s.to))
      collision match {
        case Some(collision) => {
          val collisionFrom = formatDate(collision.from) 
          val collisionTo = formatDate(collision.to) 
          println(s"collision between: $from - $to and $collisionFrom - $collisionTo")
          false
        }
        case None => true
      }
    } else
      false
  }

  def isEmployeeNameUnique(name: String, inCollection: Iterable[Employee]) : Boolean = !inCollection.exists(emp => name.equalsIgnoreCase(emp.name))
  
  private def afterOrTheSame(d1: Date, d2: Date): Boolean = d1.compareTo(d2) >= 0 
    
  private def beforeOrTheSame(d1: Date, d2: Date): Boolean = d1.compareTo(d2) <= 0 

  private def parseToDate(date: String)(implicit config: Configuration): Date = config.AppDateFormat.parse(date)
  
  private def formatDate(date: Date)(implicit config: Configuration): String = config.AppDateFormat.format(date)

}