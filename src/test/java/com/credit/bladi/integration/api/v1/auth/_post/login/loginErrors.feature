Feature: POST /api/v1/auth/login — error cases

  Background:
    * url baseUrl

  Scenario: Login with wrong password returns 401
    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(adminUsername)', password: 'wrongpassword' }
    When method POST
    Then status 401
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-401', description: 'Invalid username or password.' }

  Scenario: Login with unknown username returns 401
    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: 'nobody_xyz_999', password: 'Password1234' }
    When method POST
    Then status 401
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-401', description: 'Invalid username or password.' }

  Scenario: Login with blank username returns 400
    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '', password: 'Password1234' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: '#string' }

  Scenario: Login with username too short returns 400
    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: 'ab', password: 'Password1234' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'username must be between 3 and 100 characters' }

  Scenario: Login with password too short returns 400
    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(adminUsername)', password: 'ab' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'password must be between 6 and 100 characters' }

  Scenario: Login with missing username field returns 400
    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { password: 'Password1234' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'username must not be blank' }

  Scenario: Login with empty body returns 400
    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request {}
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: '#string' }

  Scenario: Login with missing password field returns 400
    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(adminUsername)' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'password must not be blank' }

  Scenario: Login with blank password returns 400
    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(adminUsername)', password: '' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: '#string' }

  Scenario: Login with username too long returns 400
    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And def longUsername = 'a'.repeat(101)
    And request { username: '#(longUsername)', password: 'Password1234' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'username must be between 3 and 100 characters' }

  Scenario: Login with password too long returns 400
    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And def longPassword = 'a'.repeat(101)
    And request { username: '#(adminUsername)', password: '#(longPassword)' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'password must be between 6 and 100 characters' }

  Scenario: Login with wrong Content-Type returns 415
    Given path 'api', 'v1', 'auth', 'login'
    And header Content-Type = 'text/plain'
    And request '{"username":"admin","password":"admin123"}'
    When method POST
    Then status 415
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-415', description: 'Unsupported Content-Type. Please use application/json.' }

  Scenario: Login with malformed JSON body returns 400
    Given path 'api', 'v1', 'auth', 'login'
    And header Content-Type = 'application/json'
    And request '{ invalid json !! }'
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Invalid payload or incorrect JSON format. Please check your request.' }