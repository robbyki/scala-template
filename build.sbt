import Dependencies._
import SbtPrompt.autoImport._
import com.scalapenos.sbt.prompt._
import com.typesafe.sbt.packager.docker.Cmd
import scala.sys.process._
import scala.util.Try

import sbtbuildinfo.BuildInfoKey.action
import sbtbuildinfo.BuildInfoKeys.{buildInfoKeys, buildInfoOptions, buildInfoPackage}
import sbtbuildinfo.{BuildInfoKey, BuildInfoOption}

lazy val projectName = IO.readLines(new File("PROJECT_NAME")).head

maintainer := "robby k <robbmk@gmail.com>"

inThisBuild(
  List(
    name              := projectName,
    organization      := "com.ncc1701d",
    startYear         := Some(2020),
    scalaVersion      := "2.12.12",
    homepage          := Some(url(s"https://github.com/robbyki/${name.value}")),
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    connectInput      := true,
    outputStrategy    := Some(StdoutOutput)
  )
)

Global / onChangedBuildSource := ReloadOnSourceChanges
Global / cancelable           := true
Global / fork                 := true
Global / excludeLintKeys     ++= Set(coverageEnabled)

ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % "0.4.4"

addCommandAlias("c", "compile")
addCommandAlias("cc", ";clean;compile")
addCommandAlias("dp", "docker:publish")
addCommandAlias("dpl", "docker:publishLocal")
addCommandAlias("dt", "dependencyTree")
addCommandAlias("dynDesc", ";reload;dynverGitDescribeOutput")
addCommandAlias("fix-check", "scalafixAll --check")
addCommandAlias("fix", "scalafix")
addCommandAlias("fixtest", "test:scalafix")
addCommandAlias("fixall", "scalafixAll")
addCommandAlias("fmt-check", ";scalafmtCheckAll;scalafmtSbtCheck")
addCommandAlias("fmt", "scalafmt")
addCommandAlias("fmttest", "test:scalafmt")
addCommandAlias("fmtall", ";scalafmtAll;scalafmtSbt")
addCommandAlias("mains", "show compile:discoveredMainClasses")
addCommandAlias("md", ";project docs;mdoc;project root")
addCommandAlias("pub", "publish")
addCommandAlias("publ", "publishLocal")
addCommandAlias("rel", "reload")
addCommandAlias("runm", "runMain")
addCommandAlias("test-cov", "clean;coverage;test;coverageReport")
addCommandAlias("tests", "show test:definedTestNames")
addCommandAlias("uc", "updateClassifiers")

// ammonite repl
// test:run
libraryDependencies += ("com.lihaoyi" % "ammonite" % "2.3.8" % "test").cross(CrossVersion.full)
sourceGenerators in Test += Def.task {
  val file = (sourceManaged in Test).value / "amm.scala"
  IO.write(file, """object amm extends App { ammonite.Main().run() }""")
  Seq(file)
}.taskValue

import com.scalapenos.sbt.prompt.SbtPrompt.autoImport._
promptTheme := com.scalapenos.sbt.prompt.PromptThemes.ScalapenosTheme

lazy val nukeCache = taskKey[Unit]("Nuke Cache")
nukeCache := { "scripts/nuke-cache.sh" ! }

scalacOptions ~= { options: Seq[String] => options.filterNot(Set("-Xfatal-warnings")) }

javaOptions in ThisBuild ++= Seq(
  "-Xms1024M",
  "-Xmx4096M",
  "-Xss8M",
  "-XX:+CMSClassUnloadingEnabled",
  "-XX:ReservedCodeCacheSize=512M",
  "-Dspark.master=local",
  "-Dlog4j.debug=true",
  "-Dlog4j.configurationFile=" + (resourceDirectory in Compile).value / "log4j.properties"
)

lazy val buildInfoSettings = Seq(
  buildInfoKeys := Seq[BuildInfoKey](
    name,
    version,
    scalaVersion,
    sbtVersion,
    resolvers,
    BuildInfoKey.map(name) { case (k, v) => "project" + k.capitalize -> v.capitalize },
    BuildInfoKey.action("buildTime") {
      System.currentTimeMillis
    },
    action("lastCommitHash") {
      import scala.sys.process._
      // if the build is done outside of a git repository, we still want it to succeed
      Try("git rev-parse HEAD".!!.trim).getOrElse("?")
    }
  ),
  buildInfoOptions += BuildInfoOption.BuildTime,
  buildInfoOptions += BuildInfoOption.ToJson,
  buildInfoOptions += BuildInfoOption.ToMap,
  buildInfoOptions += BuildInfoOption.BuildTime,
  buildInfoObject  := "BuildInfo"
)

lazy val testSettings = Seq(
  coverageMinimum                     := 85,
  coverageFailOnMinimum               := true,
  coverageHighlighting                := true,
  Test / compile / coverageEnabled    := true,
  Compile / compile / coverageEnabled := false,
  Test / parallelExecution            := false
)

lazy val resolverSettings = resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases"),
  Resolver.typesafeRepo("releases"),
  Resolver.typesafeRepo("snapshots"),
  Resolver.sbtPluginRepo("releases")
)

lazy val commonSettings = Seq(
  resolverSettings,
  mappings in (Compile, packageBin) ~= { _.filter(!_._1.getName.endsWith("application.conf")) },
  unmanagedBase := baseDirectory.value / "lib"
)

lazy val root = project
  .in(file("."))
  .settings(name := projectName)
  .settings(
    commonSettings,
    buildInfoSettings,
    testSettings,
    libraryDependencies ++= mainDeps
  )
  // .enablePlugins(GitVersioning)
  .enablePlugins(DockerPlugin)
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(JavaAppPackaging)

lazy val docs = project
  .in(file("mdoc"))
  .settings(
    publishArtifact      := false,
    skip in publish      := true,
    mdocVariables        := Map("VERSION" -> version.value),
    libraryDependencies ++= mainDeps
  )
  .dependsOn(root)
  .enablePlugins(MdocPlugin)
