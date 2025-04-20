package com.gaiaspace.controller

import jakarta.servlet.RequestDispatcher
import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class CustomErrorController : ErrorController {

    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest, model: Model): String {
        // Get error status
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)?.toString()?.toIntOrNull()
        
        model.addAttribute("status", status ?: 500)
        
        val statusCode = HttpStatus.valueOf(status ?: 500)
        model.addAttribute("error", statusCode.reasonPhrase)
        
        val message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE)?.toString()
        model.addAttribute("message", message ?: "Unknown error")
        
        val path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI)?.toString()
        model.addAttribute("path", path ?: request.requestURI)
        
        return "error"
    }
}