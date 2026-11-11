package com.example.pjm_match

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONArray
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PjmSignClassifierTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun modelLoadsAndClassifiesEmptyClip() {
        val labels = context.assets.open("pjm_islr_labels.json").bufferedReader().use { it.readText() }
            .let { JSONArray(it) }
            .let { array -> List(array.length()) { array.getString(it) } }
        val clip = List(PjmSignClassifier.MIN_FRAMES) { Array(PjmLandmarks.TOTAL_V) { FloatArray(3) } }

        PjmSignClassifier(context, labels).use { classifier ->
            val prediction = classifier.classify(clip)
            assertTrue(prediction.gloss in labels)
            assertTrue(prediction.confidence in 0f..1f)
        }
    }
}
