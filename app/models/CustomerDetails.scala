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

package models

import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._
import uk.gov.hmrc.http.InternalServerException

case class CustomerDetails(firstName: Option[String],
                           lastName: Option[String],
                           organisationName: Option[String],
                           tradingName: Option[String]) {

  val userName: Option[String] = {
    val name = s"${firstName.getOrElse("")} ${lastName.getOrElse("")}".trim
    if (name.isEmpty) None else Some(name)
  }

  lazy val clientName: String = (tradingName, organisationName, userName) match {
    case (Some(tName), _, _) => tName
    case (_, Some(oName), _) => oName
    case (_, _, Some(uName)) => uName
    case (_, _, _) =>
      Logger.warn("[CustomerDetails][clientName] - No entity name was returned by the API")
      throw new InternalServerException("No entity name was returned by the API")
  }
}

object CustomerDetails {

  private val firstNamePath = __ \\ "firstName"
  private val lastNamePath = __ \\ "lastName"
  private val organisationNamePath = __ \\ "organisationName"
  private val tradingNamePath = __ \\ "tradingName"

  implicit val reads: Reads[CustomerDetails] = (
    firstNamePath.readNullable[String] and
    lastNamePath.readNullable[String] and
    organisationNamePath.readNullable[String] and
    tradingNamePath.readNullable[String]
  ) (CustomerDetails.apply _)
}
