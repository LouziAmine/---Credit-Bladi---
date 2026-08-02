Feature: PATCH /api/v1/users/{id}/role — happy path (MANAGER only)

  Background:
    * url baseUrl

  Scenario: Another MANAGER can update admin role idempotently returns 204
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And def secondMgr = 'mgr2_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(secondMgr)', password: 'Password1234', role: 'MANAGER' }
    When method POST
    Then status 201

    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(secondMgr)', password: 'Password1234' }
    When method POST
    Then status 200
    And def secondMgrToken = 'Bearer ' + responseCookies['cb_access_token'].value

    Given path 'api', 'v1', 'users', 1, 'role'
    And header Authorization = secondMgrToken
    And header Content-Type = 'application/json'
    And request { role: 'MANAGER' }
    When method PATCH
    Then status 204

  Scenario: Update user role to CLIENT then restore returns 204
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And def tempUser = 'restore_' + java.util.UUID.randomUUID().toString().substring(0, 8)
    And request { username: '#(tempUser)', password: 'Password1234', role: 'MANAGER' }
    When method POST
    Then status 201

    Given path 'api', 'v1', 'auth', 'login'
    And headers publicHeaders
    And request { username: '#(tempUser)', password: 'Password1234' }
    When method POST
    Then status 200
    And def tempManagerToken = 'Bearer ' + responseCookies['cb_access_token'].value

    Given path 'api', 'v1', 'users', 1, 'role'
    And header Authorization = tempManagerToken
    And header Content-Type = 'application/json'
    And request { role: 'CLIENT' }
    When method PATCH
    Then status 204

    Given path 'api', 'v1', 'users', 1, 'role'
    And header Authorization = tempManagerToken
    And header Content-Type = 'application/json'
    And request { role: 'MANAGER' }
    When method PATCH
    Then status 204