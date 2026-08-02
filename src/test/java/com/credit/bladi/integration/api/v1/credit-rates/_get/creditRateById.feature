Feature: GET /api/v1/credit-rates/{id} — happy path and error cases

  Background:
    * url baseUrl

  Scenario: Get credit rate by valid id returns 200
    Given path 'api', 'v1', 'credit-rates', 5
    And headers authHeaders
    When method GET
    Then status 200
    And match response ==
      """
      {
        "id": 5,
        "cspCode": "INDE",
        "rateMin": 6.00,
        "rateMax": 6.50,
        "minDurationMonths": 12,
        "maxDurationMonths": 180,
        "active": true
      }
      """

  Scenario: Get credit rate by non-existent id returns 404
    Given path 'api', 'v1', 'credit-rates', 99999
    And headers authHeaders
    When method GET
    Then status 404
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-404', description: 'Credit rate record not found for the given ID.' }

  Scenario: Get credit rate by id = 0 returns 400
    Given path 'api', 'v1', 'credit-rates', 0
    And headers authHeaders
    When method GET
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'INVALID_ID: id must be a positive number.' }
