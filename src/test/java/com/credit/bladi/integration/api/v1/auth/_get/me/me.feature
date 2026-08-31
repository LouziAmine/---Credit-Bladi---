Feature: GET /api/v1/auth/me: happy path and access restrictions

  Background:
    * url baseUrl

  Scenario: Me with a valid access token cookie returns 200 with current user info
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def username = 'me_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(username)', password: 'Password1234' }
    When method POST
    Then status 201

    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(username)', password: 'Password1234' }
    When method POST
    Then status 200

    Given path 'api', 'v1', 'auth', 'me'
    And headers publicHeaders
    When method GET
    Then status 200
    And match response == { username: '#(username)', role: 'CLIENT' }

  Scenario: Me without authentication returns 401
    Given path 'api', 'v1', 'auth', 'me'
    And headers publicHeaders
    When method GET
    Then status 401
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-401', description: 'Authentication required. Please log in.' }

  Scenario: Me with an admin session reflects the MANAGER role
    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(adminUsername)', password: '#(adminPassword)' }
    When method POST
    Then status 200

    Given path 'api', 'v1', 'auth', 'me'
    And headers publicHeaders
    When method GET
    Then status 200
    And match response == { username: '#(adminUsername)', role: 'MANAGER' }