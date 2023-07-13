package cw.com.ap.fahm.MobilProje;

import android.graphics.Bitmap;

public class BitmapHelper {
    private Bitmap bitmap = null;
    private static final BitmapHelper instance=new BitmapHelper();

    public BitmapHelper() {
    }
//BURADA BİTMAP'TE OLAN DEĞŞİKLİKLERİ YAPIP DAHA SONRA YAPILAN DEĞİŞİKLERİ GET VE SET EDEREK YOLLAMAK İÇİN KULANILIR
    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static BitmapHelper getInstance() {
        return instance;
    }
}
