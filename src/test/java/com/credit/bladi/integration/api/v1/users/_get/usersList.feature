Feature: GET /api/v1/users: happy path (MANAGER only)

  Background:
    * url baseUrl

  Scenario: Get users list with default pagination returns 200 with paginated result
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    When method GET
    Then status 200
    And match response.currentPage == 1
    And match response.size == 20
    And match response.total == '#number'
    And match response.items == '#[_ > 0]'
    And match each response.items contains { id: '#uuid', username: '#string', role: '#string' }

  Scenario: Get users list with explicit page and size returns 200
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And param page = 1
    And param size = 5
    When method GET
    Then status 200
    And match response.currentPage == 1
    And match response.size == 5
    And match response.items == '#[_ <= 5]'
