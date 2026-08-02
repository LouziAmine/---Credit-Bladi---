Feature: PATCH /api/v1/bam-parameters/{id} — happy path

  Background:
    * url baseUrl

  Scenario: Patch processing fees ratio returns 204
    Given path 'api', 'v1', 'bam-parameters', 1
    And headers authHeaders
    And request { "processingFeesRatio": 0.0035 }
    When method PATCH
    Then status 204

  Scenario: Patch multiple fields at once returns 204
    Given path 'api', 'v1', 'bam-parameters', 1
    And headers authHeaders
    And request
      """
      {
        "processingFeesRatio": 0.0030,
        "minProcessingFees": 3000.00,
        "maxProcessingFees": 6000.00
      }
      """
    When method PATCH
    Then status 204

  Scenario: Patch active flag returns 204
    Given path 'api', 'v1', 'bam-parameters', 1
    And headers authHeaders
    And request { "active": true }
    When method PATCH
    Then status 204

  Scenario: Patch registration fee ratio returns 204
    Given path 'api', 'v1', 'bam-parameters', 1
    And headers authHeaders
    And request { "registrationFeeRatio": 0.0400 }
    When method PATCH
    Then status 204

  Scenario: Patch land conservation ratio returns 204
    Given path 'api', 'v1', 'bam-parameters', 1
    And headers authHeaders
    And request { "landConservationRatio": 0.0150 }
    When method PATCH
    Then status 204

  Scenario: Patch notary fee ratio returns 204
    Given path 'api', 'v1', 'bam-parameters', 1
    And headers authHeaders
    And request { "notaryFeeRatio": 0.0100 }
    When method PATCH
    Then status 204

  Scenario: Patch a field then GET confirms the updated value persists
    Given path 'api', 'v1', 'bam-parameters', 1
    And headers authHeaders
    And request { "processingFeesRatio": 0.0040 }
    When method PATCH
    Then status 204

    Given path 'api', 'v1', 'bam-parameters'
    And headers authHeaders
    When method GET
    Then status 200
    And match response.items[0].processingFeesRatio == 0.0040

    Given path 'api', 'v1', 'bam-parameters', 1
    And headers authHeaders
    And request { "processingFeesRatio": 0.0030 }
    When method PATCH
    Then status 204