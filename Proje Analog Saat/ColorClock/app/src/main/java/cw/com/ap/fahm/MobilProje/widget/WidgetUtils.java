/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cw.com.ap.fahm.MobilProje.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.TypedValue;
import android.widget.RemoteViews;

import cw.com.ap.fahm.MobilProje.R;

public class WidgetUtils {
    static final String TAG = "WidgetUtils";

    //BURADA WİDGET SAATİMİZİN BOYUTUNU BELİRLİYORUZ
    public static void setClockSize(Context context, RemoteViews clock, float scale) {
        float fontSize = context.getResources().getDimension(R.dimen.widget_big_font_size);
        clock.setTextViewTextSize(
                R.id.the_clock, TypedValue.COMPLEX_UNIT_PX, fontSize * scale);
    }

//BU METODUMUZ ÖLÇEKLERİMİZİ ORANLARI OLARAK ALMAMIZI SAĞLIYOR WİDGET İÇERİSİNDE
    public static float getScaleRatio(Context context, Bundle options, int id) {
        if (options == null) {
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
            if (widgetManager == null) {

                return 1f;
            }
            options = widgetManager.getAppWidgetOptions(id);
        }
        //BU METODUMUZ ÖLÇEKLERİMİZİ ORANLARI OLARAK ALMAMIZI SAĞLIYOR WİDGET İÇERİSİNDE
        if (options != null) {
            int minWidth = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
            if (minWidth == 0) {

                return 1f;
            }
            Resources res = context.getResources();
            float density = res.getDisplayMetrics().density;
            float ratio = (density * minWidth) / res.getDimension(R.dimen.min_digital_widget_width);

            int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
            if (minHeight > 0 && (density * minHeight)
                    < res.getDimension(R.dimen.min_digital_widget_height)) {
                ratio = Math.min(ratio, getHeightScaleRatio(context, options, id));
            }
            return (ratio > 1) ? 1 : ratio;
        }
        return 1;
    }

    //BU METODUMUZ YÜKSEKLİK  ÖLÇEKLERİMİZİ ORANLARI OLARAK ALMAMIZI SAĞLIYOR WİDGET İÇERİSİNDE
    private static float getHeightScaleRatio(Context context, Bundle options, int id) {
        if (options == null) {
            AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
            if (widgetManager == null) {

                return 1f;
            }
            options = widgetManager.getAppWidgetOptions(id);
        }
        if (options != null) {
            int minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
            if (minHeight == 0) {

                return 1f;
            }
            Resources res = context.getResources();
            float density = res.getDisplayMetrics().density;

            float lblBox = 1.35f * res.getDimension(R.dimen.label_font_size);

            if (res.getDimension(R.dimen.min_digital_widget_height) - lblBox > 0) {
                float ratio = ((density * minHeight) - lblBox)
                        / (res.getDimension(R.dimen.min_digital_widget_height) - lblBox);
                return (ratio > 1) ? 1 : ratio;
            }
        }
        return 1;
    }


//WİDGET DAKİ YÜKSEKLİK VE ÖLÇEKLERİMİZİ ORANLI OLARAK ALDIKTAN SONRA widgetmanagerin boş olup olmadığını kontrol edip ve options un
    //boş olup olmadığını kontrol edip işlemlerimizi yaptığımız metodumuzdur.
    public static boolean showList(Context context, int id, float scale) {
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        if (widgetManager == null) {

            return true;
        }
        Bundle options = widgetManager.getAppWidgetOptions(id);
        if (options == null) {

            return true;
        }
        Resources res = context.getResources();
        String whichHeight = res.getConfiguration().orientation ==
                Configuration.ORIENTATION_PORTRAIT
                ? AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT
                : AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT;
        int height = options.getInt(whichHeight);
        if (height == 0) {
//WİDGET DAKİ YÜKSEKLİK VE ÖLÇEKLERİMİZİ ORANLI OLARAK ALDIKTAN SONRA widgetmanagerin boş olup olmadığını kontrol edip ve options un
            //boş olup olmadığını kontrol edip işlemlerimizi yaptığımız metodumuzdur.
            return true;
        }
        float density = res.getDisplayMetrics().density;

        float lblBox = 1.35f * res.getDimension(R.dimen.label_font_size);
        float neededSize = res.getDimension(R.dimen.digital_widget_list_min_fixed_height) +
                2 * lblBox +
                scale * res.getDimension(R.dimen.digital_widget_list_min_scaled_height);
        return ((density * height) > neededSize);
    }


}

