Feature: POST /api/v1/auth/logout — does not require authentication

  Background:
    * url baseUrl

  Scenario: Logout without any token returns 204 (nothing to revoke)
    Given path 'api', 'v1', 'auth', 'logout'
    And headers publicHeaders
    When method POST
    Then status 204

  Scenario: Logout with a malformed JWT still returns 204
    Given path 'api', 'v1', 'auth', 'logout'
    And headers publicHeaders
    And header Authorization = 'Bearer this.is.not.a.valid.jwt'
    When method POST
    Then status 204

  Scenario: Logging out twice with the same token is idempotent (204 both times)
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def username = 'revoke_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(username)', password: 'Password1234' }
    When method POST
    Then status 201

    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(username)', password: 'Password1234' }
    When method POST
    Then status 200
    And def issuedToken = 'Bearer ' + responseCookies['cb_access_token'].value

    Given path 'api', 'v1', 'auth', 'logout'
    And headers publicHeaders
    And header Authorization = issuedToken
    When method POST
    Then status 204

    Given path 'api', 'v1', 'auth', 'logout'
    And headers publicHeaders
    And header Authorization = issuedToken
    When method POST
    Then status 204