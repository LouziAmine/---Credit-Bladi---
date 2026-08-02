Feature: POST /api/v1/simulation — error cases

  Background:
    * url baseUrl
    * def requestBody =
      """
      {
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

  Scenario: Simulate with amount below minimum (70000 MAD) returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.amount = 50000
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Minimum amount is 70000' }

  Scenario: Simulate with amount above maximum (30000000 MAD) returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.amount = 50000000
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Maximum amount is 30000000' }

  Scenario: Simulate with duration below minimum (12 months) returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.duration = 6
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Minimum duration is 12 months' }

  Scenario: Simulate with duration above maximum (300 months) returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.duration = 360
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Maximum duration is 300 months' }

  Scenario: Simulate with invalid cspCode returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.cspCode = 'INVALID'
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'cspCode must be one of: RETR, SALA, FONC, PROF, INDE' }

  Scenario: Simulate with invalid productType returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.productType = 'FOO'
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'productType must be one of: CONVENTIONAL, MOURABAHA' }

  Scenario: Simulate with invalid nationalityCode format returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.nationalityCode = 'FRANCE'
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Nationality code must follow ISO 3166-1 alpha-2 format (e.g. MA, FR)' }

  Scenario: Simulate with missing amount returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.amount = null
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Amount is required' }

  Scenario: Simulate with missing moroccanResident returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.moroccanResident = null
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'moroccanResident is required' }

  Scenario: Simulate with empty body returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And request {}
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: '#string' }

  Scenario: Simulate with missing duration returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.duration = null
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Duration is required' }

  Scenario: Simulate with missing cspCode returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.cspCode = null
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'cspCode is required' }

  Scenario: Simulate with missing nationalityCode returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.nationalityCode = null
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Nationality code is required' }

  Scenario: Simulate with multiple invalid fields returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.amount = -1
    And requestBody.duration = 500
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: '#string' }

  Scenario: Simulate with unknown JSON field silently ignores it and returns 200
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.unknownField = 'oops'
    And request requestBody
    When method POST
    Then status 200

  Scenario: Simulate with multiple JSON objects in body returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And header Content-Type = 'application/json'
    And request '{"amount":500000,"duration":120,"cspCode":"SALA","nationalityCode":"MA","moroccanResident":true}{"amount":300000}'
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'BATCH_NOT_ALLOWED: only one JSON object is allowed per request.' }

  Scenario: Simulate with wrong Content-Type returns 415
    Given path 'api', 'v1', 'simulation'
    And header Content-Type = 'text/plain'
    And request '{"amount":500000,"duration":120,"cspCode":"SALA","nationalityCode":"MA","moroccanResident":true}'
    When method POST
    Then status 415
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-415', description: 'Unsupported Content-Type. Please use application/json.' }

  Scenario: Simulate SALA with duration exceeding CSP max (241 > 240) returns 422
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.duration = 241
    And request requestBody
    When method POST
    Then status 422
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-422', description: 'Duration out of range for this CSP profile (BAM regulation).' }

  Scenario: Simulate RETR with duration exceeding CSP max (241 > 240) returns 422
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.cspCode = 'RETR'
    And requestBody.duration = 241
    And request requestBody
    When method POST
    Then status 422
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-422', description: 'Duration out of range for this CSP profile (BAM regulation).' }

  Scenario: Simulate PROF with duration exceeding CSP max (181 > 180) returns 422
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.cspCode = 'PROF'
    And requestBody.duration = 181
    And request requestBody
    When method POST
    Then status 422
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-422', description: 'Duration out of range for this CSP profile (BAM regulation).' }

  Scenario: Simulate INDE with duration exceeding CSP max (181 > 180) returns 422
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.cspCode = 'INDE'
    And requestBody.duration = 181
    And request requestBody
    When method POST
    Then status 422
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-422', description: 'Duration out of range for this CSP profile (BAM regulation).' }

  Scenario: Simulate FONC with duration exceeding max (301 > 300) returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.cspCode = 'FONC'
    And requestBody.duration = 301
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Maximum duration is 300 months' }

  Scenario: Simulate SALA with age-at-maturity exceeding the BAM ceiling (60+20=80 > 75) returns 422
            — regression test for the age-at-maturity rule now applying to every CSP profile, not
            just RETR
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.age = 60
    And request requestBody
    When method POST
    Then status 422
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-422', description: 'Credit maturity exceeds the maximum allowed age at maturity (BAM regulation).' }

  Scenario: Simulate SALA with age-at-maturity exactly at the BAM ceiling (55+20=75) returns 200
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.age = 55
    And request requestBody
    When method POST
    Then status 200

  Scenario: Simulate SALA with loan amount exceeding 90% LTV of property value returns 422
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.propertyValue = 500000
    And request requestBody
    When method POST
    Then status 422
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-422', description: 'Loan amount exceeds 90% of property value. A minimum deposit of 10% is required (BAM regulation).' }

  Scenario: Simulate SALA with monthly payment exceeding 40% debt-ratio ceiling returns 422
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.monthlyIncome = 8000
    And request requestBody
    When method POST
    Then status 422
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-422', description: 'Monthly payment exceeds 40% of declared monthly income (BAM regulation).' }

  Scenario: Simulate with nationalityCode in lowercase returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.nationalityCode = 'ma'
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Nationality code must follow ISO 3166-1 alpha-2 format (e.g. MA, FR)' }

  Scenario: Simulate with nationalityCode of 1 character returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.nationalityCode = 'M'
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Nationality code must follow ISO 3166-1 alpha-2 format (e.g. MA, FR)' }

  Scenario: Simulate with well-formed but non-existent nationalityCode returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.nationalityCode = 'ZZ'
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Nationality code is not a recognized ISO 3166-1 alpha-2 country code' }

  Scenario: Simulate with cspCode in lowercase returns 400
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.cspCode = 'sala'
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'cspCode must be one of: RETR, SALA, FONC, PROF, INDE' }

  Scenario: Simulate with amount as string returns 400 (type mismatch)
    Given path 'api', 'v1', 'simulation'
    And headers publicHeaders
    And requestBody.amount = 'not-a-number'
    And request requestBody
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: "Field 'amount' has an invalid type. Please check your request." }

  Scenario: Simulate with malformed JSON body returns 400
    Given path 'api', 'v1', 'simulation'
    And header Content-Type = 'application/json'
    And request '{ invalid json !! }'
    When method POST
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Invalid payload or incorrect JSON format. Please check your request.' }

  Scenario: GET on unknown sub-path of simulation endpoint returns 401
    Given path 'api', 'v1', 'simulation', 'unknown-sub-path'
    And headers publicHeaders
    When method GET
    Then status 401
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-401', description: 'Authentication required. Please log in.' }