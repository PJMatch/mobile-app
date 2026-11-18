package com.example.pjm_match

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import kotlin.math.roundToInt

data class GestureMetric(val title: String, val detail: String)

data class SignAttempt(
    val sign: String,
    val score: Int,
    val predictions: List<SignPrediction>,
    val metrics: List<GestureMetric>,
    val timestampMs: Long,
) {
    val topPrediction: SignPrediction? get() = predictions.firstOrNull()
    val recognizedAsTarget: Boolean get() = topPrediction?.gloss == sign
}

data class SignProgress(
    val sign: String,
    val attempts: Int,
    val bestScore: Int,
    val lastAttempt: SignAttempt,
)

object SignPracticeStore {
    private const val PREFS = "sign_practice"
    private const val KEY_PROGRESS = "progress"
    private const val SAVED_PREDICTIONS = 5

    fun save(context: Context, attempt: SignAttempt): SignProgress {
        val all = loadAll(context).associateBy { it.sign }.toMutableMap()
        val previous = all[attempt.sign]
        val updated = SignProgress(
            sign = attempt.sign,
            attempts = (previous?.attempts ?: 0) + 1,
            bestScore = maxOf(previous?.bestScore ?: 0, attempt.score),
            lastAttempt = attempt.copy(predictions = attempt.predictions.take(SAVED_PREDICTIONS)),
        )
        all[attempt.sign] = updated
        val json = JSONArray().apply { all.values.forEach { put(progressToJson(it)) } }
        prefs(context).edit().putString(KEY_PROGRESS, json.toString()).apply()
        return updated
    }

    fun progress(context: Context, sign: String): SignProgress? = loadAll(context).firstOrNull { it.sign == sign }

    fun loadAll(context: Context): List<SignProgress> {
        val raw = prefs(context).getString(KEY_PROGRESS, null) ?: return emptyList()
        return runCatching {
            val array = JSONArray(raw)
            List(array.length()) { progressFromJson(array.getJSONObject(it)) }
        }.getOrDefault(emptyList())
    }

    fun totalAttempts(context: Context): Int = loadAll(context).sumOf { it.attempts }

    fun averageBestScore(context: Context): Int? =
        loadAll(context).takeIf { it.isNotEmpty() }?.map { it.bestScore }?.average()?.roundToInt()

    private fun prefs(context: Context) = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    private fun progressToJson(progress: SignProgress) = JSONObject().apply {
        put("sign", progress.sign)
        put("attempts", progress.attempts)
        put("bestScore", progress.bestScore)
        put("last", attemptToJson(progress.lastAttempt))
    }

    private fun progressFromJson(json: JSONObject) = SignProgress(
        sign = json.getString("sign"),
        attempts = json.getInt("attempts"),
        bestScore = json.getInt("bestScore"),
        lastAttempt = attemptFromJson(json.getJSONObject("last")),
    )

    private fun attemptToJson(attempt: SignAttempt) = JSONObject().apply {
        put("sign", attempt.sign)
        put("score", attempt.score)
        put("timestampMs", attempt.timestampMs)
        put("predictions", JSONArray().apply {
            attempt.predictions.forEach { put(JSONObject().put("gloss", it.gloss).put("confidence", it.confidence.toDouble())) }
        })
        put("metrics", JSONArray().apply {
            attempt.metrics.forEach { put(JSONObject().put("title", it.title).put("detail", it.detail)) }
        })
    }

    private fun attemptFromJson(json: JSONObject): SignAttempt {
        val predictions = json.getJSONArray("predictions")
        val metrics = json.getJSONArray("metrics")
        return SignAttempt(
            sign = json.getString("sign"),
            score = json.getInt("score"),
            predictions = List(predictions.length()) {
                val p = predictions.getJSONObject(it)
                SignPrediction(p.getString("gloss"), p.getDouble("confidence").toFloat())
            },
            metrics = List(metrics.length()) {
                val m = metrics.getJSONObject(it)
                GestureMetric(m.getString("title"), m.getString("detail"))
            },
            timestampMs = json.getLong("timestampMs"),
        )
    }
}

object SignFeedback {
    fun rating(score: Int): String = when {
        score >= 90 -> "Doskonale!"
        score >= 75 -> "Bardzo dobrze!"
        score >= 60 -> "Dobry wynik"
        score >= 35 -> "Prawie, ćwicz dalej"
        else -> "Wymaga ćwiczeń"
    }

    fun summary(attempt: SignAttempt): String {
        val top = attempt.topPrediction
        return when {
            top == null -> "Brak oceny modelu dla tego nagrania."
            top.gloss == PjmSignClassifier.BLANK_GLOSS ->
                "Model nie wykrył żadnego znaku. Upewnij się, że dłonie i twarz są dobrze widoczne w kadrze."
            attempt.recognizedAsTarget && attempt.score >= 75 ->
                "Model bez wahania rozpoznał znak „${displayGloss(attempt.sign)}”. Świetna robota!"
            attempt.recognizedAsTarget ->
                "Model rozpoznał znak „${displayGloss(attempt.sign)}”, ale nie jest pewny. Dopracuj kształt i ruch dłoni."
            else ->
                "Model uznał Twój gest za „${displayGloss(top.gloss)}”. Porównaj ułożenie dłoni i ruch z nagraniem wzorcowym."
        }
    }

    fun displayGloss(gloss: String): String {
        if (gloss == PjmSignClassifier.BLANK_GLOSS) return "brak znaku"
        val polish = Locale.forLanguageTag("pl")
        return gloss.replace('-', ' ').lowercase(polish).replaceFirstChar { it.titlecase(polish) }
    }

    fun percent(confidence: Float): String = "${(confidence * 100).roundToInt()}%"
}
