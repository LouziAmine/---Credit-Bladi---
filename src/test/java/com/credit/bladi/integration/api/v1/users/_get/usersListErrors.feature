Feature: GET /api/v1/users: error cases

  Background:
    * url baseUrl

  Scenario: Get users list with page = 0 returns 400
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And param page = 0
    And param size = 10
    When method GET
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'INVALID_PAGE: page must be greater or equal to 1.' }

  Scenario: Get users list with size above maximum (50) returns 400
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And param page = 1
    And param size = 51
    When method GET
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'INVALID_SIZE: size must be between 1 and 50.' }

  Scenario: Get users list with size = 0 returns 400
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And param page = 1
    And param size = 0
    When method GET
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'INVALID_SIZE: size must be between 1 and 50.' }
