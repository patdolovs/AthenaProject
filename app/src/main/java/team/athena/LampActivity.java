package team.athena;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import java.util.HashMap;
import java.util.Map;

public class LampActivity extends AppCompatActivity {
    private String userID;
    private DocumentReference firebaseDocumentReference;
    private ToggleButton toggleButton;
    private ImageView lampImageView;
    private String lampValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutOfScreen();
        getAndSetUIDFromPreviousActivity();

        getFirebaseSensorsDocument();

        getToggleButton();
        getLampImageView();

        changeTextInToggleButtonBasedOnFirebaseData();
        setOnCheckChangedToggleButtonListener();
    }

    private void setLayoutOfScreen() {
        setContentView(R.layout.activity_lamp);
    }

    private void getAndSetUIDFromPreviousActivity() {
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
    }

    private void getFirebaseSensorsDocument() {
        firebaseDocumentReference = FirebaseFirestore.getInstance().collection("users").document("test123").collection("Uredaji").document("lampa");
    }

    private void getToggleButton() {
        toggleButton = findViewById(R.id.toggleButton);
    }

    private void getLampImageView() {
        lampImageView = findViewById(R.id.lampaImage);
    }

    private void changeTextInToggleButtonBasedOnFirebaseData() {
        firebaseDocumentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot currentLampValue, FirebaseFirestoreException e) {
                if(currentLampValue.exists()){
                    setLampValue(currentLampValue);
                    checkAndChangeTextInToggleButton();
                }
            }
        });
    }
    public void setLampValue(DocumentSnapshot lampValue) {
        this.lampValue = lampValue.getString("upaljen");
    }

    private void checkAndChangeTextInToggleButton() {
        if(lampValue.equals("true")){
            toggleButton.setSelected(true);
            toggleButton.setChecked(true);
        }
        else {
            toggleButton.setChecked(false);
            toggleButton.setSelected(false);
        }
    }

    private void setOnCheckChangedToggleButtonListener() {
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean buttonIsChecked) {
                if(buttonIsChecked){
                    changeLampImageViewON();
                    changeLampValueToTrue();
                }
                else{
                    changeLampImageViewOFF();
                    changeLampValueToFalse();
                }
            }
        });
    }

    private void changeLampImageViewON() {
        lampImageView.setImageDrawable(getResources().getDrawable(R.drawable.light_on_wht_min));
    }

    private void changeLampValueToTrue() {
        Map<String, Object> unos = new HashMap<>();
        unos.put("upaljen", "true");
        firebaseDocumentReference.set(unos);
    }

    private void changeLampImageViewOFF() {
        lampImageView.setImageDrawable(getResources().getDrawable(R.drawable.light_off_wht));
    }

    private void changeLampValueToFalse() {
        Map<String, Object> unos = new HashMap<>();
        unos.put("upaljen", "false");
        firebaseDocumentReference.set(unos);
    }

}

