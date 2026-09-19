---
name: android-ai-integration
user-invocable: false
description: Use when integrating TFLite, llama.cpp, or Hugging Face models in Android/Jetpack Compose apps. Covers ML inference, camera-based AI, quantized LLM deployment, and model downloading.
allowed-tools:
  - Read
  - Write
  - Edit
  - Bash
  - Grep
  - Glob
---

# Android AI Integration

Integration of TFLite, llama.cpp, and Hugging Face quantized models in Jetpack Compose apps.

## Architecture Overview

```
DogSense AI
├── CameraX → Frame capture
├── TFLite → Dog emotion classification (5 classes)
├── llama.cpp → Quantized LLM inference (GGUF format)
├── Hugging Face Hub API → Dynamic model download
└── Compose UI → Display results
```

## SDK Requirements

```kotlin
android {
    compileSdk = 35
    defaultConfig {
        minSdk = 26       // NNAPI support, ~93% device coverage
        targetSdk = 35
    }
}
```

## Dependencies

```kotlin
dependencies {
    // Compose BOM
    val composeBom = platform("androidx.compose:compose-bom:2024.02.00")
    implementation(composeBom)
    
    // Core
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    
    // Compose UI
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")
    
    // CameraX
    implementation("androidx.camera:camera-core:1.3.1")
    implementation("androidx.camera:camera-camera2:1.3.1")
    implementation("androidx.camera:camera-lifecycle:1.3.1")
    implementation("androidx.camera:camera-view:1.3.1")
    
    // TFLite
    implementation("org.tensorflow:tensorflow-lite:2.14.0")
    implementation("org.tensorflow:tensorflow-lite-support:0.4.4")
    
    // llama.cpp Android (build from source or use .aar)
    // implementation("com.github.nice-dev:llama-android:1.0.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

## 1. TFLite Integration (Dog Emotion Detection)

### Model Setup

```kotlin
// assets/model.tflite — 24MB, 5-class emotion classifier
class DogEmotionClassifier(private val context: Context) {
    private var interpreter: Interpreter? = null
    
    init {
        val model = loadModelFile("model.tflite")
        val options = Interpreter.Options().apply {
            setNumThreads(4)
            // Uncomment for GPU acceleration:
            // addDelegate(GpuDelegate())
        }
        interpreter = Interpreter(model, options)
    }
    
    private fun loadModelFile(filename: String): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(filename)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        return fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            fileDescriptor.startOffset,
            fileDescriptor.declaredLength
        )
    }
    
    fun classify(bitmap: Bitmap): EmotionResult {
        val input = preprocessImage(bitmap)
        val output = Array(1) { FloatArray(5) }
        
        interpreter?.run(input, output)
        
        val emotions = listOf("alert", "angry", "frown", "happy", "relax")
        val maxIndex = output[0].indices.maxByOrNull { output[0][it] } ?: 0
        
        return EmotionResult(
            emotion = emotions[maxIndex],
            confidence = output[0][maxIndex],
            allProbabilities = emotions.zip(output[0].toList()).toMap()
        )
    }
    
    private fun preprocessImage(bitmap: Bitmap): ByteBuffer {
        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val buffer = ByteBuffer.allocateDirect(4 * 224 * 224 * 3)
        buffer.order(ByteOrder.nativeOrder())
        
        val pixels = IntArray(224 * 224)
        resized.getPixels(pixels, 0, 224, 0, 0, 224, 224)
        
        for (pixel in pixels) {
            buffer.putFloat(((pixel shr 16 and 0xFF) / 255.0f))
            buffer.putFloat(((pixel shr 8 and 0xFF) / 255.0f))
            buffer.putFloat(((pixel and 0xFF) / 255.0f))
        }
        
        return buffer
    }
    
    fun close() {
        interpreter?.close()
    }
}

