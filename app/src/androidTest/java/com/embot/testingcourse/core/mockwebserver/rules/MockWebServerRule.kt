package com.embot.testingcourse.core.mockwebserver.rules

import com.embot.testingcourse.core.mockwebserver.MockWebServerUrlHolder
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class MockWebServerRule: TestWatcher() {
    val server: MockWebServer = MockWebServer()


    override fun starting(description: Description?) {
        super.starting(description)
        server.start()

        MockWebServerUrlHolder.baseUrl = server.url("/").toString()
    }

    override fun finished(description: Description?) {
        server.shutdown()
        super.finished(description)
    }

}