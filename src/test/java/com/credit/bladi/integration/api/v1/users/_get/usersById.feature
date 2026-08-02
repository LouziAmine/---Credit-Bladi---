Feature: GET /api/v1/users/{id} — happy path (MANAGER only)

  Background:
    * url baseUrl

  Scenario: Get user by valid id returns 200
    Given path 'api', 'v1', 'users', 1
    And headers authHeaders
    When method GET
    Then status 200
    And match response == { id: 1, username: 'admin', role: 'MANAGER' }