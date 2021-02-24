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

import java.io.{ByteArrayInputStream, File}

import helpers.IntegrationSpecBase
import javax.wsdl.xml.WSDLReader
import javax.wsdl.{Operation, PortType}
import org.apache.axis2.wsdl.WSDLUtil
import play.api.http.Status

import scala.collection.JavaConverters._
import scala.io.Source
import scala.util.Try
import scala.xml.XML

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
    "BusinessActivityService/ICS/RiskAnalysisOrchestrationBAS/V1/CCN2.Service.Customs.Default.ICS.RiskAnalysisOrchestrationBAS_1.0.0_1.0.0.wsdl" -> List.empty,
    "BusinessActivityService/ICS/ReferralManagementBAS/V1/CCN2.Service.Customs.Default.ICS.ReferralManagementBAS_1.0.0_CCN2_1.0.0.wsdl" -> List.empty
  )

  "all EU Files within public folder" should {
    val xmlSchemaExtension = ".xsd"
    val xmlWsdlExtension = ".wsdl"
    val baseDirectory = new File(app.path.getCanonicalPath + "/public/wsdl/eu/outbound/CR-for-NES-Services" )
    val allFilesFromEU = recursiveListFiles(baseDirectory).filter(_.isFile).filterNot(_.isHidden)

    "be correct amount of xsds and wsdls" in {
      allFilesFromEU.count(file => file.getName.contains(xmlSchemaExtension) || file.getName.contains(xmlWsdlExtension)) shouldBe 65
      allFilesFromEU.length shouldBe 65
    }

    "not contain {DestinationID} as this should have been replaced" in {
      allFilesFromEU.exists(_.getName.contains("{DestinationID}")) shouldBe false
    }

    s"return ${Status.OK} and parse to xml" when {
      allFilesFromEU.foreach( eachFile =>
        s"file is ${eachFile.getName}" in {
          val pathToFile = eachFile.getCanonicalPath.split("/CR-for-NES-Services/")(1).trim
          val resultOfGettingAsset = await(buildClient(s"/assets/wsdl/eu/outbound/CR-for-NES-Services/$pathToFile").get())
          resultOfGettingAsset.status shouldBe Status.OK

            val sourceOfFile = Source.fromFile(eachFile,"UTF-8")
            val byteArrayStreamOfFile = new ByteArrayInputStream(sourceOfFile.mkString.getBytes
            ("UTF-8"))
            val byeArrayStreamOfBody = new ByteArrayInputStream(resultOfGettingAsset.body.getBytes("UTF-8"))
            val fileFromDirectoryParsed = Try(XML.load(byteArrayStreamOfFile))

            fileFromDirectoryParsed.get shouldBe XML.load(byeArrayStreamOfBody)

            sourceOfFile.close()
            byteArrayStreamOfFile.close()
            byeArrayStreamOfBody.close()
        }
      )
    }
  }

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

  def recursiveListFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles)
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
