package com.example.iam.iam

import com.example.iam.grpc.IamRegistrationGrpcKt
import com.example.iam.grpc.RegisterClientRequest
import io.grpc.ManagedChannelBuilder
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

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

    suspend fun registerClient() {
        val request = RegisterClientRequest.newBuilder()
            .setClientId("my-client")
            .setClientSecret("my-secret")
            .addAllRedirectUris(listOf("http://localhost:8080/login/oauth2/code/iam"))
            .build()
        val response = stub.registerClient(request)
        println("Registration response: \${response.status}")
    }
}
