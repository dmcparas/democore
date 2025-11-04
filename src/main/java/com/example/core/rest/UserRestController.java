package com.example.core.rest;

import com.example.core.model.User;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final RestTemplate restTemplate = new RestTemplate();

    private final ApiService apiService = new ApiService();
    String baseUrl1 = "https://democore.onrender.com";
    String baseUrl2 = "https://democore-1.onrender.com";
    String baseUrl3 = "https://democore-2.onrender.com";

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return new User(id, "John Doe", "john@example.com");
    }

    @GetMapping("/sequence")
    public User getUserInfo() {
        String url1 = baseUrl1 + "/api/users/1";
        String url2 = baseUrl2 + "/api/users/2";
        String url3 = baseUrl3 + "/api/users/3";
        User user;
        user = restTemplate.getForObject(url1, User.class);
        user = restTemplate.getForObject(url2, User.class);
        user = restTemplate.getForObject(url3, User.class);
        return user;
    }

    @GetMapping("/parallel")
    public List<String> callApisInParallel() throws InterruptedException, ExecutionException {

        ExecutorService executor = Executors.newFixedThreadPool(3);

        try {
            // Define API URLs
            String url1 = baseUrl1 + "/api/users/1";
            String url2 = baseUrl2 + "/api/users/2";
            String url3 = baseUrl3 + "/api/users/3";

            // Create callables for each API
            List<Callable<String>> tasks = Arrays.asList(
                    apiService.callApi(url1),
                    apiService.callApi(url2),
                    apiService.callApi(url3)
            );

            // Invoke all tasks in parallel
            List<Future<String>> futures = executor.invokeAll(tasks);

            // Collect all responses
            return futures.stream()
                    .map(f -> {
                        try {
                            return f.get();
                        } catch (Exception e) {
                            return "Error: " + e.getMessage();
                        }
                    })
                    .collect(Collectors.toList());

        } finally {
            executor.shutdown();
        }
    }
}
