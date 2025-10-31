package com.example.core.graphql;

import com.example.core.model.User;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;

@Controller
public class UserGraphQLResolver {

    private final RestTemplate restTemplate = new RestTemplate();

    @QueryMapping
    public User user(@Argument String id) {
        return new User(id, "John Doe", "john@example.com");
    }
}
