package controllers

import play.api.mvc.{Action, Controller};


object Vacations extends Controller {
  import config.Configuration._

  private val dao = config.dao

  def mainPage = Action { implicit request => {
      Ok(views.html.vacations(dao.loadEmployeesNames))
    }
  }
}
