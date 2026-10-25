Feature: GET /api/v1/mourabaha-parameters: happy path

  Background:
    * url baseUrl

  Scenario: Get Mourabaha parameters with default pagination returns 200 with paginated result
    Given path 'api', 'v1', 'mourabaha-parameters'
    And headers authHeaders
    When method GET
    Then status 200
    And match response.currentPage == 1
    And match response.lastPage == 1
    And match response.size == 20
    And match response.total == 1
    And match response.items == '#[1]'
    And match response.items[0] ==
      """
      {
        "id": '#uuid',
        "profitRate": 5.25,
        "vatRate": 0.1000,
        "takafulMonthlyRate": 0.0004,
        "active": true
      }
      """

  Scenario: Get Mourabaha parameters with explicit page and size returns 200
    Given path 'api', 'v1', 'mourabaha-parameters'
    And headers authHeaders
    And param page = 1
    And param size = 10
    When method GET
    Then status 200
    And match response.currentPage == 1
    And match response.lastPage == 1
    And match response.size == 10
    And match response.total == 1
    And match response.items == '#[1]'

  Scenario: Get Mourabaha parameters with size at maximum (50) returns 200
    Given path 'api', 'v1', 'mourabaha-parameters'
    And headers authHeaders
    And param page = 1
    And param size = 50
    When method GET
    Then status 200
    And match response.currentPage == 1
    And match response.lastPage == 1
    And match response.size == 50
    And match response.total == 1

  Scenario: Get Mourabaha parameters with minimum size (1) returns 200
    Given path 'api', 'v1', 'mourabaha-parameters'
    And headers authHeaders
    And param page = 1
    And param size = 1
    When method GET
    Then status 200
    And match response.currentPage == 1
    And match response.lastPage == 1
    And match response.size == 1
    And match response.total == 1
    And match response.items == '#[1]'
