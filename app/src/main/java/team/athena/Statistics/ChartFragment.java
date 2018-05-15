package team.athena.Statistics;

import android.app.Fragment;

import com.github.mikephil.charting.data.Entry;

import java.util.ArrayList;

/**
 * Created by ori on 24-Jan-18.
 */

abstract public class ChartFragment extends Fragment {
    abstract public void setGraphData(String graphName, String[] timeStampAxisArray, ArrayList<Entry> dataY);
}
