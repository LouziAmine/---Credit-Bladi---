Feature: POST /api/v1/auth/login — happy path

  Background:
    * url baseUrl

  Scenario: Login with valid admin credentials returns 200 with user info and HttpOnly cookie
    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(adminUsername)', password: '#(adminPassword)' }
    When method POST
    Then status 200
    And match response contains { username: '#string', role: '#string' }
    And match header Set-Cookie contains 'cb_access_token'
    And match header Set-Cookie contains 'HttpOnly'

  Scenario: Login with a freshly registered user returns 200
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def username = 'login_test_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(username)', password: 'Password1234' }
    When method POST
    Then status 201

    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(username)', password: 'Password1234' }
    When method POST
    Then status 200
    And match response contains { username: '#string', role: 'CLIENT' }
    And match header Set-Cookie contains 'cb_access_token'
    And match header Set-Cookie contains 'HttpOnly'