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

import audit.AuditService
import audit.models.{AuthenticateAgentAuditModel, GetClientBusinessNameAuditModel}
import common.SessionKeys
import config.{AppConfig, ErrorHandler}
import controllers.predicates.AuthoriseAsAgentWithClient
import javax.inject.{Inject, Singleton}

import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._
import services.CustomerDetailsService
import uk.gov.hmrc.play.bootstrap.controller.FrontendController

import scala.concurrent.Future

@Singleton
class ConfirmClientVrnController @Inject()(val messagesApi: MessagesApi,
                                           val authenticate: AuthoriseAsAgentWithClient,
                                           val customerDetailsService: CustomerDetailsService,
                                           val errorHandler: ErrorHandler,
                                           val auditService: AuditService,
                                           implicit val appConfig: AppConfig) extends FrontendController with I18nSupport {

  def show: Action[AnyContent] = authenticate.async {
    implicit user =>
      customerDetailsService.getCustomerDetails(user.vrn) map {
        case Right(customerDetails) =>
          auditService.extendedAudit(
            AuthenticateAgentAuditModel(user.arn.get, user.vrn, isAuthorisedForClient = true),
            Some(controllers.agent.routes.ConfirmClientVrnController.show().url)
          )
          auditService.extendedAudit(
            GetClientBusinessNameAuditModel(user.arn.get, user.vrn, customerDetails.clientName),
            Some(controllers.agent.routes.ConfirmClientVrnController.show().url)
          )
          user.session.get(SessionKeys.redirectUrl) match {
            case Some(redirectUrl) => Ok(views.html.agent.confirmClientVrn(user.vrn, customerDetails, redirectUrl))
            case None =>
              Logger.debug("[ConfirmClientVrnController][show] - No redirect URL was found in session")
              errorHandler.showInternalServerError
          }
        case _ => errorHandler.showInternalServerError
      }
  }

    def changeClient: Action[AnyContent] = authenticate.async {
      implicit user =>
        Future.successful(
          Redirect(controllers.agent.routes.SelectClientVrnController.show())
            .removingFromSession(SessionKeys.clientVRN, SessionKeys.newReturnFrequency, SessionKeys.currentReturnFrequency)
        )
    }
}