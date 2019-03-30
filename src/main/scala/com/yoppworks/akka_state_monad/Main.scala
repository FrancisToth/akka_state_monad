package com.yoppworks.akka_state_monad

import akka.actor.ActorSystem

object Main extends App {

  val system = ActorSystem("AkkaStateMonad")
  val barRef = system.actorOf(BarActor.props)
  val fooRef = system.actorOf(FooActor.props(barRef))

//  fooRef ! FooActor.Send(SuccessfulRequest)

//  fooRef ! FooActor.Send(FailedRequest)

  fooRef ! FooActor.Send(TimeoutRequest)
}
