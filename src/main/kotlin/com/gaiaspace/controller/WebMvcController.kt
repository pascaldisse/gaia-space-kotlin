package com.gaiaspace.controller

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping

@Controller
@RequestMapping("/")
class WebMvcController {

    @GetMapping("/gaiascript")
    fun gaiaScriptPage(): String {
        return "gaiascript"
    }
}