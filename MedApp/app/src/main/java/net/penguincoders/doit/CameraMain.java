package net.penguincoders.doit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.gson.JsonArray;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraMain extends AppCompatActivity {

    private static String TAG = "Uploader";

    private ApiCore.GenericDefinitionInterface definitionInterface;
    private Button upload;
    private WebView webView;
    private ProgressDialog dialog;
    private Button camUpload;
    private EditText addToList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);

        upload = findViewById(R.id.button);
        camUpload = findViewById(R.id.camera_button);
        webView = findViewById(R.id.result);
        dialog = new ProgressDialog(CameraMain.this);


        upload.setOnClickListener(handleButtonClick);
        camUpload.setOnClickListener(handleCamOnClick);



        definitionInterface = ApiCore.getRetrofit().create(ApiCore.GenericDefinitionInterface.class);
    }
    //öffne Galerie um Bild zu wählen
    private View.OnClickListener handleButtonClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImagePicker.Companion.with(CameraMain.this).galleryOnly().start();
        }
    };
    private View.OnClickListener handleCamOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImagePicker.Companion.with(CameraMain.this).cameraOnly().start();
        }
    };
    //Wenn Bild erfolgreich gesendet werden konnte Server Antwort bekommen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            File file = ImagePicker.Companion.getFile(data);

            MultipartBody.Part requestImage = null;

            RequestBody  requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
            requestImage = MultipartBody.Part.createFormData("image",file.getName(),requestFile);

            callForApiResponse(requestImage);

        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(this, ImagePicker.Companion.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
    //Definition wie Server Antwort bekommen werden soll
    private void callForApiResponse(MultipartBody.Part requestImage){
        //1 Minute Timeoutfenster wird angezeigt
        dialog.setTitle("Loading");
        dialog.setMessage("Timeout set to 1 minute...");
        dialog.show();
//Serverantwort als String in einem View
        Call<JsonArray> call = definitionInterface.uploadFile(requestImage);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                if(response.isSuccessful()) {

                    Log.d(TAG, response.toString());
                    dialog.dismiss();
                    webView.loadDataWithBaseURL(null, response.body().toString(), "text/html", "utf-8", null);

                }
                else{
                    dialog.dismiss();
                    System.out.println("Request Error :: " + response.errorBody());
                    Toast.makeText(CameraMain.this, "Please try again.", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                dialog.dismiss();
                //webView.loadDataWithBaseURL(null,t.getMessage(),"text/html", "utf-8", null);
                System.out.println("Network Error :: " + t.getLocalizedMessage());
            }
        });

    }
}