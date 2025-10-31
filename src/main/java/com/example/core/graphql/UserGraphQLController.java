package com.example.core.graphql;

import com.example.core.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/users/graphql")
public class UserGraphQLController {

    private final RestTemplate restTemplate = new RestTemplate();
    ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/sequence")
    public User getUserInfoSequence() {
        String url1 = "http://localhost:8081/graphql";

        String query1 = "query { user(id: \"123\") { id name email } }";

        String url2 = "http://localhost:8081/graphql";

        String query2 = "query { user(id: \"456\") { id name email } }";

        String url3 = "http://localhost:8081/graphql";

        String query3 = "query { user(id: \"789\") { id name email } }";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("query", query1);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url1, request, String.class);

        requestBody.put("query", query2);
        request = new HttpEntity<>(requestBody, headers);
        response = restTemplate.postForEntity(url2, request, String.class);

        requestBody.put("query", query3);
        request = new HttpEntity<>(requestBody, headers);
        response = restTemplate.postForEntity(url3, request, String.class);

        // Deserialize JSON to GraphQLResponse
        GraphQLResponse graphQLResponse = null;
        try {
            graphQLResponse = objectMapper.readValue(response.getBody(), GraphQLResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return graphQLResponse.getData() != null ? graphQLResponse.getData().getUser() : null;
    }

    @GetMapping("/parallel")
    public User getUserInfoParallel() {
        String url = "http://localhost:8081/graphql";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create a CompletableFuture for each GraphQL request
        CompletableFuture<ResponseEntity<String>> future1 = CompletableFuture.supplyAsync(() -> {
            Map<String, Object> requestBody = Map.of("query", "query { user(id: \"123\") { id name email } }");
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            return restTemplate.postForEntity(url, request, String.class);
        });

        CompletableFuture<ResponseEntity<String>> future2 = CompletableFuture.supplyAsync(() -> {
            Map<String, Object> requestBody = Map.of("query", "query { user(id: \"456\") { id name email } }");
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            return restTemplate.postForEntity(url, request, String.class);
        });

        CompletableFuture<ResponseEntity<String>> future3 = CompletableFuture.supplyAsync(() -> {
            Map<String, Object> requestBody = Map.of("query", "query { user(id: \"789\") { id name email } }");
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            return restTemplate.postForEntity(url, request, String.class);
        });

        // Wait for all three futures to complete
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(future1, future2, future3);

        // Wait and handle the results
        allFutures.join();

        try {
            ResponseEntity<String> response = future3.get(); // Or future2.get(), future3.get()
            GraphQLResponse graphQLResponse = objectMapper.readValue(response.getBody(), GraphQLResponse.class);
            return graphQLResponse.getData() != null ? graphQLResponse.getData().getUser() : null;
        } catch (InterruptedException | ExecutionException | JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
