Feature: GET /api/v1/users/{id}: error cases

  Background:
    * url baseUrl

  Scenario: Get user by non-existent id returns 404
    Given path 'api', 'v1', 'users', java.util.UUID.randomUUID().toString()
    And headers authHeaders
    When method GET
    Then status 404
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-404', description: 'User not found.' }

  Scenario: Get user by id = 0 returns 400 (not a valid UUID)
    Given path 'api', 'v1', 'users', 0
    And headers authHeaders
    When method GET
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: "Field 'id' has an invalid type. Please check your request." }

  Scenario: Get user by negative id returns 400 (not a valid UUID)
    Given path 'api', 'v1', 'users', -1
    And headers authHeaders
    When method GET
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: "Field 'id' has an invalid type. Please check your request." }
