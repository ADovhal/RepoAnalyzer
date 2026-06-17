package com.ghpars.repoanalyzer;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GithubRepo(
        String name,
        GithubOwner owner,
        boolean fork,
        @JsonProperty("branches_url") String branchesUrl
) {
}
