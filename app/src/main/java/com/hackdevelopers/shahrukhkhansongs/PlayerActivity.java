package com.hackdevelopers.shahrukhkhansongs;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

import static android.support.constraint.Constraints.TAG;
import static com.hackdevelopers.shahrukhkhansongs.AppConstants.YOUTUBE_API_KEY;

public class PlayerActivity extends YouTubeBaseActivity  implements
        YouTubePlayer.OnInitializedListener {


    private String id;
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer player;

    //private AdView mAdView;
    //private AdRequest adRequest;
    //private InterstitialAd mInterstitialAdOnCreate, mInterstitialAdOnBack;


    private RecyclerView rvCategories;
    private ArrayList<Category> categories;
    private CategoryAdapter adapter;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.black));
        }

        init();

        id = getIntent().getStringExtra("id");

        /*adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        mInterstitialAdOnCreate = new InterstitialAd(this);
        mInterstitialAdOnCreate.setAdUnitId("ca-app-pub-9325068791934346/3615085571");
        mInterstitialAdOnCreate.loadAd(new AdRequest.Builder().build());
        mInterstitialAdOnCreate.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mInterstitialAdOnCreate.isLoaded()) {
                    mInterstitialAdOnCreate.show();
                }
            }
        });

        mInterstitialAdOnBack = new InterstitialAd(this);
        mInterstitialAdOnBack.setAdUnitId("ca-app-pub-9325068791934346/3980436134");
        mInterstitialAdOnBack.loadAd(new AdRequest.Builder().build());*/


        loadSuggestions();
    }

    private void init() {

        youTubePlayerView =
                (YouTubePlayerView) findViewById(R.id.player);

        youTubePlayerView.initialize(YOUTUBE_API_KEY, this);
        //mAdView = findViewById(R.id.adView);

        rvCategories = findViewById(R.id.rv_categories);
        categories = new ArrayList<>();
        adapter = new CategoryAdapter(getApplicationContext(), categories);

        db = FirebaseFirestore.getInstance();
    }

    private void loadSuggestions() {

        categories.clear();
        Query queryCategories = db.collection("categories").orderBy("rank");
        queryCategories.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Category newCategory = new Category();
                    newCategory.setLabel(snapshot.getString("label"));
                    newCategory.setId(snapshot.getId());
                    categories.add(newCategory);
                }

                rvCategories.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {

            player.loadVideo(id);
            player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
            player.setShowFullscreenButton(false);
            this.player = player;
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            Toast.makeText(this, errorReason.toString(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(YOUTUBE_API_KEY, this);
        }
    }

    private YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.player);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            player.setFullscreen(false);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            player.setFullscreen(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        /*if (mInterstitialAdOnBack.isLoaded()) {
            mInterstitialAdOnBack.show();
            mInterstitialAdOnBack.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    finish();
                }
            });
        }else{
            super.onBackPressed();
        }*/

    }
}
