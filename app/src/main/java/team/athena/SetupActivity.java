package team.athena;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import team.athena.NFC.NFCTagTextReader;

public class SetupActivity extends AppCompatActivity {

    public String uid;
    public String ssid;
    public String pwd;
    public static final String gatewayAddress = "http://192.168.42.1:1337/";
    private WifiManager wifiManager;
    private NfcAdapter mNfcAdapter;
    public static final String MIME_ATHENA_WIFI = "athena/wifisettings";
    private Parcelable[] rawMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        getIntentAndSetUid();
        if (getWifiManagerAndEnableIt()) {
            if (getNfcAdapterAndCheckIfDeviceSupportsNfc())
                notifyDeviceDoesNotSupportNfc();
            checkIfNfcIsEnabled();
            handleIntent(getIntent());
        }
    }

    private void getIntentAndSetUid() {
        Intent intent = getIntent();
        uid = intent.getStringExtra("user");
    }

    private boolean getWifiManagerAndEnableIt() {
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.setWifiEnabled(true);
    }

    private boolean getNfcAdapterAndCheckIfDeviceSupportsNfc() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        return mNfcAdapter == null;
    }

    private void notifyDeviceDoesNotSupportNfc() {
        Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
        finish();
    }

    private void checkIfNfcIsEnabled() {
        if (mNfcAdapter!=null && !mNfcAdapter.isEnabled())
            notifyNfcDisabled();
        else
            notifyNfcEnabled();
    }

    private void notifyNfcDisabled() {
        Toast.makeText(this, "NFC is disabled.", Toast.LENGTH_SHORT).show();
    }

    private void notifyNfcEnabled() {
        Toast.makeText(this, "Upaljen NFC!", Toast.LENGTH_SHORT).show();
    }

    public void GetSsidAndPassAndSendGetRequest(View view)
    {
        GetSsidFromField();
        GetPassFromField();
        sendGetRequest();
    }

    private void GetSsidFromField() {
        EditText Ssid = findViewById(R.id.ssidText);
        ssid = Ssid.getText().toString();
    }

    private void GetPassFromField() {
        EditText Pass = findViewById(R.id.passwordText);
        pwd = Pass.getText().toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupForegroundDispatch(this, mNfcAdapter);
    }

    @Override
    protected void onPause() {
        stopForegroundDispatch(this, mNfcAdapter);
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (ifIntentIsNotNull(intent) && intentEqualsNdefDiscovered(intent))
            if(checkIfRawMessagesFromIntentAreNotNull(intent)) {
                NdefMessage[] messages = new NdefMessage[rawMessages.length];
                for (int i = 0; i < rawMessages.length; i++) {
                    messages[i] = (NdefMessage) rawMessages[i];
                    NdefRecord[] rec=messages[i].getRecords();
                    getMessagesFromNfcAndConnectToBaseStation(rec);
                }
            }
    }

    private boolean ifIntentIsNotNull(Intent intent) {
        return intent != null;
    }

    private boolean intentEqualsNdefDiscovered(Intent intent) {
        return NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction());
    }

    private boolean checkIfRawMessagesFromIntentAreNotNull(Intent intent) {
        rawMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        return rawMessages != null;
    }

    private void getMessagesFromNfcAndConnectToBaseStation(NdefRecord[] rec) {
        byte[] byteArray = rec[0].getPayload();
        String payloadString = new String(byteArray);
        String[] cuttedString = payloadString.split(":");
        String wifiSSID = cuttedString[0].substring(1, cuttedString[0].length()-1);
        String wifiPass = cuttedString[1].substring(1, cuttedString[1].length()-1);
        Connect(wifiSSID, wifiPass);
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

    private static void setIntentFilters(IntentFilter[] filters) {
        filters[0] = new IntentFilter();
        filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
        filters[0].addCategory(Intent.CATEGORY_DEFAULT);
        try {
            filters[0].addDataType(MIME_ATHENA_WIFI);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Check your mime type.");
        }
    }

    public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
        adapter.disableForegroundDispatch(activity);
    }


    private void handleIntent(Intent intent) {
        if (intentEqualsNdefDiscovered(intent)) {
            String type = intent.getType();
            if (MIME_ATHENA_WIFI.equals(type))
                executeNdefReaderTaskForNfcAdapterExtraTag(intent);
            else
                notifyUserForErrorWhileReadingNfc();
        }
    }
    private void executeNdefReaderTaskForNfcAdapterExtraTag(Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        new NFCTagTextReader().execute(tag);
    }

    private void notifyUserForErrorWhileReadingNfc() {
        Toast.makeText(this, "Error while reading NFC Tag", Toast.LENGTH_LONG).show();
    }

    public void Connect(String networkSSID, String networkPass)
    {
        checkIfWifiManagerIsEnabledAndEnableIt();
        int netId = getNetworkIdBySettingSsidAndPass(networkSSID, networkPass);
        reconnectWifiManagerWithNewConnection(netId);
        notifyWifiConnection();
    }

    private void checkIfWifiManagerIsEnabledAndEnableIt() {
        if(!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
    }

    private int getNetworkIdBySettingSsidAndPass(String networkSSID, String networkPass) {
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = String.format("\"%s\"", networkSSID);
        conf.preSharedKey = String.format("\"%s\"", networkPass);
        return wifiManager.addNetwork(conf);
    }

    private void reconnectWifiManagerWithNewConnection(int netId) {
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }

    private void notifyWifiConnection() {
        Toast.makeText(this, "Wifi Connecting", Toast.LENGTH_LONG).show();
    }

    public void sendGetRequest() {
        RequestQueue mRequestQueue = getRequestQueue();
        mRequestQueue.start();
        String url = setUrl();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(checkIfResponseIsSuccess(response))
                            changeActivityToMainActivity();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                notifyUserForErrorWhileSendingGetMessage(error);
            }
        });
        mRequestQueue.add(stringRequest);
    }

    private boolean checkIfResponseIsSuccess(String response) {
        return  response.equals("SUCCESS");
    }

    private void changeActivityToMainActivity() {
        Intent intent = new Intent(SetupActivity.this, MainActivity.class);
        intent.putExtra("user", uid);
        SetupActivity.this.startActivity(intent);
        finish();
    }

    private void notifyUserForErrorWhileSendingGetMessage(VolleyError error) {
        Toast.makeText(this, "Failed with error msg:\t" + error.getMessage(), Toast.LENGTH_LONG).show();
    }

    private RequestQueue getRequestQueue() {
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024);
        Network network = new BasicNetwork(new HurlStack());
        return new RequestQueue(cache, network);
    }

    private String setUrl() {
        return gatewayAddress + "?ssid=" + ssid + "&pwd=" + pwd + "&user=" + uid;
    }
}
