Feature: GET /api/v1/bam-parameters/{id} — error cases

  Background:
    * url baseUrl

  Scenario: Get BAM parameters by non-existent id returns 404
    Given path 'api', 'v1', 'bam-parameters', 99999
    And headers authHeaders
    When method GET
    Then status 404
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-404', description: 'BAM parameters record not found for the given ID.' }

  Scenario: Get BAM parameters by id = 0 returns 400
    Given path 'api', 'v1', 'bam-parameters', 0
    And headers authHeaders
    When method GET
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'INVALID_ID: id must be a positive number.' }

  Scenario: Get BAM parameters by negative id returns 400
    Given path 'api', 'v1', 'bam-parameters', -1
    And headers authHeaders
    When method GET
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'INVALID_ID: id must be a positive number.' }
