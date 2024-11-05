Feature: Update a Specific Project
  As a user, I want to update a specific project by ID so that I can modify project details to keep the information accurate and current

  Background:
    Given the service is running
    And there are existing projects in the system with the following details:
      | id | title         | active |
      | 1  | Old Project   | true   |
      | 2  | Another Title | false  |
      | 3  | Partial Update| true   |

  # Normal Flow
  Scenario Outline: Update a specific project by ID
    Given a project exists with ID <id> and title "<oldTitle>"
    When I send a PUT request to "/projects/<id>" with a new title "<newTitle>"
    Then I should receive a response status code of 200
    And the project with ID <id> should have the updated title "<newTitle>"

    Examples:
      | id | oldTitle       | newTitle        |
      | 1  | Old Project    | Updated Project |
      | 2  | Another Title  | New Name        |

  # Alternate Flow
  Scenario Outline: Update a project with a partial update using POST
    Given a project exists with ID <id> and a current title "<currentTitle>"
    And the project has active status "<activeStatus>"
    When I send a POST request to "/projects/<id>" with an updated title "<updatedTitle>"
    Then I should receive a response status code of 200
    And the project with ID <id> should have the updated title "<updatedTitle>"
    And the active status should remain "<activeStatus>"

    Examples:
      | id | currentTitle    | updatedTitle   | activeStatus |
      | 3  | Partial Update  | New Partial    | true         |
      | 1  | Old Project     | Fresh Update   | true         |

  # Error Flow
  Scenario Outline: Update a project with an invalid ID
    When I send a PUT request to "/projects/<invalidId>" with new project details
    Then I should receive a response status code of 404
    And the response should contain the error message "[Invalid GUID for <invalidId> entity project]"

    Examples:
      | invalidId |
      | 99        |
      | 100       |
