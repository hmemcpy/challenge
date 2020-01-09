name := "bp-challenge"
version := "0.1"
scalaVersion := "2.13.1"

val zioVersion      = "1.0.0-RC17"
val circeVersion    = "0.12.3"
val http4sVersion   = "0.21.0-M6"
libraryDependencies ++= Seq(
  "dev.zio"        %% "zio"                 % zioVersion,
  "dev.zio"        %% "zio-streams"         % zioVersion,
  "dev.zio"        %% "zio-interop-cats"    % "2.0.0.0-RC10",

  "io.circe"       %% "circe-core"          % circeVersion,
  "io.circe"       %% "circe-generic"       % circeVersion,
  "io.circe"       %% "circe-parser"        % circeVersion,

  "org.typelevel"  %% "cats-core"           % "2.0.0",

  "org.http4s"     %% "http4s-blaze-server" % http4sVersion,
  "org.http4s"     %% "http4s-circe"        % http4sVersion,
  "org.http4s"     %% "http4s-dsl"          % http4sVersion,
  "ch.qos.logback" % "logback-classic"      % "1.2.3"
)
