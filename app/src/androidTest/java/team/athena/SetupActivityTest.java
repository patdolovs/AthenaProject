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
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.pressImeActionButton;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class SetupActivityTest {

    @Rule
    public ActivityTestRule<SetupActivity> mActivityTestRule = new ActivityTestRule<>(SetupActivity.class);

    @Test
    public void setupActivityTestGet() {
        delayToMatchAppExecutionDelay();
        setTextOfSsidTextElement();
        setTextOfPasswordTextElement();
        performClickOnSendButtonElement();

    }

    private void delayToMatchAppExecutionDelay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setTextOfSsidTextElement() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.ssidText), isDisplayed()));
        appCompatEditText.perform(replaceText("testSSID"), closeSoftKeyboard());
    }

    private void setTextOfPasswordTextElement() {
        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.passwordText), isDisplayed()));
        appCompatEditText4.perform(replaceText("testPassword"), closeSoftKeyboard());
    }

    private void performClickOnSendButtonElement() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.sendButton), withText("Send"), isDisplayed()));
        appCompatButton.perform(click());
    }

}
