Feature: POST /api/v1/auth/logout — happy path

  Background:
    * url baseUrl

  Scenario: Logout with a valid token returns 204
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def username = 'logout_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(username)', password: 'Password1234' }
    When method POST
    Then status 201

    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(username)', password: 'Password1234' }
    When method POST
    Then status 200
    And def freshToken = 'Bearer ' + responseCookies['cb_access_token'].value

    Given path 'api', 'v1', 'auth', 'logout'
    And headers publicHeaders
    And header Authorization = freshToken
    When method POST
    Then status 204