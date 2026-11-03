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
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
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
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.poselandmarker.PoseLandmarker
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.sqrt
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.min

data class Point3D(val x: Float, val y: Float, val z: Float)

data class FrameSkeleton(
    val timestampMs: Long,
    val poseLandmarks: List<Point3D>,
    val handLandmarks: List<List<Point3D>>
)

class MediaPipeDebugActivity : AppCompatActivity() {

    private var activeSignName: String = "Dzień dobry"
    private lateinit var previewView: PreviewView
    private lateinit var skeletonOverlayView: SkeletonOverlayView
    private lateinit var fpsTextView: TextView
    private lateinit var poseStatsTextView: TextView
    private lateinit var handStatsTextView: TextView
    private lateinit var recordingStatsTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var recordButton: Button
    private lateinit var compareButton: Button
    private lateinit var permissionOverlayLayout: LinearLayout

    private var cameraProvider: ProcessCameraProvider? = null
    private var cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    private var imageAnalysis: ImageAnalysis? = null
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    private var poseLandmarker: PoseLandmarker? = null
    private var handLandmarker: HandLandmarker? = null
    private var lastFrameTime = 0L

    private var frameCount = 0
    private var lastFpsTimestamp = SystemClock.uptimeMillis()
    private var currentFps = 0

    private var isRecording = false
    private var isCountingDown = false
    private var countdownTimer: CountDownTimer? = null
    private var autoStopTimer: CountDownTimer? = null
    private lateinit var countdownOverlayTextView: TextView
    private val recordedFrames = mutableListOf<FrameSkeleton>()
    private var referenceSequence: List<FrameSkeleton>? = null

    private var currentLatestPose: List<Point3D> = emptyList()
    private var currentLatestHands: List<List<Point3D>> = emptyList()

