package com.zslide.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.zslide.R;
import com.zslide.data.UserManager;
import com.zslide.data.model.Family;
import com.zslide.data.model.User;
import com.zslide.data.model.Zmoney;
import com.zslide.models.LevelInfo;
import com.zslide.widget.BaseRecyclerView;
import com.crashlytics.android.Crashlytics;
import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.buffer.BarBuffer;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.renderer.HorizontalBarChartRenderer;
import com.github.mikephil.charting.renderer.XAxisRendererHorizontalBarChart;
import com.github.mikephil.charting.utils.FSize;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

import static com.github.mikephil.charting.utils.Utils.getSizeOfRotatedRectangleByDegrees;


/**
 * Created by chulwoo on 2017. 2. 22..
 */

public class ZmoneyDashboardItemAdapter extends ExpandableListAdapter<Zmoney> {

    private String[] labels;
    private int[] levelIcons;
    private DateTimeFormatter dateFormatter;

    public ZmoneyDashboardItemAdapter(Context context, DateTimeFormatter formatter) {
        this.dateFormatter = formatter;

        Resources res = context.getResources();
        loadZmoneyTypes(res);
        loadLevelIcons(res);
    }

    private void loadZmoneyTypes(Resources res) {
        TypedArray a = res.obtainTypedArray(R.array.zmoney_types);
        labels = new String[a.length()];
        for (int i = 0; i < labels.length; i++) {
            labels[i] = a.getString(i);
        }
        a.recycle();
    }

    private void loadLevelIcons(Resources res) {
        TypedArray a = res.obtainTypedArray(R.array.level_icons);
        levelIcons = new int[a.length()];
        for (int i = 0; i < levelIcons.length; i++) {
            levelIcons[i] = a.getResourceId(i, R.drawable.img_lv_0);
        }
        a.recycle();
    }

    @Override
    public int getHeaderItemCount() {
        return 1;
    }

    @Override
    protected RecyclerView.ViewHolder onCreateHeaderItemViewHolder(ViewGroup parent, int headerViewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_zmoney_dashboard, parent, false);
        return new HeaderViewHolder(view);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateContentItemViewHolder(ViewGroup parent, int contentViewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_zmoney_dashboard, parent, false);
        ItemViewHolder holder = new ItemViewHolder(view);
        BarChart detailsView = holder.detailsView;
        detailsView.setScaleEnabled(false);
        detailsView.setMinOffset(0);
        detailsView.setDrawBarShadow(false);
        detailsView.getDescription().setEnabled(false);
        detailsView.setPinchZoom(false);
        detailsView.setDoubleTapToZoomEnabled(false);
        detailsView.setDrawGridBackground(false);
        detailsView.getLegend().setEnabled(false);
        detailsView.setRenderer(new MyHorizontalBarChartRenderer(
                detailsView, detailsView.getAnimator(), detailsView.getViewPortHandler()));
        detailsView.setDrawValueAboveBar(false);

        XAxis xl = detailsView.getXAxis();
        xl.setDrawAxisLine(false);
        xl.setDrawGridLines(false);
        xl.setDrawLabels(true);
        xl.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xl.setTextSize(15);

        YAxis yl = detailsView.getAxisLeft();
        yl.setEnabled(false);
        yl.setAxisMinimum(0);
        detailsView.getAxisRight().setEnabled(false);

