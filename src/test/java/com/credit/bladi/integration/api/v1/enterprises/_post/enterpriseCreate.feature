Feature: POST /api/v1/enterprises: happy path

  Background:
    * url baseUrl

  Scenario: Create an Enterprise and get the created resource back in the response body
    * def enterpriseName = 'Karate Create Test SARL ' + java.util.UUID.randomUUID().toString().substring(0, 8)

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
    And match responseHeaders['Location'][0] == '/api/v1/enterprises/' + response.id
    And match response ==
      """
      {
        id: '#uuid',
        name: '#(enterpriseName)',
        sector: 'Textile',
        registrationNumber: '#null',
        createdAt: '#string'
      }
      """
