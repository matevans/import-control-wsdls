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

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import com.github.tomakehurst.wiremock.stubbing.StubMapping
import org.scalatest.concurrent.{Eventually, IntegrationPatience}
import org.scalatest.time.{Millis, Seconds, Span}
import org.scalatestplus.play.ServerProvider
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.Status
import play.api.libs.ws.{WSClient, WSRequest}

object WiremockHelper extends Eventually with IntegrationPatience {
  val wiremockPort = 1111
  val wiremockHost = "localhost"
  val wiremockURL = s"http://$wiremockHost:$wiremockPort"

  def stubPost(url: String, status: Integer): StubMapping =
    stubFor(post(urlMatching(url)).willReturn(aResponse().withStatus(status)))
}

trait WiremockHelper extends ServerProvider {
  self: GuiceOneServerPerSuite =>

  import WiremockHelper._

  lazy val ws: WSClient = app.injector.instanceOf[WSClient]

  lazy val wmConfig: WireMockConfiguration = wireMockConfig().port(wiremockPort)
  lazy val wireMockServer = new WireMockServer(wmConfig)

  def startWiremock(): Unit = {
    WireMock.configureFor(wiremockHost, wiremockPort)
    wireMockServer.start()
  }

  def stopWiremock(): Unit = wireMockServer.stop()

  def resetWiremock(): Unit = WireMock.reset()

  def buildClient(path: String): WSRequest =
    ws.url(s"http://localhost:$port$path").withFollowRedirects(false)

}
