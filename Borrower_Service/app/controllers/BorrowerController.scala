package controllers

import javax.inject._
import play.api.mvc._
import services.BorrowerService
import models.Borrower
import scala.concurrent.ExecutionContext

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
    val bookIds = request.body.asFormUrlEncoded.get("bookIds").map(_.toLong)
    borrowerService.assignBooks(borrowerId, bookIds).map(_ => Redirect(routes.BorrowerController.listBorrowers))
  }

  def unassignBooks(borrowerId: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val bookIds = request.body.asFormUrlEncoded.get("bookIds").map(_.toLong)
    borrowerService.unassignBooks(borrowerId, bookIds).map(_ => Redirect(routes.BorrowerController.listBorrowers))
  }
}

