package com.example.iam.iam

import com.example.iam.grpc.DeleteClientRequest
import com.example.iam.grpc.IamRegistrationGrpcKt
import com.example.iam.grpc.RegisterClientRequest
import com.example.iam.grpc.RegisterClientResponse
import com.example.iam.grpc.UpdateClientRequest
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class IamClient(
    @Value("\${iam.grpc.host}") private val host: String,
    @Value("\${iam.grpc.port}") private val port: Int,
    private val clientRegistrationHolder: ClientRegistrationHolder
) {

    private lateinit var stub: IamRegistrationGrpcKt.IamRegistrationCoroutineStub

    @PostConstruct
    fun init() {
        val channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .build()
        stub = IamRegistrationGrpcKt.IamRegistrationCoroutineStub(channel)
    }

    suspend fun registerClient(): RegisterClientResponse {
        val request = RegisterClientRequest.newBuilder()
            .setClientName("my-dynamic-client")
            .addClientAuthenticationMethods("client_secret_basic")
            .addAuthorizationGrantTypes("authorization_code")
            .addAuthorizationGrantTypes("refresh_token")
            .addRedirectUris("http://localhost:8080/login/oauth2/code/iam")
            .addScopes("openid")
            .addScopes("profile")
            .setAccessTokenTtl(3600)
            .setRefreshTokenTtl(86400)
            .setRequireAuthorizationConsent(true)
            .build()
        println("Sending registration request...")
        val response = stub.registerClient(request)
        println("Registration successful. Client ID: \${response.clientId}")
        return response
    }

    suspend fun updateClient(redirectUris: List<String>) {
        val clientId = clientRegistrationHolder.details?.clientId
            ?: throw IllegalStateException("Client not registered yet")

        val request = UpdateClientRequest.newBuilder()
            .setClientId(clientId)
            .addAllRedirectUris(redirectUris)
            .build()
        val response = stub.updateClient(request)
        println("Update response: \${response.status}")
    }

    suspend fun deleteClient() {
        val clientId = clientRegistrationHolder.details?.clientId
            ?: throw IllegalStateException("Client not registered yet")

        val request = DeleteClientRequest.newBuilder()
            .setClientId(clientId)
            .build()
        val response = stub.deleteClient(request)
        println("Deletion response: \${response.status}")
    }

    @PreDestroy
    fun onShutdown() {
        if (clientRegistrationHolder.details != null) {
            runBlocking {
                println("Shutting down and deleting client...")
                deleteClient()
            }
        } else {
            println("Shutting down. No client to delete.")
        }
    }
}
