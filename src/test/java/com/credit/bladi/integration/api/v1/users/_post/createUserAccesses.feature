Feature: POST /api/v1/users: access restrictions (MANAGER only)

  Background:
    * url baseUrl

  Scenario: Create user without auth token returns 401
    Given path 'api', 'v1', 'users'
    And headers publicHeaders
    And request { username: 'some_user', password: 'Password1234', role: 'CLIENT' }
    When method POST
    Then status 401
    And match response ==
      """
      {
        "requestId": "#string",
        "timestamp": "#string",
        "errorCode": "CB-401",
        "description": "Authentication required. Please log in."
      }
      """

  Scenario: Create user with CLIENT JWT returns 403
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def clientUsername = 'usr_client_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(clientUsername)', password: 'Password1234' }
    When method POST
    Then status 201

    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(clientUsername)', password: 'Password1234' }
    When method POST
    Then status 200
    And def clientToken = 'Bearer ' + responseCookies['cb_access_token'].value

    Given path 'api', 'v1', 'users'
    And header Authorization = clientToken
    And header Content-Type = 'application/json'
    And def newUsername = 'hack_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(newUsername)', password: 'Password1234', role: 'MANAGER' }
    When method POST
    Then status 403
    And match response ==
      """
      {
        "requestId": "#string",
        "timestamp": "#string",
        "errorCode": "CB-403",
        "description": "Access denied for this request. Please check your permissions."
      }
      """