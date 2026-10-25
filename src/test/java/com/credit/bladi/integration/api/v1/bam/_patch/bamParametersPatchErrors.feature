Feature: PATCH /api/v1/bam-parameters/{id}: error cases

  Background:
    * url baseUrl
    Given path 'api', 'v1', 'bam-parameters'
    And headers authHeaders
    When method GET
    Then status 200
    * def id = response.items[0].id

  Scenario: Patch without an id path segment returns 405
    Given path 'api', 'v1', 'bam-parameters'
    And headers authHeaders
    And request { "processingFeesRatio": 0.0035 }
    When method PATCH
    Then status 405

  Scenario: Patch with non-numeric id returns 400
    Given path 'api', 'v1', 'bam-parameters', 'not-a-number'
    And headers authHeaders
    And request { "processingFeesRatio": 0.0035 }
    When method PATCH
    Then status 400

  Scenario: Patch with empty body returns 204 (no fields to update)
    Given path 'api', 'v1', 'bam-parameters', id
    And headers authHeaders
    And request {}
    When method PATCH
    Then status 204

  Scenario: Patch with negative processingFeesRatio returns 400
    Given path 'api', 'v1', 'bam-parameters', id
    And headers authHeaders
    And request { "processingFeesRatio": -0.001 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'processingFeesRatio must be strictly positive' }

  Scenario: Patch with negative minProcessingFees returns 400
    Given path 'api', 'v1', 'bam-parameters', id
    And headers authHeaders
    And request { "minProcessingFees": -500.0 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'minProcessingFees must be strictly positive' }

  Scenario: Patch with negative maxProcessingFees returns 400
    Given path 'api', 'v1', 'bam-parameters', id
    And headers authHeaders
    And request { "maxProcessingFees": -1000.0 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'maxProcessingFees must be strictly positive' }

  Scenario: Patch with negative registrationFeeRatio returns 400
    Given path 'api', 'v1', 'bam-parameters', id
    And headers authHeaders
    And request { "registrationFeeRatio": -0.01 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'registrationFeeRatio must be strictly positive' }

  Scenario: Patch with negative landConservationRatio returns 400
    Given path 'api', 'v1', 'bam-parameters', id
    And headers authHeaders
    And request { "landConservationRatio": -0.005 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'landConservationRatio must be strictly positive' }

  Scenario: Patch with negative notaryFeeRatio returns 400
    Given path 'api', 'v1', 'bam-parameters', id
    And headers authHeaders
    And request { "notaryFeeRatio": -0.005 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'notaryFeeRatio must be strictly positive' }

  Scenario: Patch with zero minProcessingFees returns 400
    Given path 'api', 'v1', 'bam-parameters', id
    And headers authHeaders
    And request { "minProcessingFees": 0.0 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'minProcessingFees must be strictly positive' }

  Scenario: Patch with unknown JSON field silently ignores it and returns 204
    Given path 'api', 'v1', 'bam-parameters', id
    And headers authHeaders
    And request { "unknownField": 0.05 }
    When method PATCH
    Then status 204

  Scenario: Patch with non-existent ID returns 404
    Given path 'api', 'v1', 'bam-parameters', java.util.UUID.randomUUID().toString()
    And headers authHeaders
    And request { "active": true }
    When method PATCH
    Then status 404
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-404', description: 'BAM parameters record not found for the given ID.' }

  Scenario: Patch with zero processingFeesRatio returns 400
    Given path 'api', 'v1', 'bam-parameters', id
    And headers authHeaders
    And request { "processingFeesRatio": 0.0 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'processingFeesRatio must be strictly positive' }

  Scenario: Patch with zero maxProcessingFees returns 400
    Given path 'api', 'v1', 'bam-parameters', id
    And headers authHeaders
    And request { "maxProcessingFees": 0.0 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'maxProcessingFees must be strictly positive' }

  Scenario: Patch with zero registrationFeeRatio returns 400
    Given path 'api', 'v1', 'bam-parameters', id
    And headers authHeaders
    And request { "registrationFeeRatio": 0.0 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'registrationFeeRatio must be strictly positive' }

  Scenario: Patch with zero landConservationRatio returns 400
    Given path 'api', 'v1', 'bam-parameters', id
    And headers authHeaders
    And request { "landConservationRatio": 0.0 }
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'landConservationRatio must be strictly positive' }

  Scenario: Patch with malformed JSON body returns 400
    Given path 'api', 'v1', 'bam-parameters', id
    And headers authHeaders
    And request '{ invalid json !! }'
    When method PATCH
    Then status 400
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-400', description: 'Invalid payload or incorrect JSON format. Please check your request.' }

  Scenario: Patch with wrong Content-Type returns 415
    Given path 'api', 'v1', 'bam-parameters', id
    And headers authHeaders
    And header Content-Type = 'text/plain'
    And request '{"processingFeesRatio":0.0035}'
    When method PATCH
    Then status 415
    And match response == { requestId: '#string', timestamp: '#string', errorCode: 'CB-415', description: 'Unsupported Content-Type. Please use application/json.' }