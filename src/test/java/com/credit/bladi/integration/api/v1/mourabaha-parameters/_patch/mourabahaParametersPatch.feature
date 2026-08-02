Feature: PATCH /api/v1/mourabaha-parameters/{id} — happy path

  Background:
    * url baseUrl

  Scenario: Patch profit rate returns 204
    Given path 'api', 'v1', 'mourabaha-parameters', 1
    And headers authHeaders
    And request { "profitRate": 5.50 }
    When method PATCH
    Then status 204

    Given path 'api', 'v1', 'mourabaha-parameters', 1
    And headers authHeaders
    And request { "profitRate": 5.25 }
    When method PATCH
    Then status 204

  Scenario: Patch multiple fields at once returns 204
    Given path 'api', 'v1', 'mourabaha-parameters', 1
    And headers authHeaders
    And request
      """
      {
        "profitRate": 5.30,
        "vatRate": 0.1000
      }
      """
    When method PATCH
    Then status 204

    Given path 'api', 'v1', 'mourabaha-parameters', 1
    And headers authHeaders
    And request { "profitRate": 5.25 }
    When method PATCH
    Then status 204

  Scenario: Patch active flag returns 204
    Given path 'api', 'v1', 'mourabaha-parameters', 1
    And headers authHeaders
    And request { "active": true }
    When method PATCH
    Then status 204

  Scenario: Patch VAT rate returns 204
    Given path 'api', 'v1', 'mourabaha-parameters', 1
    And headers authHeaders
    And request { "vatRate": 0.2000 }
    When method PATCH
    Then status 204

    Given path 'api', 'v1', 'mourabaha-parameters', 1
    And headers authHeaders
    And request { "vatRate": 0.1000 }
    When method PATCH
    Then status 204

  Scenario: Patch a field then GET confirms the updated value persists
    Given path 'api', 'v1', 'mourabaha-parameters', 1
    And headers authHeaders
    And request { "profitRate": 6.00 }
    When method PATCH
    Then status 204

    Given path 'api', 'v1', 'mourabaha-parameters'
    And headers authHeaders
    When method GET
    Then status 200
    And match response.items[0].profitRate == 6.00

    Given path 'api', 'v1', 'mourabaha-parameters', 1
    And headers authHeaders
    And request { "profitRate": 5.25 }
    When method PATCH
    Then status 204
