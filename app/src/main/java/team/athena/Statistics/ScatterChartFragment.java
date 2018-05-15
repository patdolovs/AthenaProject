package team.athena.Statistics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;

import java.util.ArrayList;

import team.athena.R;

public class ScatterChartFragment extends ChartFragment {
    public static final String SCATTER_CHART_FRAGMENT_KEY = "SCATTER_CHART_FRAGMENT_KEY";
    private static final String GRAPH_DATA_KEY = "graphData";
    private static final String CATEGORY_KEY = "mInitialCategory";
    private static final String TIMESTAMP_AXIS_KEY = "timeStampAxisArray";
    String[] timeStampAxisArray;
    private ArrayList<Entry> graphData;
    private String mInitialCategory;
    private ScatterChart scatterChart;

    public static ScatterChartFragment newInstance(String mInitialCategory, ArrayList<Entry> graphData, String[] timeStampAxisArray) {
        ScatterChartFragment fragment = new ScatterChartFragment();
        Bundle args = new Bundle();
        args.putSerializable(GRAPH_DATA_KEY, graphData);
        args.putSerializable(CATEGORY_KEY, mInitialCategory);
        args.putSerializable(TIMESTAMP_AXIS_KEY, timeStampAxisArray);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            graphData = (ArrayList<Entry>) getArguments().get(GRAPH_DATA_KEY);
            mInitialCategory = getArguments().getString(CATEGORY_KEY);
            timeStampAxisArray = getArguments().getStringArray(TIMESTAMP_AXIS_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scatter_chart, container, false);
        scatterChart = rootView.findViewById(R.id.scatter_chart);
        setGraphData(mInitialCategory, timeStampAxisArray, graphData);

        return rootView;
    }

    @Override
    public void setGraphData(String graphName, String[] timeStampAxisArray, ArrayList<Entry> dataY) {
        final ScatterDataSet dataSet = new ScatterDataSet(dataY, graphName);
        scatterChart.setData(new ScatterData(timeStampAxisArray, dataSet));
        scatterChart.notifyDataSetChanged();
        scatterChart.invalidate();
    }


}
