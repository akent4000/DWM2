package com.example.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private  int minValueSeekBar = 1;
    static final int GALLERY_REQUEST = 1;
    private static final String TAG = "1";
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 101;
    private  static final int Height = 350;
    TextView textView;
    SeekBar seekBar;
    private Bitmap result = null;
    private Handler ToastHan;
    final int STATUS_SAVED = 0;
    final int STATUS_NOT_SAVED = 1;
    final int STATUS_NULL = 2;
    final int STATUS_BUTTON_SHARE_ENABLE = 3;
    final int STATUS_BUTTON_SAVE_ENABLE = 4;

    @SuppressLint("SetTextI18n")
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

        Button butSh = (Button) findViewById(R.id.share);
        Button butS = (Button) findViewById(R.id.save);

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

    private void checkPermission()
    {
        int permissionStatus = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permissionStatus == PackageManager.PERMISSION_GRANTED);
        else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_WRITE_EXTERNAL_STORAGE);
        }
    }

    public void saveImage(View view) throws Exception {

        checkPermission();
        CreateFolders();
        Button but = (Button) findViewById(R.id.save);
        but.setEnabled(false);
        Runnable run = new Runnable() {
            @Override
            public void run() {
                String sdcardBmpPath = Environment.getExternalStorageDirectory() + "/DCIM/SavedImages/" + java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()) +".bmp";
                AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
                boolean isSaveResult = bmpUtil.save(result, sdcardBmpPath);
                if(isSaveResult)
                    ToastHan.sendEmptyMessage(STATUS_SAVED);
                else
                    ToastHan.sendEmptyMessage(STATUS_SAVED);
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(sdcardBmpPath))));
                ToastHan.sendEmptyMessage(STATUS_BUTTON_SAVE_ENABLE);
            }
        };
        Thread thread = new Thread(run);
        thread.start();
    }

    private void send(Bitmap bitmap) {
        Button but = (Button) findViewById(R.id.share);
        but.setEnabled(false);
        Runnable run = new Runnable() {
            @Override
            public void run() {
                try {
                    String sdcardBmpPath = Environment.getExternalStorageDirectory() + "/DCIM/LastSharedImage/" + "last_Shared_Img"+".bmp";
                    AndroidBmpUtil bmpUtil = new AndroidBmpUtil();
                    boolean isSaveResult = bmpUtil.save(bitmap, sdcardBmpPath);
                    if(isSaveResult)
                        ToastHan.sendEmptyMessage(STATUS_SAVED);
                    else
                        ToastHan.sendEmptyMessage(STATUS_NOT_SAVED);
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

                } catch (Exception e) {
                    Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                ToastHan.sendEmptyMessage(STATUS_BUTTON_SHARE_ENABLE);
            }
        };
        Thread thread = new Thread(run);
        thread.start();
    }

    public void shareImage(View view)
    {
        checkPermission();
        CreateFolders();
        send(result);
    }

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

        String text = "";
        text += (name.getText().toString().equals(""))? "" : "|n" + name.getText();
        text += (email.getText().toString().equals(""))? "" : "|e" + email.getText();
        text += (webSite.getText().toString().equals(""))? "" : "|w" + webSite.getText();
        text += (phoneNumber.getText().toString().equals(""))? "" : "|p" + phoneNumber.getText();
        text += (date.getText().toString().equals(""))? "" : "|d" + date.getText();
        text += (comment.getText().toString().equals(""))? "" : "|c" + comment.getText();
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

        random = new Random(pass);
        Bitmap bitmap = result;

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int tmp = text.length() * 3 / width / height;
        if(tmp <= 8)
        {
            setMinSeekBar(seekBar ,1 + tmp, 8);
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
        result = bitmap1;
        bitmap1 = Bitmap.createScaledBitmap(result, Height *result.getWidth()/result.getHeight(), Height, true);
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
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Decryption error ", Toast.LENGTH_SHORT).show();
            return;
        }

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
        if(bitmap != null)
        {
            result = bitmap;
            bitmap = Bitmap.createScaledBitmap(result, Height *result.getWidth()/result.getHeight(), Height, true);
            imageView.setImageBitmap(bitmap);
            bitmap = null;
        }
    }
}