package com.example.myapplication;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.io.InputStream;
import java.security.Provider;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Calendar;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.util.HashSet;
import java.util.Random;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    private  int minValueSeekBar = 1;
    static final int GALLERY_REQUEST = 1;
    private static final String TAG = "1";
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 101;
    private  static final int Heoght = 350;
    TextView textView;
    SeekBar seekBar;
    private Bitmap result = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        checkPermission();
        super.onCreate(savedInstanceState);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        textView = (TextView) findViewById(R.id.textView);
        textView.setText("Number of bits used per color - 2");
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                textView.setText("Number of bits used per color - " + String.valueOf(progress+minValueSeekBar));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        ImageView imageView = (ImageView) findViewById(R.id.imageButton);
        String filename = "standartImg.jpg";
        InputStream inputStream = null;
        try{
            inputStream = getApplicationContext().getAssets().open(filename);
            Drawable d = Drawable.createFromStream(inputStream, null);
            imageView.setImageDrawable(d);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            result = bitmap;
            imageView.setImageBitmap(bitmap);
        }
        catch (IOException e){
            e.printStackTrace();
        }
        //        @drawable/standartimg
        //imageView.set
    }

    public  void setVisible(View view)
    {
        try{

            Intent intent = new Intent(MainActivity.this, MainActivity2.class);
            startActivity(intent);
        }
        catch (Exception e){
            e.printStackTrace();
        }
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

//    private byte[] getByteArrayfromBitmap(Bitmap bitmap) {
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
//        return bos.toByteArray();
//    }
//
//    private Bitmap getBitmapfromByteArray(byte[] bitmap) {
//        return BitmapFactory.decodeByteArray(bitmap , 0, bitmap.length);
//    }
//
//
//
//    private static Bitmap getBitmapFromView(View view) {
//        //Define a bitmap with the same size as the view
//        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),      view.getHeight(), Bitmap.Config.ARGB_8888);
//        //Bind a canvas to it
//        Canvas canvas = new Canvas(returnedBitmap);
//        //Get the view's background
//        Drawable bgDrawable = view.getBackground();
//        if (bgDrawable != null)
//            //has background drawable, then draw it on the canvas
//            bgDrawable.draw(canvas);
//        else
//            //does not have background drawable, then draw white background on the canvas
//            canvas.drawColor(Color.WHITE);
//        // draw the view on the canvas
//        view.draw(canvas);
//        //return the bitmap
//        return returnedBitmap;
//    }
//
//    private Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }

    // Функция для проверки и запроса разрешения

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

//    private Bitmap adjustOpacity( Bitmap bitmap )
//    {
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        Bitmap dest = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//        int[] pixels = new int[width * height];
//        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
//        dest.setPixels(pixels, 0, width, 0, 0, width, height);
//        return dest;
//    }

//    private String SavePicture(ImageView iv, String folderToSave)
//    {
//        checkPermission();
//        OutputStream fOut = null;
//        Time time = new Time();
//        time.setToNow();
//
//        try {
//            File file = new File(folderToSave, Integer.toString(time.year) + Integer.toString(time.month) + Integer.toString(time.monthDay) + Integer.toString(time.hour) + Integer.toString(time.minute) + Integer.toString(time.second) +".jpg"); // создать уникальное имя для файла основываясь на дате сохранения
//            fOut = new FileOutputStream(file);
//
//            BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
//            Bitmap bitmap = drawable.getBitmap();
//
//            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//            fOut.flush();
//            fOut.close();
//            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(),  file.getName()); // регистрация в фотоальбоме
//        }
//        catch (Exception e) // здесь необходим блок отслеживания реальных ошибок и исключений, общий Exception приведен в качестве примера
//        {
//            return e.getMessage();
//        }
//        return "";
//    }

    public void saveImage(View view) throws Exception {
        checkPermission();
        CreateFolders();
        Bitmap bitmap = result;

        String sdcardBmpPath = Environment.getExternalStorageDirectory() + "/DCIM/SavedImages/" + java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()) +".bmp";
        AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
        boolean isSaveResult = bmpUtil.save(bitmap, sdcardBmpPath);
        if(isSaveResult)
            Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "Save error", Toast.LENGTH_SHORT).show();
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(sdcardBmpPath))));
        //Bitmap testBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.sample_text);
        //ImageView imageView = (ImageView) findViewById(R.id.imageButton);
        //BitmapDrawable drawable = (BitmapDrawable) ImageView.getDrawable();
        //Bitmap bitmap = drawable.getBitmap();
        //
        //
        //String folderToSave = Environment.getExternalStorageDirectory() + "/dcim/";
        //SavePicture(imageView, folderToSave);


        //ImageView imageView = (ImageView) findViewById(R.id.imageButton);
        //BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        //Bitmap savePic = drawable.getBitmap();

        //saveImage1(savePic);
        //send();
    }

    //private static void saveImage1(Bitmap finalBitmap) {

    //String root = Environment.getExternalStorageDirectory().getAbsolutePath();
    //File myDir = new File(root + "/saved_images");
    //Log.i("Directory", "==" + myDir);
    //myDir.mkdirs();

    //String Name = "Image-test" + ".jpg";
    //File file = new File(myDir, Name);
    //if (file.exists()) file.delete();
    //try {
    //    FileOutputStream out = new FileOutputStream(file);
    //    finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
    //    out.flush();
    //    out.close();

    //} catch (Exception e) {
    //    e.printStackTrace();
    //}
    //}

    private void send(Bitmap bitmap) {
        try {
            String sdcardBmpPath = Environment.getExternalStorageDirectory() + "/DCIM/LastSharedImage/" + "last_Shared_Img"+".bmp";
            AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
            boolean isSaveResult = bmpUtil.save(bitmap, sdcardBmpPath);
            if(isSaveResult)
                Toast.makeText(getApplicationContext(), "Image saved", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Save error", Toast.LENGTH_SHORT).show();
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(sdcardBmpPath))));
            File myFile = new File(sdcardBmpPath);
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            String ext = myFile.getName().substring(myFile.getName().lastIndexOf(".") + 1);
            String type = mime.getMimeTypeFromExtension(ext);
            Intent sharingIntent = new Intent("android.intent.action.SEND");
            sharingIntent.setType(type);
            sharingIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(myFile));
            startActivity(Intent.createChooser(sharingIntent, "Share using"));
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(sdcardBmpPath))));
            //File myFile = new File("/storage/emulated/0/saved_images/Image-test.jpg");
            //MimeTypeMap mime = MimeTypeMap.getSingleton();
            //String ext = myFile.getName().substring(myFile.getName().lastIndexOf(".") + 1);
            //String type = mime.getMimeTypeFromExtension(ext);
            //Intent sharingIntent = new Intent("android.intent.action.SEND");
            //sharingIntent.setType(type);
            //sharingIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(myFile));
            //startActivity(Intent.createChooser(sharingIntent, "Share using"));

        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void shareImage(View view)
    {
        checkPermission();
        CreateFolders();
        send(result);
    }

