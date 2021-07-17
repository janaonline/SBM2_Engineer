package com.ichangemycity.swachhbharatengineer;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ichangemycity.appdata.AppConstant;
import com.ichangemycity.appdata.AppController;
import com.ichangemycity.appdata.AppUtils;
import com.ichangemycity.appdata.ICMyCPreferenceData;
import com.ichangemycity.base.BaseAppCompatActivity;
import com.ichangemycity.callback.OnButtonClick;
import com.ichangemycity.model.CustomGallery;
import com.ichangemycity.model.SelectedImageModel;
import com.ichangemycity.permission.GetPermissionResult;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created by pattabi.raman on 18-10-2017.
 */

public class SelectImageDialogActivity extends BaseAppCompatActivity {/*
    private static Activity activity;
    //    private RippleView rippleViewCamera, rippleViewGallery;
    private TextView tvCamera, tvGallery;
    ProgressBar progress;
    List<String> permissionsRequired = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_image_dialog_activity);
        AppController.assignLanguage(SelectImageDialogActivity.this);

        permissionsRequired.clear();

        activity = SelectImageDialogActivity.this;
        progress = activity.findViewById(R.id.progress);
        tvCamera = findViewById(R.id.tvCamera);
        tvGallery = findViewById(R.id.tvGallery);
        AppController.mSelectedImageModels = new SelectedImageModel();
        AppController.latitude = 0.0;
        AppController.longitude = 0.0;
        checkForStoragePermission();
    }

    private void checkForStoragePermission() {
        permissionsRequired.add(android.Manifest.permission.INTERNET);
        permissionsRequired.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsRequired.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsRequired.add(android.Manifest.permission.CAMERA);
        runtimePermissionManager(activity, permissionsRequired, new GetPermissionResult() {
            @Override
            public void resultPermissionSuccess() {
//                Toast.makeText(activity, "Thanks for allowing permissions", Toast.LENGTH_SHORT).show();
                tvCamera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        GenerateFolders();
                        captureImage();
                    }
                });
                tvGallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(activity, AndroidCustomGalleryActivity.class));
                    }
                });

            }

            @Override
            public void resultPermissionRevoked() {
                AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, "We suggest to allow permissions to make app work as expected");

            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (AppController.mSelectedImageModels.getPathOfSelectedImage() != null) {
                if (AppController.mSelectedImageModels.getLatitude() != 0.0 && AppController.mSelectedImageModels.getLongitude() != 0.0) {
                    AppController.latitude = AppController.mSelectedImageModels.getLatitude();
                    AppController.longitude = AppController.mSelectedImageModels.getLongitude();
                    getAddressFromLatLong();
                } else {
                    new CheckImageSize().execute();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class CheckImageSize extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (AppController.mSelectedImageModels.getSizeInMB() > 8) {
                AppController.showAlert(activity, "Alert", "Total size exceeded 10MB " +
                        "of size. Please " +
                        "select an image with lesser memory to upload", false, new OnButtonClick() {

                    @Override
                    public void onPositiveButtonClicked(DialogInterface dialogInterface) {
                        dialogInterface.dismiss();
                    }

                    @Override
                    public void onNegativeButtonClicked() {

                    }
                });
            } else {
                redirectAccordingToPurposeOfImageUpload();
            }
        }
    }


    private void redirectAccordingToPurposeOfImageUpload() {
        switch (AppController.selectedPurposeToUploadImage) {
            case AppController.PURPOSE_CHANGE_STATUS:
                break;
            case AppController.PURPOSE_POST_COMMENT:
                activity.finish();
                break;

        }
        activity.finish();
    }

    private void GenerateFolders() {
        File folder = new File(Environment.getExternalStorageDirectory()
                + "/Swachhata/Images/");
        if (!folder.exists()) {
            folder.mkdir();
        }
    }


    Uri fileUri;
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final String IMAGE_DIRECTORY_NAME = "Swachhata";

    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        Looper.prepare();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fileUri = FileProvider.getUriForFile(activity, BuildConfig.APPLICATION_ID + ".provider",
                    getOutputMediaFile(MEDIA_TYPE_IMAGE));
        } else {
//            // Android version is lesser than 6.0 or the permission is already granted.
            GenerateFolders();
            fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        }

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    public static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " + IMAGE_DIRECTORY_NAME
                        + " directory");
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"
                    + timeStamp + ".jpg");
        } else {
            return null;
        }
        return mediaFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    previewCapturedImage();
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "You have cancelled image selection",
                            Toast.LENGTH_SHORT).show();
                } else {

                    AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, activity.getResources().getString(R.string.select_an_image));

                }
                break;
        }
    }

    private void previewCapturedImage() {

        try {
            String all_path = fileUri.getPath();
            CustomGallery item = new CustomGallery();
            item.sdcardPath = all_path;
//            item.sdcardPath = PublicEye.compressImage(activity,
//                    item.sdcardPath);
            SelectedImageModel selectedImageModel = new SelectedImageModel();
            File myFile = new File(fileUri.getPath());
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(System.currentTimeMillis());
            selectedImageModel.setDATE_TAKEN(AppController.getDate(cal.getTimeInMillis(), AppController.DATE_FORMAT));
            selectedImageModel.setPathOfSelectedImage(myFile.getAbsolutePath());
            selectedImageModel.setUriOfImage(fileUri);
            selectedImageModel.setThumbnails(BitmapFactory.decodeFile((myFile.getAbsolutePath())));
            AppController.mSelectedImageModels = selectedImageModel;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getAddressFromLatLong() {
        progress.setVisibility(View.VISIBLE);
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(activity, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(AppController.latitude, AppController.longitude, 1); // Here 1 represent max location result to
            // returned, by
        } catch (IOException e) {
            e.printStackTrace();
        }
        // documents
        // it
        // recommended 1
        // to 5
        if (addresses != null) {
            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address
            // lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            progress.setVisibility(View.GONE);

        } else {
            AppUtils.getInstance().hideProgressDialog(activity);
        }

    }

    @Override
    public void finish() {
        super.finish();
        AppController.selectedPurposeToUploadImage = -1;
    }
}
*/

