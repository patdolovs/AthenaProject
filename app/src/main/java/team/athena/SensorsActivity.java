package team.athena;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class SensorsActivity extends AppCompatActivity {
    private String userID;
    private DocumentReference firebaseDocumentReference;
    private TextView temperatureDataTextView;
    private TextView humidityDataTextView;
    private TextView ppmDataTextVIew;
    private String currentTemperature;
    private String currentHumidity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutOfScreen();
        getAndSetUidFromPreviousActivity();

        getTemperatureDataTextView();
        getHumidityDataTextView();
        getPpmDataTextView();

        setPPMDataTextViewOnClickListener();
    }

    private void setLayoutOfScreen() {
        setContentView(R.layout.activity_sensors);
    }

    private void getAndSetUidFromPreviousActivity() {
        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
    }

    private void getTemperatureDataTextView() {
        temperatureDataTextView =  findViewById(R.id.temperatureDataTextView);
    }

    private void getHumidityDataTextView() {
        humidityDataTextView = findViewById(R.id.humidityDataTextView);
    }

    private void getPpmDataTextView() {
        ppmDataTextVIew = findViewById(R.id.ppmDataTextView);
    }

    private void setPPMDataTextViewOnClickListener() {
        ppmDataTextVIew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SensorsActivity.this, PPMActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        getFirebaseSensorsDocument();
        setListenerForTemperatureAndHumidityChanges();
    }

    private void setListenerForTemperatureAndHumidityChanges() {
        firebaseDocumentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot currentSensorValues, FirebaseFirestoreException e) {
                if (currentSensorValues.exists()) {
                    getCurrentTemperatureAndHumidityFromSensor(currentSensorValues);
                    setSensorValuesToTextViewsAndColorThem();
                }
            }
        });
    }

    private void setSensorValuesToTextViewsAndColorThem() {
        setTemperatureDataTextView(currentTemperature);
        setHumidityDataTextView(currentHumidity);

        colorTemperatureDataTextViewBasedOnValue();
        colorHumidityDataTextViewBasedOnValue();
    }

    private void getCurrentTemperatureAndHumidityFromSensor(DocumentSnapshot currentSensorValues) {
        currentTemperature = currentSensorValues.getString("temperatura");
        currentHumidity = currentSensorValues.getString("vlaga");
    }

    private void getFirebaseSensorsDocument() {
        firebaseDocumentReference = FirebaseFirestore.getInstance().collection("users").document("test123").collection("Uredaji").document("senzor");
    }

    private void setTemperatureDataTextView(String temperature) {
        temperatureDataTextView.setText(temperature);
    }

    private void colorTemperatureDataTextViewBasedOnValue() {
        if(currentTempeartureIsInsideIdealRange()) {
            temperatureDataTextView.setTextColor(Color.GREEN);
        }
        else if(currentTemperatureIsUnderIdeal()) {
            temperatureDataTextView.setTextColor(Color.parseColor("#238aff"));
        }
        else if(currentTemperatureIsAboveIdeal()) {
            temperatureDataTextView.setTextColor(Color.RED);
        }
    }

    private boolean currentTemperatureIsAboveIdeal() {
        return Integer.parseInt(currentTemperature) > 27;
    }

    private boolean currentTemperatureIsUnderIdeal() {
        return Integer.parseInt(currentTemperature) < 18;
    }

    private boolean currentTempeartureIsInsideIdealRange() {
        return Integer.parseInt(currentTemperature) > 18 && Integer.parseInt(currentTemperature) < 27;
    }

    private void setHumidityDataTextView(String humidity) {
        humidityDataTextView.setText(humidity);
    }

    private void colorHumidityDataTextViewBasedOnValue() {
        if(currentHumidityIsInsideIdealRange()) {
            humidityDataTextView.setTextColor(Color.GREEN);
        }
        else if(currentHumiditIsUnderIdeal()) {
            humidityDataTextView.setTextColor(Color.parseColor("#238aff"));
        }
        else if(currentHumiditiyIsAboveIdeal() ) {
            humidityDataTextView.setTextColor(Color.RED);
        }
    }

    private boolean currentHumidityIsInsideIdealRange() {
        return Integer.parseInt(currentHumidity) >= 30 && Integer.parseInt(currentHumidity) <= 50;
    }

    private boolean currentHumiditIsUnderIdeal() {
        return Integer.parseInt(currentHumidity) < 30;
    }

    private boolean currentHumiditiyIsAboveIdeal() {
        return Integer.parseInt(currentHumidity) > 30;
    }


}
