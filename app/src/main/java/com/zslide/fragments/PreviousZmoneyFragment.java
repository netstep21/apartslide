package com.zslide.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.adapter.ZmoneyHistoryAdapter;
import com.zslide.data.UserManager;
import com.zslide.data.model.HomeZmoney;
import com.zslide.models.ZmoneyHistory;
import com.zslide.network.ZummaApi;
import com.zslide.network.ZummaApiErrorHandler;
import com.zslide.utils.DisplayUtil;
import com.zslide.widget.BaseRecyclerView;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.renderer.XAxisRenderer;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by chulwoo on 2015. 10. 29..
 */
public class PreviousZmoneyFragment extends BaseFragment {

    private static final int INITIAL_CHART_ENTRY_SIZE = 13;

    @BindView(R.id.container) SwipeRefreshLayout container;
    @BindView(R.id.histories) BaseRecyclerView listView;

    @BindColor(R.color.white) int circleHoleColor;
    @BindColor(R.color.gray_7) int yGridColor;
    @BindColor(R.color.black_30) int yLabelColor;
    @BindColor(R.color.zmoney_dashboard_accent) int accentColor;
    @BindColor(R.color.zmoney_dashboard_bg) int bgColor;

    @BindDimen(R.dimen.spacing_micro) int spaceYear;

    TextView currentdDteView;
    TextView currentZmoneyView;
    CombinedChart chartView;

    private Subscription refreshRequest;
    private List<ZmoneyHistory> histories;
    private List<ZmoneyHistory> listHistories;

    private ZmoneyHistoryAdapter zmoneyHistoryAdapter;

