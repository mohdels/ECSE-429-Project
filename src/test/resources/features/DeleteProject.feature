Feature: Delete a Project
  As a user, I want to delete a project by ID so that I can remove projects that are no longer relevant or necessary

  Background:
    Given the service is running
    And there are existing projects in the system with the following details:
      | id | title       | active |
      | 2  | Project A   | true   |
      | 3  | Project B   | false  |
      | 4  | Project C   | true   |

  # Normal Flow
  Scenario Outline: Delete a specific project by ID
    Given a project exists with ID <id>
    When I send a DELETE request to "/projects/<id>"
    Then I should receive a response status code of 200
    And the project with ID <id> should be deleted

    Examples:
      | id |
      | 2  |
      | 4  |

  # Alternate Flow
  Scenario Outline: Delete a project that has associated tasks
    Given a project with ID <projectId> exists and has the following associated tasks:
      | taskId |
      | 1      |
      | 2      |
    When I send a DELETE request to "/projects/<projectId>"
    Then I should receive a response status code of 200

    Examples:
      | projectId |
      | 1         |

  # Error Flow
  Scenario Outline: Attempt to delete an already deleted project
    Given a project with ID <deletedId> has already been deleted
    When I send a DELETE request to "/projects/<deletedId>"
    Then I should receive a response status code of 404
    And the response should contain the error message "[Could not find any instances with projects/<deletedId>]"

    Examples:
      | deletedId |
      | 3         |
      | 5         |
