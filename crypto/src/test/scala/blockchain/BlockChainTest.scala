package blockchain

import blockchain.BlockChain.{config, GenesisBlock}
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.{FlatSpec, FreeSpec, Matchers}
import org.scalatest.prop.GeneratorDrivenPropertyChecks

import scala.collection.immutable.Seq

class BlockChainTest extends FreeSpec with Matchers {

  object DataSetUp {
    val chain:       BlockChain  = BlockChain()
    val difficulty:  Int         = config.getInt("difficulty")
    val transaction: Transaction = Transaction("Alice", "Bob", 10, timestamp = 10)
  }
  import DataSetUp._

  "Test apply" in {
    assert(BlockChain().firstBlock == GenesisBlock)

  }

  "Test mine method" in {
    val hashActual = chain.mine(GenesisBlock)
    assert(hashActual.hash.slice(0, difficulty) == "0" * difficulty)
  }

  "Test transaction reward" in {
    chain.addBlock("Anton")
    assert(chain.latestBlock.transactions.length == 1)
  }
  "Test transaction when not enough money" in {
    chain.makePendingTransaction(transaction.sender, transaction.sender, transaction.amount)

    assert(chain.latestBlock.transactions.length == 1)
  }
  "Test transaction" in {
    chain.makePendingTransaction("Anton", transaction.sender, transaction.amount)
    chain.addBlock("Anton")
    assert(chain.getBalanceForCurrentWallet("Anton") == 190)
  }

  "Test validBlock when previous hash is invalid" in {
    assert(!chain.validBlock(Block(1, "LSkdmlKSdlvklKSdmv", 132, List(), 131, "Npm", "kskdskdmmskmdkcsd")))
  }

}
