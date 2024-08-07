val Http4sVersion = "0.23.26"
val MunitVersion = "0.7.29"
val LogbackVersion = "1.2.11"
val MunitCatsEffectVersion = "1.0.6"
val circeVersion = "0.14.6"

lazy val root = (project in file("."))
  .settings(
    organization := "io.github.dhruvphumbra",
    name := "dwolla-api-client",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "3.3.3",
    libraryDependencies ++= Seq(
      "org.http4s"        %% "http4s-ember-client"  % Http4sVersion,
      "org.http4s"        %% "http4s-circe"         % Http4sVersion,
      "org.http4s"        %% "http4s-dsl"           % Http4sVersion,
      "io.circe"          %% "circe-core"           % circeVersion,
      "io.circe"          %% "circe-literal"        % circeVersion,
      "io.circe"          %% "circe-generic"        % circeVersion,
      "io.chrisdavenport" %% "mules-caffeine"       % "0.7.0",
      "org.scalacheck"    %% "scalacheck"           % "1.17.0",
      "org.scalameta"     %% "munit"                % MunitVersion           % Test,
      "org.typelevel"     %% "munit-cats-effect-3"  % MunitCatsEffectVersion % Test,
      "ch.qos.logback"    %  "logback-classic"      % LogbackVersion,
    ),
    testFrameworks += new TestFramework("munit.Framework"),
    Compile / run / fork := true
  )
