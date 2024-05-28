package daos

import models.Borrower

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{ExecutionContext, Future}

class BorrowerDAO @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  private val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private class BorrowerTable(tag: Tag) extends Table[(Long, String, String)](tag, "borrowers") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def assignedBooks = column[String]("assigned_books")
    def * = (id, name, assignedBooks)
  }

  private val borrowers = TableQuery[BorrowerTable]

  def all(): Future[Seq[Borrower]] = db.run(borrowers.result).map(_.map {
    case (id, name, assignedBooks) => Borrower.fromDb(id, name, assignedBooks)
  })

  def insert(borrower: Borrower): Future[Unit] = {
    val dbRow = Borrower.toDb(borrower)
    db.run(borrowers += dbRow).map(_ => ())
  }

  def updateBooks(borrowerId: Long, bookIds: Seq[Long]): Future[Unit] = {
    val booksString = bookIds.mkString(",")
    db.run(borrowers.filter(_.id === borrowerId).map(_.assignedBooks).update(booksString)).map(_ => ())
  }

  def deleteBooks(borrowerId: Long): Future[Unit] = {
    db.run(borrowers.filter(_.id === borrowerId).map(_.assignedBooks).update("")).map(_ => ())
  }
}