        return holder;
    }

    @Override
    protected void onBindHeaderItemViewHolder(RecyclerView.ViewHolder headerViewHolder, int position) {
        HeaderViewHolder holder = (HeaderViewHolder) headerViewHolder;
        holder.dateView.setText(dateFormatter.format(LocalDateTime.now()));
        int total = 0;
        for (Zmoney zmoney : getAll()) {
            total += zmoney.getTotal();
        }
        holder.zmoneyView.setText(holder.getContext().getString(R.string.format_point, total));
    }

    @Override
    protected void onBindContentItemViewHolder(RecyclerView.ViewHolder contentViewHolder, int position) {
        super.onBindContentItemViewHolder(contentViewHolder, position);
        Zmoney zmoney = getItem(position);
        ItemViewHolder holder = (ItemViewHolder) contentViewHolder;
        Context context = holder.getContext();

        User user = zmoney.getUser();
        glide().load(TextUtils.isEmpty(user.getProfileImageUrl()) ?
                R.drawable.img_profile_default : user.getProfileImageUrl())
                .bitmapTransform(new CropCircleTransformation(context))
                .into(holder.thumbnailView);
        int level = 0;
        if (user.getLevelInfo() == null) {
            // 대체 왜... 서버에선 이걸 해결 안해줄까;
            Crashlytics.log("LevelInfo is null...");
        } else {
            LevelInfo.Advantage advantage = user.getLevelInfo().getAdvantage();
            if (advantage == null) {
                // 대체 왜... 서버에선 이걸 해결 안해줄까;
                Crashlytics.log("Advantage is null...");
            } else {
                level = advantage.getPriority();
            }
        }

        if (level >= levelIcons.length) {
            holder.levelView.setImageResource(levelIcons[levelIcons.length - 1]);
        } else {
            holder.levelView.setImageResource(levelIcons[level]);
        }

        holder.nicknameView.setText(user.getNickname());

        ArrayList<BarEntry> valueSet = new ArrayList<>();
        holder.zmoneyView.setText(context.getString(R.string.format_point, zmoney.getTotal()));
        int[] zmoneyArray = zmoney.asArray();
        //noinspection unchecked
        Pair<String, Integer>[] pairs = new Pair[zmoneyArray.length];
        for (int i = 0; i < zmoneyArray.length; i++) {
            pairs[i] = Pair.create(labels[i], zmoneyArray[i]);
        }
        Arrays.sort(pairs, (p1, p2) -> (p1.second).compareTo(p2.second));
        String[] sortedLabels = new String[pairs.length];
        for (int i = 0; i < sortedLabels.length; i++) {
            sortedLabels[i] = pairs[i].first;
        }
        for (int i = 0; i < zmoneyArray.length; i++) {
            valueSet.add(new BarEntry(i, pairs[i].second));
        }

        holder.detailsView.setXAxisRenderer(new MyXAxisRenderer(
                holder.detailsView.getViewPortHandler(),
                holder.detailsView.getXAxis(),
                holder.detailsView.getTransformer(YAxis.AxisDependency.LEFT),
                holder.detailsView, sortedLabels));

        BarDataSet barDataSet = new BarDataSet(valueSet, "");
        barDataSet.setColors(ContextCompat.getColor(context, R.color.zmoney_dashboard_priority4),
                ContextCompat.getColor(context, R.color.zmoney_dashboard_priority4),
                ContextCompat.getColor(context, R.color.zmoney_dashboard_priority4),
                ContextCompat.getColor(context, R.color.zmoney_dashboard_priority4),
                ContextCompat.getColor(context, R.color.zmoney_dashboard_priority3),
                ContextCompat.getColor(context, R.color.zmoney_dashboard_priority2),
                ContextCompat.getColor(context, R.color.zmoney_dashboard_priority1));
        barDataSet.setDrawValues(true);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(1);
        barData.setHighlightEnabled(false);
        barDataSet.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) ->
                context.getString(R.string.format_point, (int) value));
        barDataSet.setValueTextSize(15);
        holder.detailsView.setData(barData);
        holder.detailsView.getAxisLeft().setAxisMaximum(zmoney.getTotal());

        Family family = UserManager.getInstance().getFamilyValue();
        if (family.isFamilyLeader(user)) {
            holder.leaderBadgeView.setVisibility(View.VISIBLE);
        } else {
            holder.leaderBadgeView.setVisibility(View.GONE);
        }

        User me = UserManager.getInstance().getUserValue();
        if (zmoney.getUser().equals(me)) {
            holder.nameView.setText(user.getName());
            holder.iBadgeView.setVisibility(View.VISIBLE);
        } else {
            if (TextUtils.isEmpty(user.getName())) {
                holder.nameView.setText("");
            } else if (user.getName().length() >= 2) {
                holder.nameView.setText(user.getName().substring(0, 1) + "*" + user.getName().substring(2, user.getName().length()));
            } else {
                holder.nameView.setText("*");
            }

            holder.iBadgeView.setVisibility(View.GONE);
        }

        if (isExpanded(position)) {
            holder.arrowView.setRotation(180);
            holder.detailsContainer.setVisibility(View.VISIBLE);
        } else {
            holder.arrowView.setRotation(0);
            holder.detailsContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void open(RecyclerView.ViewHolder viewHolder, int position) {
        expandedPositions.add(position);
        ItemViewHolder holder = (ItemViewHolder) viewHolder;
        holder.detailsContainer.animate().cancel();
        holder.detailsContainer.setVisibility(View.VISIBLE);
        holder.detailsContainer.setPivotY(0);
        holder.detailsContainer.setScaleY(0);
        holder.detailsView.animateY(350);
        holder.detailsContainer.animate()
                .scaleY(1)
                .setDuration(250)
                .setListener(null)
                .start();
        holder.arrowView.animate()
                .rotation(180)
                .setDuration(250)
                .start();
    }

    @Override
    public void close(RecyclerView.ViewHolder viewHolder, int position) {
        expandedPositions.remove(position);
        ItemViewHolder holder = (ItemViewHolder) viewHolder;
        holder.detailsContainer.animate().cancel();
        holder.detailsContainer.setPivotY(0);
        holder.detailsContainer.setScaleY(1);
        holder.detailsContainer.animate()
                .scaleY(0)
                .setDuration(150)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        holder.detailsContainer.setVisibility(View.GONE);
                    }
                })
                .start();
        holder.arrowView.animate()
                .rotation(0)
                .setDuration(150)
                .start();
    }

    class HeaderViewHolder extends BaseRecyclerView.BaseViewHolder {

        @BindView(R.id.date) TextView dateView;
        @BindView(R.id.zmoney) TextView zmoneyView;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    class ItemViewHolder extends BaseRecyclerView.BaseViewHolder {

        @BindView(R.id.thumbnail) ImageView thumbnailView;
        @BindView(R.id.level) ImageView levelView;
        @BindView(R.id.nickname) TextView nicknameView;
        @BindView(R.id.name) TextView nameView;
        @BindView(R.id.iBadge) ImageView iBadgeView;
        @BindView(R.id.leaderBadge) ImageView leaderBadgeView;
        @BindView(R.id.zmoney) TextView zmoneyView;
        @BindView(R.id.arrow) ImageView arrowView;
        @BindView(R.id.detailsContainer) FrameLayout detailsContainer;
        @BindView(R.id.details) HorizontalBarChart detailsView;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private static class MyXAxisRenderer extends XAxisRendererHorizontalBarChart {

        private static Rect drawTextRectBuffer = new Rect();
        private static Paint.FontMetrics fontMetricsBuffer = new Paint.FontMetrics();

        private String[] labels;

        MyXAxisRenderer(ViewPortHandler viewPortHandler, XAxis xAxis, Transformer trans,
                        BarChart chart, String[] labels) {
            super(viewPortHandler, xAxis, trans, chart);
            this.labels = labels;
        }

        @Override
        protected void drawLabel(Canvas c, String formattedLabel, float x, float y, MPPointF anchor, float angleDegrees) {
            float drawOffsetX = 0.f;
            float drawOffsetY = 0.f;

            int index = Integer.parseInt(formattedLabel);
            if (index < 0 || index >= labels.length) {
                return;
            }
            String label = labels[index];

            final float lineHeight = mAxisLabelPaint.getFontMetrics(fontMetricsBuffer);
            mAxisLabelPaint.getTextBounds(label, 0, label.length(), drawTextRectBuffer);
            x = mViewPortHandler.contentLeft() + drawTextRectBuffer.width() +
                    Utils.convertDpToPixel(18);

            // Android sometimes has pre-padding
            drawOffsetX -= drawTextRectBuffer.left;

            // Android does not snap the bounds to line boundaries,
            //  and draws from bottom to top.
            // And we want to normalize it.
            drawOffsetY += -fontMetricsBuffer.ascent;

            // To have a consistent point of reference, we always draw left-aligned
            Paint.Align originalTextAlign = mAxisLabelPaint.getTextAlign();
            mAxisLabelPaint.setTextAlign(Paint.Align.LEFT);

            if (angleDegrees != 0.f) {

                // Move the text drawing rect in a way that it always rotates around its center
                drawOffsetX -= drawTextRectBuffer.width() * 0.5f;
                drawOffsetY -= lineHeight * 0.5f;

                float translateX = x;
                float translateY = y;

                // Move the "outer" rect relative to the anchor, assuming its centered
                if (anchor.x != 0.5f || anchor.y != 0.5f) {
                    final FSize rotatedSize = getSizeOfRotatedRectangleByDegrees(
                            drawTextRectBuffer.width(),
                            lineHeight,
                            angleDegrees);

                    translateX -= rotatedSize.width * (anchor.x - 0.5f);
                    translateY -= rotatedSize.height * (anchor.y - 0.5f);
                    FSize.recycleInstance(rotatedSize);
                }

                c.save();
                c.translate(translateX, translateY);
                c.rotate(angleDegrees);

                c.drawText(label, drawOffsetX, drawOffsetY, mAxisLabelPaint);

                c.restore();
            } else {
                if (anchor.x != 0.f || anchor.y != 0.f) {

                    drawOffsetX -= drawTextRectBuffer.width() * anchor.x;
                    drawOffsetY -= lineHeight * anchor.y;
                }

                drawOffsetX += x;
                drawOffsetY += y;

                c.drawText(label, drawOffsetX, drawOffsetY, mAxisLabelPaint);
            }

            mAxisLabelPaint.setTextAlign(originalTextAlign);
        }
    }

    private class MyHorizontalBarChartRenderer extends HorizontalBarChartRenderer {

        public MyHorizontalBarChartRenderer(BarDataProvider chart, ChartAnimator animator,
                                            ViewPortHandler viewPortHandler) {
            super(chart, animator, viewPortHandler);
        }

        @Override
        public void drawValues(Canvas c) {
            // if values are drawn
            if (isDrawingValuesAllowed(mChart)) {
                List<IBarDataSet> dataSets = mChart.getBarData().getDataSets();
                for (int i = 0; i < mChart.getBarData().getDataSetCount(); i++) {
                    IBarDataSet dataSet = dataSets.get(i);

                    if (!shouldDrawValues(dataSet))
                        continue;
                    // apply the text-styling defined by the DataSet
                    applyValueTextStyle(dataSet);
                    final float halfTextHeight = Utils.calcTextHeight(mValuePaint, "10") / 2f;
                    IValueFormatter formatter = dataSet.getValueFormatter();
                    // get the buffer
                    BarBuffer buffer = mBarBuffers[i];
                    for (int j = 0; j < buffer.buffer.length * mAnimator.getPhaseX(); j += 4) {

                        float y = (buffer.buffer[j + 1] + buffer.buffer[j + 3]) / 2f;

                        if (!mViewPortHandler.isInBoundsTop(buffer.buffer[j + 1]))
                            break;

                        if (!mViewPortHandler.isInBoundsX(buffer.buffer[j]))
                            continue;

                        if (!mViewPortHandler.isInBoundsBottom(buffer.buffer[j + 1]))
                            continue;

                        BarEntry e = dataSet.getEntryForIndex(j / 4);
                        float val = e.getY();
                        String formattedValue = formatter.getFormattedValue(val, e, i, mViewPortHandler);

                        // calculate the correct offset depending on the draw position of the value
                        float valueTextWidth = Utils.calcTextWidth(mValuePaint, formattedValue);
                        drawValue(c, formattedValue, mViewPortHandler.contentRight() - valueTextWidth,
                                y + halfTextHeight, dataSet.getValueTextColor(j / 2));
                    }
                }
            }
        }

        @Override
        protected void drawValue(Canvas c, String valueText, float x, float y, int color) {
            super.drawValue(c, valueText, x + Utils.convertDpToPixel(-18), y, color);
        }
    }
}
