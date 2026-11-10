package com.example.pjm_match

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.os.SystemClock
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.pjm_match.ui.MATCH
import com.example.pjm_match.ui.Palette
import com.example.pjm_match.ui.ProgressBarView
import com.example.pjm_match.ui.WRAP
import com.example.pjm_match.ui.horizontal
import com.example.pjm_match.ui.icon
import com.example.pjm_match.ui.label
import com.example.pjm_match.ui.onTap
import com.example.pjm_match.ui.oval
import com.example.pjm_match.ui.pad
import com.example.pjm_match.ui.place
import com.example.pjm_match.ui.roundedRect
import com.example.pjm_match.ui.vertical
import com.example.pjm_match.ui.withAlpha
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import java.util.Locale
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class AiTranslatorActivity : BaseActivity() {

    override val tabRoot = Tab.TRANSLATOR

    private lateinit var previewView: PreviewView
    private lateinit var permissionOverlayLayout: LinearLayout
    private lateinit var skeletonOverlayView: SkeletonOverlayView

    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    private var imageAnalysis: ImageAnalysis? = null
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    private var landmarkExtractor: PjmLandmarkExtractor? = null
    private var signClassifier: PjmSignClassifier? = null
    private val recognitionExecutor = Executors.newSingleThreadExecutor()

    private enum class TranslationState { IDLE, RECORDING, RECOGNIZING }

    private var translationState = TranslationState.IDLE

    @Volatile
    private var activeRecording: PjmSignRecording? = null

    private lateinit var translateButton: ActionButton
    private lateinit var resultText: TextView
    private lateinit var confidenceText: TextView
    private lateinit var confidenceBar: ProgressBarView
    private lateinit var statusBadge: LinearLayout
    private lateinit var statusBadgeText: TextView

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scaffold(Tab.TRANSLATOR) {
            place(eyebrow("Zrozum więcej"))
            place(screenTitle("Tłumacz miganie", Palette.primary), top = 6)
            place(bodyText("Pokaż znak do kamery. AI pomoże Ci odkryć jego znaczenie."), top = 10)
            place(cameraCard(), MATCH, 330, top = 22)
            translateButton = place(primaryButton("Rozpocznij tłumaczenie", Icons.trScan, iconFirst = true) {
                onTranslateButtonClicked()
            }, MATCH, 56, top = 24)
            place(resultCard(), top = 24)
            place(horizontal {
                gravity = Gravity.TOP
                place(icon(Icons.trInfo, Palette.textSecondary), WRAP, WRAP, top = 1)
                place(
                    label("Model działa na telefonie i analizuje ruch dłoni, rąk i twarzy. Zadbaj o dobre oświetlenie. AI może się mylić.", 14f, Palette.textSecondary),
                    0, WRAP, 1f, start = 12,
                )
            }, top = 20)
        }

        setTranslationState(TranslationState.IDLE)
        setupSignRecognition()
        setupMediaPipe()
        checkAndStartCamera()
    }

    private fun cameraCard(): FrameLayout = FrameLayout(this).apply {
        background = roundedRect(Palette.hero, 28)
        outlineProvider = ViewOutlineProvider.BACKGROUND
        clipToOutline = true

        previewView = place(PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }, MATCH, MATCH)

        skeletonOverlayView = place(SkeletonOverlayView(context), MATCH, MATCH)

        permissionOverlayLayout = place(vertical {
            gravity = Gravity.CENTER
            setBackgroundColor(Palette.overlay)
            pad(28)
            visibility = View.GONE
            place(icon(Icons.permissionCamera, Color.WHITE), WRAP, WRAP)
            place(label("Wymagany jest dostęp do kamery, aby korzystać z tłumacza AI", 15f, Color.WHITE, 700) {
                gravity = Gravity.CENTER
            }, WRAP, WRAP, top = 14)
            place(ActionButton(context, Palette.heroButtonText).apply {
                background = roundedRect(Palette.accentStrong, 16)
                setContent("Zezwól na dostęp")
                onTap(16, Palette.ripple) { requestCameraPermission() }
            }, WRAP, 48, top = 18)
        }, MATCH, MATCH)

        place(horizontal {
            background = roundedRect(Palette.overlay, 100)
            pad(12, 7)
            place(icon(Icons.trCamera, Palette.accentStrong), WRAP, WRAP, end = 8)
            place(label("PODGLĄD NA ŻYWO", 12f, Color.WHITE, 700) { letterSpacing = 0.03f }, WRAP, WRAP)
        }, WRAP, WRAP, top = 16, start = 16)

        place(FrameLayout(context).apply {
            background = oval(Color.WHITE.withAlpha(0.9f))
            contentDescription = "Przełącz kamerę"
            place(icon(Icons.trSwitchCamera, Palette.primary), WRAP, WRAP, gravity = Gravity.CENTER)
            onTap(22, Palette.ripple) { toggleCamera() }
        }, 44, 44, top = 14, end = 14, gravity = Gravity.TOP or Gravity.END)

        place(horizontal {
            background = roundedRect(Palette.overlay, 100)
            pad(18, 9)
            place(icon(Icons.trScanFace, Palette.accentStrong), WRAP, WRAP, end = 8)
            place(label("Dłonie i twarz w kadrze", 14f, Color.WHITE, 500), WRAP, WRAP)
        }, WRAP, WRAP, bottom = 16, gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
    }

    private fun resultCard(): LinearLayout = card(padding = 22) {
        place(horizontal {
            place(icon(Icons.trSparkles, Palette.primary), WRAP, WRAP, end = 10)
            place(label("WYNIK AI", 13f, Palette.primary, 700) { letterSpacing = 0.04f }, 0, WRAP, 1f)
            statusBadge = place(pill("Gotowy", Palette.accent, Palette.accentTint, sizeSp = 13f), WRAP, WRAP)
            statusBadgeText = statusBadge.getChildAt(statusBadge.childCount - 1) as TextView
        })
        resultText = place(label("Pokaż znak, aby zobaczyć wynik", 23f, Palette.primary, 700), top = 16)
        place(horizontal {
            place(label("Pewność rozpoznania", 14.5f, Palette.textSecondary), 0, WRAP, 1f)
            confidenceText = place(label("—", 16f, Palette.accent, 700), WRAP, WRAP)
        }, top = 16)
        confidenceBar = place(ProgressBarView(context, Palette.accentStrong, Palette.tint), MATCH, 8, top = 10)
    }

    private fun setupSignRecognition() {
        try {
            signClassifier = PjmSignClassifier(this, signs)
        } catch (t: Throwable) {
            t.printStackTrace()
            toast("Nie udało się wczytać modelu PJM", long = true)
        }
    }

    private fun setupMediaPipe() {
        try {
            landmarkExtractor = PjmLandmarkExtractor(this) { skeleton ->
                skeletonOverlayView.setSkeleton(skeleton)
                activeRecording?.add(skeleton.timestampMs, skeleton.toModelFrame())
            }
        } catch (t: Throwable) {
            t.printStackTrace()
            toast("MediaPipe niedostępne na tym urządzeniu", long = true)
        }
    }

    private fun onTranslateButtonClicked() {
        when (translationState) {
            TranslationState.IDLE -> startRecording()
            TranslationState.RECORDING -> stopRecordingAndRecognize()
            TranslationState.RECOGNIZING -> Unit
        }
    }

    private fun startRecording() {
        if (signClassifier == null || landmarkExtractor == null) {
            toast("Tłumacz jest niedostępny na tym urządzeniu")
            return
        }
        activeRecording = PjmSignRecording()
        setTranslationState(TranslationState.RECORDING)
    }

    private fun stopRecordingAndRecognize() {
        val recording = activeRecording ?: return
        activeRecording = null
        val clip = recording.resampledToTrainingFps().take(PjmSignClassifier.MAX_FRAMES)
        if (clip.size < PjmSignClassifier.MIN_FRAMES) {
            toast("Nagranie jest za krótkie")
            setTranslationState(TranslationState.IDLE)
            return
        }

        val classifier = signClassifier ?: return
        setTranslationState(TranslationState.RECOGNIZING)
        recognitionExecutor.execute {
            val prediction = runCatching { classifier.classify(clip) }
            runOnUiThread {
                if (isDestroyed) return@runOnUiThread
                prediction
                    .onSuccess { showRecognizedSign(it) }
                    .onFailure {
                        it.printStackTrace()
                        toast("Błąd rozpoznawania znaku")
                    }
                setTranslationState(TranslationState.IDLE)
            }
        }
    }

    private fun setTranslationState(state: TranslationState) {
        translationState = state
        translateButton.isEnabled = state != TranslationState.RECOGNIZING
        when (state) {
            TranslationState.IDLE -> translateButton.setContent("Rozpocznij tłumaczenie", Icons.trScan, iconFirst = true)
            TranslationState.RECORDING -> translateButton.setContent("Zakończ i rozpoznaj", Icons.trScan, iconFirst = true)
            TranslationState.RECOGNIZING -> translateButton.setContent("Rozpoznawanie…", Icons.trSparkles, iconFirst = true)
        }
        statusBadgeText.text = when (state) {
            TranslationState.IDLE -> "Gotowy"
            TranslationState.RECORDING -> "Nagrywanie"
            TranslationState.RECOGNIZING -> "Analiza"
        }
    }

    private fun showRecognizedSign(prediction: SignPrediction) {
        val recognized = prediction.gloss != PjmSignClassifier.BLANK_GLOSS
        val confidence = if (recognized) prediction.confidence else 0f
        resultText.text = if (recognized) "Rozpoznano: ${displayGloss(prediction.gloss)}" else "Nie rozpoznano znaku"
        confidenceText.text = if (recognized) "${(confidence * 100).roundToInt()}%" else "—"
        confidenceBar.fraction = confidence
    }

    private fun displayGloss(gloss: String): String {
        val polish = Locale.forLanguageTag("pl")
        return gloss.replace('-', ' ').lowercase(polish).replaceFirstChar { it.titlecase(polish) }
    }

    private fun checkAndStartCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            permissionOverlayLayout.visibility = View.GONE
            startCamera()
        } else {
            permissionOverlayLayout.visibility = View.VISIBLE
            requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionOverlayLayout.visibility = View.GONE
                startCamera()
            } else {
                permissionOverlayLayout.visibility = View.VISIBLE
                toast("Wymagany dostęp do kamery do działania tłumacza", long = true)
            }
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, ContextCompat.getMainExecutor(this))
    }

    private fun bindCameraUseCases() {
        val provider = cameraProvider ?: return
        provider.unbindAll()

        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        val analysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()

        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
            processImageProxy(imageProxy)
        }

        imageAnalysis = analysis

        try {
            provider.bindToLifecycle(this, cameraSelector, preview, analysis)
        } catch (e: Throwable) {
            e.printStackTrace()
            toast("Błąd uruchamiania kamery: ${e.localizedMessage}")
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        try {
            val bitmap = imageProxy.toBitmap()
            val matrix = Matrix().apply { postRotate(imageProxy.imageInfo.rotationDegrees.toFloat()) }
            val uprightBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

            skeletonOverlayView.setImageInfo(
                uprightBitmap.width,
                uprightBitmap.height,
                mirror = cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA
            )

            landmarkExtractor?.detectAsync(uprightBitmap, SystemClock.uptimeMillis())
        } catch (e: Throwable) {
            e.printStackTrace()
        } finally {
            imageProxy.close()
        }
    }

    private fun toggleCamera() {
        cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
            CameraSelector.DEFAULT_BACK_CAMERA
        } else {
            CameraSelector.DEFAULT_FRONT_CAMERA
        }
        bindCameraUseCases()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraProvider?.unbindAll()
        cameraExecutor.shutdown()
        cameraExecutor.awaitTermination(500, TimeUnit.MILLISECONDS)
        landmarkExtractor?.close()
        recognitionExecutor.shutdown()
        recognitionExecutor.awaitTermination(2, TimeUnit.SECONDS)
        signClassifier?.close()
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    private inner class SkeletonOverlayView(context: Context) : View(context) {

        @Volatile private var pose: List<NormalizedLandmark> = emptyList()
        @Volatile private var face: List<NormalizedLandmark> = emptyList()
        @Volatile private var hands: List<List<NormalizedLandmark>> = emptyList()

        @Volatile private var imageWidth = 0
        @Volatile private var imageHeight = 0
        @Volatile private var mirror = false

        private var scale = 1f
        private var offsetX = 0f
        private var offsetY = 0f

        private val poseLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00E5FF")
            style = Paint.Style.STROKE
            strokeWidth = dpToPx(context, 3).toFloat()
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        private val poseJointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFD700")
            style = Paint.Style.FILL
        }

        private val handLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF4081")
            style = Paint.Style.STROKE
            strokeWidth = dpToPx(context, 3).toFloat()
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        private val handJointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00E676")
            style = Paint.Style.FILL
        }

        private val bracketPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Palette.accentStrong
            style = Paint.Style.STROKE
            strokeWidth = dpToPx(context, 4).toFloat()
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        fun setImageInfo(width: Int, height: Int, mirror: Boolean) {
            imageWidth = width
            imageHeight = height
            this.mirror = mirror
        }

        fun setSkeleton(skeleton: PjmSkeleton) {
            pose = skeleton.pose
            face = skeleton.face
            hands = listOf(skeleton.leftHand, skeleton.rightHand).filter { it.isNotEmpty() }
            postInvalidate()
        }

        private fun px(lm: NormalizedLandmark): Float {
            val x = if (mirror) 1f - lm.x() else lm.x()
            return x * imageWidth * scale + offsetX
        }

        private fun py(lm: NormalizedLandmark): Float = lm.y() * imageHeight * scale + offsetY

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()
            if (w <= 0 || h <= 0) return

            drawCornerBrackets(canvas, w, h)

            if (imageWidth <= 0 || imageHeight <= 0) return
            scale = maxOf(w / imageWidth, h / imageHeight)
            offsetX = (w - imageWidth * scale) / 2f
            offsetY = (h - imageHeight * scale) / 2f

            val pose = pose
            if (pose.size >= PjmLandmarks.POSE_LEN) drawPoseSkeleton(canvas, pose)

            val face = face
            if (face.size >= PjmLandmarks.FACE_LEN) drawFacePoints(canvas, face)

            hands.forEach { hand ->
                if (hand.size >= PjmLandmarks.LH_LEN) drawHandSkeleton(canvas, hand)
            }
        }

        private fun drawCornerBrackets(canvas: Canvas, w: Float, h: Float) {
            val inset = dpToPx(context, 28).toFloat()
            val cornerLen = dpToPx(context, 40).toFloat()
            val radius = dpToPx(context, 12).toFloat()

            val left = inset
            val top = inset
            val right = w - inset
            val bottom = h - inset

            val pathTL = Path().apply {
                moveTo(left, top + cornerLen)
                lineTo(left, top + radius)
                quadTo(left, top, left + radius, top)
                lineTo(left + cornerLen, top)
            }
            canvas.drawPath(pathTL, bracketPaint)

            val pathTR = Path().apply {
                moveTo(right - cornerLen, top)
                lineTo(right - radius, top)
                quadTo(right, top, right, top + radius)
                lineTo(right, top + cornerLen)
            }
            canvas.drawPath(pathTR, bracketPaint)

            val pathBL = Path().apply {
                moveTo(left, bottom - cornerLen)
                lineTo(left, bottom - radius)
                quadTo(left, bottom, left + radius, bottom)
                lineTo(left + cornerLen, bottom)
            }
            canvas.drawPath(pathBL, bracketPaint)

            val pathBR = Path().apply {
                moveTo(right, bottom - cornerLen)
                lineTo(right, bottom - radius)
                quadTo(right, bottom, right - radius, bottom)
                lineTo(right - cornerLen, bottom)
            }
            canvas.drawPath(pathBR, bracketPaint)
        }

        private fun drawPoseSkeleton(canvas: Canvas, landmarks: List<NormalizedLandmark>) {
            val poseConnections = listOf(
                Pair(11, 12), Pair(11, 23), Pair(12, 24), Pair(23, 24),
                Pair(11, 13), Pair(13, 15),
                Pair(12, 14), Pair(14, 16),
            )

            for ((startIdx, endIdx) in poseConnections) {
                val p1 = landmarks[startIdx]
                val p2 = landmarks[endIdx]
                canvas.drawLine(px(p1), py(p1), px(p2), py(p2), poseLinePaint)
            }

            val nodeRadius = dpToPx(context, 5).toFloat()
            for (idx in PjmLandmarks.BODY) {
                val landmark = landmarks[idx]
                canvas.drawCircle(px(landmark), py(landmark), nodeRadius, poseJointPaint)
            }
        }

        private fun drawFacePoints(canvas: Canvas, landmarks: List<NormalizedLandmark>) {
            val nodeRadius = dpToPx(context, 2).toFloat()
            for (idx in PjmLandmarks.FACE) {
                canvas.drawCircle(px(landmarks[idx]), py(landmarks[idx]), nodeRadius, poseJointPaint)
            }
            for (idx in PjmLandmarks.MOUTH) {
                canvas.drawCircle(px(landmarks[idx]), py(landmarks[idx]), nodeRadius, handJointPaint)
            }
        }

        private fun drawHandSkeleton(canvas: Canvas, landmarks: List<NormalizedLandmark>) {
            val handConnections = listOf(
                Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 4),
                Pair(0, 5), Pair(5, 6), Pair(6, 7), Pair(7, 8),
                Pair(0, 9), Pair(9, 10), Pair(10, 11), Pair(11, 12),
                Pair(0, 13), Pair(13, 14), Pair(14, 15), Pair(15, 16),
                Pair(0, 17), Pair(17, 18), Pair(18, 19), Pair(19, 20),
                Pair(5, 9), Pair(9, 13), Pair(13, 17)
            )

            for ((startIdx, endIdx) in handConnections) {
                val p1 = landmarks[startIdx]
                val p2 = landmarks[endIdx]
                canvas.drawLine(px(p1), py(p1), px(p2), py(p2), handLinePaint)
            }

            val nodeRadius = dpToPx(context, 4).toFloat()
            for (idx in PjmLandmarks.HAND) {
                canvas.drawCircle(px(landmarks[idx]), py(landmarks[idx]), nodeRadius, handJointPaint)
            }
        }
    }
}
