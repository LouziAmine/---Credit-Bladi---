Feature: GET /api/v1/mourabaha-parameters/{id} — happy path

  Background:
    * url baseUrl

  Scenario: Get Mourabaha parameters by valid id returns 200
    Given path 'api', 'v1', 'mourabaha-parameters', 1
    And headers authHeaders
    When method GET
    Then status 200
    And match response ==
      """
      {
        "id": 1,
        "profitRate": 5.25,
        "vatRate": 0.1000,
        "active": true
      }
      """
