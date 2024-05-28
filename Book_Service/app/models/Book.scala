package models

import play.api.libs.json._

case class Book(id: Long, name: String, assignedStatus: Boolean = false)

object Book {
  implicit val bookFormat: OFormat[Book] = Json.format[Book]
}
