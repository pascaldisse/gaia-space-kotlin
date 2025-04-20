package com.gaiaspace.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.graphql.execution.RuntimeWiringConfigurer

@Configuration
class GraphQLConfig {

    @Bean
    fun runtimeWiringConfigurer(): RuntimeWiringConfigurer {
        return RuntimeWiringConfigurer { wiringBuilder ->
            // Register scalar types, type resolvers, and custom directives here if needed
            wiringBuilder
        }
    }
}