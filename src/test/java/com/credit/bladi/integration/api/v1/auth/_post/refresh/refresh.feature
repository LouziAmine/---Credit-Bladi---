Feature: POST /api/v1/auth/refresh — happy path

  Background:
    * url baseUrl

  Scenario: Refresh with a valid refresh token cookie returns 200 with rotated tokens
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def username = 'refresh_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(username)', password: 'Password1234' }
    When method POST
    Then status 201

    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(username)', password: 'Password1234' }
    When method POST
    Then status 200
    And def originalRefreshToken = responseCookies['cb_refresh_token'].value

    Given path 'api', 'v1', 'auth', 'refresh'
    And headers publicHeaders
    When method POST
    Then status 200
    And match response == { username: '#(username)', role: 'CLIENT' }
    And match responseCookies contains { cb_access_token: '#notnull', cb_refresh_token: '#notnull' }
    And def rotatedRefreshToken = responseCookies['cb_refresh_token'].value
    And match rotatedRefreshToken != originalRefreshToken