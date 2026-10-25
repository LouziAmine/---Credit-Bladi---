Feature: POST /api/v1/demands/{id}/advance: happy path

  Background:
    * url baseUrl

  Scenario: Classify a freshly created GE demand REAL, joining its Enterprise and opening Initiation

    Given path 'api', 'v1', 'investment-credit'
    And headers publicHeaders
    And request
      """
      {
        "companyName": "Karate Regression Test SARL",
        "enterpriseType": "GE",
        "email": "contact@karate-regression.ma",
        "phone": "0612345678",
        "amount": 1500000.00,
        "duration": 84,
        "annualRevenue": 8000000.00,
        "companyAgeYears": 5,
        "existingAnnualDebtService": 0,
        "netAnnualResult": 5000000.00,
        "guaranteesValue": 1000000.00
      }
      """
    When method POST
    Then status 200
    * def demandReference = response.demandReference
    * assert demandReference != null

    * def enterpriseName = 'Karate Regression Test SARL ' + java.util.UUID.randomUUID().toString().substring(0, 8)

    Given path 'api', 'v1', 'enterprises'
    And headers authHeaders
    And request
      """
      {
        "name": #(enterpriseName),
        "sector": "Textile"
      }
      """
    When method POST
    Then status 201

    Given path 'api', 'v1', 'enterprises'
    And headers authHeaders
    And param page = 1
    And param size = 50
    When method GET
    Then status 200
    * def enterpriseId = karate.jsonPath(response, "$.items[?(@.name=='" + enterpriseName + "')].id")[0]
    * assert enterpriseId != null

    Given path 'api', 'v1', 'demands'
    And headers authHeaders
    And param page = 1
    And param size = 10
    When method GET
    Then status 200
    * def demandId = karate.jsonPath(response, "$.items[?(@.reference=='" + demandReference + "')].id")[0]
    * assert demandId != null

    Given path 'api', 'v1', 'demands', demandId, 'eligible-assignees'
    And headers authHeaders
    And param direction = 'NEXT'
    When method GET
    Then status 200
    And assert response.length > 0
    * def initiationAssignee = response[0].username

    Given path 'api', 'v1', 'demands', demandId, 'classify'
    And headers authHeaders
    And request
      """
      {
        "classificationOutcome": "REAL",
        "enterpriseId": #(enterpriseId),
        "assigneeUsername": #(initiationAssignee),
        "note": "Client confirmé, dossier authentique"
      }
      """
    When method POST
    Then status 200
    And match response.enterprise.enterpriseId == enterpriseId
    And match response.rejected == false

    Given path 'api', 'v1', 'demands', demandId, 'pipeline'
    And headers authHeaders
    When method GET
    Then status 200
    And match response[0].status == 'DONE'
    And match response[1].status == 'CURRENT'

    Given path 'api', 'v1', 'demands', demandId, 'current-step'
    And headers authHeaders
    When method GET
    Then status 200
    And match response.assignedUsername == initiationAssignee

    Given path 'api', 'v1', 'demands'
    And headers authHeaders
    And param page = 1
    And param size = 10
    And param enterpriseId = enterpriseId
    When method GET
    Then status 200
    And match response.items[*].id contains demandId

  Scenario: Classify REAL with a client RIB keeps the Enterprise attached to the demand

    * def companyName = 'Karate RIB Test SARL ' + java.util.UUID.randomUUID().toString().substring(0, 8)
    Given path 'api', 'v1', 'investment-credit'
    And headers publicHeaders
    And request
      """
      {
        "companyName": #(companyName),
        "enterpriseType": "GE",
        "email": "contact@karate-rib.ma",
        "phone": "0612345678",
        "amount": 1500000.00,
        "duration": 84,
        "annualRevenue": 8000000.00,
        "companyAgeYears": 5,
        "existingAnnualDebtService": 0,
        "netAnnualResult": 5000000.00,
        "guaranteesValue": 1000000.00
      }
      """
    When method POST
    Then status 200
    * def demandReference = response.demandReference

    Given path 'api', 'v1', 'enterprises'
    And headers authHeaders
    And request { "name": #(companyName), "sector": "Textile" }
    When method POST
    Then status 201
    * def enterpriseId = response.id

    Given path 'api', 'v1', 'demands'
    And headers authHeaders
    And param page = 1
    And param size = 50
    When method GET
    Then status 200
    * def demandId = karate.jsonPath(response, "$.items[?(@.reference=='" + demandReference + "')].id")[0]

    Given path 'api', 'v1', 'demands', demandId, 'eligible-assignees'
    And headers authHeaders
    And param direction = 'NEXT'
    When method GET
    Then status 200
    * def initiationAssignee = response[0].username

    Given path 'api', 'v1', 'demands', demandId, 'classify'
    And headers authHeaders
    And request
      """
      {
        "classificationOutcome": "REAL",
        "enterpriseId": #(enterpriseId),
        "assigneeUsername": #(initiationAssignee),
        "clientRib": "230 780 0123456789012345 03"
      }
      """
    When method POST
    Then status 200

    Given path 'api', 'v1', 'demands', demandId
    And headers authHeaders
    When method GET
    Then status 200
    And match response.enterprise.enterpriseId == enterpriseId

    Given path 'api', 'v1', 'demands'
    And headers authHeaders
    And param page = 1
    And param size = 10
    And param enterpriseId = enterpriseId
    When method GET
    Then status 200
    And match response.items[*].id contains demandId
