Feature: Delete Existing Project
  As a user, I want to delete a project by ID so that it no longer appears in the project list

  Background:
    Given the service is running
    And the following projects exist in the system:
      | id | title         | active |
      | 1  | Office Work   | false  |

  # Normal Flow
  Scenario Outline: Delete a Project using its ID
    When I send a DELETE request to "projects/<ID>"
    Then I should receive a response status code of 200
    And the project at "projects/<ID>" should be deleted

    Examples:
      | ID |
      | 1  |

  # Alternate Flow
  Scenario Outline: Delete a Project after creating a project
    When I send a POST request to "projects" using title: "title" and active status: "false" then delete the project
    And I send a DELETE request to "projects/<ID>"
    Then We should receive a response status code of 200
    And the project at "projects/<ID>" should be deleted

    Examples:
      | ID | title       | active |
      | 5  | Project X   | true   |
      | 6  | Project Y   | false  |

  # Error Flow
  Scenario: Delete a Project with invalid ID
    When I send a DELETE request to "projects/-1"
    Then I should receive a response status code of 404
    And the response should contain the error message "[Could not find any instances with projects/-1]"
