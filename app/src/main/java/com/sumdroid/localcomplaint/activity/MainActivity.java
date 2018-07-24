package com.sumdroid.localcomplaint.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sumdroid.localcomplaint.R;
import com.sumdroid.localcomplaint.appConstant.AppConstants;
import com.sumdroid.localcomplaint.model.Complain;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class MainActivity extends BaseActivity {
    private EditText eTitle, eDescription;
    private ImageButton btnCapture;
    private ImageView imgChooseImage;
    private ImageView btnSave;
    private Activity mActivity;
    private Context mContext;
    private Bitmap captureBitmap;
    private ProgressDialog progressDialog ;
    // Creating URI.
    private Uri filePathUri;
    private MaterialBetterSpinner loginSpinner;

    // Creating StorageReference and DatabaseReference object.
    private StorageReference storageReference;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initToolbar();
        initDrawer();
        initVariable();
        initListener();
        initFunctionality();



    }

    private void initVariable() {
        mActivity=MainActivity.this;
        mContext=getApplicationContext();
        storageReference = FirebaseStorage.getInstance().getReference();
       // databaseReference = FirebaseDatabase.getInstance().getReference(AppConstants.dDATABASE_PATH);
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setIndeterminate(true);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.support_simple_spinner_dropdown_item, AppConstants.ISSUE);
        loginSpinner.setAdapter(adapter);

    }

    private void initView(){
        setContentView(R.layout.activity_main);
        eTitle =findViewById(R.id.editext_title);
        eDescription =findViewById(R.id.edit_description);
        btnCapture=findViewById(R.id.btn_camera);
        btnSave=findViewById(R.id.btn_save_complain);
        imgChooseImage=findViewById(R.id.btn_choose_image);
        loginSpinner = findViewById(R.id.spinner_login_person);

    }

    private void initListener() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext, "click on save button", Toast.LENGTH_SHORT).show();
                String userType = loginSpinner.getText().toString();

                if (userType.equals("road")) {
                    databaseReference = FirebaseDatabase.getInstance().getReference(AppConstants.ROAD);
                    UploadImageFileToFirebaseStorage();


                } else if (userType.equals("drainage")) {
                    databaseReference = FirebaseDatabase.getInstance().getReference(AppConstants.DRAIN);
                    UploadImageFileToFirebaseStorage();

                }
                else if (userType.equals("electricity")) {
                    databaseReference = FirebaseDatabase.getInstance().getReference(AppConstants.ELECTRICITY);
                    UploadImageFileToFirebaseStorage();

                } else {
                    Toast.makeText(mContext, "please select a issue", Toast.LENGTH_SHORT).show();
                }


            }
        });


        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //takePicture.setType("image/*");
                if (takePicture.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePicture, AppConstants.REQUEST_IMAGE_CAPTURE);

                }
            }
        });


        imgChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Creating intent.
                Intent intent = new Intent();
                // Setting intent type as image to select image from phone storage.
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, AppConstants.SELECT_IMAGE_TITLE), AppConstants.IMAGE_REQ_CODE);

            }

        });
    }


    private  void initFunctionality(){


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==AppConstants.REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK && data != null){

            Bundle extras = data.getExtras();
            captureBitmap = (Bitmap) extras.get("data");
            imgChooseImage.setImageBitmap(captureBitmap);



        }
        else if (requestCode == AppConstants.IMAGE_REQ_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePathUri = data.getData();

            try {

                // Getting selected image into Bitmap.
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePathUri);
                // Setting up bitmap selected image into ImageView.
                imgChooseImage.setImageBitmap(bitmap);

            }
            catch (IOException e) {

                e.printStackTrace();
            }
        }
    }


    // Creating Method to get the selected image file Extension from File Path URI.
    public String GetFileExtension(Uri uri) {

        ContentResolver contentResolver = getContentResolver();

        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri)) ;

    }

    // Creating UploadImageFileToFirebaseStorage method to upload image on storage.
    public void UploadImageFileToFirebaseStorage() {
        final String title = eTitle.getText().toString().trim();
        final String description = eDescription.getText().toString().trim();
        if (!validationcheck(title,description)){

            return;
        }
        // Setting progressDialog Title.
        progressDialog.setTitle("Uploading...");

        // Showing progressDialog.
        progressDialog.show();
        progressDialog.setCancelable(false);
        // Checking whether FilePathUri Is empty or not.
        if (filePathUri != null) {



            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child(AppConstants.STORAGE_PATH + System.currentTimeMillis() + "." + GetFileExtension(filePathUri));

            // Adding addOnSuccessListener to second StorageReference.
            storageReference2nd.putFile(filePathUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            @SuppressWarnings("VisibleForTests")
                            Complain imageUploadInfo = new Complain(taskSnapshot.getDownloadUrl().toString(), title, description);

                            // Getting image upload ID.
                            String ImageUploadId = databaseReference.push().getKey();

                            // Adding image upload id s child element into databaseReference.
                            databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Data Uploaded Successfully ", Toast.LENGTH_LONG).show();
                            clearDataField();
                        }
                    })
                    // If something goes wrong .
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            // Hiding the progressDialog.
                            progressDialog.dismiss();

                            // Showing exception erro message.
                            Toast.makeText(mContext, exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })

                    // On progress change upload time.
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // Setting progressDialog Title.
                            progressDialog.setTitle("Uploading...");

                        }
                    });
        }

        else if (captureBitmap!=null){
            encodeBitmapAndSaveToFirebase(captureBitmap,title,description);

        }
        else {

            Toast.makeText(mContext, "Please Select Image or Add Image Name", Toast.LENGTH_LONG).show();

        }


    }

    private void clearDataField() {
        eTitle.setText("");
        eDescription.setText("");
        imgChooseImage.setImageResource(R.drawable.picture);
    }

    private void encodeBitmapAndSaveToFirebase(Bitmap bitmap,String title,String description) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        String imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        Complain imageUploadInfo = new Complain(imageEncoded, title, description);
        // Getting image upload ID.
        String ImageUploadId = databaseReference.push().getKey();

        // Adding image upload id s child element into databaseReference.
        databaseReference.child(ImageUploadId).setValue(imageUploadInfo);
        progressDialog.dismiss();
        Toast.makeText(getApplicationContext(), "Data Uploaded Successfully ", Toast.LENGTH_LONG).show();
        clearDataField();
    }

    private boolean validationcheck(String title,String description){
        boolean valid=true;

        if (description.isEmpty()){
            eDescription.setError("filed is empty");
            valid=false;
        }

        if (title.isEmpty()){
            eTitle.setError("filed is empty");
            valid=false;
        }



        return valid;
    }



}
