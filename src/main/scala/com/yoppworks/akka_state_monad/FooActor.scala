package com.yoppworks.akka_state_monad

import akka.actor.{Actor, ActorRef, Cancellable, Props, Status}
import akka.pattern.CircuitBreaker
import akka.util.Timeout
import com.yoppworks.akka_state_monad.FooActor._
import com.yoppworks.akka_state_monad.State._

import scala.concurrent.Future
import scala.concurrent.duration._
import scala.util.Random

object FooActor {

  def props(target: ActorRef): Props = Props(new FooActor(target))

  private val MAX_RETRY = 3
  private val CIRCUIT_BREAKER_RESET_TIMEOUT = 3 second

  sealed trait FooActorTransition
  case object Retry           extends FooActorTransition
  case object Done            extends FooActorTransition

  case class Send(request: Request)

  type FooActorState = (Int, Any)

  // This business logic is super simple but we could come up with more
  // advanced rules combinations.
  // The map/flatMap combinator have been added for this purpose
  // There is a nice example of how this could be done here:
  // http://patricknoir.blogspot.com/2014/12/demistify-state-monad-with-scala-22.html
  def run: State[FooActorState, Unit] = {
    case (nbRetry, SuccessfulResponse)        => ((), (nbRetry, Done))
    case (nbRetry, _) if nbRetry < MAX_RETRY  => ((), (nbRetry + 1, Retry))
    case (nbRetry, _)                         => ((), (nbRetry, Done))
  }
}

class FooActor(target: ActorRef) extends Actor {

  import akka.pattern.{ask, pipe}
  import context.dispatcher

  implicit val timeout: Timeout = 3 second
  private val scheduler = context.system.scheduler

  private val cb = new CircuitBreaker(
    scheduler,
    maxFailures = 1,
    callTimeout = 1 second,
    resetTimeout = CIRCUIT_BREAKER_RESET_TIMEOUT)
    .onOpen(println("circuit breaker opened!"))
    .onClose(println("circuit breaker closed!"))
    .onHalfOpen(println("circuit breaker half-open"))

  override def postStop(): Unit = {
    super.postStop()
    println("Done!")
  }

  override def receive: Receive = {
    case Send(request) =>
      context.become(ready(0, request))
      sendRequest(request)
  }

  private def ready(totalRetry: Int, request: Request): Receive = {
    case Done  => context.stop(self)

    case Retry =>
      println(s"Retrying ($totalRetry)...")
      scheduleRetry(request)

    case msg   =>
      val (_, (nbRetry, transition)) = run((totalRetry, msg))
      context.become(ready(nbRetry, request))
      self ! transition
  }

  def sendRequest(request: Request): Future[Any] =
    cb.withCircuitBreaker(target ? request) pipeTo self

  def scheduleRetry(request: Request): Cancellable =
    scheduler.scheduleOnce(CIRCUIT_BREAKER_RESET_TIMEOUT)(sendRequest(request))
}

object BarActor {
  def props: Props = Props[BarActor]
}

class BarActor extends Actor {
  override def receive: Receive = {
    case _ =>
      Random.nextInt(3) match {
        case 0 =>
          println("Request successfully processed")
          sender() ! SuccessfulResponse

        case 1 =>
          println("Request could not be processed")
          sender() ! Status.Failure(new IllegalArgumentException("Bouuuuh!"))

        case 2 =>
          println("Request will never be processed")
      }
  }
}

class Request
case object SuccessfulResponse
