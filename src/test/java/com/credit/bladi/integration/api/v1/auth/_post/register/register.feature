Feature: POST /api/v1/auth/register: happy path

  Background:
    * url baseUrl

  Scenario: Register a new user with valid credentials returns 201
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def username = 'reg_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(username)', password: 'Password1234' }
    When method POST
    Then status 201

  Scenario: Register a user with minimum username length returns 201
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def username = 'usr'
    And def suffix = java.util.UUID.randomUUID().toString().substring(0, 6)
    And request { username: '#(username + suffix)', password: 'Password1234' }
    When method POST
    Then status 201

  Scenario: Register a user with minimum password length returns 201
    Given path 'api', 'v1', 'auth', 'register'
    And headers publicHeaders
    And def username = 'min_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(username)', password: 'Abcdef123456' }
    When method POST
    Then status 201