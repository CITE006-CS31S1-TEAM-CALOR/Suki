package com.example.pabili


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class dummytest2 {
    @Rule
    @JvmField
    var mActivityScenarioRule = ActivityScenarioRule(LoginActivity::class.java)

    @Test
    fun dummytest2() {

        val usernamerandom: String = randomstrings()
        val passwordrandom: String = randomstrings()

        val materialButton = onView(
            allOf(
                withId(R.id.btnSignup), withText("Signup"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.appcompat.widget.ContentFrameLayout")),
                        0
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val appCompatEditText = onView(
            allOf(
                withId(R.id.etUsername),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.appcompat.widget.ContentFrameLayout")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText(usernamerandom), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.etPassword),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.appcompat.widget.ContentFrameLayout")),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText(passwordrandom), closeSoftKeyboard())

        val appCompatEditText3 = onView(
            allOf(
                withId(R.id.etVerifyPassword),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.appcompat.widget.ContentFrameLayout")),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        appCompatEditText3.perform(replaceText(passwordrandom), closeSoftKeyboard())

        val materialButton2 = onView(
            allOf(
                withId(R.id.btnCustomerSignup), withText("Signup as Customer"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.appcompat.widget.ContentFrameLayout")),
                        0
                    ),
                    6
                ),
                isDisplayed()
            )
        )
        materialButton2.perform(click())

        Thread.sleep(5000)

        val appCompatEditText4 = onView(
            allOf(
                withId(R.id.etUsername),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.appcompat.widget.ContentFrameLayout")),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText4.perform(replaceText(usernamerandom), closeSoftKeyboard())

        val appCompatEditText5 = onView(
            allOf(
                withId(R.id.etPassword),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.appcompat.widget.ContentFrameLayout")),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        appCompatEditText5.perform(replaceText(passwordrandom), closeSoftKeyboard())

        val materialButton3 = onView(
            allOf(
                withId(R.id.btnCustomerLogin), withText("Login as Customer"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.appcompat.widget.ContentFrameLayout")),
                        0
                    ),
                    4
                ),
                isDisplayed()
            )
        )
        materialButton3.perform(click())

        Thread.sleep(2000)

        val materialButton4 = onView(
            allOf(
                withId(R.id.btnTempStore), withText("Temporary Mayet Store"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.appcompat.widget.ContentFrameLayout")),
                        0
                    ),
                    5
                ),
                isDisplayed()
            )
        )
        materialButton4.perform(click())

        Thread.sleep(1000)

        val materialAutoCompleteTextView = onView(
            allOf(
                withId(R.id.etOrder),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.recyclerView),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialAutoCompleteTextView.perform(click())

        val materialAutoCompleteTextView2 = onView(
            allOf(
                withId(R.id.etOrder),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.recyclerView),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialAutoCompleteTextView2.perform(replaceText("10 suka\n"), closeSoftKeyboard())

        val appCompatImageView = onView(
            allOf(
                withId(R.id.ivRemove),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.recyclerView),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        appCompatImageView.perform(click())

        Thread.sleep(1000)

        val materialAutoCompleteTextView3 = onView(
            allOf(
                withId(R.id.etOrder),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.recyclerView),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialAutoCompleteTextView3.perform(click())

        val materialAutoCompleteTextView4 = onView(
            allOf(
                withId(R.id.etOrder),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.recyclerView),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialAutoCompleteTextView4.perform(replaceText("10 suka\n"), closeSoftKeyboard())

        val materialButton5 = onView(
            allOf(
                withId(R.id.btnSubmitOrder), withText("Submit Order"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.appcompat.widget.ContentFrameLayout")),
                        0
                    ),
                    3
                ),
                isDisplayed()
            )
        )
        materialButton5.perform(click())
    }

    private fun randomstrings(): String{
        val randomstring = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
        var stringg = ""
        for(i in 1..8){
            stringg += randomstring.random()
        }
        return stringg
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
