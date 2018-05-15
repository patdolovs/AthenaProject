package team.athena;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityStatisticsTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityStatisticsTest() {
        delayToMatchAppExecutionDelay();
        checkIfStatisticsButtonIsDisplayed();
        performClickOnStatisticsButtonElement();
        delayToMatchAppExecutionDelay();
        checkIfButtonCategoryIsDisplayed();
        checkIfButtonDateIsDisplayed();
        checkIfButtonDate2IsDisplayed();
        checkIfButtonOdDoIsDisplayed();
        checkIfLineChartIsDisplayed();
        performClickOnButtonCategory2Element();
        performClickOnTitleElementWithNameHumidity();
        performClickOnButtonWithTextDateFrom();
        performClickOnButtonWithTextOK4();
        performClickOnButtonWithTextDateUntil();
        performClickOnButtonWithTextOK();
        performClickOnButtonOdDoWithShowText();
        performClickOnButtonCategory8Element();
        performClickOnTitleElementWithNamePPM();
        performClickOnButtonCategoryElement();
        performClickOnTitleElementWithNameTemperature();
        delayToMatchAppExecutionDelay();
    }

    private void delayToMatchAppExecutionDelay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void performClickOnButtonCategory8Element() {
        ViewInteraction appCompatButton8 = onView(
                allOf(withId(R.id.buttonCategory), withText("Category"),isDisplayed()));
        appCompatButton8.perform(click());
    }

    private void performClickOnButtonWithTextOK4() {
        ViewInteraction appCompatButton4 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),isDisplayed()));
        appCompatButton4.perform(click());
    }

    private void performClickOnButtonCategory2Element() {
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.buttonCategory), withText("Category"),isDisplayed()));
        appCompatButton2.perform(click());
    }

    private void checkIfStatisticsButtonIsDisplayed() {
        ViewInteraction button = onView(
                allOf(withId(R.id.statisticsButton),isDisplayed()));
        button.check(matches(isDisplayed()));
    }

    private void performClickOnStatisticsButtonElement() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.statisticsButton),isDisplayed()));
        appCompatButton.perform(click());
    }

    private void checkIfButtonCategoryIsDisplayed() {
        ViewInteraction button2 = onView(
                allOf(withId(R.id.buttonCategory),isDisplayed()));
        button2.check(matches(isDisplayed()));
    }

    private void checkIfButtonDateIsDisplayed() {
        ViewInteraction button3 = onView(
                allOf(withId(R.id.buttonDate),isDisplayed()));
        button3.check(matches(isDisplayed()));
    }

    private void checkIfButtonDate2IsDisplayed() {
        ViewInteraction button4 = onView(
                allOf(withId(R.id.buttonDate2),isDisplayed()));
        button4.check(matches(isDisplayed()));
    }

    private void checkIfButtonOdDoIsDisplayed() {
        ViewInteraction button5 = onView(
                allOf(withId(R.id.buttonOdDo),isDisplayed()));
        button5.check(matches(isDisplayed()));
    }

    private void checkIfLineChartIsDisplayed() {
        ViewInteraction viewGroup = onView(
                allOf(withId(R.id.line_chart),isDisplayed()));
        viewGroup.check(matches(isDisplayed()));
    }

    private void performClickOnTitleElementWithNameHumidity() {
        ViewInteraction appCompatTextView = onView(
                allOf(withId(android.R.id.title), withText("Humidity"),isDisplayed()));
        appCompatTextView.perform(click());
    }

    private void performClickOnButtonWithTextDateFrom() {
        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.buttonDate), withText("Date From"),isDisplayed()));
        appCompatButton3.perform(click());
    }

    private void performClickOnButtonWithTextDateUntil() {
        ViewInteraction appCompatButton5 = onView(
                allOf(withId(R.id.buttonDate2), withText("Date until"),isDisplayed()));
        appCompatButton5.perform(click());
    }

    private void performClickOnButtonWithTextOK() {
        ViewInteraction appCompatButton6 = onView(
                allOf(withId(android.R.id.button1), withText("OK"),isDisplayed()));
        appCompatButton6.perform(click());
    }

    private void performClickOnButtonOdDoWithShowText() {
        ViewInteraction appCompatButton7 = onView(
                allOf(withId(R.id.buttonOdDo), withText("Show"),isDisplayed()));
        appCompatButton7.perform(click());
    }

    private void performClickOnTitleElementWithNamePPM() {
        ViewInteraction appCompatTextView2 = onView(
                allOf(withId(android.R.id.title), withText("PPM"),isDisplayed()));
        appCompatTextView2.perform(click());
    }

    private void performClickOnButtonCategoryElement() {
        ViewInteraction appCompatButton9 = onView(
                allOf(withId(R.id.buttonCategory), withText("Category"),isDisplayed()));
        appCompatButton9.perform(click());
    }

    private void performClickOnTitleElementWithNameTemperature() {
        ViewInteraction appCompatTextView3 = onView(
                allOf(withId(android.R.id.title), withText("Temperature"),isDisplayed()));
        appCompatTextView3.perform(click());
    }

}
