package com.example.iam.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class WebController {

    @GetMapping("/")
    fun index(): String {
        return "Hello, world!"
    }

    @GetMapping("/hello")
    fun hello(): String {
        return "Hello, authenticated user!"
    }
}
