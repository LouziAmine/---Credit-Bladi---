Feature: POST /api/v1/activity-sectors: happy path and errors

  Background:
    * url baseUrl

  Scenario: Manager creates a new Activity Sector and gets the created resource back
    * def sectorName = 'Karate Textile ' + java.util.UUID.randomUUID().toString().substring(0, 8)

    Given path 'api', 'v1', 'activity-sectors'
    And headers authHeaders
    And request { name: #(sectorName) }
    When method POST
    Then status 201
    And match responseHeaders['Location'][0] == '/api/v1/activity-sectors/' + response.id
    And match response ==
      """
      {
        id: '#uuid',
        name: '#(sectorName)',
        createdAt: '#string'
      }
      """

  Scenario: Creating a sector whose name already exists (case-insensitive) returns 409
    * def sectorName = 'Karate Duplicate ' + java.util.UUID.randomUUID().toString().substring(0, 8)

    Given path 'api', 'v1', 'activity-sectors'
    And headers authHeaders
    And request { name: #(sectorName) }
    When method POST
    Then status 201

    Given path 'api', 'v1', 'activity-sectors'
    And headers authHeaders
    And request { name: #(sectorName.toUpperCase()) }
    When method POST
    Then status 409
    And match response.errorCode == 'CB-409'

  Scenario: Creating a sector without auth token returns 401
    Given path 'api', 'v1', 'activity-sectors'
    And headers publicHeaders
    And request { name: 'Sans Auth' }
    When method POST
    Then status 401

  Scenario: Creating a sector with CLIENT JWT returns 403
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def clientUsername = 'sector_post_client_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(clientUsername)', password: 'Password1234' }
    When method POST
    Then status 201

    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(clientUsername)', password: 'Password1234' }
    When method POST
    Then status 200
    And def clientToken = 'Bearer ' + responseCookies['cb_access_token'].value

    Given path 'api', 'v1', 'activity-sectors'
    And header Authorization = clientToken
    And request { name: 'Client Attempt' }
    When method POST
    Then status 403
