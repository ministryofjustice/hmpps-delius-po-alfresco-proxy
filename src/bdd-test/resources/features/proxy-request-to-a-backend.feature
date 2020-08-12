Feature: Test CXF route

  Scenario: search document by CRN from alfresco
    Given a document is available at "/search/1234"
    When I search document using "/search/1234" from alfresco
    Then a successful response should be returned

  Scenario: fetch document by document id from alfresco
    Given a document is available at "/fetch/1234"
    When I fetch document using "/fetch/1234" from alfresco
    Then a successful response should be returned

  Scenario: fetch document stream by document id from alfresco
    Given a document is available at "/fetchstream/1234"
    When I fetch document stream using "/fetchstream/1234" from alfresco
    Then a successful response should be returned

  Scenario: fetch and reserve document by doc id from alfresco
    Given a document is available at "/fetchandreserve/1234"
    When I fetch and reserve document using "/fetchandreserve/1234" from alfresco
    Then a successful response should be returned

  Scenario: upload a new document to alfresco
    Given I want to upload a new document to alfresco
    When I upload new document using "/uploadnew"
    Then the document should be successfully uploaded to alfresco

  Scenario: reserve document by document id from alfresco
    Given I want to reserve a document from alfresco
    When I reserve the document using "/reserve/1234" from alfresco
    Then a successful response should be returned

  Scenario: upload and release document to alfresco
    Given I want to upload and release document to alfresco
    When I upload a document using "/uploadandrelease/123"
    Then the document should be successfully uploaded and released

  Scenario: release document by document id from alfresco
    Given I want to release document from alfresco
    When I release document using "/release/1234" from alfresco
    Then a successful response should be returned

  Scenario: delete document by document id from alfresco
    Given I want to delete document from alfresco
    When I delete document using "/delete/1234" from alfresco
    Then a successful response should be returned

  Scenario: delete all documents by CRN id from alfresco
    Given I want to delete multiple documents from alfresco by CRN
    When I delete all documents for CRN using "/deleteall/1234" from alfresco
    Then a successful response should be returned

  Scenario: Test POST request via CXF to a backend
    Given a running backend
    When I post data to "/uploadnew"
    Then my data should be successfully delivered to the backend