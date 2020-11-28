package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.webkit.MimeTypeMap;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;

public class MainActivity2 extends AppCompatActivity {

    private int loadImg = 0;
    static final int GALLERY_REQUEST = 1;
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 101;
    private int minAlpha = 0;
    private int minX = 0;
    private int minY = 0;
    private int minRotate = -180;
    private  int minScale = 0;
    private TextView alphaText = null;
    private SeekBar alphaSeekBar = null;
    private TextView xText = null;
    private SeekBar xSeekBar = null;
    private TextView yText = null;
    private SeekBar ySeekBar = null;
    private SeekBar rotateSeekBar = null;
    private TextView rotateText = null;
    private TextView scaleText = null;
    private SeekBar scaleSeekBar = null;
    private Bitmap original1 = null;
    private Bitmap original2  = null;
    private Bitmap rotated = null;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        checkPermission();

        ImageView imageView4 = (ImageView) findViewById(R.id.imageView4);
        alphaText = (TextView) findViewById(R.id.alphaText);
        alphaSeekBar = (SeekBar) findViewById(R.id.alpha);
        xText = (TextView) findViewById(R.id.xText);
        xSeekBar = (SeekBar) findViewById(R.id.x);
        yText = (TextView) findViewById(R.id.yText);
        ySeekBar = (SeekBar) findViewById(R.id.y);
        scaleText = (TextView) findViewById(R.id.scaleText);
        scaleSeekBar = (SeekBar) findViewById(R.id.scale);
        rotateSeekBar = (SeekBar) findViewById(R.id.rotate);
        rotateText = (TextView) findViewById(R.id.rotateText);

        ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
        ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
        imageView1.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView2.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

        rotateSeekBar.setMax(360);
        rotateSeekBar.setProgress(180);
        rotateText.setText("Rotate - " + String.valueOf(rotateSeekBar.getProgress()+minRotate) +"°");

        setMinSeekBarAlpha(0, 255);
        setMinSeekBarX(0, 1);
        setMinSeekBarY(0, 1);
        setMinSeekBarScale(0, 1000);

        original2 = drawableToBitmap((Drawable) getResources().getDrawable(R.drawable.wm1));
        original1 = drawableToBitmap((Drawable) getResources().getDrawable(R.drawable.standartimg));
        rotated = original2;


        original1.setHasAlpha(true);
        original2.setHasAlpha(true);
        rotated.setHasAlpha(true);

        setMinSeekBarX(0, original1.getWidth() - rotated.getWidth()*scaleSeekBar.getProgress()/1000);
        setMinSeekBarY(0, original1.getHeight() - rotated.getHeight()*scaleSeekBar.getProgress()/1000);

        LayerDrawable layer = setLayer();
        layer.setLayerSize(0, original1.getWidth(), original1.getHeight());

        imageView1.setImageDrawable(layer);
        imageView2.setImageBitmap(original2);



        alphaSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                alphaText.setText("Alpha - " + String.valueOf(progress+minAlpha));
                LayerDrawable layer1 = setLayer();
                imageView1.setImageDrawable(layer1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        xSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                xText.setText("X - " + String.valueOf(progress+minX));
                LayerDrawable layer1 = setLayer();
                imageView1.setImageDrawable(layer1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ySeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                yText.setText("Y - " + String.valueOf(progress+minY));
                LayerDrawable layer1 = setLayer();
                imageView1.setImageDrawable(layer1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        scaleSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                int scale = progress+minScale;

                scaleText.setText("Scale - " + String.valueOf(scale/10) + "." + String.valueOf(scale%10) + "%");
                setMinSeekBarX(0, original1.getWidth() - rotated.getWidth()*scaleSeekBar.getProgress()/1000);
                setMinSeekBarY(0, original1.getHeight() - rotated.getHeight()*scaleSeekBar.getProgress()/1000);

                LayerDrawable layer1 = setLayer();
                imageView1.setImageDrawable(layer1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rotateSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                rotateText.setText("Rotate - " + String.valueOf(progress+minRotate) +"°");
                rotated = rotateBitmap(original2, (float)(progress + minRotate));
                setMaxScale();
                setMinSeekBarX(0, original1.getWidth() - rotated.getWidth()*scaleSeekBar.getProgress()/1000);
                setMinSeekBarY(0, original1.getHeight() - rotated.getHeight()*scaleSeekBar.getProgress()/1000);
                LayerDrawable layer1 = setLayer();
                imageView1.setImageDrawable(layer1);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

//        String filename = "standartImg.jpg";
//        InputStream inputStream = null;
//        try{
//            //LayerDrawable layerDrawable = (LayerDrawable) view.getBackground();
//            inputStream = getApplicationContext().getAssets().open(filename);
//            Drawable d = Drawable.createFromStream(inputStream, null);
//            imageView.setImageDrawable(d);
//            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            //layerDrawable.setDrawable(0, d);
//            //layerDrawable.setDrawable(1, d);
//            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
//            Bitmap bitmap = drawable.getBitmap();
//            original1 = bitmap;
//            original2 = bitmap;
//        }
//        catch (IOException e){
//            e.printStackTrace();
//        }
    }

    public   void  CreateFolders()
    {
        String sdcardBmpPath = Environment.getExternalStorageDirectory() + "/DCIM/LastSharedImage";
        File file = new File(sdcardBmpPath);
        if (!file.exists())
        {
            file.mkdirs();
        }
        sdcardBmpPath = Environment.getExternalStorageDirectory() + "/DCIM/SavedImages";
        file = new File(sdcardBmpPath);
        if (!file.exists())
        {
            file.mkdirs();
        }
    }

    public void saveImage1(View view) throws Exception {
        checkPermission();
        CreateFolders();
        ImageView imageView = (ImageView) findViewById(R.id.imageView4);
        if(!hasImage(imageView))
        {
            Toast.makeText(getApplicationContext(), "You haven't created an image yet", Toast.LENGTH_SHORT).show();
            return;
        }
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();



        String sdcardBmpPath = Environment.getExternalStorageDirectory() + "/DCIM/SavedImages/" + java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()) + ".png";
        try (FileOutputStream out = new FileOutputStream(sdcardBmpPath)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Save error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(sdcardBmpPath))));
    }

