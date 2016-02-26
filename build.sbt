name := "validator"

scalaVersion := "2.11.7"

Revolver.settings

libraryDependencies ++= Seq(
  "com.pubnub" % "pubnub" % "3.5.6",
  "com.typesafe.akka" %% "akka-actor" % "2.4.2",
  "com.typesafe.akka" %% "akka-stream" % "2.4.2",
  "com.typesafe.play" %% "play-json" % "2.4.6"
)
