package com.teste.chromakey;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ImageView img;
    private ImageButton bt_camera;
    RelativeLayout layoutImg, tudo, l_msg;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    String caminhoImagem;
    File photoFile = null;
    Bitmap foto;
    TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).hide();

        inicializarComponentes();
        inicializarListeners();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    Uri.fromFile(photoFile)
            ));
            mostrarImagem();
        }
    }

    private void inicializarComponentes(){
        img = (ImageView) findViewById(R.id.img);
        bt_camera = (ImageButton) findViewById(R.id.camera);
        layoutImg = (RelativeLayout) findViewById(R.id.layout_img);
        tudo = (RelativeLayout) findViewById(R.id.tudo);
        msg = (TextView)findViewById(R.id.msg);
        l_msg = (RelativeLayout) findViewById(R.id.l_msg);
    }

    private void inicializarListeners() {
        bt_camera.setOnClickListener(v -> AbrirCamera());
    }

    private void AbrirCamera(){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = criarArquivo();
            } catch (IOException ignored) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.teste.chromakey",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }

    }

    private File criarArquivo() throws IOException{
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        caminhoImagem = image.getAbsolutePath();
        return image;
    }

    private void mostrarImagem() {

        int targetW = img.getWidth();
        int targetH = img.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(caminhoImagem, bmOptions);
        foto = rotacionarImagem(bitmap);

        chromakey(foto);
    }

    private Bitmap rotacionarImagem(Bitmap imagem){
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(imagem, 0, 0, imagem.getWidth(), imagem.getHeight(), matrix, true);
    }

    private void chromakey(final Bitmap imagem){
        imagem.setHasAlpha(true);
        img.setImageBitmap(imagem);
        Msg();

        new Thread(() -> {

            for(int i = 0;i < imagem.getWidth();i++){
                for (int j = 0; j <imagem.getHeight();j++){

                    int corPixel = imagem.getPixel(i,j);
                    int R = (corPixel >> 16) & 0xff;
                    int G = (corPixel >>  8) & 0xff;
                    int B = (corPixel      ) & 0xff;



                    if(G >= (G+R+B)*0.4){
                        imagem.setPixel(i,j,0);
                    }

                    if(i == imagem.getWidth()-1 && j == imagem.getHeight()-1){
                        l_msg.post(() -> l_msg.setVisibility(View.INVISIBLE));
                    }
                }

                img.post(() -> img.setImageBitmap(imagem));
            }
        }).start();

    }

    private void Msg() {
        l_msg.setVisibility(View.VISIBLE);
    }
}
