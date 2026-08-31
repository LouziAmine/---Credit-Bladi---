Feature: POST /api/v1/users: error cases (MANAGER only)

  Background:
    * url baseUrl

  Scenario: Create user with duplicate username returns 409
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And request { username: '#(adminUsername)', password: 'Password1234', role: 'CLIENT' }
    When method POST
    Then status 409
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-409', description: 'Username is already taken.' }

  Scenario: Create user with username too short returns 400
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And request { username: 'ab', password: 'Password1234', role: 'CLIENT' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'username must be between 3 and 100 characters' }

  Scenario: Create user with blank username returns 400
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And request { username: '', password: 'Password1234', role: 'CLIENT' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: '#string' }

  Scenario: Create user with password too short returns 400
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And request { username: 'validuser', password: 'Ab1', role: 'CLIENT' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'password must be between 12 and 100 characters' }

  Scenario: Create user with missing password returns 400
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And request { username: 'validuser', role: 'CLIENT' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'password must not be blank' }

  Scenario: Create user with invalid role value returns 400
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And request { username: 'validuser', password: 'Password1234', role: 'SUPERADMIN' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'role must be one of: CLIENT, MANAGER' }

  Scenario: Create user with missing username field returns 400
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And request { password: 'Password1234', role: 'CLIENT' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'username must not be blank' }

  Scenario: Create user with missing role field returns 400
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And request { username: 'validuser99', password: 'Password1234' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'role must not be null' }

  Scenario: Create user with null role returns 400
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And request { username: 'validuser99', password: 'Password1234', role: null }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'role must not be null' }

  Scenario: Create user with username too long returns 400
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And def longUsername = 'a'.repeat(101)
    And request { username: '#(longUsername)', password: 'Password1234', role: 'CLIENT' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'username must be between 3 and 100 characters' }

  Scenario: Create user with password too long returns 400
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And def longPassword = 'Aa1'.repeat(33) + 'aa'
    And def username = 'toolong_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(username)', password: '#(longPassword)', role: 'CLIENT' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'password must be between 12 and 100 characters' }

  Scenario: Create user with malformed JSON body returns 400
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And request '{ invalid json !! }'
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Invalid payload or incorrect JSON format. Please check your request.' }

  Scenario: Create user with wrong Content-Type returns 415
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And header Content-Type = 'text/plain'
    And request '{"username":"testuser","password":"Password1234","role":"CLIENT"}'
    When method POST
    Then status 415
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-415', description: 'Unsupported Content-Type. Please use application/json.' }

  Scenario: Create user with empty body returns 400
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And request {}
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: '#string' }