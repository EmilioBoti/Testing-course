package com.embot.testingcourse.core.utils

object JsonUtils {

    fun readJsonFile(fileName: String): String {
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().context
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

}

fun String.asAsset(): String = JsonUtils.readJsonFile(this)