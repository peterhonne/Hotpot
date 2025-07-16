package com.hotpot.user;

import com.hotpot.common.entity.UserInfo;
import com.hotpot.user.controller.UserController;
import com.hotpot.user.service.UserService;
import org.hamcrest.BaseMatcher;
import org.hamcrest.core.IsSame;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * @author Peter
 * @date 2023/3/27
 * @description
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mvc;

    private static final String TOKEN = "eyJraWQiOiI1NDk2NmM0My01ODE5LTQyYTgtOTk3Ny03NTYyYWQwNzg0MWMiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJwZXRlciIsImF1ZCI6ImF1dGgtc2VydmVyIiwibmJmIjoxNjc5OTg1MzY3LCJpc3MiOiJodHRwOi8vMTI3LjAuMC4xOjkwODEiLCJleHAiOjE2Nzk5ODcxNjcsImlhdCI6MTY3OTk4NTM2NywiYXV0aG9yaXRpZXMiOlsiUk9MRV9hZG1pbiIsInVzZXI6c2VsZWN0Il19.H0_P9JHgXn47OTddWpTvjUuP1dnWap20AO1XGU9CmgDCqf54oG-DjZePojYKBy65gbfwvBwPB1S-wI7UxLTpZYEnQxskYW3G-Ke--4vE5Gtw0LRTcCL_stf7zApPzcuFaeiWynNSCXMb9-SuBMcZtI2kBlFOfhgUP1swSJHotLjNbct5dWVihB7DEooBgBzpT1Hu30iimZ9PjeYCcuqv9BnT75_ClJTqHulO5WQH1cdlxgUlecqD7t3hLj7IwiSMwXVlGzBQoHgPdRh1P-jG8VZqfmT5BjAbSgu36CbxSsRLwSLLoAjV4LqMRvcumNe9E2azzqw3glJV0G8tJB7VKQ";

    @Test
    public void testGetUserInfo() throws Exception {
        given(this.userService.getInfoByUsername("peter"))
                .willReturn(new UserInfo());
        this.mvc.perform(MockMvcRequestBuilders.get(URI.create("/user/getInfoByUsername/peter"))
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sysUser", new IsSame<>(null)));
    }


}
