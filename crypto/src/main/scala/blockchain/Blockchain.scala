package blockchain

import blockchain.BlockChain.config
import com.roundeights.hasher.Implicits._

import java.io.File
import com.typesafe.config.{Config, ConfigFactory}

import java.util.Date
import scala.annotation.tailrec
import scala.collection.mutable.ListBuffer
import scala.language.postfixOps
import java.util.concurrent.atomic.{AtomicInteger, AtomicReference}

case class Block(
  index:        Int,
  previousHash: String,
  nonce:        Int,
  transactions: List[Transaction],
  timestamp:    Long,
  data:         String,
  hash:         String
)

final case class Transaction(sender: String, receiver: String, amount: Int, timestamp: Long = -1)

class BlockChain private (
  blocks:              ListBuffer[Block],
  pendingTransactions: ListBuffer[Transaction],
  transactionsToMine:  ListBuffer[Transaction],
  difficulty:          Int
) {
  import BlockChain._

  def addBlock(data: String): BlockChain = {
    miningFlag.set(true)
    synchronized {
      transactionsToMine.append(pendingTransactions.toList: _*)
      pendingTransactions.clear()
    }
    blocks.append(generateNextBlock(data))
    miningFlag.set(false)
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
  def getBlockchainSize:      Int               = blocks.length

  def generateNextBlock(blockData: String): Block = {
    val previousBlock = latestBlock
    val nextIndex     = previousBlock.index + 1
    val nonce         = 0
    val nextTimestamp = new Date().getTime / 1000
    val transactions  = transactionsToMine.toList :+ Transaction("System", blockData, config.getInt("rewardAmount"))
    transactionsToMine.clear()
    val initHash = calculateHash(nextIndex, previousBlock.hash, nonce, transactions, nextTimestamp, blockData)
    val block    = Block(nextIndex, previousBlock.hash, nonce, transactions, nextTimestamp, blockData, initHash)
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
    val sent = blocks.map(block => block.transactions.filter(tc => tc.sender == wallet).map(_.amount).sum).sum
    val received =
      blocks
        .map(block => block.transactions.filter(tc => tc.receiver == wallet).map(_.amount).sum)
        .sum
    received - sent
  }

  def walletPredicate(f: Transaction => Boolean): Int =
    blocks.map(block => block.transactions.map { case tc if f(tc) => tc.amount }.sum).sum

}

object BlockChain {

  val config: Config = ConfigFactory.load("values.conf")

  val miningFlag: AtomicReference[Boolean] = new AtomicReference[Boolean](false)

  object GenesisBlock
    extends Block(
      config.getInt("index"),
      config.getString("previousHash"),
      config.getInt("nonce"),
      List(),
      config.getLong("timestamp"),
      config.getString("data"),
      config.getString("hash")
    )
  val diff: Int = config.getInt("difficulty")

  def apply(): BlockChain = new BlockChain(ListBuffer(GenesisBlock), ListBuffer(), ListBuffer(), diff)

  def validBlock(newBlock: Block, previousBlock: Block): Boolean =
    previousBlock.index + 1 == newBlock.index &&
      previousBlock.hash == newBlock.previousHash &&
      calculateHashForBlock(newBlock) == newBlock.hash

  def calculateHashForBlock(block: Block): String =
    calculateHash(block.index, block.previousHash, block.nonce, block.transactions, block.timestamp, block.data)

  def isMining: Boolean = miningFlag.get()

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
