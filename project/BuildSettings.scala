import sbt.Keys._
import sbt.{Def, Defaults, Project, file}


object BuildSettings {

  lazy val basicSettings: Seq[Def.Setting[_]] = Defaults.coreDefaultSettings ++
    Seq(
      organization          :=  "com.knoldus",
      scalaVersion          :=  "2.12.6",
      version               :=  "0.0.1",
      javacOptions          :=  Seq("-source", "1.8", "-target", "1.8"),
      resolvers             ++= Dependencies.resolutionRepo
    )

  def BaseProject(name: String): Project =
    Project(name, file(s"$name"))
}