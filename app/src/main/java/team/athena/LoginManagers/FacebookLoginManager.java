package team.athena.LoginManagers;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import team.athena.LoginActivity;
import team.athena.R;

public class FacebookLoginManager {

    private final LoginActivity loginActivity;
    public final CallbackManager facebookCallbackManager;
    private AuthCredential facebookCredentials;

    public FacebookLoginManager(LoginActivity _loginActivity){
        this.loginActivity = _loginActivity;
        facebookCallbackManager = CallbackManager.Factory.create();
        initializeAndConfigure();
    }

    private void initializeAndConfigure() {
        findFacebookButtonAndSetReadPermissions();
        registerCallbackManager();
    }

    private void findFacebookButtonAndSetReadPermissions() {
        LoginButton mFacebookSignInButton = loginActivity.findViewById(R.id.facebook_button);
        mFacebookSignInButton.setReadPermissions("email", "public_profile");
    }

    private void registerCallbackManager(){
        LoginManager.getInstance().registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult> () {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessTokenAndSendItToFirebase(loginResult.getAccessToken());
                    }

                    @Override
                    public void onCancel() {
                        notifyUserCanceledLogin();
                    }
                    @Override
                    public void onError(FacebookException exception) {
                        notifyLoginError();
                    }
                });
    }

    private void handleFacebookAccessTokenAndSendItToFirebase(AccessToken token) {
        getFacebookCredentialsFromToken(token);
        loginActivity.firebaseManager.firebaseAuth.signInWithCredential(facebookCredentials)
                .addOnCompleteListener(loginActivity, new OnCompleteListener< AuthResult >() {
                    @Override
                    public void onComplete(@NonNull Task< AuthResult > task) {
                        if (task.isSuccessful())
                            loginActivity.firebaseManager.changeActivityToMainOrSetupBasedOnBaseStation();
                        else
                            notifyLoginError();
                    }
                });
    }

    private void getFacebookCredentialsFromToken(AccessToken token) {
        facebookCredentials = FacebookAuthProvider.getCredential(token.getToken());
    }

    private void notifyLoginError() {
        Toast.makeText(loginActivity, "An error occurred during login.", Toast.LENGTH_LONG).show();
    }

    private void notifyUserCanceledLogin() {
        Toast.makeText(loginActivity, "Login canceled by user", Toast.LENGTH_LONG).show();
    }
}
