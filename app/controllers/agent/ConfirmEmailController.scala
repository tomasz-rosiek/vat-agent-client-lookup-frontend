/*
 * Copyright 2018 HM Revenue & Customs
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

package controllers.agent

import common.SessionKeys
import config.{AppConfig, ErrorHandler}
import controllers.predicates.AuthoriseAsAgentOnly
import javax.inject.{Inject, Singleton}
import models.Agent
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent}
import services.EmailVerificationService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class ConfirmEmailController @Inject()(val authenticate: AuthoriseAsAgentOnly,
                                       val messagesApi: MessagesApi,
                                       val emailVerificationService: EmailVerificationService,
                                       val errorHandler: ErrorHandler,
                                       implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  def show: Action[AnyContent] = authenticate { implicit agent =>

    extractSessionEmail(agent) match {
      case Some(email) =>
        Ok(views.html.agent.confirmEmail(email))
      case _ =>
        Redirect(routes.CapturePreferenceController.show())
    }
  }

  def isEmailVerified: Action[AnyContent] = authenticate.async { implicit agent =>

    extractSessionEmail(agent) match {
      case Some(email) =>
        emailVerificationService.isEmailVerified(email) map {
          case Some(true) =>
            Redirect(routes.SelectClientVrnController.show())
          case Some(false) =>
            Redirect(routes.VerifyEmailController.sendVerification())
          case _ =>
            errorHandler.showInternalServerError
        }

      case _ =>
        Logger.info("[ConfirmEmailController][updateNotificationPreference] no email address found in session")
        Future.successful(Redirect(routes.CapturePreferenceController.show()))
    }
  }

  private[controllers] def extractSessionEmail(agent: Agent[AnyContent]): Option[String] = {
    agent.session.get(SessionKeys.notificationsEmail).filter(_.nonEmpty)
  }
}