# Blockchain implemented in Scala + Akka


Project description
-------------------
  
This is a simplified custom blockchain that is implemented on Scala with Akka.


Mining
------
To mine a block you need to solve the cryptographic task: 
if current block hash doesn't contain n zeros in the beginning, we increase nonce field by one and do this step until we have n zeros in the beginning
of the hash. Hash is evaluated using all field in the block. A user who mined a block gets reward from the system.


![image](https://user-images.githubusercontent.com/64196164/160470672-ac59612e-9e56-49d3-af92-6731fa405f22.png)


These functions(in BlockChain class) are used to mine blocks:


      def generateNextBlock(blockData: String): Block
      final def mine(block: Block): Block 
      

Also we can fast and easily check if a block was mined in a legal way(this is called proof of work):


      def validBlock(newBlock: Block, previousBlock: Block): Boolean =
        previousBlock.index + 1 == newBlock.index &&
        previousBlock.hash == newBlock.previousHash &&
        calculateHashForBlock(newBlock) == newBlock.hash
        

Transactions
------------
When someone wants to make a transaction and write it to the blockchain, this transaction will be sent to other nodes and validated by them.
After validation it will be added to pending transactions(ledger) and appear in the next mined block. Also when a message with a blockchain version from another node is received, it is compared with a blockchain version on the current node, and if the received blockchain is longer, then the blockchain on the current node is replaced by received one. 


![image](https://user-images.githubusercontent.com/64196164/160535194-84462207-419c-47dd-b1a0-b067d8b00993.png)



Peer2peer
---------
Peer2peer network is implemented with Akka remoute. When someone installed the app and wants to become a part of the peer2peer network, he need to add some seed node address to connect. 
When someone wants to make a transaction and write it to the blockchain, this transaction will be sent to other nodes and validated by them.
Also when some node mined a block, this block is broadcasted to other nodes and validated by them, and when validation is successful, block is added 
to the blockchain.


![image](https://user-images.githubusercontent.com/64196164/160535787-7aebc33e-0360-4239-a37f-9b9e6b00ce4d.png)




Build and run
-------------


The app can be run using docker-compose.

First build docker image:

    sbt docker:publshLocal


Then run

    docker-compose up

This will start 4 nodes, which will first connect to node1 (the seed node) and then proceed to build a P2P network.

To create an account use:

    curl -v -H POST http://localhost:9000/signup -H 'Content-Type: application/json' -d '{"username":"http://localhost:9000", "password":"admin" }'
    
As a username use server local address.

Visualisation:

Open  blockchain-ui and run:

    npm run serve
    
  
  

   


    
    
    