//    private Bitmap loadBitmapFromView(View v) {
//        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
//                Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(b);
//        v.draw(c);
//        v.invalidate();
//        return b;
//    }

    private void setMinSeekBar(SeekBar seekBar, int min, int max)
    {
        textView = (TextView) findViewById(R.id.textView);
        if(seekBar.getProgress()+minValueSeekBar >= min)
        {
            seekBar.setProgress(seekBar.getProgress()+minValueSeekBar-min);
        }
        minValueSeekBar = min;
        seekBar.setMax(max-min);
        textView.setText("Number of bits used per color - " + String.valueOf(seekBar.getProgress()+minValueSeekBar));
    }

    private int getNextTemp(Random random, int width, int  height, HashSet<Integer> states)
    {
        boolean xy = true;
        int temp = 0;
        while (xy)
        {
            xy = false;
            temp = (int) (random.nextDouble() * (width*height));
            if(states.contains(temp))
            {
                xy = true;
            }
        }
        return temp;
    }

    private int setBitInColor(int color, int bitInColor, int oneOrZero)
    {
        int[] ones = {
                2147483519,
                2147483583,
                2147483615,
                2147483631,
                2147483639,
                2147483643,
                2147483645,
                2147483646
        };
        int[] zeros = {
                128,
                64,
                32,
                16,
                8,
                4,
                2,
                1
        };
        color &= ones[bitInColor];
        color |= oneOrZero*zeros[bitInColor];
        return color;
    }

    private int getBitInByty(byte b, int bitInByte)
    {
        byte ones[] = {
                (byte)128,
                (byte)64,
                (byte)32,
                (byte)16,
                (byte)8,
                (byte)4,
                (byte)2,
                (byte)1
        };
        b &= ones[bitInByte];
        b >>= 7 - bitInByte;
        return (int)b;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void encrypt(View view) {
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        ImageView imageView = (ImageView) findViewById(R.id.imageButton);
        EditText name = (EditText) findViewById(R.id.Name);
        EditText email = (EditText) findViewById(R.id.Email);
        EditText webSite = (EditText) findViewById(R.id.WebSite);
        EditText phoneNumber = (EditText) findViewById(R.id.PhoneNumber);
        EditText date = (EditText) findViewById(R.id.Date);
        EditText comment = (EditText) findViewById(R.id.Comment);
        EditText password = (EditText) findViewById(R.id.Password);

        if(password.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(), "Password is empty", Toast.LENGTH_SHORT).show();
            return;
        }


        //String text = "|s|n" + name.getText() + "|e" + email.getText() + "|w" + webSite.getText() + "|p" + phoneNumber.getText() + "|d" + date.getText() + "|c" + comment.getText() + "|0";
        String text = "";
        text += (name.getText().toString().equals(""))? "" : "|n" + name.getText();
        text += (email.getText().toString().equals(""))? "" : "|e" + email.getText();
        text += (webSite.getText().toString().equals(""))? "" : "|w" + webSite.getText();
        text += (phoneNumber.getText().toString().equals(""))? "" : "|p" + phoneNumber.getText();
        text += (date.getText().toString().equals(""))? "" : "|d" + date.getText();
        text += (comment.getText().toString().equals(""))? "" : "|c" + comment.getText();

//        IvParameterSpec iv = null;
//        SecretKeySpec sks = null;
//        try {
//            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
//            sr.setSeed(password.getText().toString().getBytes());
//            iv = new IvParameterSpec(sr.generateSeed(16));
//            KeyGenerator kg = KeyGenerator.getInstance("AES");
//            kg.init(128, sr);
//            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
//        } catch (Exception e) {
//            Log.e("Crypto", "AES secret key spec error");
//            Toast.makeText(getApplicationContext(), "AES secret key spec error", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        byte[] encodedBytes = null;
//        try {
//            Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
//            c.init(Cipher.ENCRYPT_MODE, sks, iv);
//            encodedBytes = c.doFinal(text.getBytes());
//        } catch (NoSuchAlgorithmException e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Encryption error", Toast.LENGTH_SHORT).show();
//            return;
//        } catch (NoSuchPaddingException e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Encryption error", Toast.LENGTH_SHORT).show();
//            return;
//        } catch (InvalidKeyException e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Encryption error", Toast.LENGTH_SHORT).show();
//            return;
//        } catch (BadPaddingException e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Encryption error", Toast.LENGTH_SHORT).show();
//            return;
//        } catch (IllegalBlockSizeException e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Encryption error", Toast.LENGTH_SHORT).show();
//            return;
//        } catch (InvalidAlgorithmParameterException e) {
//            e.printStackTrace();
//            Toast.makeText(getApplicationContext(), "Encryption error", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        text = new String(encodedBytes);


        text = "|s" + text + "|0";
        int pass = 0;
        String pas = password.getText().toString();
        byte[] passw = pas.getBytes();
        for(int i = 0; i < pas.length(); i++)
        {
            pass += (int)passw[i];
            pass <<= (int)passw[i]%3;
        }

        Random random = new Random(pass);

//        Key publicKey = null;
//        byte[] textData = text.getBytes();
//        //IvParameterSpec iv = new IvParameterSpec();

        random = new Random(pass);
        Bitmap bitmap = result;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if(text.length()*3/width/height <= 8)
        {
            setMinSeekBar(seekBar ,1 + text.length()*3/width/height, 8);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "The text is too long or the image is too small", Toast.LENGTH_SHORT).show();
            return;
        }

        int[] srcPixels = new int[width * height];
        bitmap.getPixels(srcPixels, 0, width, 0, 0, width, height);
        Bitmap bitmap1 = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap1.setPixels(srcPixels, 0, width, 0, 0, width, height);
        HashSet<Integer> states = new HashSet<Integer>();

        bitmap.setHasAlpha(false);
        bitmap1.setHasAlpha(false);

        byte[] txt = text.getBytes();
        int temp = getNextTemp(random, width, height, states);
        states.add(temp);
        int x = temp%width;
        int y = temp/width;
        int bitOnColor = seekBar.getProgress() + minValueSeekBar;
        int bitInColor = 8 - bitOnColor;
        int color = 0;
        int PixelColor = bitmap.getPixel(x, y);
        int r = Color.red(PixelColor);
        int g = Color.green(PixelColor);
        int b = Color.blue(PixelColor);
        for(int i = 0; i < text.length(); i++)
        {
            for(int bit = 0; bit < 8; bit++)
            {
                if(color == 2 && bitInColor == 8)
                {
                    bitmap1.setPixel(x, y, Color.rgb(r, g, b));
                    temp = getNextTemp(random, width, height, states);
                    states.add(temp);
                    x = temp%width;
                    y = temp/width;
                    color = 0;
                    bitInColor = 8 - bitOnColor;
                    PixelColor = bitmap.getPixel(x, y);
                    r = Color.red(PixelColor);
                    g = Color.green(PixelColor);
                    b = Color.blue(PixelColor);
                    bit--;
                }
                else
                {
                    switch (color)
                    {
                        case (0):
                            r = setBitInColor(r, bitInColor, getBitInByty(txt[i], bit));
                            bitInColor++;
                            if(bitInColor == 8)
                            {
                                color++;
                                bitInColor = 8 - bitOnColor;
                            }
                            if(i == text.length()-1 && bit == 7)
                            {
                                bitmap1.setPixel(x, y, Color.rgb(r, g, b));
                            }
                            break;
                        case (1):
                            g = setBitInColor(g, bitInColor, getBitInByty(txt[i], bit));
                            bitInColor++;
                            if(bitInColor == 8)
                            {
                                color++;
                                bitInColor = 8 - bitOnColor;
                            }
                            if(i == text.length()-1 && bit == 7)
                            {
                                bitmap1.setPixel(x, y, Color.rgb(r, g, b));
                            }
                            break;
                        case (2):
                            b = setBitInColor(b, bitInColor, getBitInByty(txt[i], bit));
                            bitInColor++;
                            if(i == text.length()-1 && bit == 7)
                            {
                                bitmap1.setPixel(x, y, Color.rgb(r, g, b));
                            }
                            break;
                    }
                }
            }
        }
