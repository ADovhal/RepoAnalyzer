package com.ghpars.repoanalyzer;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import org.wiremock.spring.InjectWireMock;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestTestClient
@EnableWireMock({
        @ConfigureWireMock(
                name =  "github-mock",
                baseUrlProperties = "github.api.base-url"
        )
})
class GithubControllerIntegrationTest {

    @Autowired
    private RestTestClient restTestClient;

    @InjectWireMock("github-mock")
    private WireMockServer wireMockServer;

    @Test
    void shouldReturnAllNonForkReposWithBranches() throws Exception {

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/users/octocat/repos"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                    {
                                        "name": "Hello-World",
                                        "fork": false,
                                        "owner": {  "login":"octocat"   },
                                        "branches_url": "http://localhost:%d/repos/octocat/Hello-World/branches{/branch}"
                                    },
                                    {
                                        "name": "Fork",
                                        "fork": true,
                                        "owner": {  "login":"octocat"   },
                                        "branches_url": "http://localhost:%d/repos/octocat/Fork/branches{/branch}"
                                    }
                                ]
                                """.formatted(wireMockServer.port(), wireMockServer.port()))));

        wireMockServer.stubFor(WireMock.get(WireMock.urlPathEqualTo("/repos/octocat/Hello-World/branches"))
                .willReturn(WireMock.aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                                [
                                    {
                                        "name": "master",
                                        "commit": { "sha": "c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc"   }
                                    }
                                ]
                                """
                        )));

        restTestClient.get().uri("/api/info/octocat")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.length()").isEqualTo(1)
                .jsonPath("$[0].repoName").isEqualTo("Hello-World")
                .jsonPath("$[0].ownerLogin").isEqualTo("octocat")
                .jsonPath("$[0].branches[0].name").isEqualTo("master")
                .jsonPath("$[0].branches[0].lastCommitSha").isEqualTo("c5b97d5ae6c19d5c5df71a34c7fbeeda2479ccbc");
    }

    @Test
    void shouldReturn404WhenUserNotFound() {
        wireMockServer.stubFor(WireMock.get("/users/nonexistent/repos")
                .willReturn(WireMock.aResponse()
                        .withStatus(404)));

        restTestClient.get().uri("/api/info/nonexistent")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("User nonexistent not found.");
    }
}