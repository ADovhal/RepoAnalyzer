package com.ghpars.repoanalyzer;

public record ErrorResponse(
        int status,
        String message
) {
}
