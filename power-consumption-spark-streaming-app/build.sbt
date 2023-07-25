name := "power-consumption-spark-streaming"

version := "0.1"

scalaVersion := "2.12.17"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % "3.1.2",
  "org.apache.spark" %% "spark-streaming" % "3.1.2",
  "org.apache.spark" %% "spark-sql" % "3.1.2",
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % "3.1.2",
  "org.apache.kafka" % "kafka-clients" % "2.8.0",
  
)
libraryDependencies += "org.xerial" % "sqlite-jdbc" % "3.34.0"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.4"

resourceDirectory in Compile := baseDirectory.value / "src" / "main" / "resources"