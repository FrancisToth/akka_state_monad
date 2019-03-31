package com.yoppworks.akka_state_monad


object State {
  type State[S, A] = S => (A, S)

  def apply[S, A](r: State[S, A]): State[S, A] = (s: S) => r(s)

  def map[S, A, B](s: State[S, A])(f: A => B): State[S, B] = state => {
    val (a, newState) = s(state)
    (f(a), newState)
  }

  def flatMap[S, A, B](s: State[S, A])(f: A => State[S, B]): State[S, B] = state => {
    val (a, newState) = s(state)
    f(a)(newState)
  }

  implicit class StateOps[S, A](s: State[S, A]) {
    def map[B](f: A => B): State[S, B] = State.map(s)(f)
    def flatMap[B](f: A => State[S, B]): State[S, B] = State.flatMap(s)(f)
  }
}

