package com.udacity.stockhawk.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static com.udacity.stockhawk.data.Contract.Quote.URI;

/**
 * RemoteViewsService controlling the data being shown in the scrollable weather detail widget
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class StockWidgetRemoteViewsService extends RemoteViewsService {

    public final String LOG_TAG = StockWidgetRemoteViewsService.class.getSimpleName();
    private static final String[] STOCKK_COLUMNS = {
            Contract.Quote.TABLE_NAME + "." + Contract.Quote._ID,
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
            Contract.Quote.COLUMN_HISTORY
    };
    // these indices must match the projection
    static final int POSITION_ID = 0;
    static final int POSITION_SYMBOL = 1;
    static final int POSITION_PRICE = 2;
    static final int POSITION_ABSOLUTE_CHANGE = 3;
    static final int POSITION_PERCENTAGE_CHANGE = 4;
    static final int POSITION_HISTORY = 5;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // Nothing to do
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(URI,
                        STOCKK_COLUMNS,
                        null,
                        null,
                        Contract.Quote.COLUMN_ABSOLUTE_CHANGE + " ASC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_detail_list_item);

                DecimalFormat dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                DecimalFormat dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
                dollarFormatWithPlus.setPositivePrefix("+$");
                DecimalFormat  percentageFormat = (DecimalFormat) NumberFormat.getPercentInstance(Locale.getDefault());
                percentageFormat.setMaximumFractionDigits(2);
                percentageFormat.setMinimumFractionDigits(2);
                percentageFormat.setPositivePrefix("+");

                float rawAbsoluteChange = data.getFloat(POSITION_ABSOLUTE_CHANGE);
                float percentageChange = data.getFloat(POSITION_PERCENTAGE_CHANGE);

                String price =  dollarFormat.format(data.getFloat(Contract.Quote.POSITION_PRICE));
                String symbol = data.getString(Contract.Quote.POSITION_SYMBOL);
                String change = dollarFormatWithPlus.format(rawAbsoluteChange);
                String percentage = percentageFormat.format(percentageChange / 100);

                String description = symbol;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    setRemoteContentDescription(views, description);
                }

                if (rawAbsoluteChange > 0) {
                    views.setViewVisibility(R.id.changeTotalPositive, View.VISIBLE);
                    views.setViewVisibility(R.id.changeTotalNegative, View.GONE);
                } else {
                    views.setViewVisibility(R.id.changeTotalPositive, View.GONE);
                    views.setViewVisibility(R.id.changeTotalNegative, View.VISIBLE);
                }

                views.setTextViewText(R.id.symbol, symbol);
                views.setTextViewText(R.id.price, price);
                views.setTextViewText(R.id.changeTotalNegative, change);
                views.setTextViewText(R.id.changeTotalPositive, change);
                views.setTextViewText(R.id.changePercentage, percentage);

                final Intent fillInIntent = new Intent();
                fillInIntent.setData(URI);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);
                return views;
            }

            @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.symbol, description);
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_detail_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return data.getLong(POSITION_ID);
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
