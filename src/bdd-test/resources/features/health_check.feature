Feature: The application's health status

#  Scenario: The API in a stable state
#    Given the PO Alfresco Proxy API is running
#    And alfresco is healthy
#    When I request the health of the PO Alfresco Proxy API
#    Then a stable response per the JSON "expectations/stable_health.json" is returned
#
#  Scenario: The API in an unstable state
#    Given the PO Alfresco Proxy API is running
#    But alfresco is not healthy
#    When I request the health of the PO Alfresco Proxy API
#    Then an unstable response per the JSON "expectations/unstable_health.json" is returned