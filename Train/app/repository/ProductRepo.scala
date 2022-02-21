package repository

import org.bson.codecs.configuration.{CodecProvider, CodecRegistry}
import cats.effect.{IO, IOApp}
import cats.effect.IO._
import cats.effect.unsafe.implicits.global
import org.mongodb.scala.bson.{Document, ObjectId}
import org.mongodb.scala.{Completed, FindObservable, MongoClient, MongoCollection, MongoDatabase, Observer, SingleObservable}
import org.mongodb.scala.bson.codecs.Macros._
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.{equal, gte}
import org.mongodb.scala.result.{DeleteResult, UpdateResult}

import scala.language.higherKinds
import cats.implicits._
import com.google.inject.ImplementedBy



case class Product(_id: String, name: String, cost: String)
object Product {
  def apply(name: String, cost: String): Product = Product((new ObjectId()).toString, name, cost);
}

@ImplementedBy(classOf[ProductRepositoryImpl])
trait ProductRepository {
  def findById(objectId: String): IO[Product]

  def findAll(): IO[Seq[Product]]

  def create(product: Product): IO[Product]

  def update(person: Product): IO[Boolean]

  def delete(objectId: String): IO[Boolean]

  def deleteAll(): IO[Boolean]

  def createMany(list: List[Product]): IO[List[Product]]
}


class ProductRepositoryImpl extends ProductRepository {
  implicit val ec: scala.concurrent.ExecutionContext = scala.concurrent.ExecutionContext.global
  val mongoClient: MongoClient = MongoClient()
  val database: MongoDatabase = mongoClient.getDatabase("mydb")
  val codecRegistry: CodecRegistry = fromRegistries(fromProviders(classOf[Product]), org.mongodb.scala.MongoClient.DEFAULT_CODEC_REGISTRY)

  val collectionP: MongoCollection[Product] = database.withCodecRegistry(codecRegistry).getCollection("product")

  def idEqual(objectId: String): Bson =
    equal("_id", new ObjectId(objectId))

  override def findById(objectId: String): IO[Product] = {
    val observableFind: FindObservable[Product] = collectionP.find(idEqual(objectId))
    fromFuture[Product](IO(observableFind.first().head()))
  }

  override def findAll(): IO[Seq[Product]] = {
    val observableFind: FindObservable[Product] = collectionP.find()
    fromFuture[Seq[Product]](IO(observableFind.toFuture()))
  }

//  override def findByFilter(): IO[Seq[Product]] = {
//    val observableFind: FindObservable[Product] = collectionP
//      .find(Filters.and(Filters.gt("cost", 90)))
//      .sort(descending("cost")).limit(3)
//    fromFuture[Seq[Product]](IO(observableFind.toFuture()))
//  }

  override def create(product: Product): IO[Product] =  {
    val insertObservable: SingleObservable[Completed] = collectionP.insertOne(product)
    fromFuture[Product](IO(insertObservable.head().map(_ => product)))
  }

  override def createMany(list: List[Product]): IO[List[Product]] =  {
    val insertObservable: SingleObservable[Completed] = collectionP.insertMany(list)
    fromFuture[List[Product]](IO(insertObservable.head().map(_ => list)))
  }
  

  override def update(product: Product): IO[Boolean] = {
    val observableUpdate: SingleObservable[UpdateResult] = collectionP.replaceOne(idEqual(product._id.toString), product)
    fromFuture[Boolean](IO(observableUpdate.head().map(_.wasAcknowledged)))
  }

  override def delete(objectId: String): IO[Boolean] = {
    val observableDelete: SingleObservable[DeleteResult] = collectionP.deleteOne(idEqual(objectId))
    fromFuture[Boolean](IO(observableDelete.head().map(_.wasAcknowledged)))
  }
  override def deleteAll(): IO[Boolean] = {
    val observableDelete: SingleObservable[Completed] = collectionP.drop()
    fromFuture[Boolean](IO(observableDelete.head().map(_ => true)))
  }
}
