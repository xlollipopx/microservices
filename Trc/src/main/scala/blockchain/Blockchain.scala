package blockchain

import com.roundeights.hasher.Implicits._

import java.util.Date
import scala.language.postfixOps
import scala.util.{Success, Try}

case class Block(index: Int, previousHash: String, transactions: Seq[Transaction], timestamp: Long, data: String, hash: String)

case class Transaction(from: String, to: String, amount: Int, timestamp: Long = -1)
object GenesisBlock extends Block(0, "0",Seq(),  1497359352,
  "Genesis block", "ccce7d8349cf9f5d9a9c8f9293756f584d02dfdb953361c5ee36809aa0f560b4")

case class BlockChain  (var blocks: Seq[Block]) {
  import BlockChain._

  def addBlock( data: String ): BlockChain = new BlockChain(generateNextBlock(data) +: blocks)

  def addBlock(block: Block): Try[BlockChain] ={
     Success(BlockChain(blocks :+ block))
  }

  def firstBlock: Block = blocks.last
  def latestBlock: Block = blocks.head

  def generateNextBlock( blockData: String ): Block = {
    val previousBlock = latestBlock
    val nextIndex = previousBlock.index + 1
    val nextTimestamp = new Date().getTime / 1000
    val nextHash = calculateHash(nextIndex, previousBlock.hash, Seq(), nextTimestamp, blockData)
    Block(nextIndex, previousBlock.hash, Seq(),  nextTimestamp, blockData, nextHash)
  }

  def validBlock( newBlock: Block ): Boolean = BlockChain.validBlock(newBlock, latestBlock)

}


object BlockChain {

  def apply(): BlockChain = new BlockChain(Seq(GenesisBlock))

  def validBlock(newBlock: Block, previousBlock: Block): Boolean =
    previousBlock.index + 1 == newBlock.index &&
      previousBlock.hash == newBlock.previousHash &&
      calculateHashForBlock(newBlock) == newBlock.hash
  def calculateHashForBlock( block: Block ): String = calculateHash(block.index, block.previousHash,block.transactions,  block.timestamp, block.data)

  def calculateHash(index: Int, previousHash: String, transactions: Seq[Transaction], timestamp: Long, data: String): String =
    s"$index:$previousHash:$timestamp:$data:${transactions.toString()}".sha256.hex

}
