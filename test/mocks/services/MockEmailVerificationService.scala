/*
 * Copyright 2019 HM Revenue & Customs
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

package mocks.services

import org.scalatest.{BeforeAndAfterEach, Suite}
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito.{reset, when}
import services.EmailVerificationService
import uk.gov.hmrc.http.HeaderCarrier
import org.mockito.ArgumentMatchers

import scala.concurrent.Future

trait MockEmailVerificationService extends MockitoSugar with BeforeAndAfterEach {
  this: Suite =>

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockEmailVerificationService)
  }

  val mockEmailVerificationService: EmailVerificationService = mock[EmailVerificationService]

  def mockGetEmailVerificationState(emailAddress: String)(response: Future[Option[Boolean]]): Unit =
    when(mockEmailVerificationService.isEmailVerified(
      ArgumentMatchers.eq(emailAddress)
    )(ArgumentMatchers.any[HeaderCarrier])) thenReturn response

  def mockCreateEmailVerificationRequest(response: Option[Boolean]): Unit =
    when(mockEmailVerificationService.createEmailVerificationRequest(
      ArgumentMatchers.any(),
      ArgumentMatchers.any()
    )(ArgumentMatchers.any[HeaderCarrier])) thenReturn Future.successful(response)
}
