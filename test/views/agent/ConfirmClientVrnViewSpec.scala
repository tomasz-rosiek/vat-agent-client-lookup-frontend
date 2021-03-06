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

package views.agent

import assets.{BaseTestConstants, CustomerDetailsTestConstants}
import assets.messages.{BaseMessages, ConfirmClientVrnPageMessages => viewMessages}
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import views.ViewBaseSpec

class ConfirmClientVrnViewSpec extends ViewBaseSpec {

  "The Confirm Change Client VRN page" when {

    val exampleRedirectUrl = "/homepage"

    "given an individual with no trading name" should {

      lazy val view = views.html.agent.confirmClientVrn(
        BaseTestConstants.vrn,
        CustomerDetailsTestConstants.customerDetailsIndividual
      )(request, messages, mockConfig)

      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct document title of '${viewMessages.title}'" in {
        document.title shouldBe viewMessages.title
      }

      s"have the correct page heading of '${viewMessages.heading}'" in {
        elementText("h1") shouldBe viewMessages.heading
      }

      "have the correct heading and text for the business name section" in {
        elementText("h2") shouldBe viewMessages.name
        elementText("article > p:nth-of-type(1)") shouldBe CustomerDetailsTestConstants.customerDetailsIndividual.userName.get
      }

      "have the correct heading and text for the VAT number section" in {
        elementText("h3") shouldBe viewMessages.vrn
        elementText("article > p:nth-of-type(2)") shouldBe BaseTestConstants.vrn
      }

      "have a confirm button" which {

        s"has the text '${BaseMessages.confirm}'" in {
          elementText("a.button") shouldBe BaseMessages.confirmAndContinue
        }

        "has a link to the redirect URL" in {
          element("a.button").attr("href") shouldBe controllers.agent.routes.ConfirmClientVrnController.redirectToSessionUrl().url
        }
      }

      "have an edit different client link" which {

        s"has the text '${viewMessages.edit}'" in {
          elementText("article > p > a") shouldBe viewMessages.edit
        }

        "has the correct URL" in {
          element("article > p > a").attr("href") shouldBe controllers.agent.routes.ConfirmClientVrnController.changeClient().url
        }
      }
    }


    "given an individual with a trading name" should {

      lazy val view = views.html.agent.confirmClientVrn(
        BaseTestConstants.vrn,
        CustomerDetailsTestConstants.customerDetailsAllInfo
      )(request, messages, mockConfig)

      lazy implicit val document: Document = Jsoup.parse(view.body)

      s"have the correct document title of '${viewMessages.title}'" in {
        document.title shouldBe viewMessages.title
      }

      s"have the correct page heading of '${viewMessages.heading}'" in {
        elementText("h1") shouldBe viewMessages.heading
      }

      "have the correct heading and text for the client name section" in {
        elementText("h2") shouldBe viewMessages.name
        elementText("article > p:nth-of-type(1)") shouldBe CustomerDetailsTestConstants.customerDetailsAllInfo.tradingName.get
      }
    }
  }
}
