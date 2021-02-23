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

import java.io.File

import helpers.IntegrationSpecBase
import javax.wsdl.xml.WSDLReader
import javax.wsdl.{Operation, PortType}
import org.apache.axis2.wsdl.WSDLUtil

import scala.collection.JavaConverters._


class AssetsControllerISpec extends IntegrationSpecBase {

  val wsdlBaseUrl = s"http://localhost:$port/assets/wsdl/eu/outbound/CR-for-NES-Services/"
  val wsdlOperationsForFileNames = Map(
    "BusinessActivityService/ICS/ReferralManagementBAS/V1/CCN2.Service.Customs.Default.ICS.ReferralManagementBAS_1.0.0_CCN2_1.0.0.wsdl" -> List(
      "IE4Q04requestAdditionalInformation",
      "IE4R02provideAdditionalInformation",
      "IE4Q05requestHRCM",
      "IE4R03provideHRCMResult"
    ),
    "BusinessActivityService/ICS/RiskAnalysisOrchestrationBAS/V1/CCN2.Service.Customs.Default.ICS.RiskAnalysisOrchestrationBAS_1.0.0_CCN2_1.0.0.wsdl" -> List(
      "IE4N03notifyERiskAnalysisHit",
      "IE4S01submitEScreeningAssessment",
      "IE4S02submitRiskAnalysisResult",
      "IE4S02updateERiskAnalysisResult",
      "IE4S01updateEScreeningResult"
    ),
//    "Policies/CCN2/CCN2.Service.Platform.SecurityPolicies.wsdl" -> List.empty,
    "BusinessActivityService/ICS/RiskAnalysisOrchestrationBAS/V1/CCN2.Service.Customs.Default.ICS.RiskAnalysisOrchestrationBAS_1.0.0_1.0.0.wsdl" -> List.empty,
    "BusinessActivityService/ICS/ReferralManagementBAS/V1/CCN2.Service.Customs.Default.ICS.ReferralManagementBAS_1.0.0_CCN2_1.0.0.wsdl" -> List.empty
  )

  wsdlOperationsForFileNames.map {
    case (fileName, wsdlOperationList) =>
      s"$wsdlBaseUrl" when {
        val wsdlUrl = wsdlBaseUrl + fileName
        s"a request is made for $fileName" should {
          val operations = parseWsdlAndGetOperationsNames(wsdlUrl)
          wsdlOperationList.foreach(
            wsdlOperation =>
              s"include the operation $wsdlOperation" in {
                operations should contain(wsdlOperation)
            }
          )
        }
      }
  }


  def parseWsdlAndGetOperationsNames(wsdlUrl: String): List[String] = {
    val reader: WSDLReader =
      WSDLUtil.newWSDLReaderWithPopulatedExtensionRegistry
    reader.setFeature("javax.wsdl.importDocuments", true)
    val wsdlDefinition = reader.readWSDL(wsdlUrl)
    val portType =
      wsdlDefinition.getAllPortTypes.asScala.values.head.asInstanceOf[PortType]
    portType.getOperations.asScala.map(_.asInstanceOf[Operation].getName).toList
  }
}
