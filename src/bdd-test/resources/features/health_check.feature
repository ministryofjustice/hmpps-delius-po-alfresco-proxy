Feature: The application's health status

  Scenario: The API in a stable state
    Given the Alfresco Proxy API is running
    And alfresco is healthy
    When I request the health of the Alfresco Proxy API
    Then a response stating that the service is "stable" is returned

  Scenario: The API in an unstable state
    Given the Alfresco Proxy API is running
    But alfresco is not healthy
    When I request the health of the Alfresco Proxy API
    Then a response stating that the service is "unstable" is returned