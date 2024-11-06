Feature: Get a Specific Project
  As a user, I want to retrieve a specific project so that I can view its details.

  Background:
    Given the service is running
    And the following projects exist in the system:
      | id | title       | active |
      | 1  | Office Work | false  |

  # Normal Flow
  Scenario Outline: Get a Project using its ID
    When We send a GET request to "projects/<id>"
    Then We should receive a response status code of 200
    And the response should contain a project with ID "<id>" and title "<title>"

    Examples:
      | id | title         |
      | 1  | Office Work   |

  # Alternate Flow
  Scenario Outline: Get a Project using its title
    When We send a GET request to "projects" with title parameter "<title>" to get a project
    Then We should receive a response status code of 200
    And the response should contain a project with ID "<id>" and title "<title>"

    Examples:
      | id | title         |
      | 1  | Office Work   |

  # Error Flow
  Scenario Outline: Get a Project with invalid ID
    When I send a GET request to "projects/<invalid_id>"
    Then I should receive a response status code of 404
    And the response should contain the error message "[Could not find an instance with projects/<invalid_id>]"

    Examples:
      | invalid_id |
      | 9999       |
      | abc123     |
