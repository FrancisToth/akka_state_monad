name := "akka_state_monad"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.21"
)

scalacOptions ++= Seq(
  "-language:postfixOps",
  "-language:higherKinds",
  "-deprecation",
  "-encoding", "UTF-8",
  "-feature",
  "-unchecked"
)