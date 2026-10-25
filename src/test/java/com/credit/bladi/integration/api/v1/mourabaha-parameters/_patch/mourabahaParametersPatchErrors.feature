Feature: PATCH /api/v1/mourabaha-parameters/{id}: error cases

  Background:
    * url baseUrl
    Given path 'api', 'v1', 'mourabaha-parameters'
    And headers authHeaders
    When method GET
    Then status 200
    * def id = response.items[0].id

  Scenario: Patch without an id path segment returns 405
    Given path 'api', 'v1', 'mourabaha-parameters'
    And headers authHeaders
    And request { "profitRate": 5.50 }
    When method PATCH
    Then status 405

  Scenario: Patch with non-numeric id returns 400
    Given path 'api', 'v1', 'mourabaha-parameters', 'not-a-number'
    And headers authHeaders
    And request { "profitRate": 5.50 }
    When method PATCH
    Then status 400

  Scenario: Patch with empty body returns 204 (no fields to update)
    Given path 'api', 'v1', 'mourabaha-parameters', id
    And headers authHeaders
    And request {}
    When method PATCH
    Then status 204

  Scenario: Patch with negative profitRate returns 400
    Given path 'api', 'v1', 'mourabaha-parameters', id
    And headers authHeaders
    And request { "profitRate": -0.5 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'profitRate must be strictly positive' }

  Scenario: Patch with zero profitRate returns 400
    Given path 'api', 'v1', 'mourabaha-parameters', id
    And headers authHeaders
    And request { "profitRate": 0.0 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'profitRate must be strictly positive' }

  Scenario: Patch with negative vatRate returns 400
    Given path 'api', 'v1', 'mourabaha-parameters', id
    And headers authHeaders
    And request { "vatRate": -0.05 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'vatRate must be strictly positive' }

  Scenario: Patch with zero vatRate returns 400
    Given path 'api', 'v1', 'mourabaha-parameters', id
    And headers authHeaders
    And request { "vatRate": 0.0 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'vatRate must be strictly positive' }

  Scenario: Patch with unknown JSON field silently ignores it and returns 204
    Given path 'api', 'v1', 'mourabaha-parameters', id
    And headers authHeaders
    And request { "unknownField": 0.05 }
    When method PATCH
    Then status 204

  Scenario: Patch with non-existent ID returns 404
    Given path 'api', 'v1', 'mourabaha-parameters', java.util.UUID.randomUUID().toString()
    And headers authHeaders
    And request { "active": true }
    When method PATCH
    Then status 404
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-404', description: 'Mourabaha parameters record not found for the given ID.' }

  Scenario: Patch with malformed JSON body returns 400
    Given path 'api', 'v1', 'mourabaha-parameters', id
    And headers authHeaders
    And request '{ invalid json !! }'
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Invalid payload or incorrect JSON format. Please check your request.' }

  Scenario: Patch with wrong Content-Type returns 415
    Given path 'api', 'v1', 'mourabaha-parameters', id
    And headers authHeaders
    And header Content-Type = 'text/plain'
    And request '{"profitRate":5.50}'
    When method PATCH
    Then status 415
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-415', description: 'Unsupported Content-Type. Please use application/json.' }
