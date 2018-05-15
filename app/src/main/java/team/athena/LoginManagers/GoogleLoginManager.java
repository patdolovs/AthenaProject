package team.athena.LoginManagers;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import team.athena.LoginActivity;
import team.athena.R;


public class GoogleLoginManager {

    private LoginActivity loginActivity;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient googleApiClient;
    public static final int RC_SIGN_IN = 9001;

    public GoogleLoginManager(LoginActivity _loginActivity){
        this.loginActivity = _loginActivity;
        initializeAndConfigureGoogleLogin();
    }

    private void initializeAndConfigureGoogleLogin() {
        configureDataRequestedByGoogle();
        buildAGoogleApiClient();
        addEventListenersAndPermissionsToButtons();

    }

    private void configureDataRequestedByGoogle() {
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(loginActivity.getString(R.string.client_id))
                .requestEmail()
                .build();
    }

    private void buildAGoogleApiClient(){
        googleApiClient = new GoogleApiClient.Builder(loginActivity)
                .enableAutoManage(loginActivity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        notifyConnectionFailed();}})
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
    }

    private void addEventListenersAndPermissionsToButtons(){
        loginActivity.findViewById(R.id.google_button).setOnClickListener(loginActivity);
    }

    public void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        loginActivity.startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void notifyConnectionFailed() {
        Toast.makeText(
                loginActivity,
                "Connection failed, please try again.",
                Toast.LENGTH_LONG).show();
    }
}
