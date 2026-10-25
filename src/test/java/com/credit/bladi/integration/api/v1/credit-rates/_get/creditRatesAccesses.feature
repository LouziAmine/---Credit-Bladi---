Feature: GET /api/v1/credit-rates: access restrictions

  Background:
    * url baseUrl

  Scenario: Get credit rates without auth token returns 401
    Given path 'api', 'v1', 'credit-rates'
    And headers publicHeaders
    When method GET
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

  Scenario: Get credit rates with CLIENT JWT returns 403
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def clientUsername = 'creditrate_get_client_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(clientUsername)', password: 'Password1234' }
    When method POST
    Then status 201

    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(clientUsername)', password: 'Password1234' }
    When method POST
    Then status 200
    And def clientToken = 'Bearer ' + responseCookies['cb_access_token'].value

    Given path 'api', 'v1', 'credit-rates'
    And header Authorization = clientToken
    When method GET
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
