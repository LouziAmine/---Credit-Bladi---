Feature: POST /api/v1/users — happy path (MANAGER only)

  Background:
    * url baseUrl

  Scenario: Create a CLIENT user returns 201
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And def username = 'client_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(username)', password: 'Password1234', role: 'CLIENT' }
    When method POST
    Then status 201

  Scenario: Create a MANAGER user returns 201
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And def username = 'mgr_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(username)', password: 'Password1234', role: 'MANAGER' }
    When method POST
    Then status 201

  Scenario: Create user then verify created user can login returns 200
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And def newUsername = 'chain_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(newUsername)', password: 'Password1234', role: 'CLIENT' }
    When method POST
    Then status 201

    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(newUsername)', password: 'Password1234' }
    When method POST
    Then status 200
    And match response contains { username: '#string', role: 'CLIENT' }