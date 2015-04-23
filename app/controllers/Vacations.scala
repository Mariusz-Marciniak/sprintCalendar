package controllers

import play.api.Routes
import play.api.libs.json.JsArray
import play.api.mvc.{Action, Controller}


object Vacations extends Controller {
  import config.Configuration._

  private val settingsDao = configuration.settingsDao
  private val vacationsDao = configuration.vacationsDao

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
      val key = vacationsDao.VacationsPrefix+name
      vacationsDao.saveVacations(key, request.body \ key)
    }}
    Ok(views.html.vacations(names))
  }

  def vacations(employee: String) = Action { implicit request =>
    Ok(vacationsDao.loadVacations(employee).getOrElse(JsArray()))
  }

  def mainPage = Action { implicit request => {
      Ok(views.html.vacations(settingsDao.loadEmployeesNames))
    }
  }
}
