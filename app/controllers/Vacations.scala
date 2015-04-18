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
  private val VacationsPrefix = "vacationsOf"

  def javascriptRoutes = Action {implicit request =>
    Ok(
      Routes.javascriptRouter("vacationsJsRoutes") (
        routes.javascript.Vacations.vacations,
        routes.javascript.Vacations.saveVacations
      )
    ).as("text/javascript")
  }

  def saveVacations = Action(parse.json) { implicit request =>
    val names = settingsDao.loadEmployeesNames
    names.foreach { case name => {
      val key = VacationsPrefix+name
      vacationsDao.saveVacations(key, convertToJsArray(request.body \ key))
    }}
    Ok(views.html.vacations(names))
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
