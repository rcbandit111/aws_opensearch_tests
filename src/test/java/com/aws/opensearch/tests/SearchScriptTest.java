package com.aws.opensearch.tests;

import org.junit.jupiter.api.*;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.SearchRequest;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.transport.aws.AwsSdk2Transport;
import org.opensearch.client.transport.aws.AwsSdk2TransportOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SearchScriptTest {

    static final Logger LOGGER = LoggerFactory.getLogger(IndexTest.class);

    private OpenSearchClient client;

    private final String clusterUrl = System.getenv("CLUSTERURL");
    private final String accessKey = System.getenv("ACCESSKEY");
    private final String secretKey = System.getenv("SECRETKEY");
    private final String region = System.getenv("REGION");

    @BeforeAll
    public void awsConnection() throws Exception {
        AwsSdk2Transport transport = new AwsSdk2Transport(
                ApacheHttpClient.builder().build(),
                clusterUrl,
                Region.of(region),
                AwsSdk2TransportOptions.builder()
                        .setCredentials(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                        .build());

        client = new OpenSearchClient(transport);
        LOGGER.debug("Successfully initialized client object");

        var info = client.info();
        if (info.clusterName().isEmpty()) {
            LOGGER.error("No nodes available on OpenSearch cluster:{}", clusterUrl);
            throw new Exception("No nodes available on OpenSearch cluster");
        } else {
            LOGGER.info("Client connected to node:{}", info.clusterName());
        }
    }

    /**
     * Search Script
     */
    @Test
    @Order(1)
    public void searchScriptTest() {

    }

    public <T> SearchResponse<T> search(SearchRequest.Builder requestBuilder, Class<T> clazz) {
        try {
            SearchRequest request = requestBuilder.build();
            return client.search(request, clazz);
        } catch (Exception exception) {
            LOGGER.error("Search operation failed.", exception);
            throw new RuntimeException(exception);
        }
    }

}
