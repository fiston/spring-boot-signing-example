package com.example.iam.config

import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.stereotype.Component

@Component("clientRegistrationRepository")
class InMemoryMutableClientRegistrationRepository : ClientRegistrationRepository, Iterable<ClientRegistration> {
    private val registrations: MutableMap<String, ClientRegistration> = mutableMapOf()

    override fun findByRegistrationId(registrationId: String): ClientRegistration? {
        return registrations[registrationId]
    }

    override fun iterator(): Iterator<ClientRegistration> {
        return registrations.values.iterator()
    }

    fun save(registration: ClientRegistration) {
        registrations[registration.registrationId] = registration
    }
}