    private void send(Bitmap bitmap) {
        String sdcardBmpPath = Environment.getExternalStorageDirectory() + "/DCIM/LastSharedImage/" + "last_Shared_Img_Visible"+".png";
        try (FileOutputStream out = new FileOutputStream(sdcardBmpPath)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT).show();
            File myFile = new File(sdcardBmpPath);
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String ext = myFile.getName().substring(myFile.getName().lastIndexOf(".") + 1);
            String type = mime.getMimeTypeFromExtension(ext);
            Intent sharingIntent = new Intent("android.intent.action.SEND");
            sharingIntent.setType(type);
            sharingIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(myFile));
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(sdcardBmpPath))));
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(), "Save error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        //File myFile = new File("/storage/emulated/0/saved_images/Image-test.jpg");
        //MimeTypeMap mime = MimeTypeMap.getSingleton();
        //String ext = myFile.getName().substring(myFile.getName().lastIndexOf(".") + 1);
        //String type = mime.getMimeTypeFromExtension(ext);
        //Intent sharingIntent = new Intent("android.intent.action.SEND");
        //sharingIntent.setType(type);
        //sharingIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(myFile));
        //startActivity(Intent.createChooser(sharingIntent, "Share using"));

    }

    private boolean hasImage(@NonNull ImageView view) {
        Drawable drawable = view.getDrawable();
        boolean hasImage = (drawable != null);
        if (hasImage && (drawable instanceof BitmapDrawable))
        {
            hasImage = ((BitmapDrawable)drawable).getBitmap() != null;
        }
        return hasImage;
    }

    public void shareImage1(View view)
    {
        checkPermission();
        CreateFolders();
        ImageView imageView = (ImageView) findViewById(R.id.imageView4);
        if(!hasImage(imageView))
        {
            Toast.makeText(getApplicationContext(), "You haven't created an image yet", Toast.LENGTH_SHORT).show();
            return;
        }
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        send(bitmap);
    }

    private void checkPermission()
    {
        // Проверка, если разрешение не предоставлено
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED);
        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }

    private static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private LayerDrawable setLayer()
    {
        LayerDrawable layer = (LayerDrawable) getResources().getDrawable(R.drawable.listlay);
        Drawable temp =  new BitmapDrawable(getResources(), original1);
        layer.setDrawableByLayerId(R.id.bit1, temp);
        temp =  new BitmapDrawable(getResources(), rotated);
        temp.setAlpha(minAlpha+alphaSeekBar.getProgress());
        layer.setDrawableByLayerId(R.id.bit2, temp);
        layer.setLayerSize(1, rotated.getWidth()*scaleSeekBar.getProgress()/1000, rotated.getHeight()*scaleSeekBar.getProgress()/1000);
        layer.setLayerInset(1, minX+xSeekBar.getProgress(), minY+ySeekBar.getProgress(), 0, 0);
        return layer;
    }

    private Bitmap Blend()
    {
        rotated.setHasAlpha(true);
        int xN = xSeekBar.getProgress()+minX;
        int yN = ySeekBar.getProgress()+minY;
        int Alpha = alphaSeekBar.getProgress();
        int width = original1.getWidth();
        int height = original1.getHeight();
        int[] srcPixels = new int[width * height];
        original1.getPixels(srcPixels, 0, width, 0, 0, width, height);
        Bitmap res = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        res.setPixels(srcPixels, 0, width, 0, 0, width, height);
        width = rotated.getWidth()*scaleSeekBar.getProgress()/1000;
        height = rotated.getHeight()*scaleSeekBar.getProgress()/1000;
        Bitmap bitmap2 = null;
        if(width != 0 && height != 0)
        {
            bitmap2 = Bitmap.createScaledBitmap(rotated, width, height, true);
        }
        for(int x = xN; x < bitmap2.getWidth()+xN; x++)
        {
            for(int y = yN; y < bitmap2.getHeight()+yN; y++)
            {
                int color1 = original1.getPixel(x, y);
                int color2 = bitmap2.getPixel(x-xN, y-yN);
                float srcA = (((float)Color.alpha(color1))/255f);
                float dstA = (((float)Color.alpha(color2))/255f)*((float)Alpha/255f);
//                float outA = srcA + dstA*(1- srcA);
                float r = (float)Color.red(color1);
                float g = (float)Color.green(color1);
                float b = (float)Color.blue(color1);
                float r1 = (float)Color.red(color2);
                float g1 = (float)Color.green(color2);
                float b1 = (float)Color.blue(color2);
                int rO = (int)(r1*dstA+r*(1-dstA));
                int gO = (int)(g1*dstA+g*(1-dstA));
                int bO = (int)(b1*dstA+b*(1-dstA));
                float outA = dstA+srcA*(1-dstA);
//                int rO = (int)((dstA*r+srcA*r1*(1-dstA))/outA);
//                int gO = (int)((dstA*g+srcA*g1*(1-dstA))/outA);
//                int bO = (int)((dstA*b+srcA*b1*(1-dstA))/outA);
                //res.setPixel(x, y, Color.argb(Color.alpha(color2), rO, gO, bO));
//                float alpha = (float)Color.alpha(color1) * (float)Color.alpha(color2)/255f/255f;
//                int r = (int)(Color.red(color1) * alpha + (1-alpha)*Color.red(color2));
//                int g = (int)(Color.green(color1) * alpha + (1-alpha)*Color.green(color2));
//                int b = (int)(Color.blue(color1) * alpha + (1-alpha)*Color.blue(color2));
                int Result = Color.argb((int)(outA*255f), rO, gO, bO);
                res.setPixel(x, y, Result);

            }
        }
        return res;
    }

    public void mixImages(View v)
    {
        Bitmap res = Blend();
        ImageView imageView = (ImageView) findViewById(R.id.imageView4);
        imageView.setImageBitmap(res);
    }

    private void setMinSeekBarAlpha(int min, int max)
    {
        if(alphaSeekBar.getProgress()+minAlpha >= min)
        {
            alphaSeekBar.setProgress(alphaSeekBar.getProgress()+minAlpha-min);
        }
        minAlpha = min;
        alphaSeekBar.setMax(max-min);
        alphaText.setText("Alpha - " + String.valueOf(alphaSeekBar.getProgress()+minAlpha));
    }

    private void setMinSeekBarX(int min, int max)
    {
        if(xSeekBar.getProgress()+minX >= min)
        {
            xSeekBar.setProgress(xSeekBar.getProgress()+minX-min);
        }
        minX = min;
        xSeekBar.setMax(max-min);
        xText.setText("X - " + String.valueOf(xSeekBar.getProgress()+minX));
    }

    private void setMinSeekBarY(int min, int max)
    {
        if(ySeekBar.getProgress()+minY >= min)
        {
            ySeekBar.setProgress(ySeekBar.getProgress()+minY-min);
        }
        minY = min;
        ySeekBar.setMax(max-min);
        yText.setText("Y - " + String.valueOf(ySeekBar.getProgress()+minY));
    }

    private void setMinSeekBarScale(int min, int max)
    {
        if(scaleSeekBar.getProgress()+minScale >= min)
        {
            scaleSeekBar.setProgress(scaleSeekBar.getProgress()+minScale-min);
        }
        minScale = min;
        scaleSeekBar.setMax(max-min);
        int scale = (scaleSeekBar.getProgress()+minScale);

        scaleText.setText("Scale - " + String.valueOf(scale/10) + "." + String.valueOf(scale%10) + "%");
    }

    public void loadImage1(View view) {

        loadImg = 0;
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }
    public void loadImage2(View view) {

        loadImg = 1;
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    public  void setInVisible(View view)
    {
        try{

            Intent intent = new Intent(MainActivity2.this, MainActivity.class);
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private void setMaxScale()
    {
//        double angle = (double) (rotateSeekBar.getProgress()+minScale) - Math.atan2(original2.getWidth(), original2.getHeight());
//        double temp = original1.getHeight()/Math.sin(Math.atan2(original2.getWidth(), original2.getHeight()));
//        int w = (int)(temp * Math.cos(angle));
//        int h = (int)(temp * Math.sin(angle));
        if(rotated.getWidth() > original1.getWidth() || rotated.getHeight() > original1.getHeight())
        {
            setMinSeekBarScale(0, Math.min(1000*original1.getWidth()/rotated.getWidth(), 1000*original1.getHeight()/rotated.getHeight()));
        }
        else
        {
            setMinSeekBarScale(0, 1000);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Bitmap bitmap = null;

        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
//        Bitmap bitmap1 = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//
//        int col, r, g, b, w, h;
//        w = bitmap.getWidth();
//        h = bitmap.getHeight();
//        for(int x = 0; x < w; x++)
//        {
//            for(int y = 0; y < h; y++)
//            {
//                col = bitmap.getPixel(x, y);
//                r = Color.red(col);
//                g = Color.green(col);
//                b = Color.blue(col);
//                bitmap1.setPixel(x, y, Color.rgb( r, g, b));
//            }
//        }

//        String sdcardBmpPath = Environment.getExternalStorageDirectory() + "/DCIM/" + java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()) +".bmp";
//        AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
//        boolean isSaveResult = bmpUtil.save(bitmap, sdcardBmpPath);
//        if(isSaveResult);
//            //Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT).show();
//        else
//            Toast.makeText(getApplicationContext(), "Save error", Toast.LENGTH_SHORT).show();
//        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(sdcardBmpPath))));
//        Bitmap bmp1 =BitmapFactory.decodeFile(sdcardBmpPath);
//        File fdel = new File(sdcardBmpPath);
        ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
        ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
        LayerDrawable layer = (LayerDrawable) getResources().getDrawable(R.drawable.listlay);
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        //View v = (View) findViewById(R.id.view);
        //LayerDrawable layerDrawable = (LayerDrawable) v.getBackground();
        if (bitmap != null) {
            switch (loadImg)
            {
                case(0):
                    boolean a = layer.setDrawableByLayerId(R.id.bit1, d);
                    if(a)
                    {
                        original1 = bitmap;
                        setMaxScale();
                        layer = setLayer();
                        imageView1.setImageDrawable(layer);
                    }
                    break;
                case (1):
                    original2 = bitmap;
                    rotated = rotateBitmap(original2, (float) (rotateSeekBar.getProgress()+minRotate));
                    setMaxScale();
                    layer = setLayer();
                    imageView1.setImageDrawable(layer);
                    imageView2.setImageBitmap(bitmap);
                    break;

            }
        }


//        BitmapDrawable drawable2 = (BitmapDrawable) imageView2.getDrawable();
//
//        Drawable draw2 = imageView2.getDrawable();
//        //Drawable draw1 = layerDrawable.getDrawable(0);
//
//      //  original1 = drawableToBitmap(draw1);
//        original2 = drawable2.getBitmap();
//
//        draw2.setAlpha(alphaSeekBar.getProgress()+minAlpha);
//     //   Drawable drawableArray[] = new Drawable[] { draw1, draw2 };
//       // LayerDrawable res = new LayerDrawable(drawableArray);
//       // res.setLayerInset(0 , 0, 0, 0, 0);
//      //  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//     //       res.setLayerSize(1, original2.getWidth()*scaleSeekBar.getProgress()/100, original2.getHeight()*scaleSeekBar.getProgress()/100);
//      //  }
//      //  res.setLayerInset(1 , xSeekBar.getProgress()+minX, ySeekBar.getProgress()+minY, 0, 0);
//        //BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
////        imageView1.setL
//        //bitmap = ;
//        original1 = bitmap;
    }
}