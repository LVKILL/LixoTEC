package com.example.rauber.lixotec.Activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rauber.lixotec.Model.Coleta;
import com.example.rauber.lixotec.R;
import com.example.rauber.lixotec.SharedPreferences.SystemPreferences;
import com.example.rauber.lixotec.ViewModel.ColetaViewModel;
import com.google.gson.JsonObject;
import com.gun0912.tedpermission.TedPermissionResult;
import com.tedpark.tedpermission.rx2.TedRx2Permission;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PictureScreen extends AppCompatActivity implements View.OnClickListener {

    private String pickenHour;
    private String pickenDate;
    private String pickenAddres;
    private Button buttonContinue;
    private ProgressBar progressCircle;
    private RelativeLayout relativeLayoutButtonPicture;
    private TextView textViewLabelTakePicture;
    private ColetaViewModel coletaViewModel;
    private SystemPreferences systemPreferences;
    private ImageView coletaImageView;
    private String imageStr = "";
    private Context context;
    private byte[] image;
    private Bitmap imageToUpload;
    private File imgFile;
    private String currentPhotoPath;
    static final int REQUEST_TAKE_PHOTO = 1;
    private static PictureScreen parent;
    private static final int REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        relativeLayoutButtonPicture = findViewById(R.id.RelativeLayoutTakePic);
        coletaImageView = findViewById(R.id.ImageViewColeta);
        buttonContinue = findViewById(R.id.ButtonContinue);
        textViewLabelTakePicture = findViewById(R.id.TextViewLabelTakePicture);
        context = this;
        systemPreferences = new SystemPreferences(this);

        coletaViewModel = ViewModelProviders.of(this).get(ColetaViewModel.class);
        progressCircle = findViewById(R.id.ProgressCircle);
        progressCircle.setVisibility(View.INVISIBLE);
        parent = this;
        verifyPermissions();




        if(getIntent().getExtras() != null){
            pickenHour = getIntent().getExtras().getString("pickedHour");
            pickenDate = getIntent().getExtras().getString("selectedDate");
            pickenAddres = getIntent().getExtras().getString("addressId");
        }else{
            pickenHour = "00h00";
            pickenDate = "0000-00-00";
            pickenAddres = "0";
        }
        publishObserver();
    }

    private void verifyPermissions(){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                permissions[2]) == PackageManager.PERMISSION_GRANTED){
            relativeLayoutButtonPicture.setOnClickListener(this);
            buttonContinue.setOnClickListener(this);
        }else{
            ActivityCompat.requestPermissions(PictureScreen.this, permissions, REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        verifyPermissions();
    }

    private void publishObserver(){
        coletaViewModel.observeColetaResponse().observe(this, (@Nullable String s) -> {
                String[] jsonResponse = convertJsonResponse(s);
                if(!jsonResponse[2].equals("0")){
                    addToLocalDatabase(createSynchronizationJson(Integer.parseInt(jsonResponse[2])));
                    uploadImageToServer(jsonResponse[2]);
                    dialogResponse(PictureScreen.this, jsonResponse);
                }else{
                    progressCircle.setVisibility(View.INVISIBLE);
                    dialogError(context, jsonResponse[1]);
                }
        });
    }

    private void uploadImageToServer(String idColeta) {
        String jsonIdColeta = "";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("acao","envioImagemColeta");
            jsonObject.put("idColeta",idColeta);
            jsonIdColeta = jsonObject.toString();
        }catch (JSONException je){
            Log.v("tag"," error in uploadImageToServer in PictureScreen.class "+je.getMessage());
        }
try {
    coletaViewModel.uploadImage(imgFile, jsonIdColeta);
    Log.v("tag","absolute path : "+imgFile.getAbsolutePath() + "\n Is file ? : "+imgFile.isFile()
    +"\n Name : "+imgFile.getName()+ "\n to String : "+imgFile.toString());

}catch (IOException ioe){Log.v("tag","error in uploadimagetoserver() "+ioe.getMessage());
}
        }

    private String createSynchronizationJson(int idColeta) {
        String syncronizationJson = "";

        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("acao","buscarColeta");
            jsonObject.put("id_coleta",idColeta);

            syncronizationJson = jsonObject.toString();

        }catch (JSONException je){
            Log.v("TAG","Error on createSynchronizationJson() in PictureScreen.java "+je.getMessage());
        }

        return syncronizationJson;
    }

    public String[] convertJsonResponse(String json){
        String[] result = new String[3];

        try{
            JSONArray jsonArray = new JSONArray(json);
            if(!jsonArray.getJSONObject(0).getString("codigo").equals("600")) {

                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String responseCode = jsonObject.getString("codigo");
                    String responseMessage = jsonObject.getString("mensagem");
                    String idColeta = jsonObject.get("idColeta").toString();
                    Log.v("tag", "tag d cefes" + jsonObject);
                    result[0] = responseCode;
                    result[1] = responseMessage;
                    result[2] = idColeta;
                }
            }else{
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                result[0] = jsonObject.getString("codigo");
                result[1] = jsonObject.getString("mensagem");
                result[2] = "0";
            }
        }catch(JSONException je){
            Log.v("tag", ""+je.getMessage());
        }

        return result;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ButtonContinue:
                progressCircle.setVisibility(View.VISIBLE);
                PrepareImageInBackground prepareImageInBackground = new PrepareImageInBackground(parent);
                prepareImageInBackground.execute();
                break;
            case R.id.RelativeLayoutTakePic:
                loadPicture();
                break;
        }
    }
    private void prepareImage() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if(imageToUpload != null) {
            imageToUpload.compress(Bitmap.CompressFormat.PNG, 75, stream);
            image = stream.toByteArray();
            imageStr = Base64.encodeToString(image, Base64.DEFAULT);
            progressCircle.setVisibility(View.INVISIBLE);
        }else{
            imageStr = "";
        }

    }

    private void loadPicture() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if(takePicture.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            }catch (IOException ex){
                Log.e("Error","Error on function loadPicture() "+ex.getMessage());
            }

            if(photoFile != null){
                Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.rauber.lixotec", photoFile);
                takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePicture, REQUEST_TAKE_PHOTO);

            }
        }

    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd__HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        currentPhotoPath = image.getAbsolutePath();
        imgFile = image;
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        setPicture();

    }

    private void setPicture(){
        int targetWidth = coletaImageView.getWidth();
        int targetHeight = coletaImageView.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoWidth = bmOptions.outWidth;
        int photoHeight = bmOptions.outHeight;
        Log.v("tag"," photoWidth = "+photoWidth);
        Log.v("tag"," photoHeight3 = "+photoHeight);

        int scaleFactor = Math.min(photoWidth/targetWidth, photoHeight/targetHeight);

        bmOptions.inJustDecodeBounds =  false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        imageToUpload = bitmap;
        coletaImageView.setImageBitmap(bitmap);
        textViewLabelTakePicture.setVisibility(View.INVISIBLE);
    }
    private void dialogError(Context context, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = getLayoutInflater().inflate(R.layout.alert_dialog_mensagem_padrao, null);

        TextView textViewMessage = layout.findViewById(R.id.TextViewMessage);
        Button buttonAgree = layout.findViewById(R.id.ButtonAgree);
        builder.setView(layout);
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        textViewMessage.setText(message);
        buttonAgree.setOnClickListener((View v) -> {

            alertDialog.dismiss();

        });

        alertDialog.show();

    }
    private void dialogResponse(Context context, String [] message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = getLayoutInflater().inflate(R.layout.alert_dialog_mensagem_padrao, null);

        TextView textViewMessage = layout.findViewById(R.id.TextViewMessage);
        Button buttonAgree = layout.findViewById(R.id.ButtonAgree);
        builder.setView(layout);
        AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final String responseCode = message[0];
        String responseMessage = message[1];

        progressCircle.setVisibility(View.GONE);

        textViewMessage.setText(responseMessage);
        buttonAgree.setOnClickListener((View v) -> {
                if(responseCode.equals("200")){
                    Intent mainScreen = new Intent(PictureScreen.this, MainActivity.class);
                    startActivity(mainScreen);
                    finish();
                }else if(responseCode.equals("400")){
                    Intent addressScreen = new Intent(PictureScreen.this, SelectAddressActivity.class);
                    startActivity(addressScreen);
                    finish();
                }
                alertDialog.dismiss();
        });
        alertDialog.show();
    }

    private void addToLocalDatabase(String json){
        coletaViewModel.sincronizarColeta(json);
    }

    private void dialogMessage(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View layout = getLayoutInflater().inflate(R.layout.alert_dialog_mensagem_padrao, null);

        TextView textViewMessage = layout.findViewById(R.id.TextViewMessage);
        Button buttonAgree = layout.findViewById(R.id.ButtonAgree);
        builder.setView(layout);
        AlertDialog alertDialog = builder.create();
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        String[] responseMessageSplitted = pickenDate.split("/");
        if(responseMessageSplitted[2].length() == 1){
            responseMessageSplitted[2] = "0".concat(responseMessageSplitted[2]);
        }
        if(responseMessageSplitted[1].length() == 1){
            responseMessageSplitted[1] = "0".concat(responseMessageSplitted[1]);
        }
        String responseMessage = context.getResources().getString(R.string.text_view_confirm_date).concat(responseMessageSplitted[2]
                .concat("/"+responseMessageSplitted[1]
                        .concat("/"+responseMessageSplitted[0])))
                + "\n";

        String date = context.getResources().getString(R.string.text_view_confirm_date) + pickenDate+"\n";
        String time =  context.getResources().getString(R.string.text_view_confirm_hour) + pickenHour;
        String dateTime = responseMessage.concat(time);
        textViewMessage.setText(dateTime);
        buttonAgree.setOnClickListener((View v) -> {

                String json = createColetaJson();
                Log.v("tag","jss"+json);
                coletaViewModel.registerColeta(json);
                progressCircle.setVisibility(View.VISIBLE);
                alertDialog.dismiss();

        });

        alertDialog.show();

    }
    private String createColetaJson() {
    String json = "";
        try{
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("acao","registrar");
            jsonObject.put("idusuario",systemPreferences.getUserId());
            jsonObject.put("imagem","");
            Log.v("tag","pickenaddress"+pickenAddres);
            jsonObject.put("idendereco",pickenAddres);
            String coletaHour = createColetaHour();
            String coletaDate = pickenDate;
            String coletaHourAndDate = coletaDate + ":" + coletaHour;
            jsonObject.put("datahora", coletaHourAndDate);
            jsonObject.put("realizada",0);


            json = jsonObject.toString();

        }catch(JSONException je){
            Log.e("tag","je : "+je);

        }

    return json;
    }

    private String createColetaHour() {
        String[] arrayHour = pickenHour.split("h");
        String coletaHour = arrayHour[0] + ":"+arrayHour[1]+":00";
        return coletaHour;

    }

    class PrepareImageInBackground extends AsyncTask<Void, Void, Void>{

        private PictureScreen parent;

        public PrepareImageInBackground(PictureScreen parent) {
            this.parent = parent;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            prepareImage();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            parent.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!imageStr.equals("")){

                        dialogMessage(PictureScreen.this);
                    }else{
                        progressCircle.setVisibility(View.GONE);
                        Toast.makeText(PictureScreen.this, "Adicione uma foto do lixo eletrônico.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}