    public static PreviousZmoneyFragment newInstance() {

        Bundle args = new Bundle();

        PreviousZmoneyFragment fragment = new PreviousZmoneyFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.fragment_zmoney_previous;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //animateChart();
        }
    }

    public void animateChart() {
        if (histories != null && histories.size() > 0) {
            // highlight last value
            Highlight highlight = new Highlight(histories.size() - 1, 0, 0);
            highlight.setDataIndex(0);
            chartView.highlightValue(highlight);
            ZmoneyHistory history = histories.get(histories.size() - 1);
            setValue(history.getYear(), history.getMonth(), history.getTotalReward());
        }
        // animate
        chartView.animateY(300);
    }

    @Override
    protected void setupLayout(View view, Bundle savedInstanceState) {
        zmoneyHistoryAdapter = new ZmoneyHistoryAdapter(this::showMoreData);
        listView.setLayoutManager(new LinearLayoutManager(getActivity()));
        listView.setAdapter(zmoneyHistoryAdapter);

        View headerView = LayoutInflater.from(view.getContext())
                .inflate(R.layout.header_zmoney_previous, listView, false);
        currentdDteView = ButterKnife.findById(headerView, R.id.currentDate);
        currentZmoneyView = ButterKnife.findById(headerView, R.id.currentZmoney);
        chartView = ButterKnife.findById(headerView, R.id.chart);
        zmoneyHistoryAdapter.addHeaderView(headerView);

        container.setColorSchemeColors(accentColor);
        container.setOnRefreshListener(this::refresh);
        chartView.setNoDataText("정보를 가져오고 있습니다.");
        chartView.setExtraBottomOffset(20);
        chartView.setPinchZoom(false);
        chartView.setDoubleTapToZoomEnabled(false);
        chartView.setScaleEnabled(false);
        chartView.setDescription(null);
        chartView.getLegend().setEnabled(false);
        chartView.setDrawMarkers(true);
        chartView.setMarker(new MyMarkerView(getContext()));
        chartView.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            private Highlight lastHighlight;

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                lastHighlight = h;
                try {
                    ZmoneyHistory history = histories.get((int) e.getX());
                    setValue(history.getYear(), history.getMonth(), (int) e.getY());
                } catch (Exception ex) {
                    // pass
                }
            }

            @Override
            public void onNothingSelected() {
                chartView.highlightValue(lastHighlight);
            }
        });
        setupXAxis(chartView.getXAxis());
        setupYAxis(chartView.getAxisLeft(), chartView.getAxisRight());
        refresh();
    }

    private void setupXAxis(XAxis xAxis) {
        chartView.setXAxisRenderer(new MyXAxisRenderer(
                chartView.getViewPortHandler(), chartView.getXAxis(),
                chartView.getTransformer(YAxis.AxisDependency.LEFT)));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter((value, axis) -> {
            Calendar c = Calendar.getInstance();
            int currentYear = c.get(Calendar.YEAR);
            int currentMonth = c.get(Calendar.MONTH) + 1;

            ZmoneyHistory history = histories.get((int) value);
            int year = history.getYear();
            int month = history.getMonth();
            String stringYear = getString(R.string.format_year_short, year % 100);
            String stringMonth = getString(R.string.format_month, month);

            if (year == currentYear && month == currentMonth) {
                stringMonth = stringMonth + "*";
            }

            if (year != currentYear) {
                if ((value - 0.5f) == chartView.getXAxis().getAxisMinimum()) {
                    return stringYear + " " + stringMonth;
                }
            } else if ((month == 1 || month == 2)) {
                return stringYear + " " + stringMonth;
            }

            return stringMonth;
        });
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
    }

    private void setupYAxis(YAxis yl, YAxis yr) {
        chartView.setRendererLeftYAxis(new MyYAxisRenderer(
                chartView.getViewPortHandler(), yl, chartView.getTransformer(YAxis.AxisDependency.LEFT)));
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(true);
        yl.setGridDashedLine(new DashPathEffect(new float[]{10, 4}, 1));
        yl.setGridColor(yGridColor);
        yl.setDrawZeroLine(true);
        yl.setTextSize(10);
        yl.setTextColor(yLabelColor);
        yl.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        yl.setValueFormatter((value, axis) -> {
            if (value == 0) {
                return "";
            } else if (value < 100) {
                value = (value / 10.0f);
                if (value % 1.0 == 0) {
                    return String.format(getString(R.string.format_ten), (int) value);
                } else {
                    return String.format(getString(R.string.format_ten_f), value);
                }
            } else if (value < 1000) {
                value = (value / 100.0f);
                if (value % 1.0 == 0) {
                    return String.format(getString(R.string.format_hundred), (int) value);
                } else {
                    return String.format(getString(R.string.format_hundred_f), value);
                }
            } else if (value < 10000) {
                value = (value / 1000.0f);
                if (value % 1.0 == 0) {
                    return String.format(getString(R.string.format_k), (int) value);
                } else {
                    return String.format(getString(R.string.format_k_f), value);
                }
            } else {
                value = (value / 10000.0f);
                if (value % 1.0 == 0) {
                    return String.format(getString(R.string.format_10k), (int) value);
                } else {
                    return String.format(getString(R.string.format_10k_f), value);
                }
            }
        });

        yr.setEnabled(false);
        yl.setAxisMinimum(0);
    }

    public void cancelRefresh() {
        if (refreshRequest != null && !refreshRequest.isUnsubscribed()) {
            refreshRequest.unsubscribe();
            container.setRefreshing(false);
        }
    }

    public void refresh() {
        cancelRefresh();

        container.setRefreshing(true);
        refreshRequest = ZummaApi.zmoney().history()
                .subscribeOn(Schedulers.newThread())
                .map(histories -> {
                    this.histories = histories;
                    this.listHistories = new ArrayList<>();
                    int end = histories.size();
                    int start = end - INITIAL_CHART_ENTRY_SIZE;
                    if (start < 0) {
                        start = 0;
                    }

                    for (int i = end - 1; i >= start; i--) {
                        listHistories.add(histories.get(i));
                    }

                    return this.histories;
                })
                //.map(this::addCurrentMonthData)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnTerminate(() -> container.setRefreshing(false))
                .subscribe(histories -> onHistoryChanged(), ZummaApiErrorHandler::handleError);
    }

    private List<ZmoneyHistory> addCurrentMonthData(List<ZmoneyHistory> histories) {
        List<ZmoneyHistory.Reward> rewards = new ArrayList<>();
        HomeZmoney homeZmoney = UserManager.getInstance().getZmoneyValue();
        /*if (homeZmoney.isNotNull()) {
            if (family == null) {
                rewards.add(
                        new ZmoneyHistory.Reward(
                                user.getId(),
                                user.getName(),
                                user.getCurrentSavedCash()));
            } else {
                for (User member : family.getMembers()) {
                    rewards.add(
                            new ZmoneyHistory.Reward(
                                    member.getId(),
                                    member.getName(),
                                    member.getCurrentSavedCash()));
                }
            }
        }
        */
        Calendar calendar = Calendar.getInstance();
        if (histories.size() == 0) {
            calendar.setTime(new Date());
        } else {
            ZmoneyHistory last = histories.get(histories.size() - 1);
            calendar.set(last.getYear(), last.getMonth() - 1, 1);
            calendar.add(Calendar.MONTH, 1);
        }

        /*ZmoneyHistory current = new ZmoneyHistory(
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, rewards);
        histories.add(current);*/

        return histories;
    }

    public void onHistoryChanged() {
        this.histories = new ArrayList<>(histories);
        zmoneyHistoryAdapter.replaceAll(listHistories);
        zmoneyHistoryAdapter.notifyDataSetChanged();

        ZmoneyHistory current = this.histories.get(this.histories.size() - 1);
        currentdDteView.setText(getString(R.string.format_date2, current.getYear(), current.getMonth()));
        currentZmoneyView.setText(getString(R.string.format_point, current.getTotalReward()));

        ArrayList<BarEntry> bgEntries = new ArrayList<>();
        ArrayList<Entry> entries = new ArrayList<>();

        int historySize = this.histories.size();
        if (historySize < INITIAL_CHART_ENTRY_SIZE) {
            // 0원짜리 기록 추가
            Calendar calendar = Calendar.getInstance();
            ZmoneyHistory lastHistory = this.histories.get(historySize - 1);
            calendar.set(lastHistory.getYear(), lastHistory.getMonth() - 1, 1);
            calendar.add(Calendar.MONTH, -historySize);
            for (int i = 0; i < (INITIAL_CHART_ENTRY_SIZE - historySize); i++) {
                ZmoneyHistory history = new ZmoneyHistory(
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
                this.histories.add(0, history);
                calendar.add(Calendar.MONTH, -1);
            }
        } else if (historySize > INITIAL_CHART_ENTRY_SIZE) {
            zmoneyHistoryAdapter.showLoadMoreProgress();
        }

        float max = 0;
        historySize = this.histories.size();
        for (int i = 0; i < historySize; i++) {
            ZmoneyHistory history = this.histories.get(i);
            int totalReward = history.getTotalReward();
            if (totalReward > max) {
                max = totalReward;
            }

            bgEntries.add(new BarEntry(i, 0));
            entries.add(new Entry(i, totalReward));
        }
        chartView.getXAxis().setAxisMinimum(this.histories.size() - INITIAL_CHART_ENTRY_SIZE - 0.5f);
        chartView.getXAxis().setAxisMaximum(this.histories.size() - 0.5f);
        if (max < 1000) {
            max = 1000;
        }
        // 현재 달 배경 추가

        int length = (int) (Math.log10(max)) + 1;
        int digit = (int) Math.pow(10, length - 1);
        int firstNumber = (int) max / digit;
        int chartMax = (firstNumber + 1) * digit;
        bgEntries.remove(historySize - 1);
        bgEntries.add(new BarEntry(historySize - 1, chartMax - 0.001f));

        LineDataSet lineDataSet = new LineDataSet(entries, null);
        lineDataSet.setDrawHorizontalHighlightIndicator(false);
        lineDataSet.setHighLightColor(accentColor);
        lineDataSet.setColor(accentColor);
        lineDataSet.setDrawValues(false);
        lineDataSet.setCircleColor(accentColor);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setDrawCircleHole(false);
        lineDataSet.setCircleRadius(1f);
        LineData lineData = new LineData(lineDataSet);

        BarDataSet barDataSet = new BarDataSet(bgEntries, null);
        barDataSet.setColor(bgColor);
        barDataSet.setDrawValues(false);

        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(1f);
        barData.setHighlightEnabled(false);

        CombinedData data = new CombinedData();
        data.setData(lineData);
        data.setData(barData);
        chartView.setData(data);

        YAxis yl = chartView.getAxisLeft();
        yl.setAxisMaximum(chartMax);
        yl.setGranularity(chartMax / 2);
        animateChart();
    }

    public void showMoreData() {
        int diff = histories.size() - listHistories.size();
        if (diff == 0) {
            // does not exists anymore
            return;
        }

        int prevSize = listHistories.size();
        int subListEnd = histories.size() - listHistories.size() - 1;
        int subListStart = subListEnd - 12;
        if (subListStart < 0) {
            subListStart = 0;
        }

        for (int i = subListEnd - 1; i >= subListStart; i--) {
            zmoneyHistoryAdapter.add(histories.get(i));
        }

        zmoneyHistoryAdapter.notifyItemRangeInserted(prevSize, listHistories.size() - prevSize);
        zmoneyHistoryAdapter.hideLoadMoreProgress();

        chartView.getXAxis().setAxisMinimum(this.histories.size() - this.listHistories.size() - 1.5f);
        chartView.setData(chartView.getData());
    }

    public void setValue(int year, int month, int value) {
        currentdDteView.setText(getString(R.string.format_date2, year, month));
        currentZmoneyView.setText(getString(R.string.format_point, value));
    }

    class MyXAxisRenderer extends XAxisRenderer {

        Rect textBuffer;
        Paint yearPaint;

        public MyXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans) {
            super(viewPortHandler, xAxis, trans);
            yearPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            yearPaint.setColor(yLabelColor);
            yearPaint.setTextAlign(Paint.Align.CENTER);
            yearPaint.setTextSize(Utils.convertDpToPixel(10f));
            textBuffer = new Rect();
        }

        @Override
        protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
            String[] labels = formattedLabel.split(" ");
            if (labels.length == 2) {
                mAxisLabelPaint.getTextBounds(labels[0], 0, labels[0].length(), textBuffer);
                Utils.drawXAxisValue(c, labels[0], x, y + textBuffer.height() + spaceYear, yearPaint, anchor, angleDegrees);
                formattedLabel = labels[1];
            }

            if (formattedLabel.contains("*")) {
                mAxisLabelPaint.setTypeface(Typeface.DEFAULT_BOLD);
                formattedLabel = formattedLabel.substring(0, formattedLabel.length() - 1);
            }
            super.drawLabel(c, formattedLabel, x, y, anchor, angleDegrees);
            mAxisLabelPaint.setTypeface(Typeface.DEFAULT);
        }
    }

    class MyYAxisRenderer extends YAxisRenderer {

        public MyYAxisRenderer(ViewPortHandler viewPortHandler, YAxis yAxis, Transformer trans) {
            super(viewPortHandler, yAxis, trans);
        }

        @Override
        protected void drawYLabels(Canvas c, float fixedPosition, float[] positions, float offset) {
            float textSize = Utils.convertDpToPixel(10);
            for (int i = 0; i < positions.length; i++) {
                positions[i] = positions[i] + textSize;
            }
            super.drawYLabels(c, fixedPosition, positions, offset);
        }
    }

    class MyMarkerView extends MarkerView {

        public MyMarkerView(Context context) {
            super(context, R.layout.view_zmoney_dashboard_marker);
            int offset = DisplayUtil.dpToPx(5, context.getResources());
            setOffset(-offset, -offset);
        }
    }
}