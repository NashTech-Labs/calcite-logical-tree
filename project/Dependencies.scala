import sbt._

object Dependencies {


  def testDependencies(deps: ModuleID*): Seq[ModuleID] = deps.map(_ % Test)

  val resolutionRepo = Seq()

  object V {
    val mockitoCoreVersion = "2.23.0"
    val scalaTestVersion = "3.0.5"
    val babelParserVersion = "1.22.0"
    val calciteCoreVersion = "1.22.0"

    val config           = "1.3.4"
    val pureconfig       = "0.11.0"
    val slf4j            = "1.7.26"
    val scalacheck       = "1.14.0"

  }

  object Libraries {
    val calciteBabelParser = "org.apache.calcite" % "calcite-babel" % V.babelParserVersion
    val calciteCore = "org.apache.calcite" % "calcite-core" % V.calciteCoreVersion

    //Test libraries
    val scalaTest = "org.scalatest" %% "scalatest" % V.scalaTestVersion
    val mockitoCore = "org.mockito" % "mockito-core" % V.mockitoCoreVersion
    val calciteTest = "org.apache.calcite" % "calcite-core" % V.calciteCoreVersion classifier "tests"
    val hamsterCore = "org.hamcrest" % "hamcrest-core" % "2.1"
    val hamsterLibrary = "org.hamcrest" % "hamcrest-library" % "2.1"
    val junit            =  "org.junit.jupiter" % "junit-jupiter-api" % "5.7.0" % Test

    // Logging
    val config           = "com.typesafe"                     %  "config"                            % V.config
    val slf4j            = "org.slf4j"                  % "slf4j-simple"                              % V.slf4j
    val log4jOverSlf4j   = "org.slf4j"                        %  "log4j-over-slf4j"                  % V.slf4j
    val pureconfig       = "com.github.pureconfig"            %% "pureconfig"                        % V.pureconfig
    val scalacheck       = "org.scalacheck"                   %% "scalacheck"                        % V.scalacheck      % Test
    val jacksonScala = "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.10.0"
  }
}