package com.wendev.kolas.ui.scan

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

/**
 * Thin, non-composable wrapper around CameraX. Lives here (not in the ViewModel)
 * because preview binding needs a [LifecycleOwner] and a [PreviewView]; the
 * business flow (permission, classify, persist, navigate) stays in
 * [ScanViewModel]. The screen only binds this to its lifecycle and asks it to
 * fire the shutter.
 */
class ScanCameraController(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private val onImageCaptured: (Bitmap) -> Unit,
    private val onError: (String?) -> Unit
) {

    private var cameraProvider: ProcessCameraProvider? = null
    private var imageCapture: ImageCapture? = null

    fun bind(previewView: PreviewView) {
        val providerFuture = ProcessCameraProvider.getInstance(context)
        providerFuture.addListener(
            {
                try {
                    val provider = providerFuture.get()
                    val preview = Preview.Builder().build().also { previewUseCase ->
                        previewUseCase.surfaceProvider = previewView.surfaceProvider
                    }
                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    provider.unbindAll()
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        capture
                    )
                    cameraProvider = provider
                    imageCapture = capture
                } catch (throwable: Throwable) {
                    onError(throwable.message)
                }
            },
            ContextCompat.getMainExecutor(context)
        )
    }

    fun capture() {
        val capture = imageCapture ?: return
        capture.takePicture(
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = try {
                        image.toUprightBitmap()
                    } finally {
                        image.close()
                    }
                    if (bitmap == null) {
                        onError(null)
                    } else {
                        onImageCaptured(bitmap)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception.message)
                }
            }
        )
    }

    fun unbind() {
        imageCapture = null
        cameraProvider?.unbindAll()
        cameraProvider = null
    }

    /** JPEG bytes live in plane 0; EXIF rotation is applied from [ImageProxy.imageInfo]. */
    private fun ImageProxy.toUprightBitmap(): Bitmap? {
        val buffer = planes.firstOrNull()?.buffer ?: return null
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)

        val decoded = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return null
        val rotation = imageInfo.rotationDegrees
        if (rotation == 0) return decoded

        val matrix = Matrix().apply { postRotate(rotation.toFloat()) }
        val rotated = Bitmap.createBitmap(
            decoded,
            0,
            0,
            decoded.width,
            decoded.height,
            matrix,
            true
        )
        if (rotated !== decoded) decoded.recycle()
        return rotated
    }
}
