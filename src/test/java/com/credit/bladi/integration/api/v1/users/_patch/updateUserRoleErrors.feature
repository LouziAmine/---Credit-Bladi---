Feature: PATCH /api/v1/users/{id}/role: error cases (MANAGER only)

  Background:
    * url baseUrl
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And param size = 50
    When method GET
    Then status 200
    * def adminId = karate.jsonPath(response, "$.items[?(@.username=='admin')]")[0].id

  Scenario: Update non-existent user returns 404
    Given path 'api', 'v1', 'users', java.util.UUID.randomUUID().toString(), 'role'
    And headers authHeaders
    And request { role: 'CLIENT' }
    When method PATCH
    Then status 404
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-404', description: 'User not found.' }

  Scenario: Update user role with invalid role value returns 400
    Given path 'api', 'v1', 'users', adminId, 'role'
    And headers authHeaders
    And request { role: 'SUPERADMIN' }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'role must be one of: CLIENT, MANAGER' }

  Scenario: Update user role with null role returns 400
    Given path 'api', 'v1', 'users', adminId, 'role'
    And headers authHeaders
    And request { role: null }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'role must not be null' }

  Scenario: Update user role with empty body returns 400
    Given path 'api', 'v1', 'users', adminId, 'role'
    And headers authHeaders
    And request {}
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'role must not be null' }

  Scenario: Update user role with malformed JSON body returns 400
    Given path 'api', 'v1', 'users', adminId, 'role'
    And headers authHeaders
    And request '{ invalid json !! }'
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Invalid payload or incorrect JSON format. Please check your request.' }

  Scenario: Update user role with wrong Content-Type returns 415
    Given path 'api', 'v1', 'users', adminId, 'role'
    And headers authHeaders
    And header Content-Type = 'text/plain'
    And request '{"role":"CLIENT"}'
    When method PATCH
    Then status 415
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-415', description: 'Unsupported Content-Type. Please use application/json.' }