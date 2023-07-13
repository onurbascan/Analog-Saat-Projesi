/*
 * Copyright (C) 2009 The Android Open Source Project
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


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;

import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.widget.RemoteViews;

import cw.com.ap.fahm.MobilProje.BitmapHelper;
import cw.com.ap.fahm.MobilProje.MainActivity;
import cw.com.ap.fahm.MobilProje.R;


public class AnalogAppWidgetProvider extends AppWidgetProvider {
//Burada galeriden seçtiğimiz fotoğrafımızı widget'a eklenme işlemi yapılmakta
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
//AppWidgetManager sınıfından appwidgetmagerr adında bir nesne oluşturuyoruz
            AppWidgetManager appWidgetManagerr = AppWidgetManager.getInstance(context);
// aşşağıda widget a tıklandığında mainactivity class ına geçiş yani uygulamanın widget olmayan haline geçme olayı yapılmakta
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//widget'a galieriden seçilen resim aşşağıda widget ın view kısmına yerleştiriliyor
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.analog_appwidgetnew);
            views.setOnClickPendingIntent(R.id.backgroundImage, pendingIntent);
            Bitmap newBitmap=getCircleBitmap(BitmapHelper.getInstance().getBitmap());
            views.setImageViewBitmap(R.id.backgroundImage, newBitmap);
            appWidgetManagerr.updateAppWidget(appWidgetId, views);


        }
    }


    //Bu metod ise saat circle'ının boyutlarını rengini ve canvas özelliklerini ayarlamak için kullanılıyor
    public static Bitmap getCircleBitmap(Bitmap bm) {

        int sice = Math.min((bm.getWidth()), (bm.getHeight()));

        Bitmap bitmap = ThumbnailUtils.extractThumbnail(bm, sice, sice);

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);

        final int color = 0xffff0000;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth((float) 4);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}

