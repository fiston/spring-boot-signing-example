package com.example.iam.iam

import com.example.iam.grpc.IamRegistrationGrpcKt
import com.example.iam.grpc.RegisterClientRequest
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class IamClient(
    @Value("\${iam.grpc.host}") private val host: String,
    @Value("\${iam.grpc.port}") private val port: Int
) {

    private lateinit var stub: IamRegistrationGrpcKt.IamRegistrationCoroutineStub

    @PostConstruct
    fun init() {
        val channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .build()
        stub = IamRegistrationGrpcKt.IamRegistrationCoroutineStub(channel)
    }

    private val clientId = "my-client"

    suspend fun registerClient() {
        val request = RegisterClientRequest.newBuilder()
            .setClientId(clientId)
            .setClientSecret("my-secret")
            .addAllRedirectUris(listOf("http://localhost:8080/login/oauth2/code/iam"))
            .build()
        val response = stub.registerClient(request)
        println("Registration response: \${response.status}")
    }

    suspend fun updateClient(redirectUris: List<String>) {
        val request = com.example.iam.grpc.UpdateClientRequest.newBuilder()
            .setClientId(clientId)
            .addAllRedirectUris(redirectUris)
            .build()
        val response = stub.updateClient(request)
        println("Update response: ${response.status}")
    }

    suspend fun deleteClient() {
        val request = com.example.iam.grpc.DeleteClientRequest.newBuilder()
            .setClientId(clientId)
            .build()
        val response = stub.deleteClient(request)
        println("Deletion response: ${response.status}")
    }

    @PreDestroy
    fun onShutdown() {
        runBlocking {
            println("Shutting down and deleting client...")
            deleteClient()
        }
    }
}
