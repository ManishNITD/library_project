package controllers

import javax.inject._
import play.api.mvc._
import services.BorrowerService
import models.Borrower
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BorrowerController @Inject()(cc: ControllerComponents, borrowerService: BorrowerService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def listBorrowers: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    borrowerService.allBorrowers().map { borrowers =>
      Ok(views.html.borrowers.borrowerList(borrowers))
    }
  }

  def addBorrower: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val borrowerData = request.body.asFormUrlEncoded.get
    val name = borrowerData("name").head
    val borrower = Borrower(0, name, Seq.empty)
    borrowerService.addBorrower(borrower).map(_ => Redirect(routes.BorrowerController.listBorrowers))
  }

  def assignBooks(borrowerId: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.body.asFormUrlEncoded match {
      case Some(formData) =>
        val bookIds = formData.get("bookIds").flatMap(_.headOption).map(_.split(",").map(_.trim.toLong).toSeq).getOrElse(Seq.empty)
        borrowerService.assignBooks(borrowerId, bookIds).map(_ => Redirect(routes.BorrowerController.listBorrowers))
      case None => Future.successful(BadRequest("Form data missing"))
    }
  }

  def unassignBooks(borrowerId: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.body.asFormUrlEncoded match {
      case Some(formData) =>
        val bookIds = formData.get("bookIds").flatMap(_.headOption).map(_.split(",").map(_.trim.toLong).toSeq).getOrElse(Seq.empty)
        borrowerService.unassignBooks(borrowerId, bookIds).map(_ => Redirect(routes.BorrowerController.listBorrowers))
      case None => Future.successful(BadRequest("Form data missing"))
    }
  }
}
