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
@CrossOrigin(origins = "*")
public class UserRestController {

    private final RestTemplate restTemplate = new RestTemplate();

    private final ApiService apiService = new ApiService();
    String baseUrl1 = "https://democore.onrender.com";
    String baseUrl2 = "https://democore-1.onrender.com";
    String baseUrl3 = "https://democore-2.onrender.com";

    @GetMapping("/{id}")
    public User getUser(@PathVariable String id) {
        return new User(id, "John Doe", "john@example.com", true);
    }

    @GetMapping("/smallerPayload/{id}")
    public User getUserSmallerPayload(@PathVariable String id) {
        return new User(id, "John Doe", "john@example.com", false);
    }
}
