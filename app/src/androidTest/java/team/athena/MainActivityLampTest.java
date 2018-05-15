package team.athena;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityLampTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityLampTest() {
        delayToMatchAppExecutionDelay();
        checkIfLampButtonElementIsDisplayed();
        performClickOnLampButtonElement();
        delayToMatchAppExecutionDelay();
        checkIfToggleButtonIsDisplayed();
        performClickOnToggleOFFButtonElement();
        delayToMatchAppExecutionDelay();
        performClickOnToggleONButtonElement();
    }

    private void delayToMatchAppExecutionDelay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void checkIfLampButtonElementIsDisplayed() {
        ViewInteraction appCompatButton1 = onView(
                allOf(withId(R.id.lampButton), isDisplayed()));
        appCompatButton1.check(matches(isDisplayed()));
    }

    private void performClickOnLampButtonElement() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.lampButton), isDisplayed()));
        appCompatButton.perform(click());
    }

    private void checkIfToggleButtonIsDisplayed() {
        ViewInteraction toggleButton = onView(
                allOf(withId(R.id.toggleButton), isDisplayed()));
        toggleButton.check(matches(isDisplayed()));
    }

    private void performClickOnToggleOFFButtonElement() {
        ViewInteraction toggleButton2 = onView(
                allOf(withId(R.id.toggleButton), withText("OFF"), isDisplayed()));
        toggleButton2.perform(click());
    }

    private void performClickOnToggleONButtonElement() {
        ViewInteraction toggleButton3 = onView(
                allOf(withId(R.id.toggleButton), withText("ON"), isDisplayed()));
        toggleButton3.perform(click());
    }

}
