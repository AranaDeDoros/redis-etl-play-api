name := "play-redis-api"
version := "1.0"
scalaVersion := "2.13.12"
resolvers += "Typesafe Releases" at "https://repo.typesafe.com/typesafe/releases/"

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)

libraryDependencies ++= Seq(
  guice,
  "com.typesafe.play" %% "play" % "2.9.0",
  "com.typesafe.play" %% "play-json" % "2.9.2",
  "redis.clients" % "jedis" % "5.1.0",
  "com.typesafe" % "config" % "1.4.3", // para configuraciones .conf
)
