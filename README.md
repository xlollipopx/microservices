# Blockchain implemented in Scala + Akka

## Project description 



Building, running, etc
----------------------


The app can be run using docker-compose.

First build docker image:

    sbt docker:publshLocal


Then run

    docker-compose up

This will start 4 nodes, which will first connect to node1 (the seed node) and then proceed to build a P2P network.
