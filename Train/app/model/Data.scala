package model

import model.ProductRepo.products

case class Product(id: String, name: String, cost: String)

class ProductRepo {
  def getProduct(id: String): Option[Product] = products.find(x => x.id == id)

  def getAll(): List[Product] = products

  def addProduct(product: Product): Product = {
    products = products ++ List(product)
    product
  }
}

object ProductRepo{
  var products = List(Product("1", "phone", "1000"), Product("2", "book", "20"), Product("3", "cap", "25"))

}