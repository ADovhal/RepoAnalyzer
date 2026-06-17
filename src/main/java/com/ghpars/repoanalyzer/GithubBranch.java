package com.ghpars.repoanalyzer;

public record GithubBranch(
        String name,
        GithubCommit commit
) {
}
