import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc" %% "bootstrap-backend-play-27" % "5.10.0",
    "org.apache.axis2" % "axis2-kernel" % "1.7.9"
  )

  val test = Seq(
    "uk.gov.hmrc" %% "bootstrap-test-play-27" % "5.10.0" % Test,
    "org.scalatest" %% "scalatest" % "3.2.5" % "test, it",
    "com.typesafe.play" %% "play-test" % current % Test,
    "com.vladsch.flexmark" % "flexmark-all" % "0.36.8" % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % "test, it",
    "com.github.tomakehurst" % "wiremock-standalone" % "2.27.2" % IntegrationTest
  )
}
