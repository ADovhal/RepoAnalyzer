package com.ghpars.repoanalyzer;

public record BranchInfo(
        String name,
        String lastCommitSha
) {
}
