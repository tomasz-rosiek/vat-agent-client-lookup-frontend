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

package connectors.httpParsers

import connectors.httpParsers.CreateEmailVerificationRequestHttpParser.{EmailAlreadyVerified, EmailVerificationRequestSent}
import connectors.httpParsers.CreateEmailVerificationRequestHttpParser.CreateEmailVerificationRequestHttpReads.read
import models.errors.UnexpectedError
import play.api.http.Status._
import uk.gov.hmrc.http.HttpResponse
import uk.gov.hmrc.play.test.UnitSpec

class CreateEmailVerificationRequestHttpParserSpec extends UnitSpec {

  "CreateEmailVerificationRequestHttpReads" when {

    "the response status is CREATED" should {

      "return an EmailVerificationRequestSent object" in {
        val httpResponse: HttpResponse = HttpResponse(responseStatus = CREATED)
        read("", "", httpResponse) shouldBe Right(EmailVerificationRequestSent)
      }
    }

    "the response status is CONFLICT" should {

      "return an EmailAlreadyVerified object" in {
        val httpResponse: HttpResponse = HttpResponse(responseStatus = CONFLICT)
        read("", "", httpResponse) shouldBe Right(EmailAlreadyVerified)
      }
    }

    "the response returns an unexpected status" should {

      "return an error model with the status and response body" in {
        val httpResponse : HttpResponse = HttpResponse(responseStatus = INTERNAL_SERVER_ERROR)
        read("", "", httpResponse) shouldBe Left(UnexpectedError(INTERNAL_SERVER_ERROR, httpResponse.body))
      }
    }
  }
}
