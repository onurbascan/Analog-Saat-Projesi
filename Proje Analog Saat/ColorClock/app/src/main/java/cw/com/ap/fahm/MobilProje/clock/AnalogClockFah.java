
package cw.com.ap.fahm.MobilProje.clock;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.BroadcastReceiver;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.View;
import java.util.TimeZone;

import cw.com.ap.fahm.MobilProje.R;


public class AnalogClockFah extends View {

    //BURADA DEĞİİKENLERİMİZİ TANIMLIYORUZ
    private Time mCalendar;

    private final Drawable mHourHand;
    private final Drawable mMinuteHand;
    private final Drawable mSecondHand;
    private final Drawable mDial;

    private final int mDialWidth;
    private final int mDialHeight;

    private boolean mAttached;
    //BURADA DEĞİİKENLERİMİZİ TANIMLIYORUZ
    private final Handler mHandler = new Handler();
    private float mSeconds;
    private float mMinutes;
    private float mHour;
    private boolean mChanged;
    private final Context mContext;
    private String mTimeZoneId;
    private boolean mNoSeconds = false;
    //BURADA DEĞİİKENLERİMİZİ TANIMLIYORUZ
    private final float mDotRadius;
    private final float mDotOffset;
    private Paint mDotPaint;
    private  int mFaceColor =0x00000000;

    public AnalogClockFah(Context context) {
        this(context, null);
    }

    public AnalogClockFah(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
//BURADA ANALOG SAAATİMİZDE BULUNAN SANİYE SAAAT VE DAKİKA DİAL LERİNİ GETRAWABLE İLE ELDE EDİYORUZ
    public AnalogClockFah(Context context, AttributeSet attrs,
                       int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        Resources r = mContext.getResources();

        mDial = r.getDrawable(R.drawable.clock_face);
        mHourHand = r.getDrawable(R.drawable.clock_analog_hour_mipmap);
        mMinuteHand = r.getDrawable(R.drawable.clock_analog_minute_mipmap);
        mSecondHand = r.getDrawable(R.drawable.clock_analog_second_mipmap);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.AnalogClock);
        mDotRadius = a.getDimension(R.styleable.AnalogClock_jewelRadius, 0);
        mDotOffset = a.getDimension(R.styleable.AnalogClock_jewelOffset, 0);

        //BURADA RENK DEĞİŞİM İŞLEMİ YAPMAK İÇİN KULLANDIĞIMIZ BİR METOD
        final int dotColor = a.getColor(R.styleable.AnalogClock_jewelColor, Color.WHITE);
        if (dotColor != 0) {
            mDotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mDotPaint.setColor(dotColor);
        }

        mCalendar = new Time();

        mDialWidth = mDial.getIntrinsicWidth();
        mDialHeight = mDial.getIntrinsicHeight();
    }
//BURASI İSE BULUNDUĞUMUZ BÖLGEYE AİT SAAT BİLGİSİNİ ALIP ANALOG SAAATE O SAAT BİLGİSİNİ YOLLUYOR BU SAYEDE
    //HEM ANALOG SAATTE HEMDE ÜSTÜNDE BULUNAN DİGİTAL SAATTE O BÖLGENİN SAAAT BİLGİSİ YER ALIYOR
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (!mAttached) {
            mAttached = true;
            IntentFilter filter = new IntentFilter();

            filter.addAction(Intent.ACTION_TIME_TICK);
            filter.addAction(Intent.ACTION_TIME_CHANGED);
            filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);

            getContext().registerReceiver(mIntentReceiver, filter, null, mHandler);
        }
        mCalendar = new Time();
        onTimeChanged();
        post(mClockTick);

    }
//Burada analog saat uygulaması penceresinden bağımsız olduğumuzda çalışacak metod bulunmakta
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mAttached) {
            getContext().unregisterReceiver(mIntentReceiver);
            removeCallbacks(mClockTick);
            mAttached = false;
        }
    }
//BURADA DİAL YÜKSEKLİK GENİŞLİK VE ÖLÇEKLERİNİ DEĞİŞKENLERE ATIP KOŞULLARA GÖRE İŞLEMLER YAPIYORUZ
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize =  MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize =  MeasureSpec.getSize(heightMeasureSpec);

        float hScale = 1.0f;
        float vScale = 1.0f;

        if (widthMode != MeasureSpec.UNSPECIFIED && widthSize < mDialWidth) {
            hScale = (float) widthSize / (float) mDialWidth;
        }
//BURADA DİAL YÜKSEKLİK GENİŞLİK VE ÖLÇEKLERİNİ DEĞİŞKENLERE ATIP KOŞULLARA GÖRE İŞLEMLER YAPIYORUZ
        if (heightMode != MeasureSpec.UNSPECIFIED && heightSize < mDialHeight) {
            vScale = (float )heightSize / (float) mDialHeight;
        }

        float scale = Math.min(hScale, vScale);

        setMeasuredDimension(resolveSizeAndState((int) (mDialWidth * scale), widthMeasureSpec, 0),
                resolveSizeAndState((int) (mDialHeight * scale), heightMeasureSpec, 0));
    }
//Burada boyut değiştirme işlemi yapılıp mchanged ile true değeri verilmektedir.
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mChanged = true;
    }
