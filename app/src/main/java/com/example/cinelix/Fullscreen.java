package com.example.cinelix;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Collections;

public class Fullscreen extends AppCompatActivity {

    private static final int PERMISSION_STORAGE_CODE = 1000;
    private SimpleExoPlayer player;
    private PlayerView playerView;
    TextView textView;
    boolean fullscreen = false;
    ImageView fullscreenButton;
    Button downloadbtn;
    public String title;
    private String url;
    private boolean playwhenready=false;
    private int currentWindow=0;
    private long playbackposition=0;

    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        InterstitialAd.load(this,"ca-app-pub-3940256099942544/1033173712"/*myactual Interstitial ca-app-pub-8361032125437158/1492264192*/, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        mInterstitialAd = null;
                    }
                });//        ActionBar actionBar=getSupportActionBar();
//        actionBar.setTitle("Fullscreen");
//
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);

        playerView=findViewById(R.id.exoplayer_fullscreen);
        textView=findViewById(R.id.tv_fullscreen);
        downloadbtn=findViewById(R.id.download_btn);
        fullscreenButton=playerView.findViewById(R.id.exoplayer_fullscreen_icon);

        Intent intent=getIntent();
        url=intent.getExtras().getString("ur");
        title=intent.getExtras().getString("nam");

        textView.setText(title);

        downloadbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(Fullscreen.this);
                } else {
                    Toast.makeText(Fullscreen.this, "Ad not available", Toast.LENGTH_SHORT).show();
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (checkCallingOrSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_DENIED){
                        String permission = (Manifest.permission.WRITE_EXTERNAL_STORAGE);

                        requestPermissions(new String[]{permission},PERMISSION_STORAGE_CODE);
                    }else {
                        startDownloading(url);
                    }
                }else {
                    startDownloading(url);
                }
            }
        });

        fullscreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fullscreen){
                    fullscreenButton.setImageDrawable(ContextCompat.getDrawable(Fullscreen.this,R.drawable.expand));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                    if (getSupportActionBar() !=null){
                        getSupportActionBar().show();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerView.getLayoutParams();
                    params.width = params.MATCH_PARENT;
                    params.height = (int) (200 * getApplicationContext().getResources().getDisplayMetrics().density);
                    playerView.setLayoutParams(params);
                    fullscreen=false;
                    textView.setVisibility(View.VISIBLE);
                    downloadbtn.setVisibility(View.VISIBLE);
                }else {
                    fullscreenButton.setImageDrawable(ContextCompat.getDrawable(Fullscreen.this,R.drawable.shrink));
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                     | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                    if (getSupportActionBar() !=null){
                        getSupportActionBar().hide();
                    }
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)playerView.getLayoutParams();
                    params.width=params.MATCH_PARENT;
                    params.height=params.MATCH_PARENT;
                    playerView.setLayoutParams(params);
                    fullscreen=true;
                    textView.setVisibility(View.INVISIBLE);
                    downloadbtn.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void startDownloading(String url) {

        DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download");
        request.setDescription("Downloading File");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,title+"mooviz");
        DownloadManager manager=(DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    private MediaSource buildMediaSource(Uri uri){
//        DataSource.Factory datasourcefactory=new DefaultHttpDataSourceFactory("video");
        DataSource.Factory datasourcefactory =
                new DefaultHttpDataSource.Factory().setUserAgent("video");
        return new ProgressiveMediaSource.Factory(datasourcefactory)
                .createMediaSource(MediaItem.fromUri(uri));
    }

    private void initalizeplayer(){
        SimpleExoPlayer simpleExoPlayer=new SimpleExoPlayer.Builder(Fullscreen.this).build();
        playerView.setPlayer(simpleExoPlayer);
        Uri uri=Uri.parse(url);
        MediaItem mediaItem=MediaItem.fromUri(uri);
        MediaSource mediaSource=buildMediaSource(uri);
        simpleExoPlayer.addMediaItems(Collections.singletonList(mediaItem));
        simpleExoPlayer.prepare();
        simpleExoPlayer.setPlayWhenReady(playwhenready);
        simpleExoPlayer.seekTo(currentWindow,playbackposition);
        simpleExoPlayer.prepare(mediaSource,false,false);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Util.SDK_INT>=26){
            initalizeplayer();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Util.SDK_INT>=26 || player==null){
//            initalizeplayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Util.SDK_INT>26){
            releasePlayer();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Util.SDK_INT>=26){
            releasePlayer();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        player.stop();
        releasePlayer();

        final Intent intent =new Intent();
        setResult(RESULT_OK,intent);
        finish();
    }

    private void releasePlayer(){
        if (player!=null){
            playwhenready=player.getPlayWhenReady();
            playbackposition=player.getCurrentPosition();
            currentWindow=player.getCurrentWindowIndex();
            player=null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){

            case PERMISSION_STORAGE_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownloading(url);
                }
                else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}