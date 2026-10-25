Feature: GET /api/v1/activity-sectors: public happy path

  Background:
    * url baseUrl

  Scenario: Anyone (even unauthenticated) can list activity sectors, and a newly created one shows up
    * def sectorName = 'Karate List ' + java.util.UUID.randomUUID().toString().substring(0, 8)

    Given path 'api', 'v1', 'activity-sectors'
    And headers authHeaders
    And request { name: #(sectorName) }
    When method POST
    Then status 201

    Given path 'api', 'v1', 'activity-sectors'
    And headers publicHeaders
    When method GET
    Then status 200
    And match response[*].name contains sectorName
