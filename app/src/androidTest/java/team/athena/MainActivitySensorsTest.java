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
public class MainActivitySensorsTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivitySensorsTest() {
        checkIfSensorsButtonElementIsDisplayed();
        delayToMatchAppExecutionDelay();
        performClickOnSensorsButtonElement();
        delayToMatchAppExecutionDelay();
        checkIfTemperatureDataIsDisplayed();
        checkIfHumidityDataIsDisplayed();
        checkIfTextViewPpmDataIsDisplayed();
        performClickOnPpmDataTextViewElement();
        delayToMatchAppExecutionDelay();
        checkIfPPMDataIsDisplayed();
        checkIfCorrectedPPMDataIsDisplayed();
        checkIfRZeroDataIsDisplayed();
        checkIfCorrectedRZeroDataIsDisplayed();
        checkIfResistanceDataIsDisplayed();
    }

    private void checkIfSensorsButtonElementIsDisplayed() {
        ViewInteraction appCompatButton1 = onView(
                allOf(withId(R.id.sensorsButton), isDisplayed()));
        appCompatButton1.check(matches(isDisplayed()));
    }

    private void delayToMatchAppExecutionDelay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void performClickOnSensorsButtonElement() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.sensorsButton), isDisplayed()));
        appCompatButton.perform(click());
    }

    private void checkIfTemperatureDataIsDisplayed() {
        ViewInteraction textView = onView(
                allOf(withId(R.id.temperatureDataTextView), isDisplayed()));
        textView.check(matches(isDisplayed()));
    }

    private void checkIfHumidityDataIsDisplayed() {
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.humidityDataTextView),isDisplayed()));
        textView2.check(matches(isDisplayed()));
    }

    private void checkIfTextViewPpmDataIsDisplayed() {
        ViewInteraction textView3 = onView(
                allOf(withId(R.id.ppmDataTextView), withText("Details..."), isDisplayed()));
        textView3.check(matches(isDisplayed()));
    }

    private void performClickOnPpmDataTextViewElement() {
        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.ppmDataTextView), withText("Details..."),isDisplayed()));
        appCompatTextView.perform(click());
    }

    private void checkIfPPMDataIsDisplayed() {
        ViewInteraction textView4 = onView(
                allOf(withId(R.id.PPMDataTextView), isDisplayed()));
        textView4.check(matches(isDisplayed()));
    }

    private void checkIfCorrectedPPMDataIsDisplayed() {
        ViewInteraction textView5 = onView(
                allOf(withId(R.id.correctedPPMDataTextView), isDisplayed()));
        textView5.check(matches(isDisplayed()));
    }

    private void checkIfRZeroDataIsDisplayed() {
        ViewInteraction textView6 = onView(
                allOf(withId(R.id.RZeroDataTextView), isDisplayed()));
        textView6.check(matches(isDisplayed()));
    }

    private void checkIfCorrectedRZeroDataIsDisplayed() {
        ViewInteraction textView7 = onView(
                allOf(withId(R.id.correctedRZeroDataTextView), isDisplayed()));
        textView7.check(matches(isDisplayed()));
    }

    private void checkIfResistanceDataIsDisplayed() {
        ViewInteraction textView8 = onView(
                allOf(withId(R.id.resistanceDataTextView), isDisplayed()));
        textView8.check(matches(isDisplayed()));
    }

}
