Feature: Get All Todos
  As a user, I want to get all todo tasks so that I can retrieve their details

  Background:
    Given the service is running
    And the following todos exist in the system:
      | id | title          | doneStatus | description |
      | 1  | scan paperwork | false      |             |
      | 2  | file paperwork | false      |             |

  # Normal Flow
  Scenario: Get all Todo tasks
    When I send a GET request to "todos"
    Then I should receive a response status code of 200
    And the response should contain a list of todos
    And the list should include todos with the following details:
      | id | title          | doneStatus |
      | 1  | scan paperwork | false      |
      | 2  | file paperwork | false      |

  # Alternate Flow
  Scenario: Get all Todo tasks that are not yet completed
    When I send a GET request to "todos" using filter "?doneStatus=false"
    Then I should receive a response status code of 200
    And the response should contain a list of todos
    And the list should include todos with the following details:
      | id | title          | doneStatus |
      | 1  | scan paperwork | false      |
      | 2  | file paperwork | false      |

  # Error Flow
  Scenario: Get all Todo tasks using an invalid endpoint
    When I send a GET request to "todos/all"
    Then I should receive a response status code of 404
    And the response should contain the error message "[Could not find an instance with todos/all]"
