Feature: POST /api/v1/mortgage-credit: happy path

  Background:
    * url baseUrl

  Scenario: Simulate SALA profile returns 200 with full response structure
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
    And match response contains deep
      """
      {
        "amount": 500000.0,
        "duration": 240,
        "cspCode": "SALA",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "registrationFees": 24000.00,
        "landConservationFees": 9000.00,
        "notaryFees": 6000.00,
        "processingFees": 3000.00,
        "totalFees": 42000.00
      }
      """
    And match response.conventional contains deep
      """
      {
        "rateMin": 5.0,
        "rateMax": 5.5,
        "monthlyPaymentMin": 3299.78,
        "monthlyPaymentMax": 3439.44,
        "totalInterestMin": 291947.20,
        "totalInterestMax": 325465.60,
        "totalCostMin": 791947.20,
        "totalCostMax": 825465.60
      }
      """
    And match response.amortizationSchedule == '#[240]'
    And match response.amortizationSchedule[0].installmentNumber == 1
    And match response.amortizationSchedule[0].interest == '#number'
    And match response.amortizationSchedule[239].installmentNumber == 240
    And match response.amortizationSchedule[239].endingBalance == 0.0

  Scenario: Simulate FONC profile (fonctionnaire, max duration 300 months) returns 200
    Given path 'api', 'v1', 'mortgage-credit'
    And headers publicHeaders
    And request
      """
      {
        "applicantName": "Karim Benali",
        "email": "karim.benali@example.ma",
        "phone": "0612345678",
        "amount": 1000000.00,
        "duration": 300,
        "cspCode": "FONC",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "propertyValue": 1200000.00,
        "monthlyIncome": 30000.00,
        "age": 40
      }
      """
    When method POST
    Then status 200
    And match response contains deep
      """
      {
        "amount": 1000000.0,
        "duration": 300,
        "cspCode": "FONC",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "registrationFees": 48000.00,
        "landConservationFees": 18000.00,
        "notaryFees": 12000.00,
        "processingFees": 3000.00,
        "totalFees": 81000.00
      }
      """
    And match response.conventional contains deep
      """
      {
        "rateMin": 4.8,
        "rateMax": 5.0,
        "monthlyPaymentMin": 5729.97,
        "monthlyPaymentMax": 5845.90,
        "totalInterestMin": 718991.00,
        "totalInterestMax": 753770.00,
        "totalCostMin": 1718991.00,
        "totalCostMax": 1753770.00
      }
      """

  Scenario: Simulate RETR profile (retraite) returns 200
    Given path 'api', 'v1', 'mortgage-credit'
    And headers publicHeaders
    And request
      """
      {
        "applicantName": "Karim Benali",
        "email": "karim.benali@example.ma",
        "phone": "0612345678",
        "amount": 300000.00,
        "duration": 120,
        "cspCode": "RETR",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "propertyValue": 400000.00,
        "monthlyIncome": 15000.00,
        "age": 60
      }
      """
    When method POST
    Then status 200
    And match response contains deep
      """
      {
        "amount": 300000.0,
        "duration": 120,
        "cspCode": "RETR",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "registrationFees": 16000.00,
        "landConservationFees": 6000.00,
        "notaryFees": 4000.00,
        "processingFees": 3000.00,
        "totalFees": 29000.00
      }
      """
    And match response.conventional contains deep
      """
      {
        "rateMin": 4.6,
        "rateMax": 4.6,
        "monthlyPaymentMin": 3123.63,
        "monthlyPaymentMax": 3123.63,
        "totalInterestMin": 74835.60,
        "totalInterestMax": 74835.60,
        "totalCostMin": 374835.60,
        "totalCostMax": 374835.60
      }
      """

  Scenario: Simulate PROF profile (professionnel) returns 200
    Given path 'api', 'v1', 'mortgage-credit'
    And headers publicHeaders
    And request
      """
      {
        "applicantName": "Karim Benali",
        "email": "karim.benali@example.ma",
        "phone": "0612345678",
        "amount": 700000.00,
        "duration": 180,
        "cspCode": "PROF",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "propertyValue": 900000.00,
        "monthlyIncome": 30000.00,
        "age": 35
      }
      """
    When method POST
    Then status 200
    And match response contains deep
      """
      {
        "amount": 700000.0,
        "duration": 180,
        "cspCode": "PROF",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "registrationFees": 36000.00,
        "landConservationFees": 13500.00,
        "notaryFees": 9000.00,
        "processingFees": 3000.00,
        "totalFees": 61500.00
      }
      """
    And match response.conventional contains deep
      """
      {
        "rateMin": 5.5,
        "rateMax": 6.0,
        "monthlyPaymentMin": 5719.58,
        "monthlyPaymentMax": 5907.00,
        "totalInterestMin": 329524.40,
        "totalInterestMax": 363260.00,
        "totalCostMin": 1029524.40,
        "totalCostMax": 1063260.00
      }
      """

  Scenario: Simulate INDE profile (indépendant) returns 200
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
    And match response contains deep
      """
      {
        "amount": 400000.0,
        "duration": 120,
        "cspCode": "INDE",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "registrationFees": 20000.00,
        "landConservationFees": 7500.00,
        "notaryFees": 5000.00,
        "processingFees": 3000.00,
        "totalFees": 35500.00
      }
      """
    And match response.conventional contains deep
      """
      {
        "rateMin": 6.0,
        "rateMax": 6.5,
        "monthlyPaymentMin": 4440.82,
        "monthlyPaymentMax": 4541.92,
        "totalInterestMin": 132898.40,
        "totalInterestMax": 145030.40,
        "totalCostMin": 532898.40,
        "totalCostMax": 545030.40
      }
      """

  Scenario: Simulate as MRE non-resident (Moroccan abroad) returns 200
    Given path 'api', 'v1', 'mortgage-credit'
    And headers publicHeaders
    And request
      """
      {
        "applicantName": "Karim Benali",
        "email": "karim.benali@example.ma",
        "phone": "0612345678",
        "amount": 800000.00,
        "duration": 240,
        "cspCode": "SALA",
        "nationalityCode": "FR",
        "moroccanResident": false,
        "propertyValue": 1000000.00,
        "monthlyIncome": 30000.00,
        "age": 35
      }
      """
    When method POST
    Then status 200
    And match response contains deep
      """
      {
        "amount": 800000.0,
        "duration": 240,
        "cspCode": "SALA",
        "nationalityCode": "FR",
        "moroccanResident": false,
        "registrationFees": 40000.00,
        "landConservationFees": 15000.00,
        "notaryFees": 10000.00,
        "processingFees": 3000.00,
        "totalFees": 68000.00
      }
      """
    And match response.conventional contains deep
      """
      {
        "rateMin": 5.0,
        "rateMax": 5.5,
        "monthlyPaymentMin": 5279.65,
        "monthlyPaymentMax": 5503.10,
        "totalInterestMin": 467116.00,
        "totalInterestMax": 520744.00,
        "totalCostMin": 1267116.00,
        "totalCostMax": 1320744.00
      }
      """

  Scenario: Simulate with minimum amount (70000 MAD) returns 200
    Given path 'api', 'v1', 'mortgage-credit'
    And headers publicHeaders
    And request
      """
      {
        "applicantName": "Karim Benali",
        "email": "karim.benali@example.ma",
        "phone": "0612345678",
        "amount": 70000.00,
        "duration": 12,
        "cspCode": "SALA",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "propertyValue": 100000.00,
        "monthlyIncome": 20000.00,
        "age": 30
      }
      """
    When method POST
    Then status 200
    And match response contains deep
      """
      {
        "amount": 70000.0,
        "duration": 12,
        "cspCode": "SALA",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "registrationFees": 4000.00,
        "landConservationFees": 1500.00,
        "notaryFees": 1000.00,
        "processingFees": 3000.00,
        "totalFees": 9500.00
      }
      """
    And match response.conventional contains deep
      """
      {
        "rateMin": 5.0,
        "rateMax": 5.5,
        "monthlyPaymentMin": 5992.52,
        "monthlyPaymentMax": 6008.57,
        "totalInterestMin": 1910.24,
        "totalInterestMax": 2102.84,
        "totalCostMin": 71910.24,
        "totalCostMax": 72102.84
      }
      """

  Scenario: Simulate with maximum amount (30000000 MAD) returns 200
    Given path 'api', 'v1', 'mortgage-credit'
    And headers publicHeaders
    And request
      """
      {
        "applicantName": "Karim Benali",
        "email": "karim.benali@example.ma",
        "phone": "0612345678",
        "amount": 30000000.00,
        "duration": 240,
        "cspCode": "SALA",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "propertyValue": 34000000.00,
        "monthlyIncome": 600000.00,
        "age": 40
      }
      """
    When method POST
    Then status 200
    And match response contains deep
      """
      {
        "amount": 30000000.0,
        "duration": 240,
        "cspCode": "SALA",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "registrationFees": 1360000.00,
        "landConservationFees": 510000.00,
        "notaryFees": 340000.00,
        "processingFees": 6000.00,
        "totalFees": 2216000.00
      }
      """
    And match response.conventional contains deep
      """
      {
        "rateMin": 5.0,
        "rateMax": 5.5,
        "monthlyPaymentMin": 197986.72,
        "monthlyPaymentMax": 206366.19,
        "totalInterestMin": 17516812.80,
        "totalInterestMax": 19527885.60,
        "totalCostMin": 47516812.80,
        "totalCostMax": 49527885.60
      }
      """

  Scenario: Simulate RETR at maximum allowed duration (240 months) returns 200
    Given path 'api', 'v1', 'mortgage-credit'
    And headers publicHeaders
    And request
      """
      {
        "applicantName": "Karim Benali",
        "email": "karim.benali@example.ma",
        "phone": "0612345678",
        "amount": 300000.00,
        "duration": 240,
        "cspCode": "RETR",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "propertyValue": 400000.00,
        "monthlyIncome": 10000.00,
        "age": 50
      }
      """
    When method POST
    Then status 200
    And match response contains deep
      """
      {
        "amount": 300000.0,
        "duration": 240,
        "cspCode": "RETR",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "registrationFees": 16000.00,
        "landConservationFees": 6000.00,
        "notaryFees": 4000.00,
        "processingFees": 3000.00,
        "totalFees": 29000.00
      }
      """
    And match response.conventional contains deep
      """
      {
        "rateMin": 4.6,
        "rateMax": 4.6,
        "monthlyPaymentMin": 1914.18,
        "monthlyPaymentMax": 1914.18,
        "totalInterestMin": 159403.20,
        "totalInterestMax": 159403.20,
        "totalCostMin": 459403.20,
        "totalCostMax": 459403.20
      }
      """

  Scenario: Simulate FONC at minimum allowed duration (12 months) returns 200
    Given path 'api', 'v1', 'mortgage-credit'
    And headers publicHeaders
    And request
      """
      {
        "applicantName": "Karim Benali",
        "email": "karim.benali@example.ma",
        "phone": "0612345678",
        "amount": 300000.00,
        "duration": 12,
        "cspCode": "FONC",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "propertyValue": 400000.00,
        "monthlyIncome": 100000.00,
        "age": 30
      }
      """
    When method POST
    Then status 200
    And match response contains
      """
      {
        "amount": 300000.0,
        "duration": 12,
        "cspCode": "FONC",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "registrationFees": 16000.00,
        "landConservationFees": 6000.00,
        "notaryFees": 4000.00,
        "processingFees": 3000.00,
        "totalFees": 29000.00
      }
      """
    And match response.conventional contains deep
      """
      {
        "rateMin": 4.8,
        "rateMax": 5.0,
        "monthlyPaymentMin": 25654.76,
        "monthlyPaymentMax": 25682.24,
        "totalInterestMin": 7857.12,
        "totalInterestMax": 8186.88,
        "totalCostMin": 307857.12,
        "totalCostMax": 308186.88
      }
      """

  Scenario: Simulate FONC near the debt-ratio ceiling is ACCEPTED at the applicable rate (4.8%),
            even though it would exceed the ceiling at the CSP's rateMax (5.0%): regression test
            for the debt-ratio gate using applicableRate instead of the worst-case rate
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
        "cspCode": "FONC",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "propertyValue": 600000.00,
        "monthlyIncome": 8700.00,
        "age": 40
      }
      """
    When method POST
    Then status 200
    And match response contains deep
      """
      {
        "applicableRate": 4.8,
        "taeg": 5.74
      }
      """
    And match response.conventional contains deep
      """
      {
        "monthlyPayment": 3244.79,
        "adiMonthlyPremium": 200.00,
        "allInMonthlyPayment": 3444.79,
        "totalInterest": 278749.60,
        "totalAdiCost": 48000.00,
        "totalCost": 778749.60,
        "allInMonthlyPaymentMax": 3499.78
      }
      """

  Scenario: Simulate INDE at maximum allowed duration (180 months) returns 200
    Given path 'api', 'v1', 'mortgage-credit'
    And headers publicHeaders
    And request
      """
      {
        "applicantName": "Karim Benali",
        "email": "karim.benali@example.ma",
        "phone": "0612345678",
        "amount": 400000.00,
        "duration": 180,
        "cspCode": "INDE",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "propertyValue": 500000.00,
        "monthlyIncome": 20000.00,
        "age": 35
      }
      """
    When method POST
    Then status 200
    And match response contains deep
      """
      {
        "amount": 400000.0,
        "duration": 180,
        "cspCode": "INDE",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "registrationFees": 20000.00,
        "landConservationFees": 7500.00,
        "notaryFees": 5000.00,
        "processingFees": 3000.00,
        "totalFees": 35500.00
      }
      """
    And match response.conventional contains deep
      """
      {
        "rateMin": 6.0,
        "rateMax": 6.5,
        "monthlyPaymentMin": 3375.43,
        "monthlyPaymentMax": 3484.43,
        "totalInterestMin": 207577.40,
        "totalInterestMax": 227197.40,
        "totalCostMin": 607577.40,
        "totalCostMax": 627197.40
      }
      """

  Scenario: Simulate SALA with coBorrower=true doubles the ADI premium (BAM regulation) returns 200
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
        "age": 35,
        "coBorrower": true
      }
      """
    When method POST
    Then status 200
    And match response contains deep
      """
      {
        "applicableRate": 5.25
      }
      """
    And match response.conventional contains deep
      """
      {
        "monthlyPayment": 3369.22,
        "adiMonthlyPremium": 400.00,
        "allInMonthlyPayment": 3769.22
      }
      """

  Scenario: Simulate Mourabaha Immobilier (240k over 60 months) matches the real bank screenshot
    Given path 'api', 'v1', 'mortgage-credit'
    And headers publicHeaders
    And request
      """
      {
        "applicantName": "Karim Benali",
        "email": "karim.benali@example.ma",
        "phone": "0612345678",
        "amount": 240000.00,
        "duration": 60,
        "productType": "MOURABAHA",
        "cspCode": "SALA",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "propertyValue": 300000.00,
        "monthlyIncome": 20000.00,
        "age": 40
      }
      """
    When method POST
    Then status 200
    And match response contains deep
      """
      {
        "amount": 240000.0,
        "duration": 60,
        "productType": "MOURABAHA",
        "applicableRate": 5.25
      }
      """
    And match response.mourabaha contains deep
      """
      {
        "marginHt": 33534.55,
        "vatOnMargin": 3353.45,
        "marginTtc": 36888.00,
        "monthlyPaymentTtc": 4614.80,
        "totalCostTtc": 276888.00
      }
      """
    And match response.amortizationSchedule == '#[60]'
    And match response.amortizationSchedule[0] contains { installmentNumber: 1, beginningBalance: 240000.0 }
    And match response.amortizationSchedule[59] contains { installmentNumber: 60, endingBalance: 0.0 }

  Scenario: Simulate Mourabaha Immobilier (360k over 120 months) matches the real bank screenshot
    Given path 'api', 'v1', 'mortgage-credit'
    And headers publicHeaders
    And request
      """
      {
        "applicantName": "Karim Benali",
        "email": "karim.benali@example.ma",
        "phone": "0612345678",
        "amount": 360000.00,
        "duration": 120,
        "productType": "MOURABAHA",
        "cspCode": "SALA",
        "nationalityCode": "MA",
        "moroccanResident": true,
        "propertyValue": 420000.00,
        "monthlyIncome": 20000.00,
        "age": 40
      }
      """
    When method POST
    Then status 200
    And match response contains deep
      """
      {
        "amount": 360000.0,
        "duration": 120,
        "productType": "MOURABAHA",
        "applicableRate": 5.25
      }
      """
    And match response.mourabaha contains deep
      """
      {
        "marginHt": 104310.55,
        "vatOnMargin": 10431.05,
        "marginTtc": 114741.60,
        "monthlyPaymentTtc": 3956.18,
        "totalCostTtc": 474741.60
      }
      """
    And match response.amortizationSchedule == '#[120]'
    And match response.amortizationSchedule[0] contains { installmentNumber: 1, beginningBalance: 360000.0 }
    And match response.amortizationSchedule[119] contains { installmentNumber: 120, endingBalance: 0.0 }
