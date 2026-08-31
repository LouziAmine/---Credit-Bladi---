Feature: GET /api/v1/credit-rates/{id}: happy path and error cases

  Background:
    * url baseUrl

  Scenario: Get credit rate by valid id returns 200
    Given path 'api', 'v1', 'credit-rates'
    And headers authHeaders
    And param size = 50
    When method GET
    Then status 200
    * def indeRate = karate.jsonPath(response, "$.items[?(@.cspCode=='INDE')]")[0]

    Given path 'api', 'v1', 'credit-rates', indeRate.id
    And headers authHeaders
    When method GET
    Then status 200
    And match response ==
      """
      {
        "id": '#(indeRate.id)',
        "cspCode": "INDE",
        "rateMin": 6.00,
        "rateMax": 6.50,
        "rateFormula": "MAX",
        "minDurationMonths": 12,
        "maxDurationMonths": 180,
        "active": true
      }
      """

  Scenario: Get credit rate by non-existent id returns 404
    Given path 'api', 'v1', 'credit-rates', java.util.UUID.randomUUID().toString()
    And headers authHeaders
    When method GET
    Then status 404
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-404', description: 'Credit rate record not found for the given ID.' }

  Scenario: Get credit rate by id = 0 returns 400 (not a valid UUID)
    Given path 'api', 'v1', 'credit-rates', 0
    And headers authHeaders
    When method GET
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: "Field 'id' has an invalid type. Please check your request." }
