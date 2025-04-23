package com.gaiaspace

import com.gaiaspace.service.KotlinCompilerService
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import java.nio.file.Files
import java.nio.file.Paths

/**
 * Test for compiling main.gaia to Kotlin
 */
class GaiaScriptTest {
    
    private val logger = LoggerFactory.getLogger(GaiaScriptTest::class.java)

    @Test
    fun testGaiaScriptTemplate() {
        val kotlinCompilerService = KotlinCompilerService()
        
        // Use a simple GaiaScript example
        val gaiaScript = """
            component GaiaTest {
                // UI Components
                γ.Panel(width: 400, height: 600) {
                    γ.Label(text: "Hello from GaiaScript Test!")
                    γ.Button(text: "Click me", onClick: handleClick)
                }
                
                // Event handler
                function handleClick() {
                    console.log("Button clicked")
                }
            }
            
            // Initialize app
            GaiaTest.render()
        """.trimIndent()
        
        // Generate Kotlin code
        val kotlinCode = kotlinCompilerService.generateKotlinFromGaiaScript(gaiaScript, "TestApp")
        
        // Print the generated code
        logger.info("Generated Kotlin code: \n$kotlinCode")
        
        // No need to actually compile - just check code generation
        assert(kotlinCode.contains("TestAppContent"))
        assert(kotlinCode.contains("Button(onClick = { counter++ })"))
    }
}