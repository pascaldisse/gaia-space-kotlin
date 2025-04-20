package com.gaiaspace.config

import org.springframework.boot.web.server.ErrorPage
import org.springframework.boot.web.server.ErrorPageRegistrar
import org.springframework.boot.web.server.ErrorPageRegistry
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus

@Configuration
class ErrorConfig {

    @Bean
    fun errorPageRegistrar(): ErrorPageRegistrar {
        return ErrorPageRegistrar { registry: ErrorPageRegistry ->
            registry.addErrorPages(
                ErrorPage(HttpStatus.NOT_FOUND, "/error"),
                ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error"),
                ErrorPage(HttpStatus.FORBIDDEN, "/error"),
                ErrorPage(HttpStatus.UNAUTHORIZED, "/error")
            )
        }
    }
}