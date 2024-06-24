package com.nasyithm.dicostoryv2.view.story.add

import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.nasyithm.dicostoryv2.JsonConverter
import com.nasyithm.dicostoryv2.R
import com.nasyithm.dicostoryv2.data.remote.retrofit.ApiConfig
import com.nasyithm.dicostoryv2.util.EspressoIdlingResource
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AddStoryActivityTest {

    private val mockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        ApiConfig.BASE_URL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun addStoryWithoutImage_Failed() {
        AddStoryActivity.currentImageUri = null

        val scenario = ActivityScenario.launch(AddStoryActivity::class.java)

        onView(withId(R.id.ivPreview))
            .check(matches(isDisplayed()))
        onView(withId(R.id.etDescription))
            .perform(typeText("test"), closeSoftKeyboard())
        onView(withId(R.id.btnUpload))
            .perform(click())
        onView(withText(R.string.failed))
            .check(matches(isDisplayed()))
        onView(withText(R.string.input_image_first))
            .check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun addStoryWithoutDescription_Failed() {
        AddStoryActivity.currentImageUri = Uri.parse("android.resource://com.nasyithm.dicostoryv2/drawable/dicostory_icon")

        val scenario = ActivityScenario.launch(AddStoryActivity::class.java)

        onView(withId(R.id.ivPreview))
            .check(matches(isDisplayed()))
        onView(withId(R.id.etDescription))
            .check(matches(isDisplayed()))
        onView(withId(R.id.btnUpload))
            .perform(click())
        onView(withText(R.string.failed))
            .check(matches(isDisplayed()))
        onView(withText(R.string.input_desc_first))
            .check(matches(isDisplayed()))

        scenario.close()
    }

    @Test
    fun addStory_Succeed() {
        AddStoryActivity.currentImageUri = Uri.parse("android.resource://com.nasyithm.dicostoryv2/drawable/dicostory_icon")

        val scenario = ActivityScenario.launch(AddStoryActivity::class.java)

        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_response.json"))
        mockWebServer.enqueue(mockResponse)

        onView(withId(R.id.ivPreview))
            .check(matches(isDisplayed()))
        onView(withId(R.id.etDescription))
            .perform(typeText("test"), closeSoftKeyboard())
        onView(withId(R.id.btnUpload))
            .perform(click())

        val recordedRequest = mockWebServer.takeRequest()
        assertEquals("/stories", recordedRequest.path)
        assertEquals("POST", recordedRequest.method)
        val contentType = recordedRequest.getHeader("Content-Type")
        val contentTypeSplited = contentType.toString().split(";")
        assertEquals("multipart/form-data", contentTypeSplited[0])
        assertNotNull(recordedRequest.getHeader("Authorization"))

        onView(withText(R.string.succeed))
            .check(matches(isDisplayed()))
        onView(withText(R.string.story_added))
            .check(matches(isDisplayed()))

        scenario.close()
    }
}