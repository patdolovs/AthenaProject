package team.athena;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class PPMActivity extends AppCompatActivity {

    private String userID;
    private DocumentReference firebaseDocumentReference;
    private String correctedPPMValue;
    private String correctedRZeroValue;
    private String PPMValue;
    private String RZeroValue;
    private String resistanceValue;
    private TextView correctedPPMDataTextView;
    private TextView correctedRZeroDataTextView;
    private TextView PPMDataTextView;
    private TextView RZeroDataTextView;
    private TextView resistanceDataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutOfScreen();
        getAndSetUidFromPreviousActivity();
        getUIDataTextViews();
    }

    private void setLayoutOfScreen() {
        setContentView(R.layout.activity_ppm);
    }

    private void getAndSetUidFromPreviousActivity() {
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
    }

    private void getUIDataTextViews() {
        correctedPPMDataTextView = findViewById(R.id.correctedPPMDataTextView);
        correctedRZeroDataTextView = findViewById(R.id.correctedRZeroDataTextView);
        PPMDataTextView = findViewById(R.id.PPMDataTextView);
        RZeroDataTextView = findViewById(R.id.RZeroDataTextView);
        resistanceDataTextView = findViewById(R.id.resistanceDataTextView);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getFirebaseDocumentReference();
        getSensorValuesFromFirebaseDocumentReference();
    }

    private void getFirebaseDocumentReference() {
        firebaseDocumentReference = FirebaseFirestore.getInstance().collection("users").document("test123").collection("Uredaji").document("kvalitetaZraka");
    }

    private void getSensorValuesFromFirebaseDocumentReference() {
        firebaseDocumentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot currentSensorValues, FirebaseFirestoreException e) {
                if (currentSensorValues.exists()) {
                    getCurrentSensorValues(currentSensorValues);
                    setSensorValuesToTextViewsAndColorThem();
                }
            }
        });
    }

    private void getCurrentSensorValues(DocumentSnapshot currentSensorValues) {
        correctedPPMValue = currentSensorValues.getString("CorrectedPPM");
        correctedRZeroValue = currentSensorValues.getString("CorrectedRZero");
        PPMValue = currentSensorValues.getString("PPM");
        RZeroValue = currentSensorValues.getString("RZero");
        resistanceValue = currentSensorValues.getString("Resistance");
    }

    private void setSensorValuesToTextViewsAndColorThem() {
        fillTextViewsWithData();
        colorPPMDataTextViewBasedOnValue();
    }

    private void fillTextViewsWithData() {
        fillPPMDataTextView();
        fillRZeroDataTextView();
        fillResistanceDataTextView();
        fillCorrectedPPMDataTextView();
        fillCorrectedRZeroDataTextView();
    }

    private void fillPPMDataTextView() {
        PPMDataTextView.setText(PPMValue);
    }

    private void fillRZeroDataTextView() {
        RZeroDataTextView.setText(RZeroValue);
    }

    private void fillResistanceDataTextView() {
        resistanceDataTextView.setText(resistanceValue);
    }

    private void fillCorrectedPPMDataTextView() {
        correctedPPMDataTextView.setText(correctedPPMValue);
    }

    private void fillCorrectedRZeroDataTextView() {
        correctedRZeroDataTextView.setText(correctedRZeroValue);
    }

    private void colorPPMDataTextViewBasedOnValue() {
        if(CorrectedPPMValueIsInsideIdealRange()) {
            PPMDataTextView.setTextColor(Color.GREEN);
        }
        else if(correctedPPMValueIsUnderIdeal()) {
            PPMDataTextView.setTextColor(Color.parseColor("#238aff"));
        }
        else if(correctedPPMValueIsAboveIdeal()) {
            PPMDataTextView.setTextColor(Color.RED);
        }
    }

    private boolean CorrectedPPMValueIsInsideIdealRange() {
            return Integer.parseInt(PPMValue) > 350 && Integer.parseInt(PPMValue) < 1000;
    }

    private boolean correctedPPMValueIsUnderIdeal() {
        return Integer.parseInt(PPMValue) < 350;
    }

    private boolean correctedPPMValueIsAboveIdeal() {
        return Integer.parseInt(PPMValue) > 1000;
    }

}
