package com.example.iam.controller

import com.example.iam.config.InMemoryMutableClientRegistrationRepository
import com.example.iam.iam.IamClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class WebControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var iamClient: IamClient

    @Autowired
    private lateinit var clientRegistrationRepository: InMemoryMutableClientRegistrationRepository

    @BeforeEach
    fun setup() {
        val registration = ClientRegistration.withRegistrationId("iam")
            .clientId("test-client")
            .clientSecret("test-secret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope("openid")
            .authorizationUri("http://localhost:9000/oauth2/authorize")
            .tokenUri("http://localhost:9000/oauth2/token")
            .userInfoUri("http://localhost:9000/userinfo")
            .userNameAttributeName("sub")
            .clientName("IAM")
            .build()

        clientRegistrationRepository.save(registration)
    }

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
