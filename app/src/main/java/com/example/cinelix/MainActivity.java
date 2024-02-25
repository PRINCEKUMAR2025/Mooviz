package com.example.cinelix;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.reporting.MessagingClientEvent;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_VIDEO=1;
    VideoView videoView;
    ImageButton logOut;
    ProgressBar progressBar;
    EditText editText;
    private Uri videoUri;
    MediaController mediaController;
    Button button;
    StorageReference storageReference;
    DatabaseReference databaseReference;

//    DatabaseReference databaseReferenceids;
    Member member;
    UploadTask uploadTask;
    FirebaseAuth auth;
    LottieAnimationView Lottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("mooviz");
        member=new Member();
        storageReference= FirebaseStorage.getInstance().getReference("Video");
        databaseReference= FirebaseDatabase.getInstance().getReference("video");

//        databaseReferenceids = FirebaseDatabase.getInstance().getReference("deviceids");

        videoView=findViewById(R.id.videoview_main);
        logOut=findViewById(R.id.logout);
        auth=FirebaseAuth.getInstance();
        button=findViewById(R.id.button_upload_main);
        progressBar=findViewById(R.id.progressBar_main);
        editText=findViewById(R.id.et_video_name);
        mediaController=new MediaController(this);
        videoView.setMediaController(mediaController);
        videoView.start();
        Lottie=findViewById(R.id.Lottie);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadVideo();
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent=new Intent(getApplicationContext(),SignIn.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICK_VIDEO || resultCode==RESULT_OK||data!=null||data.getData()!=null){
            try {
                videoUri =data.getData();

                videoView.setVideoURI(videoUri);
            }catch (Exception e){
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void ChooseVideo(View view) {
        Intent intent=new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_VIDEO);
    }

    private String getExt(Uri uri){
        ContentResolver contentResolver=getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

//    private void deviceids(){
//        String id= Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID);
//        String i=databaseReferenceids.push().getKey();
//        databaseReferenceids.child(i).setValue(id);
//
//    }

    private void UploadVideo(){
        String videoName=editText.getText().toString();
        String search=editText.getText().toString().toLowerCase();
        if (videoUri !=null || !TextUtils.isEmpty(videoName)){

            progressBar.setVisibility(View.VISIBLE);
            final StorageReference reference=storageReference.child(
                    System.currentTimeMillis()+"."+getExt(videoUri));
            uploadTask = reference.putFile(videoUri);

            Task<Uri> urlTask=uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, com.google.android.gms.tasks.Task<Uri>>() {
                @Override
                public com.google.android.gms.tasks.Task<Uri> then(@NonNull com.google.android.gms.tasks.Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull com.google.android.gms.tasks.Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUrl = task.getResult();
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(MainActivity.this, "Video Uploaded", Toast.LENGTH_SHORT).show();

                        member.setName(videoName);
                        member.setVideourl(downloadUrl.toString());
                        member.setSearch(search);
                        String i=databaseReference.push().getKey();
                        databaseReference.child(i).setValue(member);
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Failed...", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
        }
    }

    public void ShowVideo(View view) {
        Intent intent=new Intent(MainActivity.this,Showvideo.class);
        startActivity(intent);
    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        deviceids();
//    }
}