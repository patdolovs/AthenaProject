package team.athena;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.ArrayMap;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import team.athena.Statistics.ChartFragment;
import team.athena.Statistics.LineChartFragment;
import team.athena.Statistics.ScatterChartFragment;

import static team.athena.Statistics.LineChartFragment.LINE_CHART_FRAGMENT_KEY;
import static team.athena.Statistics.ScatterChartFragment.SCATTER_CHART_FRAGMENT_KEY;

/**
 * Created by Patrik on 16-Jan-18.
 */

public class StatisticsActivity extends AppCompatActivity implements View.OnClickListener {
    private String userID;
    private DocumentReference documentReference;
    private Button btnCategory;
    private Button btnFilterDates;
    private PopupMenu popup;
    private PopupMenu popupGraph;
    private SortedMap<Date, Par> sortedMap;
    private TreeMap<Date, Par> treeMapforSortedData = new TreeMap<>();
    private String selectedCategory = "Temperature";
    private Date dateSorted;
    private Date dateFrom;
    private Date dateUntil;
    private ArrayList<String> timestamp = new ArrayList<>();
    private ArrayList<Entry> temperature = new ArrayList<>();
    private ArrayList<Entry> humidity = new ArrayList<>();
    private Map<String, Object> firebaseData = new ArrayMap<>();
    private Object[] sortedFieldOfValues;
    private Button btnDatePicker;
    private Button btnDatePicker2;
    private EditText txtDate, txtDate2;
    private int mYear, mMonth, mDay;
    private ArrayList<String> timestampSorted = new ArrayList<>();
    private ArrayList<Entry> temperatureSorted = new ArrayList<>();
    private ArrayList<Entry> humiditySorted = new ArrayList<>();
    private ChartFragment chartFragment;
    private Button btnGraphType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLayoutOfScreen();
        disableKeyboardAtStart();
        getAndSetUIDFromPreviousActivity();
        setTypefaceValuesToTextboxes();
        chartFragment = ScatterChartFragment.newInstance(selectedCategory, temperature, timestamp.toArray(new String[0]));
        getFragmentManager().beginTransaction()
                .add(R.id.chart_container, chartFragment, SCATTER_CHART_FRAGMENT_KEY)
                .commit();
    }

    public void disableKeyboardAtStart() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onClick(View view) {

        if (userClickedOnFirstDatePicker(view)) {
            getCalendarInstance();
            showDateFromPickerDialogAndSetDateValue();
        }

        if (userClickedOnSecondDatePicker(view)) {

            getCalendarInstance();
            showDateUntilPickerDialogAndSetDateValue();
        }


    }

    private boolean userClickedOnFirstDatePicker(View view) {

        return view == btnDatePicker;
    }

    private boolean userClickedOnSecondDatePicker(View view) {

        return view == btnDatePicker2;
    }

    @Override
    protected void onStart() {
        super.onStart();
        getFirebaseSensorsDocument();
        addFirebaseSnapshotListener();
    }

    private void setLayoutOfScreen() {
        setContentView(R.layout.activity_statistics);
        btnGraphType = findViewById(R.id.graphType_button);
        btnDatePicker = findViewById(R.id.buttonDate);
        btnDatePicker.setOnClickListener(this);
        btnDatePicker2 = findViewById(R.id.buttonDate2);
        btnDatePicker2.setOnClickListener(this);
        txtDate = findViewById(R.id.textDate);
        txtDate2 = findViewById(R.id.textDate2);
        btnCategory = findViewById(R.id.buttonCategory);
        btnCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inflatePopupCategoryMenu();
                setPopupMenuOnClickListener();
                popup.show();

            }
        });
        btnGraphType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                inflatePopupGraphType();
                setPopupGraphOnClickListener();
                popupGraph.show();

            }
        });
    }

    private void getAndSetUIDFromPreviousActivity() {
        Intent intent = getIntent();
        userID = intent.getStringExtra("userUID");
    }


    private void getFilterDatesButton() {
        btnFilterDates = findViewById(R.id.buttonOdDo);
    }

    private void setTypefaceValuesToTextboxes() {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Exo2-MediumExpanded.otf");
        txtDate.setTypeface(typeface);
        txtDate2.setTypeface(typeface);
    }

    private void getFirebaseSensorsDocument() {

        documentReference = FirebaseFirestore.getInstance()
                .collection("users").document("stattest")
                .collection("Uredaji").document("senzor")
                .collection("temperatura").document("temperatura");
    }

    private void makeInvalidDateValuesToast() {
        Toast.makeText(
                StatisticsActivity.this,
                "Invalid range selected",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void getCalendarInstance() {
        final Calendar calendar = Calendar.getInstance();
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
    }

    private void parseDateFromPickerValue(int dayOfMonth, int year, int monthOfYear) {

        try {
            String tempDatum = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
            SimpleDateFormat tempSimple = new SimpleDateFormat("d.M.yyyy");
            dateFrom = tempSimple.parse(tempDatum);
        } catch (ParseException exception) {

            exception.printStackTrace();
        }
    }

    private void parseDateUntilPickerValue(int dayOfMonth, int year, int monthOfYear) {

        try {
            String tempDatum = dayOfMonth + "." + (monthOfYear + 1) + "." + year;
            SimpleDateFormat tempSimple = new SimpleDateFormat("d.M.yyyy");
            dateUntil = tempSimple.parse(tempDatum);
        } catch (ParseException exception) {

            exception.printStackTrace();
        }
    }

    private void setDateFromTextboxValue(int dayOfMonth, int year, int monthOfYear) {
        txtDate.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year);
    }

    private void setDateUntilTextboxValue(int dayOfMonth, int year, int monthOfYear) {
        txtDate2.setText(dayOfMonth + "." + (monthOfYear + 1) + "." + year);
    }

    private void showDateFromPickerDialogAndSetDateValue() {

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        setDateFromTextboxValue(dayOfMonth, year, monthOfYear);
                        parseDateFromPickerValue(dayOfMonth, year, monthOfYear);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void showDateUntilPickerDialogAndSetDateValue() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        setDateUntilTextboxValue(dayOfMonth, year, monthOfYear);
                        parseDateUntilPickerValue(dayOfMonth, year, monthOfYear);
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public void addFirebaseSnapshotListener() {
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                if (documentSnapshot.exists()) {

                    firebaseData = documentSnapshot.getData();
                    sortedFieldOfValues = new Object[firebaseData.size()];
                    fillSortedFieldOfValuesWithMapValues();
                    parseDataAndFillDataContainersForChart();
                    getFilterDatesButton();
                    setBtnFilterDatesOnClickListener();
                    fillGraphBasedOnCategory(selectedCategory);

                }
            }
        });
    }


    public void inflatePopupCategoryMenu() {
        popup = new PopupMenu(StatisticsActivity.this, btnCategory);
        popup.getMenuInflater()
                .inflate(R.menu.popup_menu, popup.getMenu());
    }

    public void inflatePopupGraphType() {
        popupGraph = new PopupMenu(StatisticsActivity.this, btnGraphType);
        popupGraph.getMenuInflater()
                .inflate(R.menu.popup_graph_type, popupGraph.getMenu());
    }

    public void setPopupMenuOnClickListener() {

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                setLineChartDataBasedOnClickedCategory(item);
                makeSelectedValueToast(item);

                return true;
            }
        });
    }

    public void setPopupGraphOnClickListener() {

        popupGraph.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {

                setFragmentBasedOnSelectedItemFromPopup(item);
                makeSelectedValueToast(item);

                return true;
            }
        });
    }

    private void setFragmentBasedOnSelectedItemFromPopup(MenuItem item) {
        String selectedType = item.getTitle().toString();
        String fragmentName = "";
        switch (selectedType) {
            case "Scatter":
                fragmentName = SCATTER_CHART_FRAGMENT_KEY;
                chartFragment = ScatterChartFragment.newInstance(selectedCategory, temperature, timestamp.toArray(new String[0]));
                break;
            case "Line":
                fragmentName = LINE_CHART_FRAGMENT_KEY;
                chartFragment = LineChartFragment.newInstance(selectedCategory, temperature, timestamp.toArray(new String[0]));
                break;
        }

        getFragmentManager().beginTransaction().replace(R.id.chart_container, chartFragment, fragmentName).commit();
    }

    private void setLineChartDataBasedOnClickedCategory(MenuItem item) {
        fillGraphBasedOnCategory(item.getTitle().toString());
    }

    private void fillGraphBasedOnCategory(String category) {
        switch (category) {
            case "Humidity":
                selectedCategory = "Humidity";
                chartFragment.setGraphData(selectedCategory, timestampSorted.toArray(new String[0]), humiditySorted);
                break;

            case "Temperature":
                selectedCategory = "Temperature";
                chartFragment.setGraphData(selectedCategory, timestampSorted.toArray(new String[0]), temperatureSorted);
        }
    }

    private void makeSelectedValueToast(MenuItem item) {
        Toast.makeText(
                StatisticsActivity.this,
                "Showing : " + item.getTitle(),
                Toast.LENGTH_SHORT
        ).show();
    }

    private void setBtnFilterDatesOnClickListener() {
        btnFilterDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txtDate.getText().length() > 0 && txtDate2.getText().length() > 0) {
                    if (dateFrom.before(dateUntil)) {
                        setSortedDataContainers();
                        fillDataContainersWithSortedValues();
                    } else {
                        makeInvalidDateValuesToast();
                    }
                } else {
                    makeInvalidDateValuesToast();
                }
            }
        });
    }

    private void setSortedDataContainers() {
        sortedMap = treeMapforSortedData.subMap(dateFrom, dateUntil);

    }

    private void fillSortedFieldOfValuesWithMapValues() {

        for (Map.Entry<String, Object> form : firebaseData.entrySet()) {

            int kljucBroj = Integer.parseInt(form.getKey());
            sortedFieldOfValues[kljucBroj] = form.getValue().toString();

        }
    }

    private void parseDataAndFillDataContainersForChart() {

        int iterator = 0;
        for (Object row : sortedFieldOfValues) {

            row = row.toString().substring(1, row.toString().length() - 1);
            String[] rowValues = row.toString().split(", ");
            timestamp.add(rowValues[0].substring(4, rowValues[0].length()));
            parseFirebaseTimestampValue(rowValues[0]);
            treeMapforSortedData.put(dateSorted, new Par(rowValues[1], rowValues[2]));
            temperature.add(new Entry(Float.parseFloat(rowValues[1]), iterator));
            humidity.add(new Entry(Float.parseFloat(rowValues[2]), iterator));
            iterator++;
        }
        //// TODO: 24-Jan-18 create method to sort data based on filters that exist and fill the arrays
        //sortData();
        temperatureSorted.clear();
        humiditySorted.clear();
        timestampSorted.clear();
        temperatureSorted.addAll(temperature);
        humiditySorted.addAll(humidity);
        timestampSorted.addAll(timestamp);

    }

    private void parseFirebaseTimestampValue(String podatak) {
        try {

            String datum = podatak;
            SimpleDateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT+01:00' yyyy", Locale.US);
            dateSorted = format.parse(datum);

        } catch (ParseException exception) {

            exception.printStackTrace();
        }
    }

    private void fillDataContainersWithSortedValues() {
        int iterate = 0;
        for (SortedMap.Entry<Date, Par> red : sortedMap.entrySet()) {

            timestampSorted.add(red.getKey().toString());
            temperatureSorted.add(new Entry(Float.parseFloat(red.getValue().getTemperatura()), iterate));
            humiditySorted.add(new Entry(Float.parseFloat(red.getValue().getVlaga()), iterate));
            iterate++;

        }
    }

    public class Par {
        String temperatura;
        String vlaga;

        Par(String temperatura, String vlaga) {
            this.temperatura = temperatura;
            this.vlaga = vlaga;
        }

        String getTemperatura() {
            return temperatura;
        }

        String getVlaga() {
            return vlaga;
        }
    }


}
