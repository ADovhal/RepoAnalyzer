package com.ghpars.repoanalyzer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class GithubClient {

    private final RestClient restClient;

    public GithubClient(@Value("${github.api.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<GithubRepo> fetchRepos(String username) {
        return restClient.get()
                .uri("/users/{username}/repos", username)
                .retrieve()
                .onStatus(httpStatusCode -> httpStatusCode.value() == 404,
                        ((request, response) -> {
                            throw new UserNotFoundException(username);
                        }))
                .body(new ParameterizedTypeReference<>() {
                });
    }

    public List<GithubBranch> fetchBranches(String owner, String repoName) {
        return restClient.get()
                .uri("/repos/{owner}/{repo}/branches", owner, repoName)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }

}
