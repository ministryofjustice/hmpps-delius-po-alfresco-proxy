Feature: Virus Checks

  Scenario: attempt a virus upload to alfresco
    Given I have a virus compromised document "eicar.txt" to upload
    When I call "/uploadnew" to upload the document
    Then I should receive a response with status code "403"