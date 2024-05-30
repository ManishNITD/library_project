package services

import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import daos.BookDAO

import javax.inject._
import models.Book
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import play.api.libs.json.Json

import scala.concurrent.Future

@Singleton
class BookService @Inject()(bookDAO: BookDAO)(implicit system: ActorSystem, mat: Materializer) {
  private val kafkaConsumerSettings = ConsumerSettings(system, new StringDeserializer, new StringDeserializer)
    .withBootstrapServers("34.47.143.23:9092")
    .withGroupId("book-service-group")
    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest")

  Consumer
    .plainSource(kafkaConsumerSettings, Subscriptions.topics("book-topic"))
    .mapAsync(1) { msg =>
      val json = Json.parse(msg.value())
      val bookId = (json \ "id").as[Long]
      val assigned = (json \ "assigned").as[Boolean]
      updateBookAssignmentStatus(bookId, assigned)
    }
    .runWith(Sink.ignore)

  private def updateBookAssignmentStatus(bookId: Long, assigned: Boolean): Future[Unit] = {
    bookDAO.updateAssignedStatus(bookId, assigned)
  }

  def allBooks(): Future[Seq[Book]] = bookDAO.all()

  def addBook(book: Book): Future[Unit] = bookDAO.insert(book)

  def deleteBook(id: Long): Future[Boolean] = bookDAO.delete(id)
}
