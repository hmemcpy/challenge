name := "bp-challenge"
version := "0.1"
scalaVersion := "2.13.1"

val zioVersion   = "1.0.0-RC17"
val circeVersion = "0.12.3"
val catsVersion  = "2.0.0"

libraryDependencies ++= Seq(
  "dev.zio"       %% "zio"           % zioVersion,
  "dev.zio"       %% "zio-streams"   % zioVersion,
  "io.circe"      %% "circe-core"    % circeVersion,
  "io.circe"      %% "circe-generic" % circeVersion,
  "io.circe"      %% "circe-parser"  % circeVersion,
  "org.typelevel" %% "cats-core"     % catsVersion
)
