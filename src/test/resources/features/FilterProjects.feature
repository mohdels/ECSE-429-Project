Feature: Filter Projects by Specific Attributes
  As a user, I want to filter projects by specific attributes so that I can find projects that meet specific criteria

  Background:
    Given the service is running
    And there are existing projects in the system with the following details:
      | id | title        | completed |
      | 1  | ProjectA     | true      |
      | 2  | ProjectB     | false     |
      | 3  | ProjectC     | true      |

  # Normal Flow
  Scenario Outline: Filter projects by title and completed status
    Given there are multiple projects with varying titles and statuses
    When I send a GET request to "/projects?title=<title>&completed=<completed>"
    Then I should receive a response status code of 200
    And the response should contain projects with title "<title>" and active status "<active>"

    Examples:
      | title    | completed |
      | ProjectA | true      |
      | ProjectB | false     |

  # Alternate Flow
  Scenario Outline: Filter projects with a non-matching title or status
    Given there are no projects with the title "<title>" and completed status "<completed>"
    When I send a GET request to "/projects?title=<title>&completed=<completed>"
    Then I should receive a response status code of 200
    And the response should contain an empty list

    Examples:
      | title              | completed |
      | NonExistentProject | false     |
      | UnknownProject     | true      |

  # Error Flow
  Scenario Outline: Filter projects with an invalid query parameter
    When I send a GET request to "/projects?<invalidParam>=value"
    Then I should receive a response status code of 400

    Examples:
      | invalidParam |
      | unknownParam |
      | wrongParam   |
