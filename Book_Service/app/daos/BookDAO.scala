package daos

import models.Book
import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{Future, ExecutionContext}

class BookDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class BookTable(tag: Tag) extends Table[Book](tag, "books") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def assignedStatus = column[Boolean]("assigned_status")
    def * = (id, name, assignedStatus) <> ((Book.apply _).tupled, Book.unapply)
  }

  private val books = TableQuery[BookTable]

  def all(): Future[Seq[Book]] = db.run(books.result)

  def insert(book: Book): Future[Unit] = db.run(books += book).map(_ => ())

  def delete(id: Long): Future[Unit] = db.run(books.filter(_.id === id).delete).map(_ => ())

  def updateAssignedStatus(id: Long, assigned: Boolean): Future[Unit] = {
    val query = for (book <- books if book.id === id) yield book.assignedStatus
    db.run(query.update(assigned)).map(_ => ())
  }
}
