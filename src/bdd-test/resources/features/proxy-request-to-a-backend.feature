Feature: Test CXF route

  Scenario: Forward request via CXF to a backend
    Given a running backend
    When I request data from the backend
    Then a response should be returned
