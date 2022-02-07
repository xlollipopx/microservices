import scala.annotation.tailrec

object Test {

  def sumK(list: List[Int], k: Int) = for {
    i <- (0 to list.size - k)
  } yield (list.slice(i, i + k).sum)

  def fibonacci(n: Int): Long = {
    @tailrec
    def fib(a: Long, b: Long, n: Int): Long = n match {
      case 0L => a
      case n  => fib(b, a + b, n - 1)
    }
    fib(0, 1, n)
  }

  def sum(l: List[List[Int]]): Int = {
    l.map(x => x.sum).max
  }
  def productFib(prod: Long): Array[Long] = {
    @tailrec
    def fib(a: Long, b: Long): Array[Long] = prod match {
      case _ if a * b == prod => Array(a, b, 1)
      case _ if a * b > prod  => Array(a, b, 0)
      case _                  => fib(b, a + b)
    }
    fib(0, 1)
  }

  def main(args: Array[String]): Unit = {

    print(productFib(4895).mkString("Array(", ", ", ")"))
  }
}
