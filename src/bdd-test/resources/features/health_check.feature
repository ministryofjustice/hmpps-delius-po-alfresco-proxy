Feature: The application's health status

  Scenario: The application is in a healthy state
    Given the application is running
    And alfresco is healthy
    When I request it's health
    Then a JSON response per "expectations/actuator_health.json" should be returned