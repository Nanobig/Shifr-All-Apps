package com.uz.myapp;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PhotoCrypto extends AppCompatActivity {

    Button encrypt, decrypt;
    String image;
    ClipboardManager clipboardManager;
    ImageView imgView;
    EditText encIng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_photo_crypto);
        encrypt = findViewById(R.id.enc_btn);
        decrypt = findViewById(R.id.dec_btn);
        imgView = findViewById(R.id.imgView);
        encIng = findViewById(R.id.enc_txt);
        encIng.setEnabled(false);

        clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);


        encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PhotoCrypto.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(PhotoCrypto.this, new String[]{
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    }, 100);
                } else {
                    selectPhoto();
                }
            }
        });

        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] bytes = Base64.decode(encIng.getText().toString(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                imgView.setImageBitmap(bitmap);
            }
        });

    }

    private void selectPhoto() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), 100);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            selectPhoto();
        } else {

            Toast.makeText(this, "Permisson ruxsati mavjud emas ", Toast.LENGTH_SHORT).show();
        }
    }

    public void copyCode(View view) {
        String codes = encIng.getText().toString().trim();
        if (!codes.isEmpty()) {

            ClipData temp = ClipData.newPlainText("text", codes);
            clipboardManager.setPrimaryClip(temp);

            Toast.makeText(this, "Nusxalandi", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {

            Uri uri = data.getData();
            Bitmap bitmap;
            ImageDecoder.Source source = null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                source = ImageDecoder.createSource(this.getContentResolver(), uri);

                try {
                    bitmap = ImageDecoder.decodeBitmap(source);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] bytes = stream.toByteArray();
                    image = Base64.encodeToString(bytes, Base64.DEFAULT);
                    encIng.setText(image);
                    Toast.makeText(this, " Rasm shriftlandi!", Toast.LENGTH_SHORT).show();

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }


    }
}