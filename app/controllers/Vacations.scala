package controllers

import play.api.Routes
import play.api.mvc.{Action, Controller};
import play.api.libs.json.JsArray

import java.util.NoSuchElementException
import dao.VacationsDao


object Vacations extends Controller {
  import config.Configuration._

  private val settingsDao = config.settingsDao
  private val vacationsDao = config.vacationsDao

  def javascriptRoutes = Action {implicit request =>
    Ok(
      Routes.javascriptRouter("vacationsJsRoutes") (
        routes.javascript.Vacations.vacations
      )
    ).as("text/javascript")
  }

  def vacations(employee: String) = Action { implicit request => {
      try {
        Ok(vacationsDao.loadVacations(employee))
      } catch {
        case e: NoSuchElementException => Ok(JsArray())
      }
    }
  }


  def mainPage = Action { implicit request => {
      Ok(views.html.vacations(settingsDao.loadEmployeesNames))
    }
  }
}
