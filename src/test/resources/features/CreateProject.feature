Feature: Create a New Project
  As a user, I want to create a new project without providing an ID so that I can quickly add a new project with the required details only

  Background:
    Given the service is running

  # Normal Flow
  Scenario Outline: Create a new project successfully with title and active status
    When I send a POST request to "/projects" using title: "<title>" and active status: "<active>"
    Then I should receive a response status code of 201
    And the response should contain a project with title: "<title>" and active status: "<active>"

    Examples:
      | title          | active |
      | New Project    | true   |
      | Another Project| false  |

  # Alternate Flow
  Scenario: Create a new project with no fields provided
    When I send a POST request to "/projects" with no fields in the body
    Then I should receive a response status code of 201
    And the response should contain a project with default values for all fields

  # Error Flow
  Scenario: Create a new project with invalid data type
    When I send a POST request to "/projects" using an active status: "notABoolean"
    Then I should receive a response status code of 400
    And the response should contain the error message "[Failed Validation: active should be BOOLEAN]"
