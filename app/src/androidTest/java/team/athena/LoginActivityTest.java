package team.athena;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void loginActivityTest() {
        delayToMatchAppExecutionDelay();
        checkIfGoogleLogInButtonExists();
        checkIfTwitterLogInButtonExists();
        checkIfFacebookLogInButtonExists();

    }

    private void delayToMatchAppExecutionDelay() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void checkIfGoogleLogInButtonExists(){
        ViewInteraction frameLayout = onView(
                allOf(withId(R.id.google_button), isDisplayed()));
        frameLayout.check(matches(isDisplayed()));
    }

    private void checkIfTwitterLogInButtonExists(){
        ViewInteraction button = onView(
                allOf(withId(R.id.button_twitter_login), isDisplayed()));
        button.check(matches(isDisplayed()));
    }

    private void checkIfFacebookLogInButtonExists(){
        ViewInteraction button2 = onView(
                allOf(withId(R.id.facebook_button), isDisplayed()));
        button2.check(matches(isDisplayed()));
    }

}
