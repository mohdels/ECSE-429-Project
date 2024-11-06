Feature: Update Existing Project
  As a user, I want to update an existing project so that I can modify its details.

  Background:
    Given the service is running
    And the following projects exist in the system:
      | id | title       | description |
      | 1  | Office Work |             |

  # Normal Flow
  Scenario Outline: Update a Project's title and description successfully using PUT
    When I send a PUT request to "projects/<ID>" using title: "<title>" and description: "<description>" to update project
    Then We should receive a response status code of 200
    And the response should have a project with title: "<title>" and description: "<description>"

    Examples:
      | ID | title       | description        |
      | 1  | Project 1   | description - PUT  |

  # Alternate Flow
  Scenario Outline: Update a Project's title and description successfully using POST
    When I send a POST request to "projects/<ID>" using title: "<title>" and description: "<description>" to update project
    Then We should receive a response status code of 200
    And the response should have a project with title: "<title>" and description: "<description>"

    Examples:
      | ID | title              | description         |
      | 1  | Project 1 - Post   | description - POST  |

  # Error Flow
  Scenario Outline: Update a Project's title and description with invalid ID using PUT
    When I send a PUT request to "projects/-1" using title: "<title>" and description: "<description>" to update project
    Then We should receive a response status code of 404
    And the response should contain the following error message: "Invalid GUID for -1 entity project"

    Examples:
      | ID | title              | description          |
      | 1  | Project 1 - Error  | description - Error  |
