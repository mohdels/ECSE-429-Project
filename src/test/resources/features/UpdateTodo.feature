Feature: Update Existing Todo
  As a user, I want to update an existing todo task so that I can modify its details

  Background:
    Given the service is running
    And the following todos exist in the system:
      | id | title          | doneStatus | description |
      | 1  | scan paperwork | false      |             |
      | 2  | file paperwork | false      |             |

  # Normal Flow
  Scenario Outline: Update a Todo task's description successfully using PUT
    When I send a PUT request to "todos/<ID>" using title: "title" and description: "description"
    Then I should receive a response status code of 200
    And the response should have a todo task with title: "title" and description: "description"

    Examples:
      | ID | title    | description                |
      | 1  | Todo 1   | New description of Todo 1  |
      | 2  | Todo 2   | New description of Todo 2  |

  # Alternate Flow
  Scenario Outline: Update a Todo task's description successfully using POST
    When I send a POST request to "todos/<ID>" using title: "title" and description: "description"
    Then I should receive a response status code of 200
    And the response should have a todo task with title: "title" and description: "description"

    Examples:
      | ID | title        | description                    |
      | 1  | Todo 1 POST  | New description of Todo 1 POST |
      | 2  | Todo 2 POST  | New description of Todo 2 POST |

  # Error Flow
  Scenario Outline: Update a Todo task's description with invalid ID using PUT
    When I send a PUT request to "todos/-1" using title: "title" and description: "description"
    Then I should receive a response status code of 404
    And the response should contain the error message "[Invalid GUID for -1 entity todo]"

    Examples:
      | title    | description                |
      | Todo 1   | New description of Todo 1  |
      | Todo 2   | New description of Todo 2  |