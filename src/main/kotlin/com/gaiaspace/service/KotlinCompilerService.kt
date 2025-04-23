package com.gaiaspace.service

import org.springframework.stereotype.Service
import java.nio.file.Files
import java.nio.file.Paths
import java.io.File
import org.slf4j.LoggerFactory
import org.springframework.util.FileSystemUtils
import java.util.concurrent.TimeUnit

data class CompilationResult(
    val success: Boolean,
    val message: String,
    val compiledClasses: List<String> = emptyList(),
    val outputDirectory: String = ""
)

/**
 * Service for compiling Kotlin code from GaiaScript.
 * Takes Kotlin source code, compiles it, and returns the result.
 */
@Service
class KotlinCompilerService {
    
    private val logger = LoggerFactory.getLogger(KotlinCompilerService::class.java)
    
    // Temporary directory for compilation
    private val tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "gaia_kotlin_compiler")
    
    /**
     * Compiles GaiaScript generated Kotlin code
     * 
     * @param kotlinCode The Kotlin source code to compile
     * @param packageName The package name to use for the compiled code
     * @return The compilation result
     */
    fun compileKotlinCode(kotlinCode: String, packageName: String = "com.gaiaspace.generated"): CompilationResult {
        logger.info("Compiling Kotlin code with package: $packageName")
        
        // Create temp directory for compilation
        val tempDirFile = tempDir.toFile()
        if (tempDirFile.exists()) {
            FileSystemUtils.deleteRecursively(tempDirFile)
        }
        Files.createDirectories(tempDir)
        
        // Create package directories
        val packagePath = packageName.replace(".", "/")
        val srcDir = tempDir.resolve("src/main/kotlin/$packagePath")
        Files.createDirectories(srcDir)
        
        // Write Kotlin source file
        val sourceFile = srcDir.resolve("GeneratedApp.kt").toFile()
        
        val fullSource = """
            package $packageName
            
            $kotlinCode
        """.trimIndent()
        
        sourceFile.writeText(fullSource)
        
        // Create build.gradle.kts file
        val buildGradleFile = tempDir.resolve("build.gradle.kts").toFile()
        buildGradleFile.writeText("""
            plugins {
                id("org.jetbrains.kotlin.jvm") version "1.9.22"
                application
            }
            
            repositories {
                mavenCentral()
                google()
            }
            
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
                // For Android/UI dependencies (just stubs for compilation)
                compileOnly("androidx.activity:activity-compose:1.8.1")
                compileOnly("androidx.compose.material3:material3:1.1.2")
                compileOnly("androidx.compose.foundation:foundation:1.5.4")
                compileOnly("androidx.compose.ui:ui:1.5.4")
                compileOnly("org.tensorflow:tensorflow-lite:2.14.0")
            }
            
            kotlin {
                jvmToolchain(17)
            }
            
            application {
                mainClass.set("$packageName.GeneratedAppKt")
            }
        """.trimIndent())
        
        // Create settings.gradle.kts
        val settingsGradleFile = tempDir.resolve("settings.gradle.kts").toFile()
        settingsGradleFile.writeText("""
            rootProject.name = "gaia-generated-app"
        """.trimIndent())
        
        // Execute gradle to compile
        try {
            val process = ProcessBuilder(
                "./gradlew", "compileKotlin", "--no-daemon", "--console=plain"
            )
                .directory(tempDirFile)
                .redirectErrorStream(true)
                .start()
            
            val output = process.inputStream.bufferedReader().readText()
            val completed = process.waitFor(60, TimeUnit.SECONDS)
            
            if (!completed) {
                process.destroyForcibly()
                return CompilationResult(
                    success = false,
                    message = "Compilation timed out after 60 seconds."
                )
            }
            
            if (process.exitValue() != 0) {
                return CompilationResult(
                    success = false,
                    message = "Compilation failed:\n$output"
                )
            }
            
            // Find compiled class files
            val classesDir = tempDir.resolve("build/classes/kotlin/main")
            val classFiles = Files.walk(classesDir)
                .filter { it.toString().endsWith(".class") }
                .map { classesDir.relativize(it).toString() }
                .toList()
            
            return CompilationResult(
                success = true,
                message = "Compilation successful. Generated ${classFiles.size} class files.",
                compiledClasses = classFiles,
                outputDirectory = classesDir.toString()
            )
            
        } catch (e: Exception) {
            logger.error("Error compiling Kotlin code", e)
            return CompilationResult(
                success = false,
                message = "Error: ${e.message}"
            )
        }
    }
    
    /**
     * Generates Kotlin code from GaiaScript 
     * This is a simplified version that doesn't parse GaiaScript, just converts
     * a template to Kotlin code
     */
    fun generateKotlinFromGaiaScript(gaiaScript: String, appName: String): String {
        // For now, just a simple templating system
        return """
            import android.os.Bundle
            import androidx.activity.ComponentActivity
            import androidx.activity.compose.setContent
            import androidx.compose.foundation.layout.*
            import androidx.compose.material3.*
            import androidx.compose.runtime.*
            import androidx.compose.ui.Modifier
            import androidx.compose.ui.Alignment
            
            /**
             * Generated from GaiaScript:
             * ```
             * ${gaiaScript.lines().joinToString("\n * ")}
             * ```
             */
            class ${appName}Activity : ComponentActivity() {
                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    setContent {
                        MaterialTheme {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = MaterialTheme.colorScheme.background
                            ) {
                                ${appName}Content()
                            }
                        }
                    }
                }
            }
            
            @Composable
            fun ${appName}Content() {
                var counter by remember { mutableStateOf(0) }
                
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Welcome to $appName")
                    Text("Counter: ${"$"}{counter}")
                    Button(onClick = { counter++ }) {
                        Text("Increment")
                    }
                }
            }
        """.trimIndent()
    }
    
    /**
     * Cleans up temporary compilation files
     */
    fun cleanup() {
        try {
            FileSystemUtils.deleteRecursively(tempDir)
        } catch (e: Exception) {
            logger.warn("Failed to clean up temp directory: $tempDir", e)
        }
    }
}