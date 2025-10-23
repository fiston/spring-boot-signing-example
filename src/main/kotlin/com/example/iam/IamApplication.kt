package com.example.iam

import com.example.iam.iam.IamClient
import kotlinx.coroutines.runBlocking
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class IamApplication {

    @Bean
    fun commandLineRunner(iamClient: IamClient) = CommandLineRunner {
        runBlocking {
            iamClient.registerClient()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<IamApplication>(*args)
}
