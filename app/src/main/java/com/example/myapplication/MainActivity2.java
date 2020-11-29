package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    private Bitmap result = null;
    private Bitmap scaledResult = null;
    private Bitmap rotated = null;
    private Bitmap scaledOriginal1 = null;
    private Bitmap scaledOriginal2  = null;
    private Handler Han;
    private Handler ToastHan;

    final int STATUS_SAVED = 0;
    final int STATUS_NOT_SAVED = 1;
    final int STATUS_NULL = 2;
    final int STATUS_BUTTON_SHARE_ENABLE = 3;
    final int STATUS_BUTTON_SAVE_ENABLE = 4;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        checkPermission();


        ImageView imageView4 = (ImageView) findViewById(R.id.imageView4);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
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

        progressBar.setVisibility(ProgressBar.VISIBLE);
        progressBar.setMax(100);
        progressBar.setProgress(0);

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
        scaledOriginal1 = original1;
        scaledOriginal2 = original2;

        scaledOriginal1.setHasAlpha(true);
        scaledOriginal2.setHasAlpha(true);
        original1.setHasAlpha(true);
        original2.setHasAlpha(true);
        rotated.setHasAlpha(true);

        setMinSeekBarX(0, original1.getWidth() - rotated.getWidth()*scaleSeekBar.getProgress()/1000);
        setMinSeekBarY(0, original1.getHeight() - rotated.getHeight()*scaleSeekBar.getProgress()/1000);

        int h = 525;
        int h1 = original1.getHeight();
        int h2 = original2.getHeight();
        int w1 = original1.getWidth();
        int w2 = original2.getWidth();
        double rotate = rotateSeekBar.getProgress()+minRotate;
        int scale = scaleSeekBar.getProgress();
        LayerDrawable layer = (LayerDrawable) getResources().getDrawable(R.drawable.listlay);
        Drawable temp =  new BitmapDrawable(getResources(), scaledOriginal1);
        layer.setDrawableByLayerId(R.id.bit1, temp);
        temp =  new BitmapDrawable(getResources(), rotated);
        temp.setAlpha(minAlpha+alphaSeekBar.getProgress());
        layer.setDrawableByLayerId(R.id.bit2, temp);
        double tmp = rotated.getWidth()*w2*scale;
        tmp = tmp/(double)w2*Math.sin((180-rotate)*Math.PI/180)/1000f;
        double tmp1 = rotated.getHeight()*h2*scale;
        tmp1 = tmp1/(double)h1*Math.cos((180-rotate)*Math.PI/180)/1000f;
        layer.setLayerSize(1, (int)tmp, (int)tmp1);
        layer.setLayerInset(1, (minX+xSeekBar.getProgress()), (minY+ySeekBar.getProgress()), 0, 0);
        layer.setLayerSize(0, original1.getWidth(), original1.getHeight());

        Button but = (Button) findViewById(R.id.button5);
        Button butSh = (Button) findViewById(R.id.button7);
        Button butS = (Button) findViewById(R.id.button6);

        imageView1.setImageDrawable(layer);
        imageView2.setImageBitmap(original2);

        progressBar.setMax(1000);

        Han = new Handler() {
            public void handleMessage(android.os.Message msg) {
                progressBar.setProgress(msg.what);
                if(msg.what == 1000)
                {
                    but.setEnabled(true);
                    imageView4.setImageBitmap(scaledResult);
                }
            };
        };

        ToastHan = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what)
                {
                    case (STATUS_SAVED):
                        Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT).show();
                        break;
                    case (STATUS_NOT_SAVED):
                        Toast.makeText(getApplicationContext(), "Save error", Toast.LENGTH_SHORT).show();
                        break;
                    case (STATUS_NULL):
                        Toast.makeText(getApplicationContext(), "You haven't created an image yet", Toast.LENGTH_SHORT).show();
                        break;
                    case (STATUS_BUTTON_SHARE_ENABLE):
                        butSh.setEnabled(true);
                        break;
                    case (STATUS_BUTTON_SAVE_ENABLE):
                        butS.setEnabled(true);
                        break;
                }
            };
        };

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

                int scaleI = progress+minScale;
                setMaxXY();
                scaleText.setText("Scale - " + String.valueOf(scaleI/10) + "." + String.valueOf(scaleI%10) + "%");

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
                rotated = rotateBitmapF(scaledOriginal2, (float)(progress + minRotate));
                setMaxScale();
                setMaxXY();
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
    }

    private void setMaxXY()
    {
        double scale = (double) scaleSeekBar.getProgress()/1000f;
        double rotate = rotateSeekBar.getProgress()+minRotate;
        double wR = original2.getWidth()*scale;
        double hR = original2.getHeight()*scale;

        double x = getX(rotate, wR, hR);
        double y = getY(rotate, wR, hR);
        setMinSeekBarX(0, original1.getWidth() - (int)(x));
        setMinSeekBarY(0, original1.getHeight() - (int)(y));
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
        Button but = (Button) findViewById(R.id.button6);
        but.setEnabled(false);
        Runnable run = new Runnable() {
            @Override
            public void run() {
                ImageView imageView = (ImageView) findViewById(R.id.imageView4);
                if(!hasImage(imageView))
                {
                    ToastHan.sendEmptyMessage(STATUS_NULL);
                    ToastHan.sendEmptyMessage(STATUS_BUTTON_SAVE_ENABLE);
                    return;
                }


                String sdcardBmpPath = Environment.getExternalStorageDirectory() + "/DCIM/SavedImages/" + java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()) + ".png";
                try (FileOutputStream out = new FileOutputStream(sdcardBmpPath)) {
                    result.compress(Bitmap.CompressFormat.PNG, 100, out);
                    ToastHan.sendEmptyMessage(STATUS_SAVED);
                } catch (IOException e) {
                    ToastHan.sendEmptyMessage(STATUS_NOT_SAVED);
                    e.printStackTrace();
                }
                ToastHan.sendEmptyMessage(STATUS_BUTTON_SAVE_ENABLE);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(sdcardBmpPath))));
            }
        };
        Thread thread = new Thread(run);
        thread.start();
    }

    private void send(Bitmap bitmap) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                ImageView imageView = (ImageView) findViewById(R.id.imageView4);
                if(!hasImage(imageView))
                {
                    ToastHan.sendEmptyMessage(STATUS_BUTTON_SHARE_ENABLE);
                    ToastHan.sendEmptyMessage(STATUS_NULL);
                    return;
                }
                String sdcardBmpPath = Environment.getExternalStorageDirectory() + "/DCIM/LastSharedImage/" + "last_Shared_Img_Visible"+".png";
                try (FileOutputStream out = new FileOutputStream(sdcardBmpPath)) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    ToastHan.sendEmptyMessage(STATUS_SAVED);
                    //Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT).show();
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
                    ToastHan.sendEmptyMessage(STATUS_NOT_SAVED);
                    e.printStackTrace();
                }
                ToastHan.sendEmptyMessage(STATUS_BUTTON_SHARE_ENABLE);
            }
        };
        Thread thread = new Thread(run);
        thread.start();
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

    @Override
    protected void onDestroy() {
          minAlpha = 0;
          minX = 0;
          minY = 0;
          minRotate = 0;
          minScale = 0;
          alphaText = null;
          alphaSeekBar = null;
          xText = null;
          xSeekBar = null;
          yText = null;
          ySeekBar = null;
          rotateSeekBar = null;
          rotateText = null;
          scaleText = null;
          scaleSeekBar = null;
          original1 = null;
          original2  = null;
          result = null;
          scaledResult = null;
          rotated = null;
          scaledOriginal1 = null;
          scaledOriginal2  = null;
        Runtime.getRuntime().gc();
        super.onDestroy();
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
        Button but = (Button) findViewById(R.id.button7);
        but.setEnabled(false);
        send(result);
    }

    private void checkPermission()
    {
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED);
        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }


    private static Bitmap rotateBitmapF(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap res = null;
        res = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, false);


        return res;
    }

    private static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        Bitmap res = null;

        res = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);

        return res;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private LayerDrawable setLayer()
    {
        LinearLayout layout = (LinearLayout) findViewById(R.id.ImageLayout);
        double h = layout.getHeight();
        double h1 = original1.getHeight();
        double h2 = original2.getHeight();
        double w1 = original1.getWidth();
        double w2 = original2.getWidth();
        double rotate = rotateSeekBar.getProgress()+minRotate;
        double scale = (double) scaleSeekBar.getProgress()/1000f;

        double wR = h*w2*scale/h1;
        double hR = h*h2*scale/h1;

        double x = getX(rotate, wR, hR);
        double y = getY(rotate, wR, hR);

        rotated.setHasAlpha(true);

        LayerDrawable layer = (LayerDrawable) getResources().getDrawable(R.drawable.listlay);
        Drawable temp =  new BitmapDrawable(getResources(), scaledOriginal1);
        layer.setDrawableByLayerId(R.id.bit1, temp);
        temp =  new BitmapDrawable(getResources(), rotated);
        temp.setAlpha(minAlpha+alphaSeekBar.getProgress());
        layer.setDrawableByLayerId(R.id.bit2, temp);
        layer.setLayerSize(1, (int)x, (int)y);
        layer.setLayerInset(1, (minX+xSeekBar.getProgress())*scaledOriginal1.getWidth()/original1.getWidth(), (minY+ySeekBar.getProgress())*scaledOriginal1.getHeight()/original1.getHeight(), 0, 0);
        return layer;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private Bitmap Blend()
    {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);

        Bitmap renderRotate = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        renderRotate.setPixel(0, 0, Color.argb(0, 0, 0, 0));
        if(original2.getWidth()*scaleSeekBar.getProgress()/1000 > 0 && original2.getHeight()*scaleSeekBar.getProgress()/1000 > 0)
        {
            float rotate = (float) (minRotate + rotateSeekBar.getProgress());
            renderRotate = Bitmap.createScaledBitmap(original2, original2.getWidth()*scaleSeekBar.getProgress()/1000, original2.getHeight()*scaleSeekBar.getProgress()/1000, true);
            renderRotate = rotateBitmap(renderRotate, rotate);
        }
        renderRotate.setHasAlpha(true);


        int xN = xSeekBar.getProgress()+minX;
        int yN = ySeekBar.getProgress()+minY;
        int Alpha = alphaSeekBar.getProgress();
        int width = original1.getWidth();
        int height = original1.getHeight();
        int[] or1 = new int[width * height];
        original1.getPixels(or1, 0, width, 0, 0, width, height);
        Bitmap res = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        int width1 = renderRotate.getWidth();
        int height1 = renderRotate.getHeight();
        int[] rt2 = new int[width1*height1];
        Bitmap.createScaledBitmap(renderRotate, width1, height1, true).getPixels(rt2, 0, width1, 0, 0, width1, height1);
        Han.sendEmptyMessage(0);
        for(int y = yN;y < height1+yN; y++)
        {
            for(int x = xN;  x < width1+xN; x++)
            {
                int temp = y*width+x;
                int color1 = or1[temp];
                int color2 = rt2[(x-xN)+(y-yN)*width1];
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
                int Result = Color.argb((int)(outA*255f), rO, gO, bO);
                or1[temp] = Result;

            }
            Han.sendEmptyMessage((y-yN)*1000/(renderRotate.getHeight()));
        }
        res.setPixels(or1, 0, width, 0, 0, width, height);
        renderRotate = null;
        or1 = null;
        rt2 = null;
        return res;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void mixImages(View v)
    {
        Button but = (Button) findViewById(R.id.button5);
        but.setEnabled(false);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        ImageView imageView = (ImageView) findViewById(R.id.imageView4);
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        int h = imageView.getHeight();
        Runnable runnable = new Runnable() {
            public void run() {
                result = null;
                result = Blend();
                scaledResult = null;
                scaledResult = Bitmap.createScaledBitmap(result, (int) (result.getWidth()*h/result.getHeight()), h, true);
                Han.sendEmptyMessage(1000);
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
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

    private double getX(double rotate, double wR, double hR)
    {
        double x = 0f;
        if(rotate > 0  && rotate <= 90)
        {
            rotate = rotate;
            double rotate1 = rotate*Math.PI/180;
            x = wR*Math.cos(rotate1)+hR*Math.sin(rotate1);
        }
        else if(rotate > 90 && rotate <= 180)
        {
            rotate = rotate-90;
            double rotate1 = rotate*Math.PI/180;
            x = wR*Math.sin(rotate1)+hR*Math.cos(rotate1);
        }
        else if(rotate > -90 && rotate <= 0)
        {
            rotate = -1*rotate;
            double rotate1 = rotate*Math.PI/180;
            x = wR*Math.cos(rotate1)+hR*Math.sin(rotate1);
        }
        else
        {
            rotate = -1*rotate-90;
            double rotate1 = rotate*Math.PI/180;
            x = hR*Math.cos(rotate1)+wR*Math.sin(rotate1);
        }
        return x;
    }

    private double getY(double rotate, double wR, double hR)
    {
        double y = 0f;

        if(rotate > 0  && rotate <= 90)
        {
            rotate = rotate;
            double rotate1 = rotate*Math.PI/180;
            y = wR*Math.sin(rotate1)+hR*Math.cos(rotate1);
        }
        else if(rotate > 90 && rotate <= 180)
        {
            rotate = rotate-90;
            double rotate1 = rotate*Math.PI/180;
            y = hR*Math.sin(rotate1)+wR*Math.cos(rotate1);
        }
        else if(rotate > -90 && rotate <= 0)
        {
            rotate = -1*rotate;
            double rotate1 = rotate*Math.PI/180;
            y = hR*Math.cos(rotate1)+wR*Math.sin(rotate1);
        }
        else
        {
            rotate = -1*rotate-90;
            double rotate1 = rotate*Math.PI/180;
            y = wR*Math.cos(rotate1)+hR*Math.sin(rotate1);
        }
        return y;
    }

    private void setMaxScale()
    {
        double rotate = rotateSeekBar.getProgress()+minRotate;
        double wR = original2.getWidth();
        double hR = original2.getHeight();

        double x = getX(rotate, wR, hR);
        double y = getY(rotate, wR, hR);

        rotated.setHasAlpha(true);


        if((int)x > original1.getWidth() || (int)y > original1.getHeight())
        {
            setMinSeekBarScale(0, Math.min(1000*original1.getWidth()/(int)x, 1000*original1.getHeight()/(int)y));
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

        ImageView imageView1 = (ImageView) findViewById(R.id.imageView1);
        ImageView imageView2 = (ImageView) findViewById(R.id.imageView2);
        LayerDrawable layer = (LayerDrawable) getResources().getDrawable(R.drawable.listlay);
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        if (bitmap != null) {
            switch (loadImg)
            {
                case(0):
                    boolean a = layer.setDrawableByLayerId(R.id.bit1, d);
                    if(a)
                    {
                        original1 = null;
                        original1 = bitmap;
                        bitmap = null;
                        LinearLayout layout = (LinearLayout) findViewById(R.id.ImageLayout);
                        double h = layout.getHeight();
                        double h1 = original1.getHeight();
                        double h2 = original2.getHeight();
                        double w1 = original1.getWidth();
                        double w2 = original2.getWidth();
                        scaledOriginal1 = null;
                        scaledOriginal2 = null;
                        scaledOriginal1 = Bitmap.createScaledBitmap(original1, (int) (w1*h/h1), (int) h, true);
                        scaledOriginal2 = Bitmap.createScaledBitmap(original2, (int) (w2*h/h2), (int) h, true);
                        rotated = rotateBitmapF(scaledOriginal2, (float) (rotateSeekBar.getProgress()+minRotate));

                        setMaxScale();
                        setMaxXY();
                        layer = setLayer();
                        imageView1.setImageDrawable(layer);
                    }
                    break;
                case (1):
                    original2 = null;
                    original2 = bitmap;
                    bitmap = null;
                    LinearLayout layout = (LinearLayout) findViewById(R.id.ImageLayout);
                    int HeightImage = layout.getHeight();
                    scaledOriginal2 = null;
                    scaledOriginal2 = Bitmap.createScaledBitmap(original2, HeightImage*original2.getWidth()/original2.getHeight(), HeightImage, true);
                    rotated = rotateBitmapF(scaledOriginal2, (float) (rotateSeekBar.getProgress()+minRotate));
                    setMaxScale();
                    setMaxXY();
                    layer = setLayer();
                    imageView1.setImageDrawable(layer);
                    imageView2.setImageBitmap(scaledOriginal2);
                    break;

            }
        }
    }
}