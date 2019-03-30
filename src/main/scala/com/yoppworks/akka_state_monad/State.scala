package com.yoppworks.akka_state_monad


object State {
  type State[S, A] = S => (A, S)

  def apply[S, A](r: S => (A, S)): State[S, A] = (s: S) => r(s)

  def map[S, A, B](sa: State[S, A])(f: A => B): State[S, B] = state => {
    val (a, newState) = sa(state)
    (f(a), newState)
  }

  def flatMap[S, A, B](sa: State[S, A])(f: A => State[S, B]): State[S, B] = state => {
    val (a, newState) = sa(state)
    f(a)(newState)
  }

  implicit class StateOps[S, A](s: S => (A, S)) {
    def map[B](f: A => B): State[S, B] = State.map(s)(f)
    def flatMap[B](f: A => State[S, B]): State[S, B] = State.flatMap(s)(f)
  }
}

