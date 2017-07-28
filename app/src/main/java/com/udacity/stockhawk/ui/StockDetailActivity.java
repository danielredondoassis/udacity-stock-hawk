package com.udacity.stockhawk.ui;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.data.PrefUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.udacity.stockhawk.data.Contract.Quote.URI;

public class StockDetailActivity extends AppCompatActivity {

    @BindView(R.id.textHeader)
    AppCompatTextView textHeader;
    @BindView(R.id.reportChartLayout)
    LinearLayout reportChartLayout;
    @BindView(R.id.imageArrowDown1)
    ImageView imageArrowDown1;
    @BindView(R.id.imageArrowDown2)
    ImageView imageArrowDown2;
    @BindView(R.id.imageArrowDown3)
    ImageView imageArrowDown3;
    @BindView(R.id.imageArrowDown4)
    ImageView imageArrowDown4;
    @BindView(R.id.imageArrowDown5)
    ImageView imageArrowDown5;
    @BindView(R.id.imageArrowDown6)
    ImageView imageArrowDown6;
    @BindView(R.id.reportArrow)
    LinearLayout reportArrow;
    @BindView(R.id.reportDetailTransactionLayout)
    LinearLayout reportDetailTransactionLayout;

    private DisplayMetrics mDisplayMetrics;

    private static final String[] STOCKK_COLUMNS = {
            Contract.Quote.TABLE_NAME + "." + Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
            Contract.Quote.COLUMN_HISTORY
    };

