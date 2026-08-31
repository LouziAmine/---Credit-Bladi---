Feature: POST /api/v1/auth/register: error cases

  Background:
    * url baseUrl

  Scenario: Register with username too short returns 400
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And request { username: 'ab', password: 'Password1234' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'username must be between 3 and 100 characters' }

  Scenario: Register with password too short returns 400
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def username = 'err_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(username)', password: 'Ab1' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'password must be between 12 and 100 characters' }

  Scenario: Register with blank username returns 400
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And request { username: '', password: 'Password1234' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: '#string' }

  Scenario: Register with missing username field returns 400
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And request { password: 'Password1234' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'username must not be blank' }

  Scenario: Register with duplicate username returns 409
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And request { username: '#(adminUsername)', password: 'Password1234' }
    When method POST
    Then status 409
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-409', description: 'Username is already taken.' }

  Scenario: Register with missing password field returns 400
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And request { username: 'validuser' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'password must not be blank' }

  Scenario: Register with blank password returns 400
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def username = 'blank_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(username)', password: '' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: '#string' }

  Scenario: Register with username too long returns 400
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def longUsername = 'a'.repeat(101)
    And request { username: '#(longUsername)', password: 'Password1234' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'username must be between 3 and 100 characters' }

  Scenario: Register with password too long returns 400
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def username = 'long_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And def longPassword = 'Aa1'.repeat(33) + 'aa'
    And request { username: '#(username)', password: '#(longPassword)' }
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'password must be between 12 and 100 characters' }

  Scenario: Register with wrong Content-Type returns 415
    Given path 'api', 'v1', 'auth', 'register'
    And header Content-Type = 'text/plain'
    And request '{"username":"testuser","password":"Password1234"}'
    When method POST
    Then status 415
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-415', description: 'Unsupported Content-Type. Please use application/json.' }

  Scenario: Register with malformed JSON body returns 400
    Given path 'api', 'v1', 'auth', 'register'
    And header Content-Type = 'application/json'
    And request '{ invalid json !! }'
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Invalid payload or incorrect JSON format. Please check your request.' }

  Scenario: Register with empty body returns 400
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And request {}
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: '#string' }