//BURADA ANALOG SAATİMİZDE BULUNAN DİALLERİ YANİ SAAT DAKİKA VE SANİYE ÇUBUKLARINI CANVAS A BOYUTLANDIRARAK YERLEŞTİRME İŞLEMİ YAPILIYOR
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        boolean changed = mChanged;
        if (changed) {
            mChanged = false;
        }

        int availableWidth = getWidth();
        int availableHeight = getHeight();

        int x = availableWidth / 2;
        int y = availableHeight / 2;
//BURADA ANALOG SAATİMİZDE BULUNAN DİALLERİ YANİ SAAT DAKİKA VE SANİYE ÇUBUKLARINI CANVAS A BOYUTLANDIRARAK YERLEŞTİRME İŞLEMİ YAPILIYOR
        final Drawable dial = mDial;
        int w = dial.getIntrinsicWidth();
        int h = dial.getIntrinsicHeight();

        boolean scaled = false;
//BURADA ANALOG SAATİMİZDE BULUNAN DİALLERİ YANİ SAAT DAKİKA VE SANİYE ÇUBUKLARINI CANVAS A BOYUTLANDIRARAK YERLEŞTİRME İŞLEMİ YAPILIYOR
        if (availableWidth < w || availableHeight < h) {
            scaled = true;
            float scale = Math.min((float) availableWidth / (float) w,
                                   (float) availableHeight / (float) h);
            canvas.save();
            canvas.scale(scale, scale, x, y);
        }

        if (changed) {
            dial.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
        }
        dial.draw(canvas);

        if (mDotRadius > 0f && mDotPaint != null) {
            canvas.drawCircle(x, y - (h / 2) + mDotOffset, mDotRadius, mDotPaint);
        }
//BURADA SAAT DAKİKA VE SANİYE 360 DERECEYİ TAMAMLADIĞI ZAMAN DEĞİŞECEK OLAYLARI GERÇEKLEŞTİRİYORUZ
        drawHand(canvas, mHourHand, x, y, mHour / 12.0f * 360.0f, changed);
        drawHand(canvas, mMinuteHand, x, y, mMinutes / 60.0f * 360.0f, changed);
        if (!mNoSeconds) {
            drawHand(canvas, mSecondHand, x, y, mSeconds / 60.0f * 360.0f, changed);
        }

        if (scaled) {
            canvas.restore();
        }
    }
//HAND'i çizme işlemi yapıyoruz belirli koşullara göre canvas 'a restore ediyoruz
    private void drawHand(Canvas canvas, Drawable hand, int x, int y, float angle,
          boolean changed) {
      canvas.save();
      canvas.rotate(angle, x, y);
      if (changed) {
          final int w = hand.getIntrinsicWidth();
          final int h = hand.getIntrinsicHeight();
          hand.setBounds(x - (w / 2), y - (h / 2), x + (w / 2), y + (h / 2));
      }
      hand.draw(canvas);
      canvas.restore();
    }
//BURADA DAKİKA SAAT VE SANİYELERİMİZİ BELİRLİYOR VE ONLARI DEĞİŞKENLERİMİZE ATIYORUZ BELİRLEDĞİMİZ SAAT DAKİKA VE SANİYELER O AN Kİ SAAT DAKİKA VE SANİYELERDİR
    //VE HER SAAT DAKİKA DEĞİŞTİĞİNDE DEĞİŞKENLERE ATAMALAR YAPILMAKTA
    private void onTimeChanged() {
        mCalendar.setToNow();

        if (mTimeZoneId != null) {
            mCalendar.switchTimezone(mTimeZoneId);
        }

        int hour = mCalendar.hour;
        int minute = mCalendar.minute;
        int second = mCalendar.second;

        mSeconds = second;
        mMinutes = minute + second / 60.0f;
        mHour = hour + mMinutes / 60.0f;
        mChanged = true;

        updateContentDescription(mCalendar);
    }
//BURADA BULUNDUĞUMUZ BÖLGENİN SAAT BİLGİSİNİ ALIP KENDİ SAAT BİLGİMİZLE DEĞİŞTİRİYORUZ
    private final BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
                String tz = intent.getStringExtra("time-zone");
                mCalendar = new Time(TimeZone.getTimeZone(tz).getID());
            }
            onTimeChanged();
            invalidate();
        }
    };
//BURADA HER SANİYE DEĞİŞTİĞİNDE OLAN BİLGİYİ ANALOGCLOCKFAH'A YOLLUYORUZ
    private final Runnable mClockTick = new Runnable () {

        @Override
        public void run() {
            onTimeChanged();
            invalidate();
            AnalogClockFah.this.postDelayed(mClockTick, 1000);
        }
    };

    private void updateContentDescription(Time time) {
        final int flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR;
        String contentDescription = DateUtils.formatDateTime(mContext,
                time.toMillis(false), flags);
        setContentDescription(contentDescription);
    }

    public void setTimeZone(String id) {
        mTimeZoneId = id;
        onTimeChanged();
    }

    public void enableSeconds(boolean enable) {
        mNoSeconds = !enable;
    }
//BURADA RENK İŞLEMİ YAPILMAKTA
    public void setFaceColor(int color)
    {
        mFaceColor=color;
        mDial.setColorFilter(mFaceColor, PorterDuff.Mode.SRC_IN);
        invalidate();
    }
}

