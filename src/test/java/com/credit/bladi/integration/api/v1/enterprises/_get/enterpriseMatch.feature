Feature: GET /api/v1/enterprises/match: happy path

  Background:
    * url baseUrl

  Scenario: Find an existing Enterprise by a differently-cased/punctuated name variant
    * def baseName = 'Karate Match Test ' + java.util.UUID.randomUUID().toString().substring(0, 8)

    Given path 'api', 'v1', 'enterprises'
    And headers authHeaders
    And request
      """
      {
        "name": #(baseName),
        "sector": "Textile"
      }
      """
    When method POST
    Then status 201
    * def enterpriseId = response.id

    * def variantName = baseName.toUpperCase().replace(' ', '--')

    Given path 'api', 'v1', 'enterprises', 'match'
    And headers authHeaders
    And param name = variantName
    When method GET
    Then status 200
    And match response.matched == true
    And match response.enterprise.id == enterpriseId

  Scenario: No existing Enterprise matches an unrelated name
    Given path 'api', 'v1', 'enterprises', 'match'
    And headers authHeaders
    And param name = 'Totally Unrelated Company ' + java.util.UUID.randomUUID().toString()
    When method GET
    Then status 200
    And match response == { matched: false, enterprise: null }
