Feature: GET /api/v1/credit-rates — happy path

  Background:
    * url baseUrl

  Scenario: Get credit rates with default pagination returns 200 with paginated result
    Given path 'api', 'v1', 'credit-rates'
    And headers authHeaders
    When method GET
    Then status 200
    And match response.currentPage == 1
    And match response.lastPage == 1
    And match response.size == 20
    And match response.total == 5
    And match response.items == '#[5]'

  Scenario: Get credit rates with explicit page and size returns 200
    Given path 'api', 'v1', 'credit-rates'
    And headers authHeaders
    And param page = 1
    And param size = 2
    When method GET
    Then status 200
    And match response.currentPage == 1
    And match response.lastPage == 3
    And match response.size == 2
    And match response.total == 5
    And match response.items == '#[2]'
