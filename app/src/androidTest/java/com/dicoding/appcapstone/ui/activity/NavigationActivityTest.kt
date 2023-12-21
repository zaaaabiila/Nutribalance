package com.dicoding.appcapstone.ui.activity

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeActivityTest{
    @get:Rule
    var activityRule: ActivityScenarioRule<HomeActivity> = ActivityScenarioRule(HomeActivity::class.java)

    @Test
    fun whenUserTapAddCourseAddCourseActivityDisplayed(){
        Intents.init()

        Espresso.onView(withId(R.id.action_add)).perform(ViewActions.click())

        intended(hasComponent(AddCourseActivity::class.java.name))

        Intents.release()
    }
}