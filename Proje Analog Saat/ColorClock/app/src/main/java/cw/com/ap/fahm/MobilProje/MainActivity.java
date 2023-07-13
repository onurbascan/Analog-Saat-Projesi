package cw.com.ap.fahm.MobilProje;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cw.com.ap.fahm.MobilProje.Utils.BitmapUtils;
import cw.com.ap.fahm.MobilProje.clock.AnalogClockFah;
import cw.com.ap.fahm.MobilProje.clock.DigitalClock;

public class MainActivity extends AppCompatActivity {

    //Bu alanda sınıflardan nesne oluşturma işlemleri yapılıyor
    private Timer clockTimer = new Timer();
    private final TimerTask clockTask = new TimerTask() {
        @Override
        public void run() {
            mHandler.post(mUpdateResults);
        }
    };
    //Bu alanda sınıflardan nesne oluşturma işlemleri yapılıyor
    final Handler mHandler = new Handler();
    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateFaceColor();;
        }
    };
    //Bu alanda sınıflardan nesne oluşturma işlemleri yapılıyor
    Button btnGaleridenSec;
    public static final int PERMISSION_PICK_IMAGE=1000;
//Değişkenler oluşturuluyor
    Bitmap originalBitmap,filteredBitmap,finalBitmap;

//Burada Galeriden seçme butonunun içeriği bulunmakta
//burada Galeriden seç butonunun
// nesnesine uygun olan bir nesneyi elde etmek için findViewById yöntemini kullandık
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGaleridenSec=(Button)findViewById(R.id.btnGaleridenSec);
        //fotoğrafı bitmap sınıfından bitmap adında bir değişken oluşturarak ilk başta hiç bir arka plan yoksa clock analog.png adında 441.441 boyutlarında verdikk
        Bitmap bitmap = BitmapUtils.getBitmapFromAssets(this,"clock_analog.png",441,441);
        BitmapHelper.getInstance().setBitmap(bitmap);
    //burada galderiden seç butonuna basıldığında ne olacağı belirlendi
        btnGaleridenSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageFromGallery();
            }
        });
        updateFaceColor();
        schudleTimer();

    }
    //Burada Galeriyi açma metodu bulunmakta
    private void openImageFromGallery() {
        //Burada Galeriye erişmek için manifestten izin isteniyor

        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        //Eğer izin alınırsa veya izin alınmazsa gerçekleşecek durumlar burada belirleniyor izin alınırsa geçiş yapılıyor
                        if(report.areAllPermissionsGranted())
                        {
                            Intent intent=new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent,PERMISSION_PICK_IMAGE);
                        }
                        else
                        {
                            //izin alınmazsa permission denied uyarısı veriyor Toast ile
                            Toast.makeText(MainActivity.this,"Permission denied!",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    //izin gerekçesi gönderme işlemi yapılıyor continue permission request ile yollanıyor
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //burada onaylanmışssa ve resim seçilmişsse gerçekleşecek olaylar belirleniyor

        if(resultCode==RESULT_OK && requestCode==PERMISSION_PICK_IMAGE)
        {
            Bitmap bitmap= BitmapUtils.getBitmapFromGallery(this,data.getData(),441,441);
        //Resimin ilk halini alıyor ve finalbitmap filtered bitmap e düzenlenerek atanıyor
            originalBitmap=bitmap.copy(Bitmap.Config.ARGB_8888,true);
            finalBitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);
            filteredBitmap=originalBitmap.copy(Bitmap.Config.ARGB_8888,true);

//Formatı png olarak ayarlanıyor
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            originalBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            //resim bir diziye atanıyor
            byte[] byteArray = stream.toByteArray();
            BitmapHelper.getInstance().setBitmap(originalBitmap);

            bitmap.recycle();

        }
    }

    public void schudleTimer()
    {
        clockTimer.scheduleAtFixedRate(clockTask, 0, 3000);
    }
    public void stopTimer()
    { clockTimer.cancel();

    }
    //burada analog clock ve digital clock ID’den herhangi bir View
    // nesnesine uygun olan bir nesneyi elde etmek için findViewById yöntemini kullandık
    private void updateFaceColor()
    {
        AnalogClockFah customAnalogClock = (AnalogClockFah) findViewById(R.id.analog_clock);
        DigitalClock digital = (DigitalClock) findViewById(R.id.digital_clock);
//buarada analog clock da renk değişimi için bir sayı color değişkenine atılıyor ve customanalog clock un setface color una veriliyor.
        String strColor = "FF"+digital.getHours()*5+ digital.getMinutes()*4+digital.getSeconds()*5;
        int color=  (int) Long.parseLong(strColor, 16);
        customAnalogClock.setFaceColor(color);

    }


}
