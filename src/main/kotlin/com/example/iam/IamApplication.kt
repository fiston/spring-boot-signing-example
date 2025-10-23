package com.example.iam

import com.example.iam.config.InMemoryMutableClientRegistrationRepository
import com.example.iam.iam.ClientRegistrationDetails
import com.example.iam.iam.ClientRegistrationHolder
import com.example.iam.iam.IamClient
import kotlinx.coroutines.runBlocking
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Profile
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod

@SpringBootApplication
class IamApplication {

    @Bean
    @Profile("!test")
    fun commandLineRunner(
        iamClient: IamClient,
        clientRegistrationRepository: InMemoryMutableClientRegistrationRepository,
        clientRegistrationHolder: ClientRegistrationHolder
    ) = CommandLineRunner {
        runBlocking {
            val response = iamClient.registerClient()

            clientRegistrationHolder.details = ClientRegistrationDetails(
                clientId = response.clientId,
                clientSecret = response.clientSecret
            )

            val registration = ClientRegistration.withRegistrationId("iam")
                .clientId(response.clientId)
                .clientSecret(response.clientSecret)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
                .scope(response.scopesList)
                .authorizationUri("http://localhost:9000/oauth2/authorize")
                .tokenUri("http://localhost:9000/oauth2/token")
                .userInfoUri("http://localhost:9000/userinfo")
                .userNameAttributeName("sub")
                .clientName("IAM")
                .build()

            clientRegistrationRepository.save(registration)
        }
    }
}

fun main(args: Array<String>) {
    runApplication<IamApplication>(*args)
}
