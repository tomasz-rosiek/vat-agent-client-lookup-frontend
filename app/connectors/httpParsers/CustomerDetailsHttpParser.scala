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

package connectors.httpParsers

import connectors.httpParsers.ResponseHttpParser.HttpGetResult
import models.CustomerDetails
import models.core.ErrorModel
import play.api.Logger
import play.api.http.Status
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object CustomerDetailsHttpParser {

  implicit object CustomerDetailsReads extends HttpReads[HttpGetResult[CustomerDetails]] {

    override def read(method: String, url: String, response: HttpResponse): HttpGetResult[CustomerDetails] = {

      response.status match {
        case Status.OK =>
          Logger.debug("[CustomerCircumstancesHttpParser][read]: Status OK")
          response.json.validate[CustomerDetails].fold(
            invalid => {
              Logger.warn(s"[CustomerCircumstancesHttpParser][read]: Invalid Json - $invalid")
              Left(ErrorModel(Status.INTERNAL_SERVER_ERROR, "Invalid Json"))
            },
            valid => Right(valid)
          )
        case status =>
          Logger.warn(s"[CustomerCircumstancesHttpParser][read]: Unexpected Response, Status $status returned")
          Left(ErrorModel(status,"Downstream error returned when retrieving CustomerDetails"))
      }
    }
  }
}
