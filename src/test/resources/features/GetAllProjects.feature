Feature: Get All Projects
  As a user, I want to get all project tasks so that I can retrieve their details

  Background:
    Given the service is running
    And the following projects exist in the system:
      | id | title       | active |
      | 1  | Office Work | false  |

  # Normal Flow
  Scenario: Get all Project tasks
    When We send a GET request to "projects"
    Then We should receive a response status code of 200
    And the response should contain a list of projects
    And the list should include projects with the following details:
      | id | title       | active |
      | 1  | Office Work | false  |

  # Alternate Flow
  Scenario: Get all Project tasks that are active
    When I send a GET request to "projects" using filter "?active=false" to get projects
    Then We should receive a response status code of 200
    And the response should contain a list of projects
    And the list should include projects with the following details:
      | id | title       | active |
      | 1  | Office Work | false  |

  # Error Flow
  Scenario: Get all Project tasks using an invalid endpoint
    When We send a GET request to "projects/all"
    Then We should receive a response status code of 404
    And the response should contain the following error message: "Could not find an instance with projects/all"
