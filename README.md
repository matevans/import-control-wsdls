
# import-control-wsdls

This service is used to get the wsdl files.

## Development Setup
- Run locally: `sbt run` which runs on port `7208` by default

## Tests

Run Integration Tests: `sbt it:test`

## GET /assets/wsdl/eu/outbound/CR-for-NES-Services/:wsdlFileName

Where wsdlFileName is one of:
- "BusinessActivityService/ICS/ReferralManagementBAS/V1/CCN2.Service.Customs.Default.ICS.ReferralManagementBAS_1.0.0_1.0.0.wsdl"
- "BusinessActivityService/ICS/ReferralManagementBAS/V1CCN2.Service.Customs.Default.ICS.ReferralManagementBAS_1.0.0_CCN2_1.0.0.wsdl"
- "BusinessActivityService/ICS/RiskAnalysisOrchestrationBAS/V1/CCN2.Service.Customs.Default.ICS.RiskAnalysisOrchestrationBAS_1.0.0_1.0.0.wsdl"
- "BusinessActivityService/ICS/RiskAnalysisOrchestrationBAS/V1/CCN2.Service.Customs.Default.ICS.RiskAnalysisOrchestrationBAS_1.0.0_CCN2_1.0.0.wsdl"
- "Policies/CCN2/CCN2.Service.Platform.SecurityPolicies.wsdl"

### Response
HTTP Status code of 200 is returned with the wsdl file when wsdlFileName exists.

### Error scenarios
| Scenario | HTTP Status |
| --- | --- |
| `wsdlFileName` not recognised | `404` |

### Notes for Developer updating the wsdl files

- Current Version of EU requirements `V2.21`
- The folder structure was directly copied from the EU documentation
- Renamed the top level folder to `CR-for-NES-Services` (removing any remnants of a number)
- We replaced `{DestinationID}` in all filenames that contained it with `EU.CR`

### License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
