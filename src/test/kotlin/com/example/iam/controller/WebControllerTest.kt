package com.example.iam.controller

import com.example.iam.config.SecurityConfig
import com.example.iam.iam.IamClient
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(WebController::class)
@Import(SecurityConfig::class)
class WebControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var iamClient: IamClient

    @Test
    fun `index endpoint requires authentication`() {
        mockMvc.perform(get("/"))
            .andExpect(status().is3xxRedirection)
    }

    @Test
    @WithMockUser
    fun `index endpoint is accessible to authenticated users`() {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk)
    }

    @Test
    fun `hello endpoint requires authentication`() {
        mockMvc.perform(get("/hello"))
            .andExpect(status().is3xxRedirection)
    }

    @Test
    @WithMockUser
    fun `hello endpoint is accessible to authenticated users`() {
        mockMvc.perform(get("/hello"))
            .andExpect(status().isOk)
    }
}
