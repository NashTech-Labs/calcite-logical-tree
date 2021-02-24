name := "calcitelogicaltree"

version := "0.1"

scalaVersion := "2.12.7"

lazy val root = (project in file("."))
  .settings(BuildSettings.basicSettings)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.Libraries.calciteCore,
      Dependencies.Libraries.calciteBabelParser,
      Dependencies.Libraries.config,
      Dependencies.Libraries.slf4j,
      Dependencies.Libraries.junit,
      Dependencies.Libraries.hamsterCore,
      Dependencies.Libraries.log4jOverSlf4j,
      Dependencies.Libraries.pureconfig,
      Dependencies.Libraries.scalacheck,
      Dependencies.Libraries.jacksonScala
    )
  )

