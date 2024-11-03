Feature: Create New Todo
  As a user, I want to create a new todo task so that I can have the task saved in the list of todos for future reference

  Background:
    Given the service is running

    # Normal Flow
  Scenario Outline: Create a Todo task successfully using a title and description
    When I send a POST request to "todos" using title: "title" and description: "description"
    Then I should receive a response status code of 201
    And the response should have a todo task with title: "title" and description: "description"

    Examples:
      | title    | description            |
      | ToDo 1   | Description of ToDo 1  |
      | ToDo 2   | Description of ToDo 2  |

    # Alternate Flow
  Scenario Outline: Create a Todo task successfully using only a title
    When I send a POST request to "todos" using title: "title" and description: "description"
    Then I should receive a response status code of 201 - alternate flow
    And the response should have a todo task with title: "title" and description: "description"

    Examples:
      | title   | description  |
      | ToDo 1  |              |
      | ToDo 2  |              |

    # Error Flow
  Scenario: Create a Todo task without a title
    When I send a POST request to "todos" using title: "" and description: "description" - error flow
    Then I should receive a response status code of 400 - error flow
    And the response should contain the error message "[Failed Validation: title : can not be empty]"