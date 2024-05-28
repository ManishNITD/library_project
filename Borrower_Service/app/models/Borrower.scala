package models

import play.api.libs.json._

case class Borrower(id: Long, name: String, assignedBooks: Seq[Long])

object Borrower {
  implicit val borrowerFormat: OFormat[Borrower] = Json.format[Borrower]

  def fromDb(id: Long, name: String, assignedBooks: String): Borrower = {
    val books = if (assignedBooks.isEmpty) Seq.empty[Long] else assignedBooks.split(",").map(_.toLong).toSeq
    Borrower(id, name, books)
  }

  def toDb(borrower: Borrower): (Long, String, String) = {
    val booksString = borrower.assignedBooks.mkString(",")
    (borrower.id, borrower.name, booksString)
  }
}