data class EmotionResult(
    val emotion: String,
    val confidence: Float,
    val allProbabilities: Map<String, Float>
)
```

### Compose Integration

```kotlin
@Composable
fun EmotionDetectionScreen() {
    val classifier = remember { DogEmotionClassifier(context) }
    val emotionResult = remember { mutableStateOf<EmotionResult?>(null) }
    val isAnalyzing = remember { mutableStateOf(false) }
    
    DisposableEffect(Unit) {
        onDispose { classifier.close() }
    }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Camera preview
        CameraPreview(
            onFrame = { bitmap ->
                if (!isAnalyzing.value) {
                    isAnalyzing.value = true
                    // Run inference on background thread
                    CoroutineScope(Dispatchers.Default).launch {
                        val result = classifier.classify(bitmap)
                        withContext(Dispatchers.Main) {
                            emotionResult.value = result
                            isAnalyzing.value = false
                        }
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        )
        
        // Emotion display
        emotionResult.value?.let { result ->
            EmotionCard(result)
        }
    }
}

@Composable
fun EmotionCard(result: EmotionResult) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (result.emotion) {
                "happy" -> Color(0xFF4CAF50)
                "relax" -> Color(0xFF2196F3)
                "alert" -> Color(0xFFFF9800)
                "angry" -> Color(0xFFF44336)
                "frown" -> Color(0xFF9C27B0)
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = result.emotion.uppercase(),
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${(result.confidence * 100).toInt()}% confidence",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}
```

## 2. llama.cpp Android (Quantized LLM)

### GGUF Model Download from Hugging Face

```kotlin
class HuggingFaceModelDownloader(private val context: Context) {
    
    suspend fun downloadModel(
        modelId: String,
        fileName: String,
        onProgress: (Float) -> Unit = {}
    ): File {
        val cacheDir = File(context.filesDir, "models")
        cacheDir.mkdirs()
        
        val outputFile = File(cacheDir, fileName)
        if (outputFile.exists()) {
            return outputFile  // Already downloaded
        }
        
        val url = "https://huggingface.co/$modelId/resolve/main/$fileName"
        
        return withContext(Dispatchers.IO) {
            val connection = URL(url).openConnection()
            val totalSize = connection.contentLength.toLong()
            
            connection.inputStream.use { input ->
                outputFile.outputStream().use { output ->
                    val buffer = ByteArray(8192)
                    var bytesRead: Int
                    var totalRead = 0L
                    
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        totalRead += bytesRead
                        onProgress(totalRead.toFloat() / totalSize)
                    }
                }
            }
            
            outputFile
        }
    }
}

// Usage
val downloader = HuggingFaceModelDownloader(context)
val modelFile = downloader.downloadModel(
    modelId = "TheBloke/Llama-2-7B-Chat-GGUF",
    fileName = "llama-2-7b-chat.Q4_K_M.gguf",
    onProgress = { progress ->
        Log.d("Download", "Progress: ${(progress * 100).toInt()}%")
    }
)
```

### llama.cpp Inference

```kotlin
class LlamaInference(private val context: Context) {
    private var llamaContext: Long = 0
    
    init {
        // Load native library
        System.loadLibrary("llama-android")
    }
    
    external fun nativeLoadModel(modelPath: String, numThreads: Int): Long
    external fun nativeGenerate(context: Long, prompt: String, maxTokens: Int): String
    external fun nativeFreeContext(context: Long)
    
    fun loadModel(modelPath: String, numThreads: Int = 4) {
        llamaContext = nativeLoadModel(modelPath, numThreads)
    }
    
    suspend fun generate(
        prompt: String,
        maxTokens: Int = 256
    ): String = withContext(Dispatchers.Default) {
        nativeGenerate(llamaContext, prompt, maxTokens)
    }
    
    fun close() {
        if (llamaContext != 0L) {
            nativeFreeContext(llamaContext)
            llamaContext = 0
        }
    }
}
```

### Compose + LLM Integration

```kotlin
@Composable
fun DogChatScreen(
    emotionResult: EmotionResult,
    llamaInference: LlamaInference
) {
    var response by remember { mutableStateOf("Analyzing your dog's emotion...") }
    var isGenerating by remember { mutableStateOf(false) }
    
    LaunchedEffect(emotionResult) {
        isGenerating = true
        val prompt = """
            You are a dog behavior expert. A dog is showing ${emotionResult.emotion} emotion 
            with ${(emotionResult.confidence * 100).toInt()}% confidence.
            
            Explain what this emotion means and give advice to the dog owner.
            Keep it brief and friendly.
        """.trimIndent()
        
        response = llamaInference.generate(prompt, maxTokens = 150)
        isGenerating = false
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Emotion result header
        EmotionCard(emotionResult)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // LLM response
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "AI Assistant",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                if (isGenerating) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    Text(
                        text = response,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}
```

## 3. CameraX + AI Pipeline

### Real-time Inference

```kotlin
@Composable
fun CameraWithAI(
    classifier: DogEmotionClassifier,
    llamaInference: LlamaInference
) {
    val emotionResult = remember { mutableStateOf<EmotionResult?>(null) }
    val llmResponse = remember { mutableStateOf("") }
    val isProcessing = remember { mutableStateOf(false) }
    
    // Analyze camera frames
    LaunchedEffect(Unit) {
        while (true) {
            if (!isProcessing.value) {
                isProcessing.value = true
                
                // Capture frame from CameraX
                val bitmap = captureFrame()
                
                // Run TFLite inference
                val result = classifier.classify(bitmap)
                emotionResult.value = result
                
                // Run LLM if confidence > 70%
                if (result.confidence > 0.7f) {
                    val prompt = "A dog is showing ${result.emotion}. What should the owner do?"
                    llmResponse.value = llamaInference.generate(prompt, maxTokens = 100)
                }
                
                isProcessing.value = false
            }
            
            delay(100) // 10 FPS max
        }
    }
    
    // UI
    Column {
        CameraPreview(modifier = Modifier.weight(1f))
        
        emotionResult.value?.let { result ->
            EmotionCard(result)
        }
        
        if (llmResponse.value.isNotEmpty()) {
            Text(text = llmResponse.value, modifier = Modifier.padding(16.dp))
        }
    }
}
```

## 4. Performance Optimization

### TFLite Optimization

```kotlin
val options = Interpreter.Options().apply {
    // Multi-threaded inference
    setNumThreads(4)
    
    // GPU acceleration (if available)
    try {
        addDelegate(GpuDelegate())
    } catch (e: Exception) {
        Log.w("TFLite", "GPU not available, using CPU")
    }
    
    // NNAPI delegation (hardware acceleration)
    // addDelegate(NnApiDelegate())
}
```

### llama.cpp Optimization

```kotlin
// Use smaller quantized models for mobile
val modelConfigs = mapOf(
    "fast" to "llama-3.2-1b.Q4_K_M.gguf",      // 700MB, fastest
    "balanced" to "phi-3-mini-4k.Q4_K_M.gguf",   // 2.2GB, balanced
    "quality" to "llama-2-7b-chat.Q4_K_M.gguf"   // 4GB, best quality
)
```

## 5. Error Handling

```kotlin
sealed class AIError {
    object ModelNotFound : AIError()
    object InsufficientMemory : AIError()
    object InferenceFailed : AIError()
    data class DownloadFailed(val message: String) : AIError()
}

@Composable
fun AIScreen() {
    val error = remember { mutableStateOf<AIError?>(null) }
    
    LaunchedEffect(Unit) {
        try {
            // Load models
            val classifier = DogEmotionClassifier(context)
            val modelFile = downloader.downloadModel("...", "model.gguf")
            llamaInference.loadModel(modelFile.absolutePath)
        } catch (e: OutOfMemoryError) {
            error.value = AIError.InsufficientMemory
        } catch (e: Exception) {
            error.value = AIError.InferenceFailed
        }
    }
    
    error.value?.let { err ->
        ErrorDialog(
            message = when (err) {
                is AIError.InsufficientMemory -> "Not enough memory. Use a smaller model."
                is AIError.InferenceFailed -> "Inference failed. Please try again."
                else -> "Unknown error"
            },
            onDismiss = { error.value = null }
        )
    }
}
```

## Anti-Patterns

### Don't Run Inference on Main Thread

Bad:
```kotlin
@Composable
fun BadExample(classifier: DogEmotionClassifier) {
    Button(onClick = {
        val result = classifier.classify(bitmap)  // Blocks UI!
        // ...
    }) {
        Text("Classify")
    }
}
```

Good:
```kotlin
@Composable
fun GoodExample(classifier: DogEmotionClassifier) {
    var result by remember { mutableStateOf<EmotionResult?>(null) }
    var isProcessing by remember { mutableStateOf(false) }
    
    Button(
        onClick = {
            isProcessing = true
            CoroutineScope(Dispatchers.Default).launch {
                val res = classifier.classify(bitmap)
                withContext(Dispatchers.Main) {
                    result = res
                    isProcessing = false
                }
            }
        },
        enabled = !isProcessing
    ) {
        if (isProcessing) {
            CircularProgressIndicator(modifier = Modifier.size(24.dp))
        } else {
            Text("Classify")
        }
    }
}
```

### Don't Recreate Models on Recomposition

Bad:
```kotlin
@Composable
fun BadExample() {
    val classifier = DogEmotionClassifier(context)  // Recreated every recomposition!
    // ...
}
```

Good:
```kotlin
@Composable
fun GoodExample() {
    val classifier = remember { DogEmotionClassifier(context) }
    
    DisposableEffect(Unit) {
        onDispose { classifier.close() }
    }
    // ...
}
```

## Related Skills

- **android-jetpack-compose**: UI patterns and state management
- **android-camera**: CameraX setup and configuration
- **android-native**: JNI/NDK for native library integration
