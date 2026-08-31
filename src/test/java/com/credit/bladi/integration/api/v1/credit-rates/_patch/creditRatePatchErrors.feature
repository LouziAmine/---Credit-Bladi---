Feature: PATCH /api/v1/credit-rates/{id}: error cases

  Background:
    * url baseUrl
    Given path 'api', 'v1', 'credit-rates'
    And headers authHeaders
    And param size = 50
    When method GET
    Then status 200
    * def id = karate.jsonPath(response, "$.items[?(@.cspCode=='INDE')]")[0].id

  Scenario: Patch without an id path segment returns 405
    Given path 'api', 'v1', 'credit-rates'
    And headers authHeaders
    And request { "rateMax": 6.75 }
    When method PATCH
    Then status 405

  Scenario: Patch with non-numeric id returns 400
    Given path 'api', 'v1', 'credit-rates', 'not-a-number'
    And headers authHeaders
    And request { "rateMax": 6.75 }
    When method PATCH
    Then status 400

  Scenario: Patch with empty body returns 204 (no fields to update)
    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request {}
    When method PATCH
    Then status 204

  Scenario: Patch with negative rateMin returns 400
    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request { "rateMin": -1.0 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'rateMin must be strictly positive' }

  Scenario: Patch with zero rateMax returns 400
    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request { "rateMax": 0.0 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'rateMax must be strictly positive' }

  Scenario: Patch with rateMin greater than rateMax in the same request returns 400
    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request { "rateMin": 7.00, "rateMax": 6.50 }
    When method PATCH
    Then status 400

  Scenario: Patch with only minDurationMonths (missing maxDurationMonths) returns 400
    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request { "minDurationMonths": 24 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'minDurationMonths and maxDurationMonths must be provided together.' }

  Scenario: Patch with only maxDurationMonths (missing minDurationMonths) returns 400
    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request { "maxDurationMonths": 200 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'minDurationMonths and maxDurationMonths must be provided together.' }

  Scenario: Patch with negative minDurationMonths returns 400
    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request { "minDurationMonths": -1, "maxDurationMonths": 180 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'minDurationMonths cannot be negative' }

  Scenario: Patch with unknown JSON field silently ignores it and returns 204
    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request { "unknownField": 0.05 }
    When method PATCH
    Then status 204

  Scenario: Patch with non-existent ID returns 404
    Given path 'api', 'v1', 'credit-rates', java.util.UUID.randomUUID().toString()
    And headers authHeaders
    And request { "active": true }
    When method PATCH
    Then status 404
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-404', description: 'Credit rate record not found for the given ID.' }

  Scenario: Patch with malformed JSON body returns 400
    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And request '{ invalid json !! }'
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Invalid payload or incorrect JSON format. Please check your request.' }

  Scenario: Patch with wrong Content-Type returns 415
    Given path 'api', 'v1', 'credit-rates', id
    And headers authHeaders
    And header Content-Type = 'text/plain'
    And request '{"rateMax":6.75}'
    When method PATCH
    Then status 415
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-415', description: 'Unsupported Content-Type. Please use application/json.' }
