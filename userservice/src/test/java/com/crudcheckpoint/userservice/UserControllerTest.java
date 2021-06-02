package com.crudcheckpoint.userservice;

import com.crudcheckpoint.userservice.bean.User;
import com.crudcheckpoint.userservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;

import javax.transaction.Transactional;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {
    @Autowired
    MockMvc mvc;

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    public void mockDatabase(){
        System.out.println("Create dummy database");
        this.userRepository.save(new User(1, "john@example.com", "something-secret"));
        this.userRepository.save(new User(2, "eliza@example.com", "something-secret"));
    }

    @Test
    public void testGetUsers() throws Exception {
        RequestBuilder request = get("/users");
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].email", is("john@example.com")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].email", is("eliza@example.com")));
    }

    @Test

    public void testGetUserById() throws Exception {

        RequestBuilder request = get("/users/{id}", 1);
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("john@example.com")));
    }
    @Test
    @Transactional
    @Rollback
    public void testDeleteUserById() throws Exception {
        RequestBuilder request = delete("/users/{id}", 1);
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count", is(1)));
    }

    @Test
    @Transactional
    @Rollback
    public void testPostUser() throws Exception {
        User user =  new User();
        user.setEmail("matt@example.com");
        user.setPassword("123456");
        ObjectMapper mapper = new ObjectMapper();
        RequestBuilder request = post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(user));

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.email", is("matt@example.com")));
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateUser() throws Exception {

        User user =  new User();
        user.setEmail("matt@example.com");
        ObjectMapper mapper = new ObjectMapper();

        RequestBuilder request = patch("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(user));
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("matt@example.com")));

        User user2 =  new User();
        user2.setEmail("abc@example.com");
        user2.setPassword("123456");

        request = patch("/users/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(mapper.writeValueAsString(user2));

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.email", is("abc@example.com")));

    }

    @Test
    public void testAuthenticateUser() throws Exception {

        String user = "{\n" +
                "    \"email\": \"john@example.com\",\n" +
                "    \"password\": \"something-secret\"\n" +
                "}";

        RequestBuilder request = post("/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(user);
        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated", is(true)))
                .andExpect(jsonPath("$.user.id", is(1)))
                .andExpect(jsonPath("$.user.email", is("john@example.com")));


        user = "{\n" +
                "    \"email\": \"john@example.com\",\n" +
                "    \"password\": \"abc\"\n" +
                "}";
        request = post("/users/authenticate")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(user);

        this.mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authenticated", is(false)));
    }

}
