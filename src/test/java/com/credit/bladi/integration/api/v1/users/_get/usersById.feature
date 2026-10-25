Feature: GET /api/v1/users/{id}: happy path (MANAGER only)

  Background:
    * url baseUrl

  Scenario: Get user by valid id returns 200
    Given path 'api', 'v1', 'users'
    And headers authHeaders
    And param size = 50
    When method GET
    Then status 200
    * def adminId = karate.jsonPath(response, "$.items[?(@.username=='admin')]")[0].id

    Given path 'api', 'v1', 'users', adminId
    And headers authHeaders
    When method GET
    Then status 200
    And match response == { id: '#(adminId)', username: 'admin', fullName: 'Administrateur', email: 'admin@bladi-credit.ma', role: 'MANAGER', gradeCode: null, gradeName: null, teamCode: null, teamName: null }