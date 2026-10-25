Feature: GET /api/v1/mourabaha-parameters/{id}: happy path

  Background:
    * url baseUrl

  Scenario: Get Mourabaha parameters by valid id returns 200
    Given path 'api', 'v1', 'mourabaha-parameters'
    And headers authHeaders
    When method GET
    Then status 200
    * def id = response.items[0].id

    Given path 'api', 'v1', 'mourabaha-parameters', id
    And headers authHeaders
    When method GET
    Then status 200
    And match response ==
      """
      {
        "id": '#(id)',
        "profitRate": 5.25,
        "vatRate": 0.1000,
        "takafulMonthlyRate": 0.0004,
        "active": true
      }
      """
