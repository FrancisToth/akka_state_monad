package com.yoppworks.akka_state_monad

sealed trait Request
case object SuccessfulRequest extends Request
case object FailedRequest     extends Request
case object TimeoutRequest    extends Request

case object SuccessfulResponse