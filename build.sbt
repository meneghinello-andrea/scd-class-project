import jline.console.ConsoleReader

def consoleClearCommand = Command.command("clear-terminal") {
  state => {
    val consoleReader: ConsoleReader = new ConsoleReader()
    consoleReader.clearScreen()
    state
  }
}

val commonSettings = Seq(
  autoAPIMappings                 := true,
  commands                        += consoleClearCommand,
  licenses                        += ("GPLv2.0" -> url("http://www.gnu.org/licenses/old-licenses/gpl-2.0.html")),
  organization                    := "org.citysimulator",
  organizationName                := "Meneghinello di Andrea Meneghinello",
  scalacOptions                   := Seq("-unchecked", "-deprecation", "-encoding", "utf-8"),
  scalaVersion                    := "2.11.7",
  startYear                       := Some(2015),
  version                         := "1.0"
)

lazy val typesafeAkkaLibraries = Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.0" % "compile",
  "com.typesafe.akka" %% "akka-remote" % "2.4.0" % "compile"
)

lazy val scalaModulesLibraries = Seq(
  "org.scala-lang.modules" %% "scala-xml" % "1.0.5" % "compile"
)

lazy val testLibraries = Seq(
  "com.typesafe.akka" %% "akka-testkit" % "2.4.0" % "test",
  "org.scalatest" %% "scalatest" % "2.2.4" % "test"
)

lazy val core = project.in(file("core"))
  .settings(assemblySettings:_*)
  .settings(commonSettings:_*)
  .settings(libraryDependencies ++= typesafeAkkaLibraries)
  .settings(libraryDependencies ++= scalaModulesLibraries)
  .settings(libraryDependencies ++= testLibraries)

lazy val city = project.in(file("city"))
  .settings(assemblySettings:_*)
  .settings(commonSettings:_*)
  .settings(libraryDependencies ++= testLibraries)
  .dependsOn(core)

lazy val district = project.in(file("district"))
  .settings(assemblySettings:_*)
  .settings(commonSettings:_*)
  .settings(libraryDependencies ++= testLibraries)
  .dependsOn(core)