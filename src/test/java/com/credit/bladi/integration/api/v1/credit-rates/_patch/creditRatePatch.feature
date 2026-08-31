Feature: PATCH /api/v1/credit-rates/{id}: happy path

  Background:
    * url baseUrl
    Given path 'api', 'v1', 'credit-rates'
    And headers authHeaders
    And param size = 50
    When method GET
    Then status 200
    * def id = karate.jsonPath(response, "$.items[?(@.cspCode=='INDE')]")[0].id

  Scenario: Patch rateMax returns 204
    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request { "rateMax": 6.75 }
    When method PATCH
    Then status 204

    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request { "rateMax": 6.50 }
    When method PATCH
    Then status 204

  Scenario: Patch multiple fields at once returns 204
    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request
      """
      {
        "rateMin": 6.10,
        "rateMax": 6.60
      }
      """
    When method PATCH
    Then status 204

    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request
      """
      {
        "rateMin": 6.00,
        "rateMax": 6.50
      }
      """
    When method PATCH
    Then status 204

  Scenario: Patch duration range (both bounds together) returns 204
    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request { "minDurationMonths": 24, "maxDurationMonths": 200 }
    When method PATCH
    Then status 204

    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request { "minDurationMonths": 12, "maxDurationMonths": 180 }
    When method PATCH
    Then status 204


    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request { "rateMax": 7.25 }
    When method PATCH
    Then status 204

    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    When method GET
    Then status 200
    And match response.rateMax == 7.25

    Given path 'api', 'v1', 'mortgage-credit'
    And headers publicHeaders
    And request
      """
      {
        "applicantName": "Karim Benali",
        "email": "karim.benali@example.ma",
        "phone": "0612345678",
        "amount": 400000.00,
        "duration": 120,
        "cspCode": "INDE",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "propertyValue": 500000.00,
        "monthlyIncome": 25000.00,
        "age": 35
      }
      """
    When method POST
    Then status 200
    And match response.conventional.rateMax == 7.25

    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request { "rateMax": 6.50 }
    When method PATCH
    Then status 204

    Given path 'api', 'v1', 'mortgage-credit'
    And headers publicHeaders
    And request
      """
      {
        "applicantName": "Karim Benali",
        "email": "karim.benali@example.ma",
        "phone": "0612345678",
        "amount": 400000.00,
        "duration": 120,
        "cspCode": "INDE",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "propertyValue": 500000.00,
        "monthlyIncome": 25000.00,
        "age": 35
      }
      """
    When method POST
    Then status 200
    And match response.conventional.rateMax == 6.5
