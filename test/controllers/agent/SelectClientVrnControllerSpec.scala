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

import assets.messages.{ClientVrnPageMessages => Messages}
import common.SessionKeys
import config.ErrorHandler
import controllers.ControllerBaseSpec
import mocks.MockAuth
import org.jsoup.Jsoup
import play.api.http.Status
import play.api.test.FakeRequest
import play.api.test.Helpers._

class SelectClientVrnControllerSpec extends ControllerBaseSpec with MockAuth {

  object TestClientVrnControllerSpec extends SelectClientVrnController(
    messagesApi,
    mockAgentOnlyAuthPredicate,
    app.injector.instanceOf[ErrorHandler],
    mockConfig
  )

  "Calling the .show action" when {

    "the user is an authorised Agent" should {

      lazy val result = TestClientVrnControllerSpec.show(request)
      lazy val document = Jsoup.parse(bodyOf(result))

      "return 200" in {
        mockAgentAuthorised()
        status(result) shouldBe Status.OK
      }

      "return HTML" in {
        contentType(result) shouldBe Some("text/html")
        charset(result) shouldBe Some("utf-8")
      }

      "render the Client Vrn Page" in {
        document.select("h1").text shouldBe Messages.heading
      }
    }
  }

  "Calling the .submit action" when {

    "the user is an authorised Agent" should {

      "valid data is posted" should {

        lazy val request = FakeRequest("POST", "/").withFormUrlEncodedBody(("vrn", "123456789"))
        lazy val result = TestClientVrnControllerSpec.submit(request)

        "return 303" in {
          mockAgentAuthorised()
          status(result) shouldBe Status.SEE_OTHER
        }

        "contain the correct location header" in {
          redirectLocation(result) shouldBe Some(controllers.agent.routes.ConfirmClientVrnController.show().url)
        }

        "contain the Clients VRN in the session" in {
          session(result).get(SessionKeys.clientVRN) shouldBe Some("123456789")
        }
      }

      "invalid data is posted" should {

        lazy val request = FakeRequest("POST", "/").withFormUrlEncodedBody(("vrn", ""))
        lazy val result = TestClientVrnControllerSpec.submit(request)

        "return 400" in {
          mockAgentAuthorised()
          status(result) shouldBe Status.BAD_REQUEST
        }
      }
    }
  }
}