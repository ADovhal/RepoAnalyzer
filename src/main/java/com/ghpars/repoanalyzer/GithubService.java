package com.ghpars.repoanalyzer;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GithubService {

    private final GithubClient client;

    public GithubService(GithubClient client) {
        this.client = client;
    }

    public List<RepoSummaryResponse> getRepos(String username) {
        List<GithubRepo> repos = client.fetchRepos(username);

        return repos.stream()
                .filter(repo -> !repo.fork())
                .map(this::toSummaryDTO)
                .toList();
    }

    private RepoSummaryResponse toSummaryDTO(GithubRepo repo) {

        List<GithubBranch> branches = client.fetchBranches(repo.owner().login(), repo.name());
        List<BranchInfo> branchInfos = branches.stream()
                .map(branch -> new BranchInfo(branch.name(), branch.commit().sha()))
                .toList();

        return new RepoSummaryResponse(repo.name(), repo.owner().login(), branchInfos);
    }

}
