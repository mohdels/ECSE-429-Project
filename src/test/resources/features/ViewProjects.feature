Feature: View All Projects
  As a user, I want to retrieve all projects so that I can view all current project instances

  Background:
    Given the service is running
    And there are existing projects in the system with the following details:
      | id | title        | active |
      | 1  | Project One  | true   |
      | 2  | Project Two  | false  |
      | 3  | Project Three| true   |

  # Normal Flow
  Scenario: Retrieve all projects successfully
    Given there are multiple projects in the system
    When I send a GET request to "/projects"
    Then I should receive a response status code of 200
    And the response should contain a list of all projects

  # Alternate Flow
  Scenario: Retrieve all projects when there are no projects
    Given there are no projects in the system
    When I send a GET request to "/projects"
    Then I should receive a response status code of 200
    And the response should contain an empty list

  # Error Flow
  Scenario: Retrieve all projects with an invalid URL
    When I send a GET request to "/projects/"
    Then I should receive a response status code of 404
    And the response should contain the error message "Not Found"
