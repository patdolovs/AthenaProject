package team.athena;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import team.athena.LoginManagers.FacebookLoginManager;
import team.athena.LoginManagers.FirebaseManager;
import team.athena.LoginManagers.GoogleLoginManager;
import team.athena.LoginManagers.TwitterLoginManager;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    GoogleLoginManager googleLoginManager;
    FacebookLoginManager facebookLoginManager;
    TwitterLoginManager twitterLoginManager;
    public FirebaseManager firebaseManager;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupScreenLayout();
        Twitter.initialize(this);

        setContentView(R.layout.activity_login);
        initializeAndConfigureLoginManagers();
    }

    private void setupScreenLayout(){
        hideTitleBar();
        enableFullScreen();
    }

    private void hideTitleBar() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    private void enableFullScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    private void initializeAndConfigureLoginManagers() {
        initializeAndConfigureFirebaseManager();
        initializeAndConfigureGoogleLogin();
        initializeAndConfigureTwitterLogin();
        initializeAndConfigureFacebookLogin();
    }

    private void initializeAndConfigureFirebaseManager() {
        firebaseManager = new FirebaseManager(this);
    }
    private void initializeAndConfigureGoogleLogin() {
        googleLoginManager = new GoogleLoginManager(this);
    }
    private void initializeAndConfigureTwitterLogin() {
        twitterLoginManager = new TwitterLoginManager(this);
    }
    private void initializeAndConfigureFacebookLogin() {
        facebookLoginManager = new FacebookLoginManager(this);
    }

    @Override
    public void onClick(View loginView) {
        if (buttonClickedIsGoogleSignIn(loginView))
            googleLoginManager.signInGoogle();
    }

    private boolean buttonClickedIsGoogleSignIn(View loginView){
        return loginView.getId() == R.id.google_button;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestedCodeIsGoogleLogin(requestCode)){
            GoogleSignInResult googleSignInAttempt = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (googleSignInAttempt.isSuccess())
                signInToFirebaseWithObtainedGoogleAccount(googleSignInAttempt);
        }

        else if(requestedCodeIsTwitterLogin(requestCode))
            twitterLoginManager.twitterLoginButton.onActivityResult(requestCode, resultCode, data);

        else
            facebookLoginManager.facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private boolean requestedCodeIsGoogleLogin(int requestCode) {
        return requestCode == GoogleLoginManager.RC_SIGN_IN;
    }

    private void signInToFirebaseWithObtainedGoogleAccount(GoogleSignInResult result) {
        GoogleSignInAccount account = result.getSignInAccount();
        firebaseManager.firebaseLoginWithGoogle(account);
    }

    private boolean requestedCodeIsTwitterLogin(int requestCode) {
        return requestCode == TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE;
    }

}