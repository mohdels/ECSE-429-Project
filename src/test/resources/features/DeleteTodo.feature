Feature: Delete Existing Todo
  As a user, I want to be able to delete a todo task so that it no longer shows up in the todo list

  Background:
    Given the service is running
    And the following todos exist in the system:
      | id | title          | doneStatus | description |
      | 1  | scan paperwork | false      |             |
      | 2  | file paperwork | false      |             |

  # Normal Flow
  Scenario Outline: Delete a Todo task using its ID
    When I send a DELETE request to "todos/<ID>"
    Then I should receive a response status code of 200
    And the todo at "todos/<ID>" should be deleted

    Examples:
      | ID |
      | 1  |
      | 2  |

  # Alternate Flow
  Scenario Outline: Delete a Todo task after creating a task
    When I send a POST request to "todos" using title: "title" and description: "description"
    And I send a DELETE request to "todos/<ID>"
    Then I should receive a response status code of 200
    And the todo at "todos/<ID>" should be deleted

    Examples:
      | ID | title            | description |
      | 1  | scan paperwork   |             |
      | 2  | file paperwork   |             |

  # Error Flow
  Scenario: Delete a Todo task with invalid ID
    When I send a DELETE request to "todos/-1"
    Then I should receive a response status code of 404
    And the response should contain the error message "[Could not find any instances with todos/-1]"