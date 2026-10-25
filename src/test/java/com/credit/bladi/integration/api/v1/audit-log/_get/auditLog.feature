Feature: GET /api/v1/audit-log: happy path

  Background:
    * url baseUrl

  Scenario: Get audit log with default pagination returns 200 with paginated result
    Given path 'api', 'v1', 'mortgage-credit'
    And headers publicHeaders
    And request
      """
      {
        "applicantName": "Karim Benali",
        "email": "karim.benali@example.ma",
        "phone": "0612345678",
        "amount": 500000.00,
        "duration": 240,
        "cspCode": "SALA",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "propertyValue": 600000.00,
        "monthlyIncome": 20000.00,
        "age": 35
      }
      """
    When method POST
    Then status 200

    Given path 'api', 'v1', 'audit-log'
    And headers authHeaders
    And param page = 1
    And param size = 20
    When method GET
    Then status 200
    And match response.currentPage == 1
    And match response.size == 20
    And match response.total == '#number'
    And match response.items[0] ==
      """
      {
        "id": '#uuid',
        "username": '##string',
        "action": '#string',
        "ipAddress": '#string',
        "details": '##string',
        "performedAt": '#string',
        "entityType": '##string',
        "entityId": '##uuid'
      }
      """

  Scenario: Get audit log with explicit page and size returns 200
    Given path 'api', 'v1', 'audit-log'
    And headers authHeaders
    And param page = 1
    And param size = 5
    When method GET
    Then status 200
    And match response.currentPage == 1
    And match response.size == 5
