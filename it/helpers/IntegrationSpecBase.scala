/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package helpers

import akka.http.scaladsl.model.HttpResponse
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import helpers.WiremockHelper.stubPost
import org.scalatest.concurrent.{Eventually, IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, TestSuite}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import play.api.{Application, Environment, Mode}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration

trait IntegrationSpecBase
    extends AnyWordSpec
    with TestSuite
    with ScalaFutures
    with IntegrationPatience
    with Matchers
    with WiremockHelper
    with GuiceOneServerPerSuite
    with BeforeAndAfterEach
    with BeforeAndAfterAll
    with Eventually
    with FutureAwaits
    with DefaultAwaitTimeout {

  val mockHost: String = WiremockHelper.wiremockHost
  val mockPort: Int = WiremockHelper.wiremockPort
  val mockUrl = s"http://$mockHost:$mockPort"

  def config: Map[String, String] = Map(
    "application.router" -> "testOnlyDoNotUseInAppConf.Routes",
    "auditing.consumer.baseUri.host" -> s"$mockHost",
    "auditing.consumer.baseUri.port" -> s"$mockPort",
    "microservice.services.auth.host" -> s"$mockHost",
    "microservice.services.auth.port" -> s"$mockPort",
    "auditing.consumer.baseUri.host" -> s"$mockHost",
    "auditing.consumer.baseUri.port" -> s"$mockPort"
  )

  override implicit val patienceConfig: PatienceConfig = PatienceConfig(
    timeout = scaled(Span(15, Seconds)),
    interval = scaled(Span(200, Millis))
  )
  implicit lazy val ec: ExecutionContext =
    app.injector.instanceOf[ExecutionContext]

  protected lazy val builder: GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .in(Environment.simple(mode = Mode.Prod))
      .configure(config)

  override implicit lazy val app: Application = builder.build()

  def statusOf(res: Future[HttpResponse])(implicit timeout: Duration): Int =
    Await.result(res, timeout).status.intValue()

  override def beforeEach(): Unit = {
    resetWiremock()
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWiremock()
  }

  override def afterAll(): Unit = {
    stopWiremock()
    super.afterAll()
  }

  protected def stubAudit: StubMapping = stubPost(s"/write/audit", Status.OK)
}
