package team.athena;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private String userID;
    private Button sensorsButton;
    private Button lampButton;
    private Button statisticsButton;
    private Button deviceAddButton;
    private TextView menuText;
    private String premium = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutOfScreen();
        getAndSetUIDFromPreviousActivity();

        displayMenuText();
        getUIButtons();
        setButtonOnClickListeners();
        getUserPremiumStatus();
    }

    private void setLayoutOfScreen() {
        setContentView(R.layout.activity_main);
    }

    private void getAndSetUIDFromPreviousActivity() {
        Intent intent = getIntent();
        userID = intent.getStringExtra("userUID");
    }

    private void displayMenuText() {
        getMenuText();
        setMenuTextTypeface();
    }

    private void getMenuText() {
        menuText = findViewById(R.id.textViewMenu);
    }

    private void setMenuTextTypeface() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/FeENit27C.otf");
        menuText.setTypeface(typeface);
    }

    private void getUIButtons() {
        getSensorsButton();
        getLampButton();
        getStatisticsButton();
        getDeviceAddButton();
    }

    private void getSensorsButton() {
        sensorsButton = findViewById(R.id.sensorsButton);
    }

    private void getLampButton() {
        lampButton = findViewById(R.id.lampButton);
    }

    private void getStatisticsButton() {
        statisticsButton = findViewById(R.id.statisticsButton);
    }

    private void getDeviceAddButton() {
        deviceAddButton = findViewById(R.id.deviceAddButton);
    }

    private void getUserPremiumStatus() {

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("users").document("stattest");

        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()) {

                    DocumentSnapshot documentSnapshot = task.getResult();
                    premium = documentSnapshot.getString("premium");

                    setStatisticsButtonBackgroundForNonPremiumUser();
                }
            }
        });
    }

    private void setStatisticsButtonBackgroundForNonPremiumUser() {

        if (premium.equals("false")) {
            statisticsButton.setBackgroundResource(R.drawable.button_premium);
        }
    }


    private void setButtonOnClickListeners() {
        setSensorsButtonOnClickListener();
        setLampButtonOnClickListener();
        setStatisticButtonOnClickListener();
        setDeviceAddButtonOnClickListener();
    }

    private void setSensorsButtonOnClickListener() {
        sensorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SensorsActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }

    private void setLampButtonOnClickListener() {
        lampButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LampActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }

    private void setStatisticButtonOnClickListener() {
        statisticsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){

                if (premium.equals("true"))
                {
                    Intent intent = new Intent(MainActivity.this, StatisticsActivity.class);
                    intent.putExtra("userID", userID);
                    startActivity(intent);
                }

                else if (premium.equals("false"))
                {
                    Uri uri = Uri.parse("https://play.google.com/store"); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }

            }
        });
    }

    private void setDeviceAddButtonOnClickListener() {
        deviceAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DeviceAddActivity.class);
                intent.putExtra("userID", userID);
                startActivity(intent);
            }
        });
    }

}
