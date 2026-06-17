package com.ghpars.repoanalyzer;

import java.util.List;

public record RepoSummaryResponse(
        String repoName,
        String ownerLogin,
        List<BranchInfo> branches
) {
}
