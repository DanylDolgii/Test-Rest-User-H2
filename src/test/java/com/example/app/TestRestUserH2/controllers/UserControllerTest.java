package com.example.app.TestRestUserH2.controllers;

import com.example.app.TestRestUserH2.entity.User;
import com.example.app.TestRestUserH2.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void givenUserObject_whenCreateUser_thenReturnSavedUser() throws Exception {

        User user = User.builder()
                .name("Mike")
                .phone("555 123-1111")
                .email("mike@email.com")
                .build();
        given(userService.saveUser(any(User.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)));
        response.andDo(print()).
                andExpect(status().isCreated())
                .andExpect(jsonPath("$.name",
                        is(user.getName())))
                .andExpect(jsonPath("$.phone",
                        is(user.getPhone())))
                .andExpect(jsonPath("$.email",
                        is(user.getEmail())));

    }

        @Test
        public void givenListOfUsers_whenGetAllUsers_thenReturnUsersList() throws  Exception {

            List<User> listOfUsers = new ArrayList<>();
            listOfUsers.add(User.builder()
                    .name("Mike")
                    .phone("555 123-1111")
                    .email("mike@email.com")
                    .build());

            listOfUsers.add(User.builder()
                    .name("Noa")
                    .phone("555 123-2222")
                    .email("noa@email.com")
                    .build());

            given(userService.getAllUsers()).willReturn(listOfUsers);

            ResultActions response = mockMvc.perform(get("/api/users"));

            response.andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$.size()",
                            is(listOfUsers.size())));

        }

        @Test
        public void givenUserId_whenGetUserById_thenReturnUserObject() throws Exception {

            int userId = 1;
            User user = User.builder()
                    .name("Mike")
                    .phone("555 123-1111")
                    .email("mike@email.com")
                    .build();
            given(userService.getUserById(userId)).willReturn(Optional.of(user));

            ResultActions response = mockMvc.perform(get("/api/users/{id}", userId));
            response.andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$.name",
                            is(user.getName())))
                    .andExpect(jsonPath("$.phone",
                            is(user.getPhone())))
                    .andExpect(jsonPath("$.email",
                            is(user.getEmail())));

        }

    @Test
    public void givenInvalidUserId_whenGetUserById_thenReturnEmpty() throws Exception {

        int userId = 1;
        User user = User.builder()
                .name("Mike")
                .phone("555 123-1111")
                .email("mike@email.com")
                .build();
        given(userService.getUserById(userId)).willReturn(Optional.empty());

        ResultActions response = mockMvc.perform(get("/api/users/{id}", userId));

        response.andExpect(status().isNotFound())
                .andDo(print());

    }

    @Test
    public void givenUpdatedUser_whenUpdateUser_thenReturnUpdateUserObject() throws Exception {

        int userId = 1;
        User savedUser = User.builder()
                .name("Mike")
                .phone("555 123-1111")
                .email("mike@email.com")
                .build();

        User updatedUser = User.builder()
                .name("Mike")
                .phone("555 123-3333")
                .email("mike@email.com")
                .build();
        given(userService.getUserById(userId)).willReturn(Optional.of(savedUser));
        given(userService.updateUser(any(User.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)));


        response.andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.name",
                        is(updatedUser.getName())))
                .andExpect(jsonPath("$.phone",
                        is(updatedUser.getPhone())))
                .andExpect(jsonPath("$.email",
                        is(updatedUser.getEmail())));

    }

    @Test
    public void givenUpdatedUser_whenUpdateUser_thenReturn404() throws Exception {

        int userId = 1;
        User savedUser = User.builder()
                .name("Mike")
                .phone("555 123-1111")
                .email("mike@email.com")
                .build();

        User updatedUser = User.builder()
                .name("Mike")
                .phone("555 123-3333")
                .email("mike@email.com")
                .build();
        given(userService.getUserById(userId)).willReturn(Optional.empty());
        given(userService.updateUser(any(User.class)))
                .willAnswer((invocation) -> invocation.getArgument(0));

        ResultActions response = mockMvc.perform(put("/api/users/{id}", userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)));

        response.andExpect(status().isNotFound())
                .andDo(print());

    }

    @Test
    public void givenUserId_whenDeleteUser_thenReturn200() throws Exception {

        int userId = 1;
        willDoNothing().given(userService).deleteUser(userId);

        ResultActions response = mockMvc.perform(delete("/api/users/{id}", userId));

        response.andExpect(status().isOk())
                .andDo(print());

    }
}
