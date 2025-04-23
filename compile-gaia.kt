import com.gaiaspace.service.KotlinCompilerService
import java.nio.file.Files
import java.nio.file.Paths

/**
 * This is a standalone script to compile main.gaia into Kotlin code
 * Run with: kotlinc -classpath . compile-gaia.kt -include-runtime -d compile-gaia.jar
 * Then: java -jar compile-gaia.jar
 */
fun main() {
    println("GaiaScript Compiler")
    println("===================")
    
    try {
        val kotlinCompilerService = KotlinCompilerService()
        
        // Read main.gaia file
        val mainGaiaPath = Paths.get("main.gaia")
        if (!Files.exists(mainGaiaPath)) {
            println("Error: main.gaia file not found")
            return
        }
        
        println("Reading main.gaia...")
        val gaiaScript = Files.readString(mainGaiaPath)
        
        // Generate Kotlin code
        println("Generating Kotlin code...")
        val kotlinCode = kotlinCompilerService.generateKotlinFromGaiaScript(gaiaScript, "GaiaSpace")
        
        // Write the Kotlin code to a file
        val outputPath = Paths.get("GaiaSpaceGenerated.kt")
        Files.writeString(outputPath, kotlinCode)
        println("Generated Kotlin code written to: ${outputPath.toAbsolutePath()}")
        
        // Compile the Kotlin code
        println("Compiling Kotlin code...")
        val result = kotlinCompilerService.compileKotlinCode(kotlinCode, "com.gaiaspace.generated")
        
        // Print the result
        println("Compilation result: ${result.success}")
        println("Message: ${result.message}")
        println("Compiled classes: ${result.compiledClasses}")
        
        // Clean up
        kotlinCompilerService.cleanup()
        
    } catch (e: Exception) {
        println("Error: ${e.message}")
        e.printStackTrace()
    }
}