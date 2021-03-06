Feature: The application's health status

  Scenario: The API in a stable state
    Given the Alfresco Proxy API is running
    And alfresco is healthy
    And ClamAV is healthy
    When I request the health of the Alfresco Proxy API
    Then a response stating that the service is "stable" is returned

  Scenario: The API in an unstable state when both dependencies are unhealthy
    Given the Alfresco Proxy API is running
    But alfresco is offline
    And ClamAV is offline
    When I request the health of the Alfresco Proxy API
    Then a response stating that the service is "unstable" is returned

  Scenario: The API in an unstable state when Alfresco is unhealthy
    Given the Alfresco Proxy API is running
    But alfresco is offline
    And ClamAV is healthy
    When I request the health of the Alfresco Proxy API
    Then a response stating that the service is "unstable" is returned

  Scenario: The API in an unstable state when ClamAV is unhealthy
    Given the Alfresco Proxy API is running
    And alfresco is healthy
    But ClamAV is offline
    When I request the health of the Alfresco Proxy API
    Then a response stating that the service is "unstable" is returned