Feature: GET /api/v1/bam-parameters/{id} — happy path

  Background:
    * url baseUrl

  Scenario: Get BAM parameters by valid id returns 200
    Given path 'api', 'v1', 'bam-parameters', 1
    And headers authHeaders
    When method GET
    Then status 200
    And match response ==
      """
      {
        "id": 1,
        "processingFeesRatio": 0.0030,
        "minProcessingFees": 3000.00,
        "maxProcessingFees": 6000.00,
        "registrationFeeRatio": 0.0400,
        "landConservationRatio": 0.0150,
        "notaryFeeRatio": 0.0100,
        "ltvRatio": 0.9000,
        "maxDebtRatio": 0.4000,
        "maxAgeAtMaturity": 75,
        "adiMonthlyRate": 0.0004,
        "active": true
      }
      """
