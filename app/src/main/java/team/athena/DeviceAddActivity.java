package team.athena;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

import team.athena.NFC.NFCTagTextReader;

public class DeviceAddActivity extends AppCompatActivity {

    public String uid;
    private NfcAdapter mNfcAdapter;
    public static final String MIME_DEVICE_ADD = "athena/deviceadd";
    private FirebaseFirestore db;
    private Parcelable[] rawMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_add);
        getUidFromIntent();
        getFirebaseInstance();
        getNFCAdapterIfSupported();
        handleNFCDiscovery(getIntent());
    }

    private void getUidFromIntent() {
        Intent intent = getIntent();
        uid = intent.getStringExtra("userID");
    }

    private void getFirebaseInstance() {
        db = FirebaseFirestore.getInstance();
    }

    private void getNFCAdapterIfSupported() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null)
            closeActivityAndNotifyUserDeviceDoesNotSupportNFC();
    }

    private void closeActivityAndNotifyUserDeviceDoesNotSupportNFC() {
        Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void handleNFCDiscovery(Intent NFCDiscovery) {
        if (NFCDiscoveredNDEF(NFCDiscovery)) {
            readNDEFFromDiscoveredTag(NFCDiscovery);
        }
    }

    private void readNDEFFromDiscoveredTag(Intent NFCDiscovery) {
        String NDEFDiscoveredType = NFCDiscovery.getType();
        if (discoveredNDEFIsText(NDEFDiscoveredType))
            readTextFromDiscoveredNDEF(NFCDiscovery);
        else
            notifyUserReadingNFCTagFailed();
    }

    private boolean discoveredNDEFIsText(String NDEFDiscoveredType) {
        return MIME_DEVICE_ADD.equals(NDEFDiscoveredType);
    }

    private boolean NFCDiscoveredNDEF(Intent intent) {
        return NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction());
    }

    private void readTextFromDiscoveredNDEF(Intent NFCDiscovery) {
        Tag DiscoveredNFCTag = NFCDiscovery.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        new NFCTagTextReader().execute(DiscoveredNFCTag);
    }

    private void notifyUserReadingNFCTagFailed() {
        Toast.makeText(this, "Error while reading NFC Tag", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch(this, mNfcAdapter);
    }

    public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
        IntentFilter[] filters = new IntentFilter[1];
        setIntentFilters(filters);
        String[][] techList = new String[][]{};
        adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
    }

    @Override
    protected void onPause() {
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (checkAndVerifyIntent(intent)) {
            if (checkIfRawMessagesFromIntentAreNotNull(intent)) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                    NdefRecord[] ndefRecord = messages[i].getRecords();
                    getMessagesFromNfcAndWriteToFirebase(ndefRecord);
                }
            }
        }
    }

    private boolean checkAndVerifyIntent(Intent intent) {
        return ifIntentIsNotNull(intent) && NFCDiscoveredNDEF(intent);
    }

    private boolean ifIntentIsNotNull(Intent intent) {
        return intent != null;
    }



    private boolean checkIfRawMessagesFromIntentAreNotNull(Intent intent) {
        rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        return rawMessages != null;
    }

    private void getMessagesFromNfcAndWriteToFirebase(NdefRecord[] ndefRecord) {
        byte[] byteArray = ndefRecord[0].getPayload();
        String payloadString = new String(byteArray);
        Map<String, Object> data = new HashMap<>();
        putInitialDataValuesForLampAndSensors(data, payloadString);
        writeInUsersCollectionReferenceInitialValuesForSensors(data, payloadString);
        notifyUserForSuccessScan();
    }

    private void putInitialDataValuesForLampAndSensors(Map<String, Object> data, String plainText) {
        if(plainText.equals("lampa"))
            putLampInitialValue(data);
        else if (plainText.endsWith("senzor"))
            putTemperatureAndMoistureInitialValue(data);
    }

    private String getStringDataFromNdefRecord(NdefRecord[] ndefRecord) {
        byte[] byteArray=ndefRecord[0].getPayload();
        String payloadString = new String(byteArray);
        return payloadString.substring(3);
    }

    private void writeInUsersCollectionReferenceInitialValuesForSensors(Map<String, Object> data, String plainText) {
        CollectionReference usersCollectionRef = db.collection("users").document(uid).collection("Uredaji");
        usersCollectionRef.document(plainText).set(data);
    }

    private void putLampInitialValue(Map<String, Object> data) {
        data.put("upaljen","false");
    }

    private void putTemperatureAndMoistureInitialValue(Map<String, Object> data) {
        data.put("temperatura", "0");
        data.put("vlaga", "0");
    }

    private void notifyUserForSuccessScan() {
        Toast.makeText(this, "Uspjesno skeniran NFC, uredaj dodan", Toast.LENGTH_LONG).show();
    }



    private static void setIntentFilters(IntentFilter[] filters) {
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_DEVICE_ADD);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }
}
