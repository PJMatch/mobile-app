package com.example.pjm_match

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.SystemClock
import android.view.Gravity
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import java.util.concurrent.Executors

class AiTranslatorActivity : AppCompatActivity() {

    private lateinit var previewView: PreviewView
    private lateinit var permissionOverlayLayout: LinearLayout
    private lateinit var skeletonOverlayView: SkeletonOverlayView

    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    private var imageAnalysis: ImageAnalysis? = null
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    private var poseLandmarker: PoseLandmarker? = null
    private var handLandmarker: HandLandmarker? = null

    private var currentSignIndex = 0

    private val recognizedSigns = listOf(
        Pair("Dzień dobry", "96%"),
        Pair("Dziękuję", "98%"),
        Pair("Jak się masz?", "94%"),
        Pair("Cześć", "97%"),
        Pair("Do widzenia", "95%"),
    )

    private lateinit var resultText: TextView
    private lateinit var confidenceText: TextView
    private lateinit var progressFillView: View

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        val mainContainer = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.parseColor("#F5F5FA"))
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        }

        val scrollView = ScrollView(this).apply {
            isFillViewport = true
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1f
            )
        }

        val rootLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 48, 40, 40)
        }

        // --- Header Section ---
        val headerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 12)
        }

        val titleTextLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val subtitleTag = TextView(this).apply {
            text = "ZROZUM WIĘCEJ"
            textSize = 12f
            setTextColor(Color.parseColor("#D3821C"))
            setTypeface(null, Typeface.BOLD)
            letterSpacing = 0.08f
        }

        val mainTitle = TextView(this).apply {
            text = "Tłumacz miganie"
            textSize = 28f
            setTextColor(Color.parseColor("#1C1635"))
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 4, 0, 0)
        }

        titleTextLayout.addView(subtitleTag)
        titleTextLayout.addView(mainTitle)

        val topRightSparkleBg = GradientDrawable().apply {
            setColor(Color.parseColor("#EBE9FC"))
            cornerRadius = 48f
        }

        val topRightSparkleBtn = TextView(this).apply {
            text = "✨"
            textSize = 20f
            gravity = Gravity.CENTER
            background = topRightSparkleBg
            layoutParams = LinearLayout.LayoutParams(96, 96)
        }

        headerLayout.addView(titleTextLayout)
        headerLayout.addView(topRightSparkleBtn)

        val descriptionText = TextView(this).apply {
            text = "Pokaż znak do kamery. AI pomoże Ci odkryć jego znaczenie."
            textSize = 14f
            setTextColor(Color.parseColor("#7E7A93"))
            setPadding(0, 0, 0, 32)
        }

        rootLayout.addView(headerLayout)
        rootLayout.addView(descriptionText)

        // --- Camera Card Container ---
        val cameraCard = FrameLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dpToPx(this@AiTranslatorActivity, 360)
            ).apply {
                bottomMargin = 28
            }

            val bgDrawable = GradientDrawable().apply {
                setColor(Color.parseColor("#2A204B"))
                cornerRadius = dpToPx(this@AiTranslatorActivity, 32).toFloat()
            }
            background = bgDrawable
            outlineProvider = ViewOutlineProvider.BACKGROUND
            clipToOutline = true
        }

        // CameraX PreviewView
        previewView = PreviewView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
        cameraCard.addView(previewView)

        // MediaPipe Skeleton Overlay View
        skeletonOverlayView = SkeletonOverlayView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        cameraCard.addView(skeletonOverlayView)

        // Permission overlay inside camera card if permission not granted
        permissionOverlayLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#EE2A204B"))
            setPadding(32, 32, 32, 32)
            visibility = View.GONE
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

            val iconView = TextView(this@AiTranslatorActivity).apply {
                text = "📷"
                textSize = 36f
                gravity = Gravity.CENTER
                setPadding(0, 0, 0, 16)
            }

            val permText = TextView(this@AiTranslatorActivity).apply {
                text = "Wymagany jest dostęp do kamery,\naby korzystać z tłumacza AI"
                textSize = 14f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                setTypeface(null, Typeface.BOLD)
                setPadding(0, 0, 0, 24)
            }

            val grantBtnBg = GradientDrawable().apply {
                setColor(Color.parseColor("#FFC107"))
                cornerRadius = 24f
            }

            val grantBtn = Button(this@AiTranslatorActivity).apply {
                text = "Zezwól na dostęp do kamery"
                textSize = 14f
                setTextColor(Color.parseColor("#1C1635"))
                background = grantBtnBg
                setTypeface(null, Typeface.BOLD)
                setPadding(32, 16, 32, 16)
                setOnClickListener {
                    requestCameraPermission()
                }
            }

            addView(iconView)
            addView(permText)
            addView(grantBtn)
        }
        cameraCard.addView(permissionOverlayLayout)

        // Top-Left Badge: "📷 PODGLĄD DEMONSTRACYJNY"
        val demoBadgeBg = GradientDrawable().apply {
            setColor(Color.parseColor("#CC130C2E"))
            cornerRadius = 32f
        }

        val demoBadge = TextView(this).apply {
            text = "📷 PODGLĄD DEMONSTRACYJNY"
            textSize = 10f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            background = demoBadgeBg
            setPadding(28, 16, 28, 16)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                topMargin = dpToPx(this@AiTranslatorActivity, 16)
                marginStart = dpToPx(this@AiTranslatorActivity, 16)
            }
        }
        cameraCard.addView(demoBadge)

        // Top-Right Button: Switch Camera (Front/Back)
        val switchCamBg = GradientDrawable().apply {
            setColor(Color.parseColor("#D0FFFFFF"))
            cornerRadius = 48f
        }

        val switchCamBtn = TextView(this).apply {
            text = "🔄"
            textSize = 18f
            gravity = Gravity.CENTER
            background = switchCamBg
            layoutParams = FrameLayout.LayoutParams(
                dpToPx(this@AiTranslatorActivity, 44),
                dpToPx(this@AiTranslatorActivity, 44)
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                topMargin = dpToPx(this@AiTranslatorActivity, 16)
                marginEnd = dpToPx(this@AiTranslatorActivity, 16)
            }
            setOnClickListener {
                toggleCamera()
            }
        }
        cameraCard.addView(switchCamBtn)

        // Bottom-Center Pill Badge: "Dłonie i twarz w kadrze"
        val centerPillBg = GradientDrawable().apply {
            setColor(Color.parseColor("#E0130C2E"))
            cornerRadius = 36f
        }

        val centerPill = TextView(this).apply {
            text = "⛶  Dłonie i twarz w kadrze"
            textSize = 12f
            setTextColor(Color.WHITE)
            setTypeface(null, Typeface.BOLD)
            background = centerPillBg
            setPadding(36, 20, 36, 20)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                bottomMargin = dpToPx(this@AiTranslatorActivity, 18)
            }
        }
        cameraCard.addView(centerPill)

        rootLayout.addView(cameraCard)

        // --- Action Button: "Rozpocznij tłumaczenie" ---
        val translateButtonBg = GradientDrawable().apply {
            setColor(Color.parseColor("#2C2085"))
            cornerRadius = dpToPx(this@AiTranslatorActivity, 28).toFloat()
        }

        val translateButton = Button(this).apply {
            text = "⛶  Rozpocznij tłumaczenie"
            textSize = 16f
            setTextColor(Color.WHITE)
            background = translateButtonBg
            setTypeface(null, Typeface.BOLD)
            setPadding(32, 36, 32, 36)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 28
            }
            setOnClickListener {
                cycleNextRecognizedSign()
            }
        }
        rootLayout.addView(translateButton)

        // --- AI Result Card ("WYNIK AI") ---
        val aiResultCardBg = GradientDrawable().apply {
            setColor(Color.WHITE)
            setStroke(2, Color.parseColor("#E4E2F0"))
            cornerRadius = dpToPx(this@AiTranslatorActivity, 32).toFloat()
        }

        val aiResultCard = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            background = aiResultCardBg
            setPadding(36, 32, 36, 32)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 24
            }
        }

        // Header Row inside AI Result Card
        val cardHeaderRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 16)
        }

        val aiTag = TextView(this).apply {
            text = "✨  WYNIK AI"
            textSize = 12f
            setTextColor(Color.parseColor("#3B1895"))
            setTypeface(null, Typeface.BOLD)
            letterSpacing = 0.05f
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val exampleBadgeBg = GradientDrawable().apply {
            setColor(Color.parseColor("#FFEED6"))
            cornerRadius = 24f
        }

        val exampleBadge = TextView(this).apply {
            text = "Przykład"
            textSize = 12f
            setTextColor(Color.parseColor("#D3821C"))
            setTypeface(null, Typeface.BOLD)
            background = exampleBadgeBg
            setPadding(24, 10, 24, 10)
        }

        cardHeaderRow.addView(aiTag)
        cardHeaderRow.addView(exampleBadge)

        // Recognized text
        resultText = TextView(this).apply {
            text = "Rozpoznano: ${recognizedSigns[0].first}"
            textSize = 20f
            setTextColor(Color.parseColor("#1C1635"))
            setTypeface(null, Typeface.BOLD)
            setPadding(0, 0, 0, 20)
        }

        // Confidence row
        val confidenceRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(0, 0, 0, 12)
        }

        val confidenceLabel = TextView(this).apply {
            text = "Pewność rozpoznania"
            textSize = 13f
            setTextColor(Color.parseColor("#7E7A93"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        confidenceText = TextView(this).apply {
            text = recognizedSigns[0].second
            textSize = 14f
            setTextColor(Color.parseColor("#D3821C"))
            setTypeface(null, Typeface.BOLD)
        }

        confidenceRow.addView(confidenceLabel)
        confidenceRow.addView(confidenceText)

        // Progress bar
        val progressTrackBg = GradientDrawable().apply {
            setColor(Color.parseColor("#E4E2F0"))
            cornerRadius = 16f
        }

        val progressTrack = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            background = progressTrackBg
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 16)
        }

        val progressFillBg = GradientDrawable().apply {
            setColor(Color.parseColor("#D3821C"))
            cornerRadius = 16f
        }

        progressFillView = View(this).apply {
            background = progressFillBg
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 0.96f)
        }

        progressTrack.addView(progressFillView)

        aiResultCard.addView(cardHeaderRow)
        aiResultCard.addView(resultText)
        aiResultCard.addView(confidenceRow)
        aiResultCard.addView(progressTrack)

        rootLayout.addView(aiResultCard)

        // --- Disclaimer Note at Bottom ---
        val disclaimerLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.TOP
            setPadding(8, 0, 8, 32)
        }

        val infoIcon = TextView(this).apply {
            text = "ⓘ"
            textSize = 16f
            setTextColor(Color.parseColor("#7E7A93"))
            setPadding(0, 0, 12, 0)
        }

        val disclaimerText = TextView(this).apply {
            text = "Przykładowy kadr i wynik. To podgląd działania, nie tłumaczenie na żywo. AI może się mylić."
            textSize = 12f
            setTextColor(Color.parseColor("#7E7A93"))
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        disclaimerLayout.addView(infoIcon)
        disclaimerLayout.addView(disclaimerText)

        rootLayout.addView(disclaimerLayout)

        scrollView.addView(
            rootLayout,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            )
        )

        // --- Bottom Navigation Bar ---
        val bottomNavLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setBackgroundColor(Color.WHITE)
            setPadding(16, 16, 16, 24)
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        fun createNavItem(icon: String, label: String, isActive: Boolean, onClick: () -> Unit): LinearLayout {
            return LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                setOnClickListener { onClick() }

                val iconView = TextView(this@AiTranslatorActivity).apply {
                    text = icon
                    textSize = 20f
                    gravity = Gravity.CENTER
                }

                val labelView = TextView(this@AiTranslatorActivity).apply {
                    text = label
                    textSize = 10f
                    setTextColor(Color.parseColor(if (isActive) "#3B1895" else "#7E7A93"))
                    gravity = Gravity.CENTER
                    setTypeface(null, if (isActive) Typeface.BOLD else Typeface.NORMAL)
                }

                addView(iconView)
                addView(labelView)
            }
        }

        val navStart = createNavItem("🏠", "Start", false) {
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            })
            finish()
        }
        val navNauka = createNavItem("🔀", "Nauka", false) {
            startActivity(Intent(this, SignListActivity::class.java))
            finish()
        }
        val navAi = createNavItem("⛶", "Tłumacz AI", true) {}
        val navStats = createNavItem("📊", "Statystyki", false) {
            Toast.makeText(this, "Statystyki w przygotowaniu", Toast.LENGTH_SHORT).show()
        }
        val navProfile = createNavItem("👤", "Profil", false) {
            Toast.makeText(this, "Profil w przygotowaniu", Toast.LENGTH_SHORT).show()
        }

        bottomNavLayout.addView(navStart)
        bottomNavLayout.addView(navNauka)
        bottomNavLayout.addView(navAi)
        bottomNavLayout.addView(navStats)
        bottomNavLayout.addView(navProfile)

        mainContainer.addView(scrollView)
        mainContainer.addView(bottomNavLayout)

        setContentView(mainContainer)

        setupMediaPipe()
        checkAndStartCamera()
    }

    private fun setupMediaPipe() {
        try {
            val poseBaseOptions = BaseOptions.builder()
                .setModelAssetPath("pose_landmarker_lite.task")
                .build()

            val poseOptions = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(poseBaseOptions)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener { result, _ ->
                    runOnUiThread {
                        skeletonOverlayView.setPoseLandmarks(result.landmarks())
                    }
                }
                .setErrorListener { error ->
                    error.printStackTrace()
                }
                .build()

            poseLandmarker = PoseLandmarker.createFromOptions(this, poseOptions)

            val handBaseOptions = BaseOptions.builder()
                .setModelAssetPath("hand_landmarker.task")
                .build()

            val handOptions = HandLandmarker.HandLandmarkerOptions.builder()
                .setBaseOptions(handBaseOptions)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setNumHands(2)
                .setResultListener { result, _ ->
                    runOnUiThread {
                        skeletonOverlayView.setHandLandmarks(result.landmarks())
                    }
                }
                .setErrorListener { error ->
                    error.printStackTrace()
                }
                .build()

            handLandmarker = HandLandmarker.createFromOptions(this, handOptions)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun cycleNextRecognizedSign() {
        currentSignIndex = (currentSignIndex + 1) % recognizedSigns.size
        val (sign, confidence) = recognizedSigns[currentSignIndex]
        resultText.text = "Rozpoznano: $sign"
        confidenceText.text = confidence

        val percentFloat = confidence.replace("%", "").toFloatOrNull()?.div(100f) ?: 0.95f
        progressFillView.layoutParams = (progressFillView.layoutParams as LinearLayout.LayoutParams).apply {
            weight = percentFloat
        }

        Toast.makeText(this, "Aanalizowano kadr z kamery", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "Wymagany dostęp do kamery do działania tłumacza", Toast.LENGTH_LONG).show()
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
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Błąd uruchamiania kamery: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        val frameTime = SystemClock.uptimeMillis()
        val bitmap = imageProxy.toBitmap()

        val isFront = (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA)
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees

        val matrix = Matrix().apply {
            postRotate(rotationDegrees.toFloat())
            if (isFront) {
                postScale(-1f, 1f, bitmap.width / 2f, bitmap.height / 2f)
            }
        }

        val rotatedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )

        val mpImage = BitmapImageBuilder(rotatedBitmap).build()

        try {
            poseLandmarker?.detectAsync(mpImage, frameTime)
            handLandmarker?.detectAsync(mpImage, frameTime)
        } catch (e: Exception) {
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
        cameraExecutor.shutdown()
        poseLandmarker?.close()
        handLandmarker?.close()
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    /**
     * Custom Overlay View that draws MediaPipe Pose & Hand skeleton landmarks on top of the live camera feed.
     */
    private inner class SkeletonOverlayView(context: Context) : View(context) {

        private var poseLandmarksList: List<List<NormalizedLandmark>>? = null
        private var handLandmarksList: List<List<NormalizedLandmark>>? = null

        // Paint for Pose lines (Cyan `#00E5FF`)
        private val poseLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00E5FF")
            style = Paint.Style.STROKE
            strokeWidth = dpToPx(context, 3).toFloat()
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        // Paint for Pose joint nodes (Golden Amber `#FFD700`)
        private val poseJointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFD700")
            style = Paint.Style.FILL
        }

        // Paint for Hand lines (Magenta Pink `#FF4081`)
        private val handLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FF4081")
            style = Paint.Style.STROKE
            strokeWidth = dpToPx(context, 3).toFloat()
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        // Paint for Hand joint nodes (Bright Green `#00E676`)
        private val handJointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#00E676")
            style = Paint.Style.FILL
        }

        // Corner brackets paint
        private val bracketPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.parseColor("#FFB300")
            style = Paint.Style.STROKE
            strokeWidth = dpToPx(context, 4).toFloat()
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        fun setPoseLandmarks(landmarks: List<List<NormalizedLandmark>>) {
            this.poseLandmarksList = landmarks
            postInvalidate()
        }

        fun setHandLandmarks(landmarks: List<List<NormalizedLandmark>>) {
            this.handLandmarksList = landmarks
            postInvalidate()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()
            if (w <= 0 || h <= 0) return

            // 1. Always draw corner brackets framing
            drawCornerBrackets(canvas, w, h)

            // 2. Draw Pose Skeleton if detected
            poseLandmarksList?.forEach { pose ->
                if (pose.size >= 33) {
                    drawPoseSkeleton(canvas, pose, w, h)
                }
            }

            // 3. Draw Hand Skeleton if detected
            handLandmarksList?.forEach { hand ->
                if (hand.size >= 21) {
                    drawHandSkeleton(canvas, hand, w, h)
                }
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

        private fun drawPoseSkeleton(canvas: Canvas, landmarks: List<NormalizedLandmark>, w: Float, h: Float) {
            // Pose connections
            val poseConnections = listOf(
                // Face / Head
                Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(0, 4), Pair(4, 5), Pair(5, 6),
                Pair(3, 7), Pair(6, 8), Pair(9, 10),
                // Shoulders & Torso
                Pair(11, 12), Pair(11, 23), Pair(12, 24), Pair(23, 24),
                // Arms (Key for PJM sign language!)
                Pair(11, 13), Pair(13, 15), Pair(15, 17), Pair(15, 19), Pair(15, 21),
                Pair(12, 14), Pair(14, 16), Pair(16, 18), Pair(16, 20), Pair(16, 22),
                // Legs
                Pair(23, 25), Pair(25, 27), Pair(24, 26), Pair(26, 28)
            )

            // Draw bone connections
            for ((startIdx, endIdx) in poseConnections) {
                if (startIdx < landmarks.size && endIdx < landmarks.size) {
                    val p1 = landmarks[startIdx]
                    val p2 = landmarks[endIdx]

                    val x1 = p1.x() * w
                    val y1 = p1.y() * h
                    val x2 = p2.x() * w
                    val y2 = p2.y() * h

                    canvas.drawLine(x1, y1, x2, y2, poseLinePaint)
                }
            }

            // Draw joint nodes
            val nodeRadius = dpToPx(context, 5).toFloat()
            for (landmark in landmarks) {
                val cx = landmark.x() * w
                val cy = landmark.y() * h
                canvas.drawCircle(cx, cy, nodeRadius, poseJointPaint)
            }
        }

        private fun drawHandSkeleton(canvas: Canvas, landmarks: List<NormalizedLandmark>, w: Float, h: Float) {
            val handConnections = listOf(
                // Thumb
                Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 4),
                // Index finger
                Pair(0, 5), Pair(5, 6), Pair(6, 7), Pair(7, 8),
                // Middle finger
                Pair(0, 9), Pair(9, 10), Pair(10, 11), Pair(11, 12),
                // Ring finger
                Pair(0, 13), Pair(13, 14), Pair(14, 15), Pair(15, 16),
                // Pinky finger
                Pair(0, 17), Pair(17, 18), Pair(18, 19), Pair(19, 20),
                // Knuckles connection
                Pair(5, 9), Pair(9, 13), Pair(13, 17)
            )

            // Draw finger bones
            for ((startIdx, endIdx) in handConnections) {
                if (startIdx < landmarks.size && endIdx < landmarks.size) {
                    val p1 = landmarks[startIdx]
                    val p2 = landmarks[endIdx]

                    val x1 = p1.x() * w
                    val y1 = p1.y() * h
                    val x2 = p2.x() * w
                    val y2 = p2.y() * h

                    canvas.drawLine(x1, y1, x2, y2, handLinePaint)
                }
            }

            // Draw finger joint nodes
            val nodeRadius = dpToPx(context, 4).toFloat()
            for (landmark in landmarks) {
                val cx = landmark.x() * w
                val cy = landmark.y() * h
                canvas.drawCircle(cx, cy, nodeRadius, handJointPaint)
            }
        }
    }
}