//        for(int i = 0; i < text.length(); i++) {
//            int x;
//            int y;
//            int temp = 0;
//            boolean xy;
//            do {
//                xy = false;
//                temp = (int) (random.nextDouble() * (width*height + 1));
//                x = temp%width;
//                y = temp/width;
//                if(states.contains(temp))
//                {
//                    xy = true;
//                }
//            }while (xy);
//            states.add(temp);
//            temp = 0;
//            temp = bitmap.getPixel(x, y);
//            int alpha = Color.alpha(temp);
//            int r = Color.red(temp);
//            int g = Color.green(temp);
//            int b = Color.blue(temp);
//            int symbol = text.charAt(i);
//            symbol >>>= 5;
//            r >>>= 3;
//            r <<= 3;
//            r = r | symbol;
//            symbol = text.charAt(i);
//            symbol <<= 3+6*4;
//            symbol >>>= 5+6*4;
//            g >>>= 3;
//            g <<= 3;
//            g = g | symbol;
//            symbol = text.charAt(i);
//            symbol <<= 6+6*4;
//            symbol >>>= 6+6*4;
//            b >>>= 2;
//            b <<= 2;
//            b = b | symbol;
//            bitmap1.setPixel(x, y, Color.argb(alpha, r, g, b));
//        }

        result = bitmap1;
        bitmap1 = Bitmap.createScaledBitmap(result, Heoght*result.getWidth()/result.getHeight(), Heoght, true);
        imageView.setImageBitmap(bitmap1);
        Toast.makeText(getApplicationContext(), "Encrypted", Toast.LENGTH_SHORT).show();
    }

    private int getBitInColor(int color, int bitInColor)
    {
        int[] zeros = {
                128,
                64,
                32,
                16,
                8,
                4,
                2,
                1
        };
        color &= zeros[bitInColor];
        color >>= 7 - bitInColor;
        return color;
    }

    private byte setBitInByty(byte b, int bitInByte, int oneOrZero)
    {
        byte[] ones = {
                (byte)2147483519,
                (byte)2147483583,
                (byte)2147483615,
                (byte)2147483631,
                (byte)2147483639,
                (byte)2147483643,
                (byte)2147483645,
                (byte)2147483646
        };
        byte zeros[] = {
                (byte)128,
                (byte)64,
                (byte)32,
                (byte)16,
                (byte)8,
                (byte)4,
                (byte)2,
                (byte)1
        };
        b &= ones[bitInByte];
        b |= zeros[bitInByte]*oneOrZero;
        return b;
    }

    public void decrypt(View view) {
        SeekBar seekBar = (SeekBar) findViewById(R.id.seekBar);
        ImageView imageView = (ImageView) findViewById(R.id.imageButton);
        EditText name = (EditText) findViewById(R.id.Name);
        EditText email = (EditText) findViewById(R.id.Email);
        EditText webSite = (EditText) findViewById(R.id.WebSite);
        EditText phoneNumber = (EditText) findViewById(R.id.PhoneNumber);
        EditText date = (EditText) findViewById(R.id.Date);
        EditText comment = (EditText) findViewById(R.id.Comment);
        EditText password = (EditText) findViewById(R.id.Password);
        textView = (TextView) findViewById(R.id.textView);

        if(password.getText().toString().equals(""))
        {
            Toast.makeText(getApplicationContext(), "Password is empty", Toast.LENGTH_SHORT).show();
            return;
        }
        int pass = 0;
        String pas = password.getText().toString();
        byte[] passw = pas.getBytes();

        for(int i = 0; i < pas.length(); i++)
        {
            pass += (int)passw[i];
            pass <<= (int)passw[i]%3;
        }
        Random random = new Random(pass);

//        IvParameterSpec iv = null;
//        SecretKeySpec sks = null;
//        try {
//            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
//            sr.setSeed(password.getText().toString().getBytes());
//            iv = new IvParameterSpec(sr.generateSeed(8));
//            KeyGenerator kg = KeyGenerator.getInstance("AES");
//            kg.init(128, sr);
//            sks = new SecretKeySpec((kg.generateKey()).getEncoded(), "AES");
//        } catch (Exception e) {
//            Log.e("Crypto", "AES secret key spec error");
//            Toast.makeText(getApplicationContext(), "AES secret key spec error", Toast.LENGTH_SHORT).show();
//            return;
//        }

        Bitmap bitmap = result;
        bitmap.setHasAlpha(false);
        String text = "";
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bitOnColor = 0;
        boolean findBits = false;
        for(bitOnColor = 1; bitOnColor <= 8; bitOnColor++)
        {
            random = new Random(pass);
            HashSet<Integer> states = new HashSet<Integer>();
            int temp = getNextTemp(random, width, height, states);
            states.add(temp);
            int x = temp%width;
            int y = temp/width;
            int bitInColor = 8 - bitOnColor;
            int color = 0;
            int PixelColor = bitmap.getPixel(x, y);
            int r = Color.red(PixelColor);;
            int g = Color.green(PixelColor);
            int b = Color.blue(PixelColor);
            byte symbol = 0;
            for(int i = 0; i < 2; i++)
            {
                for(int bit = 0; bit < 8; bit++)
                {
                    if(color == 2 && bitInColor == 8)
                    {
                        color = 0;
                        temp = getNextTemp(random, width, height, states);
                        states.add(temp);
                        x = temp%width;
                        y = temp/width;
                        bitInColor = 8 - bitOnColor;
                        PixelColor = bitmap.getPixel(x, y);
                        r = Color.red(PixelColor);
                        g = Color.green(PixelColor);
                        b = Color.blue(PixelColor);
                        bit--;
                    }
                    else
                    {
                        switch (color)
                        {
                            case (0):
                                symbol = setBitInByty(symbol, bit, getBitInColor(r, bitInColor));
                                bitInColor++;
                                if(bitInColor == 8)
                                {
                                    color++;
                                    bitInColor = 8 - bitOnColor;
                                }
                                break;
                            case (1):
                                symbol = setBitInByty(symbol, bit, getBitInColor(g, bitInColor));
                                bitInColor++;
                                if(bitInColor == 8)
                                {
                                    color++;
                                    bitInColor = 8 - bitOnColor;
                                }
                                break;
                            case (2):
                                symbol = setBitInByty(symbol, bit, getBitInColor(b, bitInColor));
                                bitInColor++;
                                break;
                        }
                    }
                }
                text += (char)symbol;
            }
            if(text.equals("|s"))
            {
                text = "";
                findBits = true;
                setMinSeekBar(seekBar, 1, 8);
                seekBar.setProgress(bitOnColor - minValueSeekBar);
                textView.setText("Number of bits used per color - " + String.valueOf(seekBar.getProgress()+minValueSeekBar));
                break;
            }
            else
            {
                text = "";
            }
        }
        if(findBits)
        {
            random = new Random(pass);
            HashSet<Integer> states = new HashSet<Integer>();
            int temp = getNextTemp(random, width, height, states);
            states.add(temp);
            int x = temp%width;
            int y = temp/width;
            int bitInColor = 8 - bitOnColor;
            int color = 0;
            int PixelColor = bitmap.getPixel(x, y);
            int r = Color.red(PixelColor);;
            int g = Color.green(PixelColor);
            int b = Color.blue(PixelColor);
            byte symbol = 0;
            boolean symb = true;
            while (symb) {
                for (int bit = 0; bit < 8; bit++) {
                    if (color == 2 && bitInColor == 8) {
                        color = 0;
                        temp = getNextTemp(random, width, height, states);
                        states.add(temp);
                        x = temp % width;
                        y = temp / width;
                        bitInColor = 8 - bitOnColor;
                        PixelColor = bitmap.getPixel(x, y);
                        r = Color.red(PixelColor);
                        g = Color.green(PixelColor);
                        b = Color.blue(PixelColor);
                        bit--;
                    } else {
                        switch (color) {
                            case (0):
                                symbol = setBitInByty(symbol, bit, getBitInColor(r, bitInColor));
                                bitInColor++;
                                if (bitInColor == 8) {
                                    color++;
                                    bitInColor = 8 - bitOnColor;
                                }
                                break;
                            case (1):
                                symbol = setBitInByty(symbol, bit, getBitInColor(g, bitInColor));
                                bitInColor++;
                                if (bitInColor == 8) {
                                    color++;
                                    bitInColor = 8 - bitOnColor;
                                }
                                break;
                            case (2):
                                symbol = setBitInByty(symbol, bit, getBitInColor(b, bitInColor));
                                bitInColor++;
                                break;
                        }
                    }
                }
                text += (char) symbol;
                if(text.length() < 2) {
                    symb = true;
                }
                else
                {
                    String tmp = text.substring(text.length()-2, text.length());
                    if(tmp.charAt(0) == '|' && tmp.charAt(1) == '0')
                    {
                        symb = false;
                    }
                }
            }

//            text = text.substring(2, text.length()-2);
//
//            byte[] encodedBytes = text.getBytes();
//
//            byte[] decodedBytes = null;
//            try {
//                Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
//                c.init(Cipher.DECRYPT_MODE, sks, iv);
//                decodedBytes = c.doFinal(encodedBytes);
//            } catch (Exception e) {
//                Log.e("Crypto", "AES decryption error");
//            }
//            text = new String(decodedBytes);
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Decryption error ", Toast.LENGTH_SHORT).show();
            return;
        }
//        int textB;
//        int width = bitmap.getWidth();
//        int height = bitmap.getHeight();
//        HashSet<Integer> states = new HashSet<Integer>();
//        boolean symb = true;
//
//        while(symb)
//        {
//            symb = true;
//            textB = 0;
//            int x;
//            int y;
//            int temp = 0;
//            boolean xy;
//            do {
//                xy = false;
//                temp = (int) (random.nextDouble() * (width*height + 1));
//                x = temp%width;
//                y = temp/width;
//                if(states.contains(temp))
//                {
//                    xy = true;
//                }
//            }while (xy);
//            states.add(temp);
//            temp = 0;
//            temp = bitmap.getPixel(x, y);
//            int alpha = Color.alpha(temp);
//            int r = Color.red(temp);
//            int g = Color.green(temp);
//            int b = Color.blue(temp);
//            r <<= 5+6*4;
//            r>>>=6*4;
//            g <<= 5+6*4;
//            g >>>= 3+6*4;
//            b <<= 6+6*4;
//            b >>>= 6+6*4;
//            textB = r | g | b;
//            text += (char)textB;
//            text = text;
//            if(text.length() < 2) {
//                symb = true;
//            }
//            else
//            {
//                if(text.charAt(0) != '|')
//                {
//                    if(text.charAt(1) != 's')
//                    {
//                        Toast.makeText(getApplicationContext(), "Decryption error ", Toast.LENGTH_SHORT).show();
//                        return;
//                    }
//                }
//                if(text.charAt(text.length()-2) == '|')
//                {
//                    if(text.charAt(text.length()-1) == '0')
//                    {
//                        symb = false;
//                    }
//                }
//            }
//        }
//
        for(int i = 0; i < text.length()-1; i++)
        {
            if(text.charAt(i) == '|')
            {
                boolean find = false;
                int j = 0;
                switch (text.charAt(i+1))
                {
                    case ('n'):
                        find = false;
                        j = i+1;
                        for(;j < text.length(); j++)
                        {
                            if(text.charAt(j) == '|')
                            {
                                find = true;
                                break;
                            }
                        }
                        if(find)
                        {
                            name.setText(text.substring(i+2, j));
                        }
                        break;

                    case ('e'):
                        find = false;
                        j = i+1;
                        for(;j < text.length(); j++)
                        {
                            if(text.charAt(j) == '|')
                            {
                                find = true;
                                break;
                            }
                        }
                        if(find)
                        {
                            email.setText(text.substring(i+2, j));
                        }
                        break;
                    case ('w'):
                        find = false;
                        j = i+1;
                        for(;j < text.length(); j++)
                        {
                            if(text.charAt(j) == '|')
                            {
                                find = true;
                                break;
                            }
                        }
                        if(find)
                        {
                            webSite.setText(text.substring(i+2, j));
                        }
                        break;
                    case ('p'):
                        find = false;
                        j = i+1;
                        for(;j < text.length(); j++)
                        {
                            if(text.charAt(j) == '|')
                            {
                                find = true;
                                break;
                            }
                        }
                        if(find)
                        {
                            phoneNumber.setText(text.substring(i+2, j));
                        }
                        break;
                    case ('d'):
                        find = false;
                        j = i+1;
                        for(;j < text.length(); j++)
                        {
                            if(text.charAt(j) == '|')
                            {
                                find = true;
                                break;
                            }
                        }
                        if(find)
                        {
                            date.setText(text.substring(i+2, j));
                        }
                        break;
                    case ('c'):
                        find = false;
                        j = i+1;
                        for(;j < text.length(); j++)
                        {
                            if(text.charAt(j) == '|')
                            {
                                find = true;
                                break;
                            }
                        }
                        if(find)
                        {
                            comment.setText(text.substring(i+2, j));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        Toast.makeText(getApplicationContext(), "Decrypted", Toast.LENGTH_SHORT).show();
    }

    public void loadImage(View view) {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    @Override
    protected void onDestroy() {
        Runtime.getRuntime().gc();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Bitmap bitmap = null;
        ImageView imageView = (ImageView) findViewById(R.id.imageButton);
        switch (requestCode) {
            case GALLERY_REQUEST:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    setMinSeekBar(seekBar, 1, 8);
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        }
        result = bitmap;
        bitmap = Bitmap.createScaledBitmap(result, Heoght*result.getWidth()/result.getHeight(), Heoght, true);
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
        if(bitmap != null)
        {
            imageView.setImageBitmap(bitmap);
        }

//        fdel.delete();
    }
}

