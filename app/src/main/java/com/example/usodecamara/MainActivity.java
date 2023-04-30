package com.example.usodecamara;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button photo;
    ImageView Imgfoto;
    String rutaimagenadsoluta;

    private static final int REQUEST_CODIGO_CAMARA = 200;
    private static final int REQUEST_CODIGO_CAPTURAR_IMAGEN = 300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photo = findViewById(R.id.btnTFoto);
        Imgfoto = findViewById(R.id.IMGfoto);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Procesarfoto();
            }
        });
    }
    public void Procesarfoto(){
    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
        Tomarfoto();
    }else {
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                Manifest.permission.CAMERA
        },REQUEST_CODIGO_CAMARA);
    }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODIGO_CAMARA){
            if (permissions.length >0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Tomarfoto();
            }else {
                Toast.makeText(MainActivity.this, "Error no hay permisos", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void Tomarfoto(){
        Intent intentcamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intentcamera.resolveActivity(getPackageManager())!=null){
            File archivofoto = null;
            archivofoto = generarfoto();

            if (archivofoto!=null){
                Uri rutafoto = FileProvider.getUriForFile(MainActivity.this,"com.example.usodecamara",archivofoto);
                intentcamera.putExtra(MediaStore.EXTRA_OUTPUT, rutafoto);
                startActivityForResult(intentcamera, REQUEST_CODIGO_CAPTURAR_IMAGEN);
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==REQUEST_CODIGO_CAPTURAR_IMAGEN){
            if (resultCode == Activity.RESULT_OK){
                Imgfoto.setImageURI(Uri.parse(rutaimagenadsoluta));

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public File generarfoto(){
        String nomenclatura = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String prefijoArchivo = "UGB_"+nomenclatura+"_";
        File directorioImagen = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen = null;
        try {
            imagen = File.createTempFile(prefijoArchivo,".jpg",directorioImagen);
            rutaimagenadsoluta = imagen.getAbsolutePath();
        }catch (Exception error){
            Log.e("ErrorGenerarFoto", error.getMessage().toString());
        }
        return imagen;
    }
}