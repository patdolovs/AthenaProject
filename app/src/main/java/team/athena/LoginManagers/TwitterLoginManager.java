package team.athena.LoginManagers;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import team.athena.LoginActivity;
import team.athena.R;

public class TwitterLoginManager {
    private final LoginActivity loginActivity;
    public TwitterLoginButton twitterLoginButton;
    private AuthCredential credentials;

    public TwitterLoginManager(LoginActivity _loginActivity){
        this.loginActivity = _loginActivity;
        initializeAndConfigureTwitterLogin();
    }

    private void initializeAndConfigureTwitterLogin() {
        Twitter.initialize(loginActivity);
        findTheTwitterLoginButton();
        handleTwitterLoginResult();
    }

    private void findTheTwitterLoginButton() {
        twitterLoginButton = loginActivity.findViewById(R.id.button_twitter_login);
    }

    private void handleTwitterLoginResult() {
        twitterLoginButton.setCallback(new Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> loginData) {
                twitterHandleSuccesfulLoginData(loginData);

            }
            @Override
            public void failure(TwitterException exception) {
                twitterHandleExceptionOnLogin(exception);

            }
        });
    }

    private void twitterHandleSuccesfulLoginData(Result<TwitterSession> loginData) {
        Log.d("TwitterDebug", "twitterLogin:success" + loginData);
        handleTwitterSession(loginData.data);
    }

    private void twitterHandleExceptionOnLogin(TwitterException exception) {
        Log.w("TwitterDebug", "twitterLogin:failure", exception);
    }

    private void handleTwitterSession(TwitterSession session) {
        getCredentialsFromTwitter(session);
        signInWithTwitterCredentials();
    }

    private void getCredentialsFromTwitter(TwitterSession session) {
        credentials = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);
    }

    private void signInWithTwitterCredentials() {
        loginActivity.firebaseManager.firebaseAuth.signInWithCredential(credentials)
                .addOnCompleteListener(loginActivity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            loginActivity.firebaseManager.changeActivityToMainOrSetupBasedOnBaseStation();
                        }
                        else
                            notifySignInFailed();
                    }
                });
    }

    private void notifySignInFailed() {
        Toast.makeText(loginActivity, "Authentication failed.",
                Toast.LENGTH_SHORT).show();
    }
}
