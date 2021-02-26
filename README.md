
# import-control-wsdls

This service is used to get files for ICS2.

## Development Setup
- Run locally: `sbt run` which runs on port `7208` by default

## Tests

Run Integration Tests: `sbt it:test`

## GET /assets/wsdl/eu/outbound/CR-for-NES-Services/:fileNameAndPath

Where `:fileNameAndPath` could be:
- any file within the `CR-for-NES-Services` file structure in the `public` folder
With some common top level files referenced below, used in the `import-control-outbound-proxy` microservice
- BusinessActivityService/ICS/ReferralManagementBAS/V1/CCN2.Service.Customs.Default.ICS.ReferralManagementBAS_1.0.0_1.0.0.wsdl
- BusinessActivityService/ICS/ReferralManagementBAS/V1CCN2.Service.Customs.Default.ICS.ReferralManagementBAS_1.0.0_CCN2_1.0.0.wsdl
- BusinessActivityService/ICS/RiskAnalysisOrchestrationBAS/V1/CCN2.Service.Customs.Default.ICS.RiskAnalysisOrchestrationBAS_1.0.0_1.0.0.wsdl
- BusinessActivityService/ICS/RiskAnalysisOrchestrationBAS/V1/CCN2.Service.Customs.Default.ICS.RiskAnalysisOrchestrationBAS_1.0.0_CCN2_1.0.0.wsdl
- Policies/CCN2/CCN2.Service.Platform.SecurityPolicies.wsdl

### Success Response
HTTP Status code of 200 is returned with the file when `:fileNameAndPath` exists.

### Error Responses
| Scenario | HTTP Status |
| --- | --- |
| `:fileNameAndPath` not recognised | `404` |

### Notes for Developer updating the files to the latest version

- Current Version of EU requirements `V2.21`
- The folder structure was directly copied from the EU documentation
- Renamed the top level folder to `CR-for-NES-Services` (removing any remnants of a number)
- We replaced `{DestinationID}` in all filenames that contained it with `EU.CR`

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
