package model

import model.ProductRepo
import sangria.execution.deferred.{Fetcher, HasId}
import sangria.schema._
import model.Product
import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

object SchemaDefinition {

  val products = Fetcher.caching(
    (ctx: ProductRepo, ids: Seq[String]) =>
      Future.successful(ids.flatMap(id => ctx.getProduct(id))))(HasId(_.id))

  val Product_ =
    ObjectType(
      "Product",
      "Something you can buy",
      interfaces[ProductRepo, Product](),
      fields[ProductRepo, Product](
        Field("id", StringType,
          resolve = _.value.id),
        Field("name", OptionType(StringType),
          resolve = _.value.name),
        Field("cost", OptionType(StringType),
          resolve = _.value.cost)
      ))

  val ID = Argument("id", StringType)

  val Name = Argument("name", StringType)
  val Cost = Argument("cost", (StringType))


  val Query = ObjectType(
    "Query", fields[ProductRepo, Unit](
      Field("product", OptionType(Product_),
        arguments = ID :: Nil,
        resolve = ctx => ctx.ctx.getProduct(ctx arg ID)),
      Field("products", OptionType(ListType(Product_)),
        arguments =  Nil,
        resolve = ctx => ctx.ctx.getAll()),
      Field("addProduct", OptionType(Product_),
        arguments =  ID :: Name :: Cost :: Nil,
        resolve = c => c.ctx.addProduct(Product(c arg ID, c arg Name, c arg Cost)))
    ))

  val ProductSchema = Schema(Query)

}
