import sbt._

object Dependencies {

  object Ver {
    val scalatest:  String = "3.2.3"
  }

  private val testing: Seq[ModuleID] = Seq(
    ("org.scalatest" %% "scalatest" % Ver.scalatest)
  )

  val mainDeps: Seq[ModuleID] = testing

}
