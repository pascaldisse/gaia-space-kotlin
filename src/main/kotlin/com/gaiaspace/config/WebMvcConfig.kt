package com.gaiaspace.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig : WebMvcConfigurer {

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/**")
            .addResourceLocations("classpath:/static/")
            .setCachePeriod(3600)
            .resourceChain(true)
    }

    override fun addViewControllers(registry: ViewControllerRegistry) {
        // Forward requests to the root URL to index.html
        registry.addViewController("/").setViewName("forward:/index.html")
        
        // Add more view controllers for other static pages
        registry.addViewController("/tasks").setViewName("forward:/tasks.html")
        registry.addViewController("/tasks/new").setViewName("forward:/tasks-new.html")
        registry.addViewController("/dashboard").setViewName("forward:/index.html")
        registry.addViewController("/workspaces").setViewName("forward:/index.html")
        registry.addViewController("/projects").setViewName("forward:/index.html")
        registry.addViewController("/pipelines").setViewName("forward:/index.html")
    }
}