    private Cursor data = null;
    private ArrayList<RelativeLayout> mPolygonLayouts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stock_detail_activity);
        ButterKnife.bind(this);

        if(getIntent() == null) finish();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String stockSymbol = getIntent().getStringExtra(Contract.Quote.COLUMN_SYMBOL);

        this.mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        final DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);

        this.mPolygonLayouts = new ArrayList<RelativeLayout>();

        data = getContentResolver().query(URI,
                STOCKK_COLUMNS,
                Contract.Quote.COLUMN_SYMBOL + "='" + stockSymbol + "'",
                null,
                Contract.Quote.COLUMN_ABSOLUTE_CHANGE + " ASC");

        data.moveToFirst();

        if (reportChartLayout.getChildCount() == 0) {

            final int width = ((mDisplayMetrics.widthPixels - UnitConverter.dpToPx(40, this)) / 6);
            int height = UnitConverter.dpToPx(107, this);
            height = height - 82;

            String history = data.getString(Contract.Quote.POSITION_HISTORY);

            String[] historyAll = history.split("\n");

            final ArrayList<QuoteHistory> quoteArray = new ArrayList<>();

            for (String week : historyAll){

                String[] values = week.split(",");
                QuoteHistory quoteWeek = new QuoteHistory();
                quoteWeek.setDate(Long.parseLong(values[0]));
                quoteWeek.setValue(Float.parseFloat(values[1]));
                quoteArray.add(quoteWeek);

            }

            Collections.sort(quoteArray, new Comparator<QuoteHistory>() {
                @Override
                public int compare(QuoteHistory lhs, QuoteHistory rhs) {
                    if (rhs.getDate() > lhs.getDate())
                        return 1;
                    else if (rhs.getDate() == lhs.getDate()) // it's equals
                        return 0;
                    else
                        return -1;
                }
            });

            final ArrayList<QuoteHistory> quote6Array = new ArrayList<>();

            float highest = 0;
            for (int i = 0; i < 6; i++) {
                float aux = quoteArray.get(i).getValue();
                if (aux > highest) highest = aux;
                quote6Array.add(quoteArray.get(i));
            }

            if(quoteArray.size() > 6) quote6Array.add(quoteArray.get(6));

            Collections.sort(quote6Array, new Comparator<QuoteHistory>() {
                @Override
                public int compare(QuoteHistory lhs, QuoteHistory rhs) {
                    if (rhs.getDate() > lhs.getDate())
                        return -1;
                    else if (rhs.getDate() == lhs.getDate()) // it's equals
                        return 0;
                    else
                        return 1;
                }
            });

            for (int i = 0; i < 6; i++) {

                View layout = LayoutInflater.from(this).inflate(R.layout.custom_chart_layout, null, false);
                final RelativeLayout polygonLayout = (RelativeLayout) layout.findViewById(R.id.polygonLayout);
                RelativeLayout rightDivider = (RelativeLayout) layout.findViewById(R.id.rightDivider);
                TextView textDateHeader = (TextView) layout.findViewById(R.id.textMonthHeader);
                ImageView imageChartPolygon = (ImageView) layout.findViewById(R.id.imageChartPolygon);

                Calendar calendar = Calendar.getInstance();
                QuoteHistory model = quote6Array.get(i);

                calendar.setTimeInMillis(model.getDate());

                int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);

                String monthdate = calendar.get(Calendar.MONTH) < 10 ? ("0" + Integer.toString(calendar.get(Calendar.MONTH) + 1)) : Integer.toString(calendar.get(Calendar.MONTH) + 1);
                String monthString = (calendar.get(Calendar.DAY_OF_MONTH) < 10 ?
                        "0" + calendar.get(Calendar.DAY_OF_MONTH) :
                        Integer.toString(calendar.get(Calendar.DAY_OF_MONTH))) + "/" + monthdate;

                textDateHeader.setText(monthString);
                TextView textMaxAmountForPeriod = (TextView) layout.findViewById(R.id.textMaxAmountForPeriod);

                polygonLayout.setTag(i);

                textMaxAmountForPeriod.setVisibility(View.VISIBLE);
                textMaxAmountForPeriod.setText(dollarFormat.format(model.getValue()));

                if (i == 5) rightDivider.setVisibility(View.VISIBLE);

                QuoteHistory historyModel = null;
                QuoteHistory nextHistoryModel = quote6Array.size() < i+1 ? quote6Array.get(i+1) : null;
                QuoteHistory previousHistoryModel = i-1 > -1 ? quote6Array.get(i-1) : null;

                for (QuoteHistory history1 : quoteArray) {
                    if (history1.getDayOfYear() == dayOfYear) historyModel = history1;
                }

                if(i == 0) previousHistoryModel = historyModel;

                int historyModelHeight = historyModel != null ?
                        (int) (historyModel.getValue() * height / highest) : 0;

                int nextHistoryModelHeight = nextHistoryModel != null ?
                        (int) (nextHistoryModel.getValue() * height / highest) : 0;

                int previousHistoryModeHeight = previousHistoryModel != null ?
                        (int) (previousHistoryModel.getValue() * height / highest) : 0;

                if (AndroidVersionHelper.isJellyOrHigher()) {

                    imageChartPolygon.setBackground(new PolygonalDrawable(
                            this,
                            height,
                            i,
                            R.color.chartColor,
                            historyModelHeight,
                            width,
                            previousHistoryModeHeight,
                            nextHistoryModelHeight));
                } else {
                    imageChartPolygon.setBackgroundDrawable(new PolygonalDrawable(
                            this,
                            height,
                            i,
                            R.color.chartColor,
                            historyModelHeight,
                            width,
                            previousHistoryModeHeight,
                            nextHistoryModelHeight));
                }

                RelativeLayout.LayoutParams params2 = (RelativeLayout.LayoutParams) imageChartPolygon.getLayoutParams();
                params2.height = height;
                imageChartPolygon.setLayoutParams(params2);
                reportChartLayout.addView(layout);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) layout.getLayoutParams();
                params.weight = 1;
                layout.setLayoutParams(params);
            }
        }
    }

    class QuoteHistory {

        public QuoteHistory(){};

        private long date;
        private float value;

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public long getDate() {
            return date;
        }

        public void setDate(long date) {
            this.date = date;
        }

        public int getDayOfYear() {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(date);
            return calendar.get(Calendar.DAY_OF_YEAR);
        }
    }

    static class UnitConverter {

        public static int dpToPx(int dp, Context ctx) {
            DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
            int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            return px;
        }

        public static int pxToDp(int px, Context ctx) {
            DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
            int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
            return dp;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}