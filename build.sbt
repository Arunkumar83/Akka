name := "akka-http-microservice"
organization := "com.arun"
version := "1.0"
scalaVersion := "2.12.3"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

scalacOptions += "-Ypartial-unification" // 2.11.9+


libraryDependencies ++= {
  val akkaV       = "2.5.11"
  val akkaHttpV   = "10.1.1"
  val scalaTestV  = "3.0.1"
  Seq(
    "com.typesafe.akka" %% "akka-http" % akkaHttpV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "io.gatling.highcharts" % "gatling-charts-highcharts" % "2.3.1",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.3",
    "io.spray" %%  "spray-json" % "1.3.4",
    "org.tpolecat" %% "doobie-core"      % "0.5.3",
    "org.tpolecat" %% "doobie-h2"        % "0.5.3" // H2 driver 1.4.197 + type mappings.
      
  )
}

//Revolver.settings