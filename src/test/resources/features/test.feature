Feature: [Feature Name]
  [Brief description of the feature, explaining the user story or purpose]

  Background:
    Given [any initial setup required for all scenarios, e.g., "the service is running"]

  # Normal Flow
  Scenario Outline: [Description of the normal flow, e.g., "Successfully create an item with all required fields"]
    When I send a [HTTP method, e.g., POST] request to "<endpoint>" using [parameter 1]: "<value 1>" and [parameter 2]: "<value 2>"
    Then I should receive a response status code of <expected status code>
    And the response should contain [expected response elements, e.g., parameter 1]: "<value 1>" and [parameter 2]: "<value 2>"

    Examples:
      | parameter 1 | parameter 2      | expected status code |
      | Value 1     | Value for Param2 | 201                  |
      | Another 1   | Another 2        | 201                  |

  # Alternate Flow
  Scenario Outline: [Description of alternate flow, e.g., "Successfully create an item with only mandatory fields"]
    When I send a [HTTP method] request to "<endpoint>" using [parameter 1]: "<value 1>"
    Then I should receive a response status code of <expected status code>
    And the response should contain [expected elements, e.g., parameter 1]: "<value 1>"

    Examples:
      | parameter 1 | expected status code |
      | Value 1     | 201                  |
      | Another 1   | 201                  |

  # Error Flow
  Scenario: [Description of the error flow, e.g., "Fail to create an item without mandatory fields"]
    When I send a [HTTP method] request to "<endpoint>" using [mandatory parameter]: "<missing or invalid value>"
    Then I should receive a response status code of <error status code>
    And the response should contain the error message "<expected error message>"