    companion object {
        private const val CAMERA_PERMISSION_REQUEST_CODE = 2002
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()

        activeSignName = intent.getStringExtra(SignActivity.EXTRA_SIGN) ?: "Dzień dobry"

        val mainLayout = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        previewView = PreviewView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        mainLayout.addView(previewView)

        skeletonOverlayView = SkeletonOverlayView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        mainLayout.addView(skeletonOverlayView)

        val topBarLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(32, 48, 32, 24)
            val topBarBg = GradientDrawable().apply {
                setColor(Color.parseColor("#CC12121E"))
            }
            background = topBarBg
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP
            )
        }

        val backButton = Button(this).apply {
            text = "Powrót"
            textSize = 13f
            setTextColor(Color.WHITE)
            val btnBg = GradientDrawable().apply {
                setColor(Color.parseColor("#302B4D"))
                cornerRadius = 20f
            }
            background = btnBg
            setPadding(24, 12, 24, 12)
            setOnClickListener {
                finish()
            }
        }

        val headerTitle = TextView(this).apply {
            text = "MediaPipe Debugger"
            textSize = 16f
            setTextColor(Color.WHITE)
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        }

        val switchCamButton = Button(this).apply {
            text = "Obróć"
            textSize = 16f
            setTextColor(Color.WHITE)
            val btnBg = GradientDrawable().apply {
                setColor(Color.parseColor("#302B4D"))
                cornerRadius = 20f
            }
            background = btnBg
            setPadding(24, 12, 24, 12)
            setOnClickListener {
                toggleCamera()
            }
        }

        topBarLayout.addView(backButton)
        topBarLayout.addView(headerTitle)
        topBarLayout.addView(switchCamButton)
        mainLayout.addView(topBarLayout)

        val controlsLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setPadding(24, 16, 24, 16)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            ).apply {
                setMargins(32, 0, 32, 280)
            }
        }

        recordButton = Button(this).apply {
            text = "Rozpocznij zbieranie punktów"
            textSize = 13f
            setTextColor(Color.WHITE)
            updateRecordButtonBg(false, this)
            setPadding(32, 20, 32, 20)
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f).apply {
                marginEnd = 12
            }
            setOnClickListener {
                toggleRecording()
            }
        }

        compareButton = Button(this).apply {
            text = "Porównaj ze wzorcem"
            textSize = 13f
            setTextColor(Color.WHITE)
            val btnBg = GradientDrawable().apply {
                setColor(Color.parseColor("#302B4D"))
                setStroke(2, Color.parseColor("#B8ACFF"))
                cornerRadius = 24f
            }
            background = btnBg
            setPadding(32, 20, 32, 20)
            setTypeface(null, android.graphics.Typeface.BOLD)
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            setOnClickListener {
                compareWithReference()
            }
        }

        controlsLayout.addView(recordButton)
        controlsLayout.addView(compareButton)
        mainLayout.addView(controlsLayout)

        val hudLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 20, 28, 20)
            val hudBg = GradientDrawable().apply {
                setColor(Color.parseColor("#DD1A192B"))
                cornerRadius = 28f
                setStroke(2, Color.parseColor("#5848BD"))
            }
            background = hudBg
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM
            ).apply {
                setMargins(32, 32, 32, 40)
            }
        }

        fpsTextView = TextView(this).apply {
            text = "FPS: -- | Tryb: LIVE_STREAM"
            textSize = 12f
            setTextColor(Color.parseColor("#FFB64D"))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        poseStatsTextView = TextView(this).apply {
            text = "Pose Landmarks: Brak detekcji"
            textSize = 12f
            setTextColor(Color.parseColor("#00E5FF"))
            setPadding(0, 2, 0, 0)
        }

        handStatsTextView = TextView(this).apply {
            text = "Hand Landmarks: Brak detekcji"
            textSize = 12f
            setTextColor(Color.parseColor("#FF4081"))
            setPadding(0, 2, 0, 0)
        }

        recordingStatsTextView = TextView(this).apply {
            text = "Zbieranie punktów: Oczekiwanie na Start"
            textSize = 12f
            setTextColor(Color.parseColor("#B8ACFF"))
            setPadding(0, 2, 0, 0)
        }

        statusTextView = TextView(this).apply {
            text = "Status: Inicjalizacja MediaPipe..."
            textSize = 11f
            setTextColor(Color.parseColor("#AAA8C2"))
            setPadding(0, 2, 0, 0)
        }

        hudLayout.addView(fpsTextView)
        hudLayout.addView(poseStatsTextView)
        hudLayout.addView(handStatsTextView)
        hudLayout.addView(recordingStatsTextView)
        hudLayout.addView(statusTextView)

        mainLayout.addView(hudLayout)

        permissionOverlayLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.parseColor("#F512121E"))
            visibility = View.GONE
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )

            val msgText = TextView(this@MediaPipeDebugActivity).apply {
                text = "Wymagany jest dostęp do kamery do testów MediaPipe."
                textSize = 15f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                setPadding(32, 32, 32, 32)
            }

            val grantBtn = Button(this@MediaPipeDebugActivity).apply {
                text = "Przyznaj dostęp do kamery"
                setTextColor(Color.WHITE)
                val bg = GradientDrawable().apply {
                    setColor(Color.parseColor("#5848BD"))
                    cornerRadius = 24f
                }
                background = bg
                setPadding(32, 16, 32, 16)
                setOnClickListener {
                    requestCameraPermission()
                }
            }

            addView(msgText)
            addView(grantBtn)
        }
        mainLayout.addView(permissionOverlayLayout)

        countdownOverlayTextView = TextView(this).apply {
            text = ""
            textSize = 96f
            setTextColor(Color.parseColor("#FFC107"))
            setTypeface(null, android.graphics.Typeface.BOLD)
            gravity = Gravity.CENTER
            visibility = View.GONE
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
        }
        mainLayout.addView(countdownOverlayTextView)

        setContentView(mainLayout)

        loadReferencePatternFromAssets()
        setupMediaPipe()
        checkAndStartCamera()
    }

    private fun loadReferencePatternFromAssets() {
        try {
            val jsonString = assets.open("gesture_reference.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            val gestureName = jsonObject.optString("gestureName", "Wzorzec")
            val framesArray = jsonObject.optJSONArray("frames") ?: JSONArray()

            val loadedFrames = mutableListOf<FrameSkeleton>()
            for (i in 0 until framesArray.length()) {
                val frameObj = framesArray.getJSONObject(i)
                val ts = frameObj.optLong("timestampMs", 0L)

                val posePoints = mutableListOf<Point3D>()
                val poseArray = frameObj.optJSONArray("poseLandmarks")
                if (poseArray != null) {
                    for (j in 0 until poseArray.length()) {
                        val ptObj = poseArray.getJSONObject(j)
                        posePoints.add(
                            Point3D(
                                ptObj.getDouble("x").toFloat(),
                                ptObj.getDouble("y").toFloat(),
                                ptObj.getDouble("z").toFloat()
                            )
                        )
                    }
                }

                val handsList = mutableListOf<List<Point3D>>()
                val handsArray = frameObj.optJSONArray("handLandmarks")
                if (handsArray != null) {
                    for (h in 0 until handsArray.length()) {
                        val handPoints = mutableListOf<Point3D>()
                        val handArray = handsArray.getJSONArray(h)
                        for (j in 0 until handArray.length()) {
                            val ptObj = handArray.getJSONObject(j)
                            handPoints.add(
                                Point3D(
                                    ptObj.getDouble("x").toFloat(),
                                    ptObj.getDouble("y").toFloat(),
                                    ptObj.getDouble("z").toFloat()
                                )
                            )
                        }
                        handsList.add(handPoints)
                    }
                }
                loadedFrames.add(FrameSkeleton(ts, posePoints, handsList))
            }

            if (loadedFrames.isNotEmpty()) {
                referenceSequence = loadedFrames
                recordingStatsTextView.text = "Wczytano wzorzec z pliku: $gestureName (${loadedFrames.size} klatek)"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateRecordButtonBg(recording: Boolean, targetButton: Button? = if (::recordButton.isInitialized) recordButton else null) {
        val button = targetButton ?: recordButton
        val btnBg = GradientDrawable().apply {
            setColor(Color.parseColor(if (recording) "#D32F2F" else "#5848BD"))
            cornerRadius = 24f
        }
        button.background = btnBg
        button.text = if (recording) "Zatrzymaj zbieranie" else "Nagraj punkty"
    }

    private fun toggleRecording() {
        if (isCountingDown) {
            cancelCountdown()
            return
        }

        if (!isRecording) {
            startCountdown()
        } else {
            stopRecordingAndEvaluate()
        }
    }

    private fun startCountdown() {
        isCountingDown = true
        countdownOverlayTextView.visibility = View.VISIBLE
        countdownOverlayTextView.text = "5"

        val btnBg = GradientDrawable().apply {
            setColor(Color.parseColor("#E65100"))
            cornerRadius = 24f
        }
        recordButton.background = btnBg
        recordButton.text = "Anuluj odliczanie"

        recordingStatsTextView.text = "Przygotuj się! Start za 5 sek."

        countdownTimer?.cancel()
        countdownTimer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsLeft = (millisUntilFinished / 1000 + 1).toInt()
                countdownOverlayTextView.text = "$secondsLeft"
                recordingStatsTextView.text = "Przygotuj się! Start za $secondsLeft sek."
            }

            override fun onFinish() {
                isCountingDown = false
                countdownOverlayTextView.visibility = View.GONE
                startActualRecording()
            }
        }.start()
    }

    private fun cancelCountdown() {
        countdownTimer?.cancel()
        isCountingDown = false
        countdownOverlayTextView.visibility = View.GONE
        updateRecordButtonBg(false)
        recordingStatsTextView.text = "Odliczanie anulowane"
        Toast.makeText(this, "Anulowano odliczanie", Toast.LENGTH_SHORT).show()
    }

    private fun startActualRecording() {
        isRecording = true
        recordedFrames.clear()
        updateRecordButtonBg(true)

        val ref = referenceSequence
        if (ref != null && ref.size > 1) {
            val refDurationMs = max(1000L, ref.last().timestampMs - ref.first().timestampMs)
            val autoStopMs = refDurationMs + 1200L

            val autoStopSec = (autoStopMs / 1000f)
            recordingStatsTextView.text = "NAGRYWANIE... (Auto-Stop za ${String.format(java.util.Locale.US, "%.1f", autoStopSec)}s)"
            Toast.makeText(this, "NAGRYWANIE! Auto-Stop za ${String.format(java.util.Locale.US, "%.1f", autoStopSec)}s", Toast.LENGTH_SHORT).show()

            autoStopTimer?.cancel()
            autoStopTimer = object : CountDownTimer(autoStopMs, 500) {
                override fun onTick(millisUntilFinished: Long) {
                    if (isRecording) {
                        val secLeft = String.format(java.util.Locale.US, "%.1f", millisUntilFinished / 1000f)
                        recordingStatsTextView.text = "ZBIERANIE: ${recordedFrames.size} klatek (Auto-Stop za ${secLeft}s)"
                    }
                }

                override fun onFinish() {
                    if (isRecording) {
                        stopRecordingAndEvaluate()
                    }
                }
            }.start()
        } else {
            recordingStatsTextView.text = "ZBIERANIE PUNKTÓW... (0 klatek)"
            Toast.makeText(this, "NAGRYWANIE ROZPOCZĘTE! Wykonaj gest.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun stopRecordingAndEvaluate() {
        autoStopTimer?.cancel()
        isRecording = false
        updateRecordButtonBg(false)

        val trimmedFrames = trimTrailingGarbageFrames(recordedFrames)
        val frameCountRecorded = trimmedFrames.size

        if (frameCountRecorded > 0) {
            if (referenceSequence == null) {
                referenceSequence = trimmedFrames.toList()
                recordingStatsTextView.text = "Zbieranie zakończone: Zapisano $frameCountRecorded klatek jako WZORZEC"
                Toast.makeText(this, "Zapisano $frameCountRecorded klatek jako WZORZEC do porównań!", Toast.LENGTH_LONG).show()
            } else {
                recordingStatsTextView.text = "Zbieranie zakończone: Nagrano $frameCountRecorded klatek (obcięto końcówkę)"
                evaluateComparison(trimmedFrames, referenceSequence!!)
            }
        } else {
            recordingStatsTextView.text = "Zbieranie anulowane: Brak zebranych punktów"
            Toast.makeText(this, "Nie wykryto punktów podczas zbierania", Toast.LENGTH_SHORT).show()
        }
    }

    private fun trimTrailingGarbageFrames(rawFrames: List<FrameSkeleton>): List<FrameSkeleton> {
        if (rawFrames.size <= 8) return rawFrames

        val trimmed = rawFrames.toMutableList()

        while (trimmed.size > 5 && trimmed.last().handLandmarks.isEmpty()) {
            trimmed.removeAt(trimmed.size - 1)
        }

        val checkCount = minOf(12, trimmed.size / 3)
        if (trimmed.size > 15 && checkCount > 3) {
            val lastFrame = trimmed.last()
            val prevFrame = trimmed[trimmed.size - checkCount]
            if (lastFrame.handLandmarks.isNotEmpty() && prevFrame.handLandmarks.isNotEmpty()) {
                val lastY = lastFrame.handLandmarks[0].firstOrNull()?.y ?: 0f
                val prevY = prevFrame.handLandmarks[0].firstOrNull()?.y ?: 0f
                if (lastY > prevY + 0.06f) {
                    repeat(checkCount) {
                        if (trimmed.size > 5) trimmed.removeAt(trimmed.size - 1)
                    }
                }
            }
        }

        return if (trimmed.isNotEmpty()) trimmed else rawFrames
    }

    private fun evaluateComparison(sample: List<FrameSkeleton>, reference: List<FrameSkeleton>) {
        if (sample.isEmpty() || reference.isEmpty()) return

        val activeSample = extractActiveMotionFrames(sample)
        val activeReference = extractActiveMotionFrames(reference)

        val rawDtwDistance = calculateDtwDistance(activeSample, activeReference)

        if (rawDtwDistance > 2.0f) {
            val rejectMessage = "Ten gest jest całkowicie niezgodny ze wzorcem.\n\n" +
                                "Zbyt duża różnica w ułożeniu dłoni lub trajektorii.\n" +
                                "(Odległość DTW: ${String.format(java.util.Locale.US, "%.3f", rawDtwDistance)} > 2.00)\n\n" +
                                "Spróbuj ponownie!"
            
            AlertDialog.Builder(this)
                .setTitle("Zły gest (Odrzucono)")
                .setMessage(rejectMessage)
                .setPositiveButton("Zamknij", null)
                .show()
            return
        }

        val adjustedDtwDistance = rawDtwDistance * 0.95f
        val similarityPercent = (max(0f, (1f - adjustedDtwDistance * 0.45f) * 100f) + 5f).coerceIn(0f, 100f)
        val scorePoints = similarityPercent.toInt().coerceIn(0, 100)

        val stars = when {
            scorePoints >= 90 -> "(Doskonale!)"
            scorePoints >= 75 -> "(Bardzo dobrze!)"
            scorePoints >= 60 -> "(Dobry wynik)"
            else -> "(Wymaga ćwiczeń)"
        }

        val speedInfo = analyzeUserFriendlySpeed(activeSample, activeReference)
        val smoothnessInfo = analyzeUserFriendlySmoothness(activeSample)
        val trajectoryInfo = analyzeUserFriendlyTrajectory(rawDtwDistance, activeSample, activeReference)

        val frechetTrajectoryInfo = analyzeFrechetTrajectoryAndCompleteness(rawDtwDistance, activeSample, activeReference)
        val orientationInfo = analyzePalmOrientation(activeSample, activeReference)
        val asymmetryInfo = analyzeBimanualVelocityAsymmetry(activeSample, activeReference)
        val jerkSmoothnessInfo = analyzeMovementJerkSmoothness(activeSample)

        val resultsIntent = Intent(this, SignResultsActivity::class.java).apply {
            putExtra(SignResultsActivity.EXTRA_SIGN_NAME, activeSignName)
            putExtra(SignResultsActivity.EXTRA_SCORE, scorePoints)
            putExtra(SignResultsActivity.EXTRA_STARS, stars)
            putExtra(SignResultsActivity.EXTRA_TEMPO, "${speedInfo.first} (${speedInfo.second})")
            putExtra(SignResultsActivity.EXTRA_FLUENCY, "${smoothnessInfo.first} (${smoothnessInfo.second})")
            putExtra(SignResultsActivity.EXTRA_PRECISION, "${trajectoryInfo.first} (${trajectoryInfo.second})")
            putExtra(SignResultsActivity.EXTRA_TRAJECTORY, "${frechetTrajectoryInfo.first} (${frechetTrajectoryInfo.second})")
            putExtra(SignResultsActivity.EXTRA_ORIENTATION, "${orientationInfo.first} (${orientationInfo.second})")
            putExtra(SignResultsActivity.EXTRA_ASYMMETRY, "${asymmetryInfo.first} (${asymmetryInfo.second})")
            putExtra(SignResultsActivity.EXTRA_JERK, "${jerkSmoothnessInfo.first} (${jerkSmoothnessInfo.second})")
        }

        AlertDialog.Builder(this)
            .setTitle("✨ Wyniki Nagrania: $scorePoints / 100 pkt")
            .setMessage("Nagranie zakończone. Ocena: $stars\n\nPrzejdź do ekranu pełnych wyników, aby przejrzeć szczegóły lub ponowić próbę.")
            .setPositiveButton("Zobacz Pełne Wyniki") { _, _ ->
                startActivity(resultsIntent)
                finish()
            }
            .setNeutralButton("Ustaw jako nowy wzorzec") { _, _ ->
                referenceSequence = sample.toList()
                Toast.makeText(this, "Zaktualizowano wzorzec gestu!", Toast.LENGTH_SHORT).show()
                recordingStatsTextView.text = "Wzorzec zaktualizowany (${sample.size} klatek)"
                startActivity(resultsIntent)
                finish()
            }
            .setNegativeButton("Anuluj", null)
            .show()
    }

    private fun extractActiveMotionFrames(rawFrames: List<FrameSkeleton>): List<FrameSkeleton> {
        if (rawFrames.size <= 5) return rawFrames

        val velocities = mutableListOf<Float>()
        for (i in 0 until rawFrames.size - 1) {
            velocities.add(calculateFrameDistance(rawFrames[i], rawFrames[i + 1]))
        }

        if (velocities.isEmpty()) return rawFrames

        val avgVel = velocities.average().toFloat()
        val motionThreshold = maxOf(0.015f, avgVel * 0.35f)

        var startIndex = 0
        for (i in velocities.indices) {
            if (velocities[i] > motionThreshold) {
                startIndex = maxOf(0, i - 1)
                break
            }
        }

        var endIndex = rawFrames.size - 1
        for (i in velocities.indices.reversed()) {
            if (velocities[i] > motionThreshold) {
                endIndex = minOf(rawFrames.size - 1, i + 2)
                break
            }
        }

        if (startIndex < endIndex && (endIndex - startIndex + 1) >= 4) {
            return rawFrames.subList(startIndex, endIndex + 1)
        }

        return rawFrames
    }

    private fun analyzeUserFriendlySpeed(sample: List<FrameSkeleton>, reference: List<FrameSkeleton>): Pair<String, String> {
        if (sample.size <= 1) return Pair("Analiza pojedynczego kadru", "Czas: B/D")
        if (reference.isEmpty()) return Pair("Brak wzorca", "Czas: B/D")

        val activeSample = extractActiveMotionFrames(sample)
        val activeRef = extractActiveMotionFrames(reference)

        val sampleDurationMs = max(100L, activeSample.last().timestampMs - activeSample.first().timestampMs)
        val refDurationMs = max(100L, activeRef.last().timestampMs - activeRef.first().timestampMs)

        val sampleSec = String.format(java.util.Locale.US, "%.1f sek.", sampleDurationMs / 1000f)
        val refSec = String.format(java.util.Locale.US, "%.1f sek.", refDurationMs / 1000f)

        val ratio = sampleDurationMs.toFloat() / refDurationMs.toFloat()
        val tempoScore = (100 - kotlin.math.abs(1f - ratio) * 100 + 5f).toInt().coerceIn(0, 100)

        val mainText = when {
            ratio < 0.70f -> "Wykonano za szybko (tempo $tempoScore%)"
            ratio > 1.35f -> "Wykonano za wolno (tempo $tempoScore%)"
            else -> "Świetne tempo ($tempoScore% dopasowania)"
        }

        val advice = when {
            ratio < 0.70f -> "Aktywny ruch trwał $sampleSec (wzorzec: $refSec). Zwolnij ruch dla większej precyzji."
            ratio > 1.35f -> "Aktywny ruch trwał $sampleSec (wzorzec: $refSec). Wykonaj gest bardziej zdecydowanie."
            else -> "Aktywny ruch trwał $sampleSec (wzorzec: $refSec) – idealne tempo!"
        }

        return Pair(mainText, advice)
    }

    private fun analyzeUserFriendlySmoothness(sample: List<FrameSkeleton>): Pair<String, String> {
        if (sample.size <= 2) return Pair("Płynność: B/D", "Wymagane nagranie pełnego ruchu.")

        val velocities = mutableListOf<Float>()
        for (i in 0 until sample.size - 1) {
            val dist = calculateFrameDistance(sample[i], sample[i + 1])
            velocities.add(dist)
        }

        val meanVel = velocities.average().toFloat()
        var varianceSum = 0f
        for (v in velocities) {
            val diff = v - meanVel
            varianceSum += diff * diff
        }

        val stdDev = sqrt(varianceSum / velocities.size)
        val smoothnessScorePercent = (max(0f, (1f - stdDev * 3.5f) * 100f) + 10f).toInt().coerceIn(0, 100)

        val mainText = when {
            smoothnessScorePercent >= 80 -> "Płynność ruchu: $smoothnessScorePercent% (Wysoka)"
            smoothnessScorePercent >= 60 -> "Płynność ruchu: $smoothnessScorePercent% (Dobra)"
            else -> "Płynność ruchu: $smoothnessScorePercent% (Wymaga wygładzenia)"
        }

        val advice = when {
            smoothnessScorePercent >= 80 -> "Ruch był bardzo swobodny, gładki i naturalny."
            smoothnessScorePercent >= 60 -> "Ruch był dość płynny z akceptowalnymi wahania ręki."
            else -> "Ruch był niepewny lub przerywany. Spróbuj wykonać go jednym ciągłym ruchem."
        }

        return Pair(mainText, advice)
    }

    private fun analyzeUserFriendlyTrajectory(dtwDistance: Float, sample: List<FrameSkeleton>, reference: List<FrameSkeleton>): Pair<String, String> {
        val adjustedDtwDistance = dtwDistance * 0.90f
        
        val shapeScorePercent = (max(0f, (1f - adjustedDtwDistance * 0.9f) * 100f) + 10f).toInt().coerceIn(0, 100)

        val adviceParts = mutableListOf<String>()

        if (sample.isNotEmpty() && reference.isNotEmpty()) {
            val sStart = getHandCenterY(sample.first())
            val sEnd = getHandCenterY(sample.last())
            val rStart = getHandCenterY(reference.first())
            val rEnd = getHandCenterY(reference.last())

            // Zwiększamy tolerancję na różnicę pozycji w osi Y (ze względu na inny kąt pochylenia ekranu)
            val startDiff = sStart - rStart
            if (startDiff > 0.35f) adviceParts.add("rozpoczynasz ruch zbyt nisko")
            else if (startDiff < -0.35f) adviceParts.add("rozpoczynasz ruch zbyt wysoko")

            val endDiff = sEnd - rEnd
            if (endDiff > 0.35f) adviceParts.add("kończysz gest za nisko")
            else if (endDiff < -0.35f) adviceParts.add("kończysz gest za wysoko")

            val sMinY = sample.map { getHandCenterY(it) }.minOrNull() ?: 0f
            val sMaxY = sample.map { getHandCenterY(it) }.maxOrNull() ?: 0f
            val rMinY = reference.map { getHandCenterY(it) }.minOrNull() ?: 0f
            val rMaxY = reference.map { getHandCenterY(it) }.maxOrNull() ?: 0f
            val sRange = sMaxY - sMinY
            val rRange = rMaxY - rMinY

            val minAcceptableRange = (rRange * 0.4f) - 0.05f
            val maxAcceptableRange = (rRange * 2.5f) + 0.15f

            if (sRange < minAcceptableRange && rRange > 0.05f) adviceParts.add("ruch jest zbyt mały/płytki")
            else if (sRange > maxAcceptableRange) adviceParts.add("ruch jest przesadnie obszerny")
        }

        val mainText = when {
            shapeScorePercent >= 80 -> "Trajektoria: $shapeScorePercent% (Wysoka)"
            shapeScorePercent >= 60 -> "Trajektoria: $shapeScorePercent% (Dobra)"
            else -> "Trajektoria: $shapeScorePercent% (Niska)"
        }

        val advice = if (shapeScorePercent >= 80 && adviceParts.isEmpty()) {
            "Dłoń bardzo dobrze odwzorowała tor ruchu, wysokość i kształt gestu!"
        } else {
            var feedback = if (shapeScorePercent >= 60) "Kształt był poprawny, ale " else "Zwróć uwagę na tor ruchu dłoni: "
            feedback += if (adviceParts.isNotEmpty()) adviceParts.joinToString(" oraz ") + "." 
            else "spróbuj dokładniej odwzorować kierunek ze wzorca."
            feedback
        }

        return Pair(mainText, advice)
    }

    private fun getHandCenterY(frame: FrameSkeleton): Float {
        var sumY = 0f
        var count = 0
        frame.handLandmarks.forEach { hand ->
            hand.forEach { pt ->
                sumY += pt.y
                count++
            }
        }
        return if (count > 0) sumY / count else 0.5f // Domyślnie środek kadru w przypadku braku dłoni
    }

    private fun analyzeFrechetTrajectoryAndCompleteness(
        dtwDistance: Float,
        sample: List<FrameSkeleton>,
        reference: List<FrameSkeleton>
    ): Pair<String, String> {
        val frechetDist = calculateDiscreteFrechetDistance(sample, reference)
        val combinedDist = (dtwDistance * 0.65f) + (frechetDist * 0.35f)
        val scorePercent = (max(0f, (1f - combinedDist * 0.85f) * 100f) + 10f).toInt().coerceIn(0, 100)

        val expertScale = when {
            scorePercent >= 90 -> 5
            scorePercent >= 75 -> 4
            scorePercent >= 60 -> 3
            scorePercent >= 45 -> 2
            scorePercent >= 25 -> 1
            else -> 0
        }

        val issues = mutableListOf<String>()

        if (sample.isNotEmpty() && reference.isNotEmpty()) {
            val sStart = getHandCenter3D(sample.first())
            val sEnd = getHandCenter3D(sample.last())
            val rStart = getHandCenter3D(reference.first())
            val rEnd = getHandCenter3D(reference.last())

            val sMeanY = sample.map { getHandCenter3D(it).y }.average().toFloat()
            val rMeanY = reference.map { getHandCenter3D(it).y }.average().toFloat()
            if (sMeanY - rMeanY > 0.30f) issues.add("znak zamigany za nisko")
            else if (sMeanY - rMeanY < -0.30f) issues.add("znak zamigany za wysoko")

            val startDist = sqrt((sStart.x - rStart.x) * (sStart.x - rStart.x) + (sStart.y - rStart.y) * (sStart.y - rStart.y))
            val endDist = sqrt((sEnd.x - rEnd.x) * (sEnd.x - rEnd.x) + (sEnd.y - rEnd.y) * (sEnd.y - rEnd.y))
            if (startDist > 0.42f) issues.add("początek w złym miejscu")
            if (endDist > 0.42f) issues.add("koniec w złym miejscu")

            val sSpanX = (sample.maxOfOrNull { getHandCenter3D(it).x } ?: 0f) - (sample.minOfOrNull { getHandCenter3D(it).x } ?: 0f)
            val sSpanY = (sample.maxOfOrNull { getHandCenter3D(it).y } ?: 0f) - (sample.minOfOrNull { getHandCenter3D(it).y } ?: 0f)
            val rSpanX = (reference.maxOfOrNull { getHandCenter3D(it).x } ?: 0f) - (reference.minOfOrNull { getHandCenter3D(it).x } ?: 0f)
            val rSpanY = (reference.maxOfOrNull { getHandCenter3D(it).y } ?: 0f) - (reference.minOfOrNull { getHandCenter3D(it).y } ?: 0f)

            val sTotalSpan = sqrt(sSpanX * sSpanX + sSpanY * sSpanY)
            val rTotalSpan = sqrt(rSpanX * rSpanX + rSpanY * rSpanY)

            if (rTotalSpan > 0.08f) {
                if (sTotalSpan < (rTotalSpan * 0.45f) - 0.05f) issues.add("niepełny ruch (zbyt krótki tor)")
                else if (sTotalSpan > (rTotalSpan * 2.3f) + 0.15f) issues.add("zbyt rozległy tor ruchu")
            }
        }

        val frechetStr = String.format(java.util.Locale.US, "%.2f", frechetDist)
        val mainText = "Ocena algorytmu: $expertScale/5 ($scorePercent%) | Fréchet: $frechetStr"

        val advice = if (issues.isEmpty()) {
            "Pełność ruchu oraz punkty początkowe i końcowe są zgodne ze wzorcem."
        } else {
            "Wykryte odchylenia: ${issues.joinToString(", ")}."
        }

        return Pair(mainText, advice)
    }

    private fun calculateDiscreteFrechetDistance(sample: List<FrameSkeleton>, reference: List<FrameSkeleton>): Float {
        val n = sample.size
        val m = reference.size
        if (n == 0 || m == 0) return 1.0f

        val ca = Array(n) { FloatArray(m) { -1f } }
        for (i in 0 until n) {
            for (j in 0 until m) {
                val dist = calculateFrameDistance(sample[i], reference[j])
                ca[i][j] = when {
                    i == 0 && j == 0 -> dist
                    i > 0 && j == 0 -> maxOf(ca[i - 1][0], dist)
                    i == 0 && j > 0 -> maxOf(ca[0][j - 1], dist)
                    else -> maxOf(minOf(ca[i - 1][j], ca[i][j - 1], ca[i - 1][j - 1]), dist)
                }
            }
        }
        return ca[n - 1][m - 1]
    }

    private fun analyzePalmOrientation(
        sample: List<FrameSkeleton>,
        reference: List<FrameSkeleton>
    ): Pair<String, String> {
        val sampleNormals = sample.mapNotNull { frame ->
            frame.handLandmarks.minByOrNull { h -> h.map { it.x }.average() }?.let { calculatePalmNormal(it) }
        }
        val refNormals = reference.mapNotNull { frame ->
            frame.handLandmarks.minByOrNull { h -> h.map { it.x }.average() }?.let { calculatePalmNormal(it) }
        }

        if (sampleNormals.isEmpty() || refNormals.isEmpty()) {
            return Pair("Orientacja dłoni: B/D", "Brak wykrytych dłoni do obliczenia kąta (Palm Surface Angle).")
        }

        val steps = minOf(sampleNormals.size, refNormals.size)
        var angleSumDeg = 0f

        for (i in 0 until steps) {
            val sIdx = (i * (sampleNormals.size - 1)) / max(1, steps - 1)
            val rCenterIdx = (i * (refNormals.size - 1)) / max(1, steps - 1)
            val n1 = sampleNormals[sIdx]

            var bestAngleForFrame = 180f
            val windowStart = max(0, rCenterIdx - 3)
            val windowEnd = min(refNormals.size - 1, rCenterIdx + 3)

            for (rIdx in windowStart..windowEnd) {
                val n2 = refNormals[rIdx]
                val dot = (n1.x * n2.x + n1.y * n2.y + n1.z * n2.z).coerceIn(-1f, 1f)
                val angleDeg = Math.toDegrees(acos(dot.toDouble())).toFloat()
                if (angleDeg < bestAngleForFrame) {
                    bestAngleForFrame = angleDeg
                }
            }

            angleSumDeg += bestAngleForFrame
        }

        val rawAvgAngleDiff = angleSumDeg / steps

        val effectiveAngleDiff = max(0f, rawAvgAngleDiff - 12f)

        val orientationScore = (100f - (effectiveAngleDiff / 115f) * 100f + 5f).toInt().coerceIn(0, 100)
        val angleStr = String.format(java.util.Locale.US, "%.1f°", rawAvgAngleDiff)

        val mainText = when {
            orientationScore >= 80 -> "Orientacja dłoni: $orientationScore% (Odchylenie: $angleStr)"
            orientationScore >= 60 -> "Orientacja dłoni: $orientationScore% (Odchylenie: $angleStr)"
            else -> "Orientacja dłoni: $orientationScore% (Błąd kąta: $angleStr)"
        }

        val advice = when {
            orientationScore >= 80 -> "Kąt nachylenia płaszczyzny dłoni (Palm Surface Angle) jest zgodny ze wzorcem."
            orientationScore >= 60 -> "Dłoń jest lekko obrócona względem wzorca (śr. o $angleStr). Skoryguj rotację nadgarstka."
            else -> "Zła orientacja dłoni (różnica $angleStr) – zwróć uwagę, w którą stronę skierowana jest dłoń."
        }

        return Pair(mainText, advice)
    }

    private fun calculatePalmNormal(hand: List<Point3D>): Point3D? {
        if (hand.size < 18) return null
        val wrist = hand[0]
        val indexMcp = hand[5]
        val pinkyMcp = hand[17]

        val zDamping = 0.35f

        val v1x = indexMcp.x - wrist.x
        val v1y = indexMcp.y - wrist.y
        val v1z = (indexMcp.z - wrist.z) * zDamping

        val v2x = pinkyMcp.x - wrist.x
        val v2y = pinkyMcp.y - wrist.y
        val v2z = (pinkyMcp.z - wrist.z) * zDamping

        val nx = v1y * v2z - v1z * v2y
        val ny = v1z * v2x - v1x * v2z
        val nz = v1x * v2y - v1y * v2x

        val len = sqrt(nx * nx + ny * ny + nz * nz)
        return if (len > 1e-5f) Point3D(nx / len, ny / len, nz / len) else null
    }

    private fun analyzeBimanualVelocityAsymmetry(
        sample: List<FrameSkeleton>,
        reference: List<FrameSkeleton>
    ): Pair<String, String> {
        val refTwoHandedCount = reference.count { it.handLandmarks.size >= 2 }
        val sampleTwoHandedCount = sample.count { it.handLandmarks.size >= 2 }

        if (refTwoHandedCount < reference.size * 0.25f) {
            return if (sampleTwoHandedCount > sample.size * 0.4f) {
                Pair("Znak jednoręczny (Wykryto 2 dłonie)", "Wzorzec jest jednoręczny, a wykryto ruch obu dłoni.")
            } else {
                Pair("Znak jednoręczny: Zgodny", "Asymetria prędkości nie dotyczy (znak wykonywany jedną ręką).")
            }
        }

        if (sampleTwoHandedCount < 2) {
            return Pair("Brak drugiej dłoni (0%)", "Ten znak jest oburęczny! Użyj obu rąk.")
        }

        val (sampleLeftVel, sampleRightVel) = calculateLeftRightVelocities(sample)
        val (refLeftVel, refRightVel) = calculateLeftRightVelocities(reference)

        val sampleTotal = sampleLeftVel + sampleRightVel + 1e-5f
        val refTotal = refLeftVel + refRightVel + 1e-5f

        val sampleRatio = sampleLeftVel / sampleTotal
        val refRatio = refLeftVel / refTotal

        val asymmetryDiff = abs(sampleRatio - refRatio)
        val symmetryScore = (100f - asymmetryDiff * 180f).toInt().coerceIn(0, 100)

        val sLeftStr = String.format(java.util.Locale.US, "%.2f", sampleLeftVel)
        val sRightStr = String.format(java.util.Locale.US, "%.2f", sampleRightVel)

        val mainText = when {
            symmetryScore >= 80 -> "Symetria prędkości: $symmetryScore% (v_L: $sLeftStr | v_P: $sRightStr)"
            symmetryScore >= 60 -> "Symetria prędkości: $symmetryScore% (v_L: $sLeftStr | v_P: $sRightStr)"
            else -> "Asymetria prędkości: $symmetryScore% (v_L: $sLeftStr | v_P: $sRightStr)"
        }

        val advice = when {
            symmetryScore >= 80 -> "Proporcja prędkości między lewą a prawą ręką jest zgodna ze wzorcem."
            sampleRatio > refRatio + 0.12f -> "Lewa ręka porusza się za szybko względem prawej w porównaniu do wzorca."
            sampleRatio < refRatio - 0.12f -> "Prawa ręka porusza się za szybko względem lewej w porównaniu do wzorca."
            else -> "Niewielka asymetria prędkości między dłońmi."
        }

        return Pair(mainText, advice)
    }

    private fun calculateLeftRightVelocities(frames: List<FrameSkeleton>): Pair<Float, Float> {
        var leftVelSum = 0f
        var rightVelSum = 0f
        var pairsCount = 0

        for (i in 0 until frames.size - 1) {
            val f1 = frames[i]
            val f2 = frames[i + 1]

            if (f1.handLandmarks.size >= 2 && f2.handLandmarks.size >= 2) {
                val dt = max(1L, f2.timestampMs - f1.timestampMs) / 1000f

                val f1Sorted = f1.handLandmarks.sortedBy { hand -> hand.map { it.x }.average() }
                val f2Sorted = f2.handLandmarks.sortedBy { hand -> hand.map { it.x }.average() }

                val dLeft = calculateSingleHandDistance(f1Sorted[0], f2Sorted[0])
                val dRight = calculateSingleHandDistance(f1Sorted[1], f2Sorted[1])

                leftVelSum += dLeft / dt
                rightVelSum += dRight / dt
                pairsCount++
            }
        }

        return if (pairsCount > 0) Pair(leftVelSum / pairsCount, rightVelSum / pairsCount) else Pair(0f, 0f)
    }

    private fun calculateSingleHandDistance(h1: List<Point3D>, h2: List<Point3D>): Float {
        val minLen = minOf(h1.size, h2.size)
        if (minLen == 0) return 0f
        var sum = 0f
        for (i in 0 until minLen) {
            val dx = h1[i].x - h2[i].x
            val dy = h1[i].y - h2[i].y
            val dz = (h1[i].z - h2[i].z) * 0.5f
            sum += sqrt(dx * dx + dy * dy + dz * dz)
        }
        return sum / minLen
    }

    private fun analyzeMovementJerkSmoothness(sample: List<FrameSkeleton>): Pair<String, String> {
        if (sample.size <= 4) return Pair("Gładkość (Jerk): B/D", "Za mało klatek do obliczenia pochodnej przyspieszenia.")

        val rawVelocities = mutableListOf<Float>()
        for (i in 0 until sample.size - 1) {
            rawVelocities.add(calculateFrameDistance(sample[i], sample[i + 1]))
        }

        val velocities = mutableListOf<Float>()
        for (i in rawVelocities.indices) {
            val prev = rawVelocities[maxOf(0, i - 1)]
            val curr = rawVelocities[i]
            velocities.add(curr * 0.7f + prev * 0.3f)
        }

        val accelerations = mutableListOf<Float>()
        for (i in 0 until velocities.size - 1) {
            accelerations.add(velocities[i + 1] - velocities[i])
        }

        val jerks = mutableListOf<Float>()
        for (i in 0 until accelerations.size - 1) {
            jerks.add(abs(accelerations[i + 1] - accelerations[i]))
        }

        if (jerks.isEmpty()) return Pair("Gładkość (Jerk): B/D", "Brak danych o zmianie przyspieszenia.")

        val sortedJerks = jerks.sorted()
        val filteredJerks = if (sortedJerks.size > 4) sortedJerks.dropLast(1) else sortedJerks

        val meanJerk = filteredJerks.average().toFloat()

        val sensitivity = 3.4f
        val jerkScorePercent = (max(0f, (1f - meanJerk * sensitivity) * 100f)).toInt().coerceIn(0, 100)

        val jerkStr = String.format(java.util.Locale.US, "%.3f", meanJerk)

        val mainText = when {
            jerkScorePercent >= 80 -> "Gładkość (Jerk): $jerkScorePercent% (Płynny | Jerk: $jerkStr)"
            jerkScorePercent >= 60 -> "Gładkość (Jerk): $jerkScorePercent% (Lekkie wahania | Jerk: $jerkStr)"
            else -> "Gładkość (Jerk): $jerkScorePercent% (Szarpany | Jerk: $jerkStr)"
        }

        val advice = when {
            jerkScorePercent >= 80 -> "Niska pochodna przyspieszenia – ruch rąk jest gładki, bez nagłych zrywów."
            jerkScorePercent >= 60 -> "Wykryto niewielkie skoki przyspieszenia dłoni – prowadź dłoń bardziej jednostajnie."
            else -> "Wysoka wartość zrywu (Jerk) – ruch był szarpany lub robotyczny. Unikaj gwałtownych zatrzymań."
        }

        return Pair(mainText, advice)
    }

    private fun getHandCenter3D(frame: FrameSkeleton): Point3D {
        var sumX = 0f
        var sumY = 0f
        var sumZ = 0f
        var count = 0
        frame.handLandmarks.forEach { hand ->
            hand.forEach { pt ->
                sumX += pt.x
                sumY += pt.y
                sumZ += pt.z
                count++
            }
        }
        return if (count > 0) Point3D(sumX / count, sumY / count, sumZ / count) else Point3D(0f, 0.5f, 0f)
    }

    private fun compareWithReference() {
        val ref = referenceSequence
        if (ref == null) {
            if (recordedFrames.isNotEmpty()) {
                referenceSequence = recordedFrames.toList()
                Toast.makeText(this, "Zapisano ostatnie nagranie (${recordedFrames.size} klatek) jako wzorzec!", Toast.LENGTH_LONG).show()
                recordingStatsTextView.text = "Wzorzec zapisany (${recordedFrames.size} klatek)"
            } else {
                Toast.makeText(this, "Brak zapisanego wzorca! Najpierw nagraj punkty.", Toast.LENGTH_LONG).show()
            }
            return
        }

        if (currentLatestPose.isNotEmpty()) {
            val liveFrame = FrameSkeleton(SystemClock.uptimeMillis(), currentLatestPose, currentLatestHands)
            evaluateComparison(listOf(liveFrame), ref)
        } else {
            Toast.makeText(this, "Nie wykryto aktualnie szkieletu w kadrze", Toast.LENGTH_SHORT).show()
        }
    }

    private fun calculateFrameDistance(f1: FrameSkeleton, f2: FrameSkeleton): Float {
        var totalDist = 0f
        var count = 0

        val poseWeight = 0.3f
        val poseMinLen = minOf(f1.poseLandmarks.size, f2.poseLandmarks.size)
        for (i in 0 until poseMinLen) {
            val p1 = f1.poseLandmarks[i]
            val p2 = f2.poseLandmarks[i]
            val dx = p1.x - p2.x
            val dy = p1.y - p2.y
            val dz = (p1.z - p2.z) * 0.2f // Głębokość ciała prawie ignorowana
            totalDist += sqrt(dx * dx + dy * dy + dz * dz) * poseWeight
            count += 1 
        }

        val handWeight = 8.0f
        
        val handMinLen = minOf(f1.handLandmarks.size, f2.handLandmarks.size)
        for (h in 0 until handMinLen) {
            val h1 = f1.handLandmarks[h]
            val h2 = f2.handLandmarks[h]
            val ptsMinLen = minOf(h1.size, h2.size)
            for (i in 0 until ptsMinLen) {
                val p1 = h1[i]
                val p2 = h2[i]
                
                val dx = (p1.x - p2.x) * 1.5f
                val dy = (p1.y - p2.y) * 1.5f
                val dz = (p1.z - p2.z) * 0.8f 
                totalDist += sqrt(dx * dx + dy * dy + dz * dz) * handWeight
                count += handWeight.toInt()
            }
        }

        if (f1.handLandmarks.isEmpty() && f2.handLandmarks.isNotEmpty()) {
            totalDist += 1.5f 
            count += 1
        } else if (f1.handLandmarks.isNotEmpty() && f2.handLandmarks.isEmpty()) {
            totalDist += 1.5f 
            count += 1
        }
        
        if (f1.handLandmarks.isEmpty() && f2.handLandmarks.isEmpty()) {
            // totalDist += 0f
            count += 1
        }

        return if (count > 0) totalDist / count else 1.0f
    }

    private fun calculateDtwDistance(sample: List<FrameSkeleton>, reference: List<FrameSkeleton>): Float {
        val n = sample.size
        val m = reference.size
        if (n == 0 || m == 0) return 1.0f

        val dtw = Array(n + 1) { FloatArray(m + 1) { Float.MAX_VALUE } }
        dtw[0][0] = 0f

        for (i in 1..n) {
            for (j in 1..m) {
                val cost = calculateFrameDistance(sample[i - 1], reference[j - 1])
                val minPrev = minOf(dtw[i - 1][j], dtw[i][j - 1], dtw[i - 1][j - 1])
                dtw[i][j] = cost + minPrev
            }
        }

        val totalCost = dtw[n][m]
        return totalCost / (n + m)
    }

    private fun collectFramePoints(pose: List<NormalizedLandmark>, hands: List<List<NormalizedLandmark>>) {
        val (normalizedPose, normalizedHands) = normalizeSkeletonScale(pose, hands)

        currentLatestPose = normalizedPose
        currentLatestHands = normalizedHands

        if (isRecording && (normalizedPose.isNotEmpty() || normalizedHands.isNotEmpty())) {
            recordedFrames.add(FrameSkeleton(SystemClock.uptimeMillis(), normalizedPose, normalizedHands))
            val frameCountNow = recordedFrames.size
            val totalPointsNow = recordedFrames.sumOf { f -> f.poseLandmarks.size + f.handLandmarks.sumOf { h -> h.size } }
            recordingStatsTextView.text = "ZBIERANIE PUNKTÓW: $frameCountNow klatek ($totalPointsNow pkt)"
        }
    }

    private fun normalizeSkeletonScale(pose: List<NormalizedLandmark>, hands: List<List<NormalizedLandmark>>): Pair<List<Point3D>, List<List<Point3D>>> {
        if (pose.size < 13) return Pair(emptyList(), emptyList()) // Wymagamy barków do normalizacji

        val leftShoulder = pose[11]
        val rightShoulder = pose[12]
        
        val originX = (leftShoulder.x() + rightShoulder.x()) / 2f
        val originY = (leftShoulder.y() + rightShoulder.y()) / 2f
        val originZ = (leftShoulder.z() + rightShoulder.z()) / 2f

        val dx = leftShoulder.x() - rightShoulder.x()
        val aspectRatioCorrection = 1.77f
        val dy = (leftShoulder.y() - rightShoulder.y()) * aspectRatioCorrection
        
        val dz = leftShoulder.z() - rightShoulder.z()
        val shoulderWidth = sqrt(dx * dx + dy * dy + dz * dz)
        
        val scale = if (shoulderWidth > 0.01f) 1.0f / shoulderWidth else 1.0f

        val normalizedPose = pose.subList(11, pose.size).map {
            Point3D(
                ((it.x() - originX) * scale),
                ((it.y() - originY) * aspectRatioCorrection * scale), // Skalowanie ruchu w pionie do tej samej wirtualnej kostki
                ((it.z() - originZ) * scale)
            )
        }

        val normalizedHands = hands.map { hand ->
            hand.map {
                Point3D(
                    ((it.x() - originX) * scale),
                    ((it.y() - originY) * aspectRatioCorrection * scale), // Skalowanie ruchu w pionie
                    ((it.z() - originZ) * scale)
                )
            }
        }

        return Pair(normalizedPose, normalizedHands)
    }

    private fun setupMediaPipe() {
        try {
            val poseBaseOptions = BaseOptions.builder()
                .setModelAssetPath("pose_landmarker_lite.task")
                .setDelegate(Delegate.CPU)
                .build()

            val poseOptions = PoseLandmarker.PoseLandmarkerOptions.builder()
                .setBaseOptions(poseBaseOptions)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener { result, _ ->
                    val landmarks = result.landmarks()
                    val count = if (landmarks.isNotEmpty()) landmarks[0].size else 0
                    val poseList = if (landmarks.isNotEmpty()) landmarks[0] else emptyList()
                    val handList = skeletonOverlayView.getHandLandmarks()

                    collectFramePoints(poseList, handList)

                    runOnUiThread {
                        skeletonOverlayView.setPoseLandmarks(landmarks)
                        poseStatsTextView.text = if (count > 0) {
                            "Pose Landmarks: Wykryto $count punktów"
                        } else {
                            "Pose Landmarks: Szukanie..."
                        }
                        updateFpsCounter()
                    }
                }
                .setErrorListener { error ->
                    error.printStackTrace()
                }
                .build()

            poseLandmarker = PoseLandmarker.createFromOptions(this, poseOptions)

            val handBaseOptions = BaseOptions.builder()
                .setModelAssetPath("hand_landmarker.task")
                .setDelegate(Delegate.CPU)
                .build()

            val handOptions = HandLandmarker.HandLandmarkerOptions.builder()
                .setBaseOptions(handBaseOptions)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setNumHands(2)
                .setMinHandDetectionConfidence(0.1f)
                .setMinHandPresenceConfidence(0.1f)
                .setMinTrackingConfidence(0.1f)
                .setMinHandPresenceConfidence(0.3f)
                .setMinTrackingConfidence(0.3f)
                .setResultListener { result, _ ->
                    val landmarks = result.landmarks()
                    val totalHandPoints = landmarks.sumOf { it.size }
                    val handsDetected = landmarks.size
                    runOnUiThread {
                        skeletonOverlayView.setHandLandmarks(landmarks)
                        handStatsTextView.text = if (handsDetected > 0) {
                            "Hand Landmarks: Wykryto $handsDetected dłonie ($totalHandPoints pkt)"
                        } else {
                            "Hand Landmarks: Szukanie..."
                        }
                    }
                }
                .setErrorListener { error ->
                    error.printStackTrace()
                }
                .build()

            handLandmarker = HandLandmarker.createFromOptions(this, handOptions)
            statusTextView.text = "Status: MediaPipe Gotowy (CPU)"
        } catch (e: Throwable) {
            e.printStackTrace()
            statusTextView.text = "Błąd MediaPipe: ${e.localizedMessage}"
            Toast.makeText(this, "Błąd ładownia MediaPipe JNI: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateFpsCounter() {
        frameCount++
        val now = SystemClock.uptimeMillis()
        val elapsed = now - lastFpsTimestamp
        if (elapsed >= 1000) {
            currentFps = (frameCount * 1000 / elapsed).toInt()
            frameCount = 0
            lastFpsTimestamp = now
            fpsTextView.text = "FPS: $currentFps | Tryb: LIVE_STREAM"
        }
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
                Toast.makeText(this, "Kamera jest wymagana do debugera MediaPipe", Toast.LENGTH_LONG).show()
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
            .build()

        analysis.setAnalyzer(cameraExecutor) { imageProxy ->
            processImageProxy(imageProxy)
        }

        imageAnalysis = analysis

        try {
            provider.bindToLifecycle(this, cameraSelector, preview, analysis)
        } catch (e: Exception) {
            e.printStackTrace()
            statusTextView.text = "Błąd kamery: ${e.localizedMessage}"
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        try {
            var frameTime = SystemClock.uptimeMillis()
            if (frameTime <= lastFrameTime) {
                frameTime = lastFrameTime + 1
            }
            lastFrameTime = frameTime

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

            poseLandmarker?.detectAsync(mpImage, frameTime)
            handLandmarker?.detectAsync(mpImage, frameTime)
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
        countdownTimer?.cancel()
        autoStopTimer?.cancel()
        cameraExecutor.shutdown()
        poseLandmarker?.close()
        handLandmarker?.close()
    }

    private fun dpToPx(context: Context, dp: Int): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    private inner class SkeletonOverlayView(context: Context) : View(context) {

        private var poseLandmarksList: List<List<NormalizedLandmark>>? = null
        private var handLandmarksList: List<List<NormalizedLandmark>>? = null

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
            strokeWidth = dpToPx(context, 2).toFloat()
            strokeCap = Paint.Cap.ROUND
            strokeJoin = Paint.Join.ROUND
        }

        private val handJointPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }

        private val poseConnections = listOf(
            Pair(11, 12), Pair(11, 13), Pair(13, 15), Pair(12, 14), Pair(14, 16),
            Pair(11, 23), Pair(12, 24), Pair(23, 24)
        )

        private val handConnections = listOf(
            Pair(0, 1), Pair(1, 2), Pair(2, 3), Pair(3, 4),
            Pair(0, 5), Pair(5, 6), Pair(6, 7), Pair(7, 8),
            Pair(0, 9), Pair(9, 10), Pair(10, 11), Pair(11, 12),
            Pair(0, 13), Pair(13, 14), Pair(14, 15), Pair(15, 16),
            Pair(0, 17), Pair(17, 18), Pair(18, 19), Pair(19, 20),
            Pair(5, 9), Pair(9, 13), Pair(13, 17)
        )

        fun setPoseLandmarks(landmarks: List<List<NormalizedLandmark>>) {
            this.poseLandmarksList = landmarks
            invalidate()
        }

        fun setHandLandmarks(landmarks: List<List<NormalizedLandmark>>) {
            this.handLandmarksList = landmarks
            invalidate()
        }

        fun getHandLandmarks(): List<List<NormalizedLandmark>> {
            return handLandmarksList ?: emptyList()
        }

        override fun onDraw(canvas: Canvas) {
            super.onDraw(canvas)

            val w = width.toFloat()
            val h = height.toFloat()
            if (w <= 0 || h <= 0) return

            poseLandmarksList?.forEach { landmarkList ->
                for ((start, end) in poseConnections) {
                    if (start < landmarkList.size && end < landmarkList.size) {
                        val p1 = landmarkList[start]
                        val p2 = landmarkList[end]
                        canvas.drawLine(p1.x() * w, p1.y() * h, p2.x() * w, p2.y() * h, poseLinePaint)
                    }
                }
                landmarkList.forEachIndexed { index, lm ->
                    // Skip drawing face joint nodes (indices 0..10)
                    if (index >= 11) {
                        canvas.drawCircle(lm.x() * w, lm.y() * h, dpToPx(context, 4).toFloat(), poseJointPaint)
                    }
                }
            }

            handLandmarksList?.forEach { landmarkList ->
                for ((start, end) in handConnections) {
                    if (start < landmarkList.size && end < landmarkList.size) {
                        val p1 = landmarkList[start]
                        val p2 = landmarkList[end]
                        canvas.drawLine(p1.x() * w, p1.y() * h, p2.x() * w, p2.y() * h, handLinePaint)
                    }
                }
                landmarkList.forEach { lm ->
                    canvas.drawCircle(lm.x() * w, lm.y() * h, dpToPx(context, 3).toFloat(), handJointPaint)
                }
            }
        }
    }
}
