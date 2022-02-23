package blockchain

import com.roundeights.hasher.Implicits._

import java.util.Date
import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.language.postfixOps

case class Block(
  index:        Int,
  previousHash: String,
  nonce:        Int,
  transactions: List[Transaction],
  timestamp:    Long,
  data:         String,
  hash:         String
)

case class Transaction(sender: String, receiver: String, amount: Int, timestamp: Long = -1)
object GenesisBlock
  extends Block(
    0,
    "0",
    0,
    List(),
    123135346,
    "Genesis block",
    "ccce7d8349cf9f5d9a9c8f9293756f584d02dfdb953361c5ee36809aa0f560b4"
  )

class BlockChain private (
  blocks:              ListBuffer[Block],
  pendingTransactions: ListBuffer[Transaction],
  transactionsToMine:  ListBuffer[Transaction],
  difficulty:          Int = 4
) {
  import BlockChain._

  def addBlock(data: String): BlockChain = {
    synchronized {
      transactionsToMine.append(pendingTransactions.toList: _*)
      pendingTransactions.clear()
    }
    blocks.append(generateNextBlock(data))
    this
  }

  def addBlock(block: Block): BlockChain = synchronized {
    blocks.append(block)
    val approvedTcRemove = pendingTransactions.toList.filter(!block.transactions.contains(_))
    pendingTransactions.clear()
    pendingTransactions.append(approvedTcRemove: _*)
    this
  }

  def firstBlock:             Block             = blocks.head
  def latestBlock:            Block             = blocks.last
  def allBlocks:              List[Block]       = blocks.toList
  def getPendingTransactions: List[Transaction] = pendingTransactions.toList

  def generateNextBlock(blockData: String): Block = {
    val previousBlock = latestBlock
    val nextIndex     = previousBlock.index + 1
    val nonce         = 0
    val nextTimestamp = new Date().getTime / 1000
    val transactions  = pendingTransactions.toList
    val initHash      = calculateHash(nextIndex, previousBlock.hash, nonce, transactions, nextTimestamp, blockData)
    val block         = Block(nextIndex, previousBlock.hash, nonce, transactions, nextTimestamp, blockData, initHash)
    mine(block)
  }

  @tailrec
  final def mine(block: Block): Block = {
    if (block.hash.slice(0, difficulty) == "0" * difficulty) {
      block
    } else {
      val hash =
        calculateHash(block.index, block.previousHash, block.nonce + 1, block.transactions, block.timestamp, block.data)
      val newBlock =
        Block(block.index, block.previousHash, block.nonce + 1, block.transactions, block.timestamp, block.data, hash)
      mine(newBlock)
    }
  }

  def validBlock(newBlock: Block): Boolean = BlockChain.validBlock(newBlock, latestBlock)

  def makePendingTransaction(sender: String, receiver: String, amount: Int): Boolean = {
    if (getBalanceForCurrentWallet(sender) >= amount) {
      val tc = Transaction(sender, receiver, amount, timestamp = new Date().getTime / 1000)
      pendingTransactions.append(tc)
      true
    } else {
      false
    }
  }

  def addTransactionToPending(tc: Transaction): Unit = {
    pendingTransactions.append(tc)
  }

  def getBalanceForCurrentWallet(wallet: String): Int = {
    val sent     = walletPredicate((tc: Transaction) => tc.sender == wallet)
    val received = walletPredicate((tc: Transaction) => tc.receiver == wallet)
    received - sent
  }

  def walletPredicate(f: Transaction => Boolean): Int =
    blocks.map(block => block.transactions.map { case tc if f(tc) => tc.amount }.sum).sum

}

object BlockChain {

  def apply(): BlockChain = new BlockChain(ListBuffer(GenesisBlock), ListBuffer(), ListBuffer())

  def validBlock(newBlock: Block, previousBlock: Block): Boolean =
    previousBlock.index + 1 == newBlock.index &&
      previousBlock.hash == newBlock.previousHash &&
      calculateHashForBlock(newBlock) == newBlock.hash

  def calculateHashForBlock(block: Block): String =
    calculateHash(block.index, block.previousHash, block.nonce, block.transactions, block.timestamp, block.data)

  def calculateHash(
    index:        Int,
    previousHash: String,
    nonce:        Int,
    transactions: Seq[Transaction],
    timestamp:    Long,
    data:         String
  ): String =
    s"$index:$previousHash:$timestamp:$data:${transactions.toString()}:$nonce".sha256.hex

}
