package controllers

import javax.inject._
import play.api.mvc._
import services.BookService
import models.Book
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BookController @Inject()(cc: ControllerComponents, bookService: BookService)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  def listBooks: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    bookService.allBooks().map { books =>
      Ok(views.html.books.bookList(books))
    }
  }

  def addBook: Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    val bookData = request.body.asFormUrlEncoded.get
    val name = bookData("name").head
    val book = Book(0, name)
    bookService.addBook(book).map(_ => Redirect(routes.BookController.listBooks))
  }

  def deleteBook(id: Long): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    bookService.deleteBook(id).map { deleted =>
      if (deleted) {
        Redirect(routes.BookController.listBooks)
      } else {
        Redirect(routes.BookController.listBooks).flashing("error" -> "Book cannot be deleted as it is assigned.")
      }
    }
  }

}
