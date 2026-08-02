Feature: GET /api/v1/mourabaha-parameters/{id} — error cases

  Background:
    * url baseUrl

  Scenario: Get Mourabaha parameters by non-existent id returns 404
    Given path 'api', 'v1', 'mourabaha-parameters', 99999
    And headers authHeaders
    When method GET
    Then status 404
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-404', description: 'Mourabaha parameters record not found for the given ID.' }

  Scenario: Get Mourabaha parameters by id = 0 returns 400
    Given path 'api', 'v1', 'mourabaha-parameters', 0
    And headers authHeaders
    When method GET
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'INVALID_ID: id must be a positive number.' }

  Scenario: Get Mourabaha parameters by negative id returns 400
    Given path 'api', 'v1', 'mourabaha-parameters', -1
    And headers authHeaders
    When method GET
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'INVALID_ID: id must be a positive number.' }
