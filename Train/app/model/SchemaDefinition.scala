package model

import cats.effect.unsafe.implicits.global
import org.bson.BsonType
import repository._
import sangria.execution.deferred.{Fetcher, HasId}
import sangria.schema._

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

object SchemaDefinition {

  val Product_ =
    ObjectType(
      "Product",
      "Something you can buy",
      interfaces[ProductRepository, Product](),
      fields[ProductRepository, Product](
        Field("id", StringType,
          resolve = _.value._id),
        Field("name", OptionType(StringType),
          resolve = _.value.name),
        Field("cost", OptionType(StringType),
          resolve = _.value.cost)
      ))

  val ID: Argument[String] = Argument("id", StringType)
  val Name: Argument[String] = Argument("name", StringType)
  val Cost: Argument[String] = Argument("cost", (StringType))


  val Query: ObjectType[ProductRepository, Unit] = ObjectType(
    "Query", fields[ProductRepository, Unit](
      Field("product", OptionType(Product_),
        arguments = ID :: Nil,
        resolve = ctx => ctx.ctx.findById(ctx arg ID).unsafeToFuture()),
      Field("products", OptionType(ListType(Product_)),
        arguments =  Nil,
        resolve = ctx => ctx.ctx.findAll().unsafeToFuture())
    ))


  val Mutation: ObjectType[ProductRepository, Unit] = ObjectType(
    "Mutation", fields[ProductRepository, Unit](
      Field("addProduct", OptionType(Product_),
        arguments =  Name :: Cost :: Nil,
        resolve = c => c.ctx.create(Product(c arg Name, c arg Cost)).unsafeToFuture()),
      Field("updateProduct", OptionType(BooleanType),
        arguments =  Name :: Cost :: Nil,
        resolve = c => c.ctx.update(Product(c arg Name, c arg Cost)).unsafeToFuture()),
      Field("deleteProduct", OptionType(BooleanType),
        arguments = ID :: Nil,
        resolve = ctx => ctx.ctx.delete(ctx arg ID).unsafeToFuture()),
      Field("deleteAll", OptionType(BooleanType),
        arguments =   Nil,
        resolve = c => c.ctx.deleteAll().unsafeToFuture()),


    ))

  val ProductSchema: Schema[ProductRepository, Unit] = Schema(Query, Some(Mutation))

}
