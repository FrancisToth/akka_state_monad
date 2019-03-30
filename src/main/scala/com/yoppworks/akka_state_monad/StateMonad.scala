package com.yoppworks.akka_state_monad

object StateMonad {
  type State[S, A] = S => (A, S)
}


//  def map[S, A, B](sa: State[S, A])(f: A => B): State[S, B] = state => {
//    val (a, newState) = sa(state)
//    (f(a), newState)
//  }
//
//  def flatMap[S, A, B](sa: State[S, A])(f: A => State[S, B]): State[S, B] = state => {
//    val (a, newState) = sa(state)
//    f(a)(newState)
//  }

/*
trait State[S, A] {
    def apply(s:S): (A, S)
    def map[B](f:A =>B): State[S, B] = State { s =>
     val (a, newState) = this(s)
     (f(a), newState)
    }
    def flatMap[B](f:A=>State[S, B]): State[S, B] = State { s =>
     val (a, newState) = this(s)
     f(a)(newState)
    }
  }

  object State {
    def apply[S, A](r: S => (A, S)): State[S, A] = new State[S, A] {
      def apply(s:S) = r(s)
    }
  }
 */