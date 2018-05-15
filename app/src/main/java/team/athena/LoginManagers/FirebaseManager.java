package team.athena.LoginManagers;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;
import team.athena.LoginActivity;
import team.athena.MainActivity;
import team.athena.SetupActivity;

public class FirebaseManager {

    private final LoginActivity loginActivity;
    private FirebaseFirestore db;
    FirebaseAuth firebaseAuth;
    private AuthCredential googleCredentials;

    private CollectionReference referenceToUser;
    private DocumentReference referenceToUserBaseStation;
    private boolean baseStationExists;

    public FirebaseManager(LoginActivity _loginActivity){
        this.loginActivity = _loginActivity;
        initializeFirebase();
    }

    private void initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void firebaseLoginWithGoogle(GoogleSignInAccount googleAccount) {
        getGoogleCredentialsFromGoogleAccount(googleAccount);
        signInWithGoogleCredentials();
    }

    private void getGoogleCredentialsFromGoogleAccount(GoogleSignInAccount googleAccount) {
        googleCredentials = GoogleAuthProvider.getCredential(googleAccount.getIdToken(), null);
    }

    private void signInWithGoogleCredentials() {
        firebaseAuth.signInWithCredential(googleCredentials)
                .addOnCompleteListener(loginActivity, new OnCompleteListener< AuthResult >() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                            changeActivityToMainOrSetupBasedOnBaseStation();
                        else
                            notifyLoginFailed();
                        }
                    });
                }

    void changeActivityToMainOrSetupBasedOnBaseStation(){
        getReferenceToUser();
        getReferenceToUserBaseStation();
        checkIfBaseStationDocumentExists();
    }

    private void getReferenceToUserBaseStation() {
        referenceToUserBaseStation = referenceToUser.document("Bazna Stanica");
    }

    private void checkIfBaseStationDocumentExists() {
        referenceToUserBaseStation.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> firestoreQuery) {
                if (firestoreQuery.isSuccessful()) {
                    try {
                        DocumentSnapshot baseStationDocument = firestoreQuery.getResult();
                        if (baseStationDocument != null) {
                            setBaseStationExistsTo(true);
                            changeTheActivity(ifBaseStationDocumentExists());
                        }
                        else {
                            setBaseStationExistsTo(false);
                            changeTheActivity(ifBaseStationDocumentExists());
                        }
                    }
                    catch (Exception e) {
                        setBaseStationExistsTo(false);
                    }
                }
                else
                    notifyLoginFailed();
            }
        });
    }

    private boolean ifBaseStationDocumentExists() {
        return baseStationExists;
    }

    private void setBaseStationExistsTo(boolean _baseStationExists) {
        baseStationExists = _baseStationExists;
    }

    private void changeTheActivity(boolean baseStationExists) {
        if(baseStationExists)
            changeToMainActivity();
        else
            changeToSetupActivity();
    }

    private void changeToMainActivity() {
        String userUID = firebaseAuth.getUid();
        Intent intent = new Intent(loginActivity, MainActivity.class);
        intent.putExtra("userUID", userUID);
        loginActivity.startActivity(intent);
        loginActivity.finish();
    }

    private void changeToSetupActivity() {
        String userString = firebaseAuth.getUid();
        Intent intent = new Intent(loginActivity, SetupActivity.class);
        intent.putExtra("user",userString);
        loginActivity.startActivity(intent);
        loginActivity.finish();
    }

    private void notifyLoginFailed() {
        Toast.makeText(loginActivity, "Authentication failed.",
                Toast.LENGTH_SHORT).show();
    }

    private void createBaseStationForUser(FirebaseUser user) {
        getReferenceToUser();
        createEmptyBaseStationDocument();
    }

    private void getReferenceToUser() {
        referenceToUser = db.collection("users").document(firebaseAuth.getUid()).collection("Uredaji");
    }

    private void createEmptyBaseStationDocument() {
        Map<String, Object> emptyDocument = new HashMap<>();
        referenceToUser.document("Bazna stanica").set(emptyDocument);
    }

    private void signOut(){
        FirebaseAuth.getInstance().signOut();
    }
}
