package com.aws.opensearch.tests;

import com.aws.opensearch.tests.dto.BaseIndexDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.opensearch.client.json.JsonData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.Result;
import org.opensearch.client.opensearch._types.mapping.TypeMapping;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexResponse;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.opensearch.client.transport.aws.AwsSdk2Transport;
import org.opensearch.client.transport.aws.AwsSdk2TransportOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IndexTest {

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
     * Create an Index with mapping definitions
     */
    @Test
    @Order(1)
    public void createIndexTest() {

        Map<String, Object> mappings = new HashMap<>();
        mappings.put("test_mapping", "test_value");

        createIndex(mappings, "test");
    }

    /**
     * Insert a document in OpenSearch index.
     */
    @Test
    @Order(2)
    public void createDocumentTest() {

        BaseIndexDto baseIndexDto = new BaseIndexDto();
        baseIndexDto.setIndex("test");

        Map<String, Object> document = new HashMap<>();
        document.put("test", "test_value");

        baseIndexDto.setDocument(document);
        String id = index(baseIndexDto, "test");

        assertNotNull(id);
    }

    /**
     * Drop Index
     */
    @Test
    @Order(3)
    public void dropIndexTest() {

        dropIndex("test");
    }

    // Utility methods

    private void createIndex(Map<String, Object> indexMapping, String indexName) {
        try {

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(indexMapping);

            CreateIndexRequest request = new CreateIndexRequest.Builder()
                    .index(indexName)
                    .mappings(m -> m
                            .withJson(new StringReader(json))
                    )
                    .build();

            CreateIndexResponse createIndexResponse = client.indices().create(request);
            LOGGER.debug("Create index response is {}", createIndexResponse);
        } catch (Exception exception) {
            LOGGER.error("Could not create the index {}", indexName, exception);
            throw new RuntimeException(exception);
        }
    }

    private String index(BaseIndexDto request, String indexName) {
        try {
            IndexResponse response = client.index(i -> i
                    .index(indexName)
                    .id(request.getId())
                    .document(request.getDocument())
            );

            if (Result.Created.equals(response.result())) {
                LOGGER.info("Document successfully created at index:{}, id:{}", response.index(), response.id());
            } else {
                LOGGER.info("Document successfully updated at index:{}, id:{}, version:{}", response.index(),
                        response.id(), response.version());
            }
            return response.id();
        } catch (Exception exception) {
            LOGGER.error("Unable to index the document.", exception);
            throw new RuntimeException(exception);
        }
    }

    private void dropIndex(String indexName) {
        try {
            DeleteIndexRequest request = new DeleteIndexRequest.Builder()
                    .index(indexName)
                    .build();

            DeleteIndexResponse response = client.indices().delete(request);

            LOGGER.debug("Delete index response is {}", response.acknowledged());
        } catch (Exception exception) {
            LOGGER.error("Could not delete index {}", indexName, exception);
            throw new RuntimeException(exception);
        }
    }

    @AfterAll
    public void destroy() {
        try {
            if (client != null) {
                client._transport().close();
                LOGGER.info("OpenSearch client closed successfully");
            }
        } catch (Exception e) {
            LOGGER.error("Error closing OpenSearch client", e);
        }
    }

}
