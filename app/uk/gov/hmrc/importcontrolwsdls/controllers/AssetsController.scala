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

package uk.gov.hmrc.importcontrolwsdls.controllers

import akka.actor.ActorSystem
import akka.pattern.after
import com.google.inject.Singleton
import controllers.Assets
import javax.inject.Inject
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.importcontrolwsdls.config.AppConfig
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import scala.concurrent.ExecutionContext

@Singleton
class AssetsController @Inject()(
  cc: ControllerComponents,
  actorSystem: ActorSystem,
  appConfig: AppConfig,
  assets: Assets
)(implicit val ec: ExecutionContext)
    extends BackendController(cc) {

  def at(file: String): Action[AnyContent] = Action.async { implicit request =>
    after(appConfig.assetsDelay, actorSystem.scheduler) {
      assets.at(file)(request)
    }
  }
}
