Feature: PATCH /api/v1/bam-parameters/{id}: access restrictions

  Background:
    * url baseUrl

  Scenario: Patch without auth token returns 401
    Given path 'api', 'v1', 'bam-parameters', 1
    And headers publicHeaders
    And request { "processingFeesRatio": 0.0035 }
    When method PATCH
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

  Scenario: Patch with CLIENT JWT returns 403
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def clientUsername = 'bam_client_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(clientUsername)', password: 'Password1234' }
    When method POST
    Then status 201

    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(clientUsername)', password: 'Password1234' }
    When method POST
    Then status 200
    And def clientToken = 'Bearer ' + responseCookies['cb_access_token'].value

    Given path 'api', 'v1', 'bam-parameters', 1
    And header Authorization = clientToken
    And header Content-Type = 'application/json'
    And request { "active": true }
    When method PATCH
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