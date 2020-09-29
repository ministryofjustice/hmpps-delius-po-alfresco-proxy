Feature: Virus Checks

  Scenario: attempt a virus upload to alfresco
    Given I have a virus compromised document "eicar.txt" to upload
    And ClamAV is healthy
    When I call "/uploadnew" to upload the document
    Then I should receive a response with status code "403"

  Scenario: attempt file upload to alfresco when ClamAV is offline
    Given I have a virus compromised document "no-virus.txt" to upload
    And ClamAV is offline
    When I call "/uploadnew" to upload the document
    Then I should receive a response with status code "503"