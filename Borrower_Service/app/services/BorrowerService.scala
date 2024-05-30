package services

import akka.actor.ActorSystem
import akka.kafka.ProducerSettings
import akka.kafka.scaladsl.Producer
import akka.stream.Materializer
import akka.stream.scaladsl.Source
import daos.BorrowerDAO
import models.Borrower
import org.apache.kafka.clients.producer.{ProducerConfig, ProducerRecord}
import org.apache.kafka.common.serialization.StringSerializer
import play.api.libs.json.Json
import javax.inject._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BorrowerService @Inject()(borrowerDAO: BorrowerDAO)(implicit system: ActorSystem, ec: ExecutionContext, mat: Materializer) {
  private val kafkaProducerSettings: ProducerSettings[String, String] =
    ProducerSettings(system, new StringSerializer, new StringSerializer)
      .withBootstrapServers("34.47.143.23:9092")
      .withProperty(ProducerConfig.ACKS_CONFIG, "all")

  def allBorrowers(): Future[Seq[Borrower]] = borrowerDAO.all()

  def addBorrower(borrower: Borrower): Future[Unit] = borrowerDAO.insert(borrower)

  def assignBooks(borrowerId: Long, bookIds: Seq[Long]): Future[Unit] = {
    for {
      _ <- borrowerDAO.updateBooks(borrowerId, bookIds)
    } yield {
      val records = bookIds.map { bookId =>
        new ProducerRecord[String, String]("book-topic", Json.stringify(Json.obj("id" -> bookId, "assigned" -> true)))
      }
      Source(records.toList)
        .runWith(Producer.plainSink(kafkaProducerSettings))
    }
  }

  def unassignBooks(borrowerId: Long, bookIds: Seq[Long]): Future[Unit] = {
    for {
      _ <- borrowerDAO.unassignBooks(borrowerId, bookIds)
    } yield {
      val records = bookIds.map { bookId =>
        new ProducerRecord[String, String]("book-topic", Json.stringify(Json.obj("id" -> bookId, "assigned" -> false)))
      }
      Source(records.toList)
        .runWith(Producer.plainSink(kafkaProducerSettings))
    }
  }
}
