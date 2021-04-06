/*
 * Copyright 2021 HM Revenue & Customs
 *
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
