Feature: POST /api/v1/auth/refresh — error cases

  Background:
    * url baseUrl

  Scenario: Refresh without any refresh token cookie returns 401
    Given path 'api', 'v1', 'auth', 'refresh'
    And headers publicHeaders
    When method POST
    Then status 401
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-401', description: 'Invalid or missing refresh token.' }

  Scenario: Refresh with a garbage refresh token cookie returns 401
    Given path 'api', 'v1', 'auth', 'refresh'
    And headers publicHeaders
    And cookie cb_refresh_token = 'not-a-real-token'
    When method POST
    Then status 401
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-401', description: 'Invalid or missing refresh token.' }

  Scenario: Reusing a refresh token after it has been rotated returns 401
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def username = 'refreshreuse_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(username)', password: 'Password1234' }
    When method POST
    Then status 201

    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(username)', password: 'Password1234' }
    When method POST
    Then status 200
    And def firstRefreshToken = responseCookies['cb_refresh_token'].value

    Given path 'api', 'v1', 'auth', 'refresh'
    And headers publicHeaders
    When method POST
    Then status 200

    Given path 'api', 'v1', 'auth', 'refresh'
    And headers publicHeaders
    And cookie cb_refresh_token = firstRefreshToken
    When method POST
    Then status 401
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-401', description: 'Refresh token reuse detected. All sessions have been revoked for security reasons. Please log in again.' }