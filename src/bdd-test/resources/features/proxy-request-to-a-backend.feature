Feature: Test CXF route

  Scenario: Test GET request via CXF to a backend
    Given a document is available on the backend at "/details/1234"
    When I request "/details/1234" from the backend
    Then a successful response should be returned

  Scenario: Test POST request via CXF to a backend
    Given a running backend
    When I post data to "/uploadnew"
    Then my data should be successfully delivered to the backend