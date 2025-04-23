package com.gaiaspace.controller

import com.gaiaspace.service.KotlinCompilerService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

data class CompileRequest(
    val gaiaScript: String,
    val appName: String = "GaiaApp"
)

data class CompileResponse(
    val success: Boolean,
    val message: String,
    val kotlinCode: String = "",
    val compiledClasses: List<String> = emptyList(),
    val outputPath: String = ""
)

@RestController
@RequestMapping("/api/gaiascript")
class GaiaScriptController(private val kotlinCompilerService: KotlinCompilerService) {
    
    private val logger = LoggerFactory.getLogger(GaiaScriptController::class.java)
    
    @PostMapping("/compile")
    fun compileGaiaScript(@RequestBody request: CompileRequest): ResponseEntity<CompileResponse> {
        logger.info("Received GaiaScript compilation request for app: ${request.appName}")
        
        try {
            // Generate Kotlin code from GaiaScript
            val kotlinCode = kotlinCompilerService.generateKotlinFromGaiaScript(
                request.gaiaScript, 
                request.appName
            )
            
            // Compile the generated Kotlin code
            val compilationResult = kotlinCompilerService.compileKotlinCode(
                kotlinCode, 
                "com.gaiaspace.generated.${request.appName.lowercase()}"
            )
            
            return ResponseEntity.ok(
                CompileResponse(
                    success = compilationResult.success,
                    message = compilationResult.message,
                    kotlinCode = kotlinCode,
                    compiledClasses = compilationResult.compiledClasses,
                    outputPath = compilationResult.outputDirectory
                )
            )
        } catch (e: Exception) {
            logger.error("Error processing GaiaScript", e)
            return ResponseEntity.ok(
                CompileResponse(
                    success = false,
                    message = "Error processing GaiaScript: ${e.message}"
                )
            )
        }
    }
    
    @GetMapping("/sample")
    fun getSampleGaiaScript(): ResponseEntity<Map<String, String>> {
        val sampleGaiaScript = """
            component App {
                // UI Components
                γ.Panel(width: 300, height: 500) {
                    γ.Label(text: "Hello from GaiaScript!")
                    γ.Button(text: "Click me", onClick: handleClick)
                }
                
                // Event handler
                function handleClick() {
                    console.log("Button clicked")
                }
            }
            
            // Initialize app
            App.render()
        """.trimIndent()
        
        return ResponseEntity.ok(mapOf(
            "gaiaScript" to sampleGaiaScript,
            "appName" to "SampleApp"
        ))
    }
    
    @GetMapping("/cleanup")
    fun cleanupCompilationFiles(): ResponseEntity<Map<String, String>> {
        kotlinCompilerService.cleanup()
        return ResponseEntity.ok(mapOf("message" to "Cleaned up compilation files"))
    }
}