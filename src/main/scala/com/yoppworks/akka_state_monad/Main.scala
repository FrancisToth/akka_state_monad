package com.yoppworks.akka_state_monad

import akka.actor.ActorSystem

object Main extends App {

  val system = ActorSystem("AkkaStateMonad")
  val server = system.actorOf(ServerActor.props)
  val client = system.actorOf(ClientActor.props(server))

  client ! ClientActor.Send(new Request)
}
