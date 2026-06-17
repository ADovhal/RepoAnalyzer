# RepoAnalyzer

A Spring Boot REST API that acts as a proxy to the GitHub API.
It lists all non-fork repositories for a given GitHub user,
including branch names and their last commit SHA.

## Tech Stack

- Java 25
- Spring Boot 4.1
- Gradle (Kotlin DSL)
- WireMock (integration tests)

## Prerequisites

- Java 25 installed
- Internet access (calls api.github.com)

## How to run

./gradlew bootRun

The application starts on port 8080 by default.

## API

### Get repositories for a GitHub user

GET /api/info/{username}

Returns all non-fork repositories with branches and last commit SHA for each branch.

**Success response 200 OK:**

```json
[
  {
    "repoName": "Hello-World",
    "ownerLogin": "octocat",
    "branches": [
                {
                  "name": "main",
                  "lastCommitSha": "abc123"
                }
                ]
  }
]
```

**User not found 404:**
```json
{
    "status": 404,
    "message": "User octocat not found."
}
```
## Running tests

./gradlew test

Integration tests use WireMock to emulate GitHub API responses.
No real HTTP calls to GitHub are made during testing.

## GitHub API endpoints used internally

To collect repository data, the application makes the following calls:

1. Fetch all repositories for a user:
   GET https://api.github.com/users/{username}/repos

2. Fetch branches for each non-fork repository:
   GET https://api.github.com/repos/{owner}/{repo}/branches

## References

- GitHub REST API v3: https://developer.github.com/v3
- WireMock Spring Boot integration: https://wiremock.org/docs/spring-boot