package model

import blockchain.{Block, Transaction}

object login {
  case class LoginRequest(username: String, password: String)
  case class Credentials(username: String, password: String)
  case class Blocks(blocks: Seq[Block])
  case class Request(body: Transaction, headers: AuthHeader)
  case class AuthHeader(Authorization: String)
  case class PeerRequest(body: Peer, headers: AuthHeader)
  case class Peer(peer: String)
}