    private static Activity activity;
    private Button rippleViewCamera, rippleViewGallery;
    ProgressBar progress;
    List<String> permissionsRequired = new ArrayList<>();
    private boolean isFromCreatePost = false;

    Uri fileUri;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int OPEN_MEDIA_PICKER = 1;

    public Activity getActivity() {
        if(activity == null) {
            activity = SelectImageDialogActivity.this;
        }
        return activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_image_dialog_activity);
        AppController.getInstance().assignLanguage(SelectImageDialogActivity.this);
        try {
            isFromCreatePost = getIntent().getExtras().getBoolean("isFromCreatePost");
        } catch(NullPointerException ex) {
        }
        permissionsRequired.clear();

        activity = SelectImageDialogActivity.this;
        progress = activity.findViewById(R.id.progress);
        rippleViewCamera = findViewById(R.id.rippleViewCamera);
        rippleViewGallery = findViewById(R.id.rippleViewGallery);

        setToolbarAndCustomizeTitle(findViewById(R.id.toolbar), getString(R.string.select_an_image));

        AppController.mSelectedImageModels = new SelectedImageModel();
        checkForStoragePermission();

    }

    private void setToolbarAndCustomizeTitle(Toolbar toolbar, String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.back));

        toolbar.setNavigationOnClickListener(v -> activity.finish());
        final Drawable upArrow = getResources().getDrawable(R.mipmap.clear);
        upArrow.setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        getSupportActionBar().setHomeAsUpIndicator(upArrow);
        getSupportActionBar().setTitle(title);
        toolbar.setTitleTextColor(Color.WHITE);
    }

    private void checkForStoragePermission() {
        permissionsRequired.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        permissionsRequired.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsRequired.add(android.Manifest.permission.CAMERA);
        runtimePermissionManager(activity, permissionsRequired, new GetPermissionResult() {
            @Override
            public void resultPermissionSuccess() {
                rippleViewCamera.setOnClickListener(rippleView -> {
//                    AppController.getInstance().trackEvent(AppConstant.POST_COMPLAINT, AppConstant.IMAGE_SELECTED_CAMERA, AppConstant.IMAGE_SELECTED_CAMERA);
                    openCamera();
                });
                rippleViewGallery.setOnClickListener(rippleView -> {
//                    AppController.getInstance().trackEvent(AppConstant.POST_COMPLAINT, AppConstant.IMAGE_SELECTED_GALLERY, AppConstant.IMAGE_SELECTED_GALLERY);
                    Intent takePicture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(takePicture, OPEN_MEDIA_PICKER);
                });

               /* rippleViewSkip.setOnClickListener(rippleView -> {
                    AppController.mSelectedImageModels = null;
                    ICMyCPreferenceData.setPreference(activity, ICMyCPreferenceData.complaintUploadedImageFile, "");
                    redirectAccordingToPurposeOfImageUpload();
                });*/

                if(isFromCreatePost) {
                    rippleViewCamera.performClick();
                    rippleViewGallery.setVisibility(View.GONE);
                }
            }

            @Override
            public void resultPermissionRevoked() {
                AppUtils.getInstance().showToast(activity, AppConstant.TOAST_TYPE_INFO, "We suggest to allow permissions to make app work as expected");

            }
        });
    }

    private void redirectAccordingToPurposeOfImageUpload() {

//        switch(AppController.getInstance().selectedPurposeToUploadImage) {
//            case AppConstant.PURPOSE_POST_COMPLAINT:
//            case AppConstant.PURPOSE_CHANGE_STATUS:
//            case AppConstant.PURPOSE_POST_COMMENT:
//            case AppConstant.PURPOSE_CHANGE_PROFILE_PIC:
//            case AppConstant.PURPOSE_EDIT_COMPLAINT:
//            case AppConstant.PURPOSE_CREATE_EVENT:
//            case AppConstant.PURPOSE_EDIT_EVENT:
//            case AppConstant.PURPOSE_UPLOAD_PAN_AADHAR:
//            case AppConstant.PURPOSE_UPLOAD_ORG_AVATAR:
//            case AppConstant.PURPOSE_CREATE_POST:
//                activity.finish();
//                break;
//            case AppConstant.PURPOSE_UPLOAD_ORG_COVER:
//                isToRefreshCover = true;
                activity.finish();
//                break;
//        }

    }
    /*Open Native Camera*/
    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        //Camera intent
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                if(resultCode == RESULT_OK) {
                    CropImage.activity(fileUri).setGuidelines(CropImageView.Guidelines.ON).start(activity);
                } else if(resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "You have cancelled image selection", Toast.LENGTH_SHORT).show();
                } else {

                    AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, activity.getResources().getString(R.string.select_an_image));

                }
                break;

            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if(resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    getCroppedImageData(resultUri);
                } else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, error.getMessage());
                    activity.finish();
                }
                break;
            case OPEN_MEDIA_PICKER:
                if(resultCode == RESULT_OK && data != null) {
                    Uri uri = data.getData();
                    if(uri != null) {
                        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON).start(activity);
                    } else {
                        AppUtils.showToast(activity, AppConstant.TOAST_TYPE_INFO, "Please pick an image");
                    }
                }
                break;
        }
    }

    private void getCroppedImageData(final Uri resultUri) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        AppController.mSelectedImageModels.setDATE_TAKEN(AppController.getInstance().getDate(cal.getTimeInMillis(), AppConstant.getInstance().DATE_FORMAT));
        AppController.mSelectedImageModels.setPathOfSelectedImage(resultUri.getPath());
        AppController.mSelectedImageModels.setUriOfImage(resultUri);
        AppController.mSelectedImageModels.setArrPath(resultUri.getPath());
        AppController.mSelectedImageModels.setThumbnails(AppUtils.getInstance().getBitmapFromURI(activity, resultUri));
        redirectAccordingToPurposeOfImageUpload();
    }

    @Override
    public void finish() {
        super.finish();
        AppController.getInstance().selectedPurposeToUploadImage = -1;
    }
}