Feature: GET /api/v1/bam-parameters/{id}: error cases

  Background:
    * url baseUrl

  Scenario: Get BAM parameters by non-existent id returns 404
    Given path 'api', 'v1', 'bam-parameters', java.util.UUID.randomUUID().toString()
    And headers authHeaders
    When method GET
    Then status 404
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-404', description: 'BAM parameters record not found for the given ID.' }

  Scenario: Get BAM parameters by id = 0 returns 400 (not a valid UUID)
    Given path 'api', 'v1', 'bam-parameters', 0
    And headers authHeaders
    When method GET
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: "Field 'id' has an invalid type. Please check your request." }

  Scenario: Get BAM parameters by negative id returns 400 (not a valid UUID)
    Given path 'api', 'v1', 'bam-parameters', -1
    And headers authHeaders
    When method GET
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: "Field 'id' has an invalid type. Please check your request." }
