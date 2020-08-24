package com.hackdevelopers.shahrukhkhansongs;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.InviteEvent;
import com.crashlytics.android.answers.RatingEvent;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;

import javax.annotation.Nullable;

import static com.hackdevelopers.shahrukhkhansongs.AppConstants.APP_NAME_ID;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    private FirebaseFirestore db;
    private SongsAdapter adapter;
    private LinearLayoutManager layoutManager;
    private ArrayList<Song> songs = new ArrayList<>();
    private ArrayList<Song> searched_songs = new ArrayList<>();
    private FloatingActionButton requestSong;

    //private InterstitialAd mInterstitialAd;

    private RecyclerView songsList;
    private ImageView coverImage;
    private View navHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().logCustom(new CustomEvent("Open Drawer"));
            }
        });
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        coverImage = navHeader.findViewById(R.id.imageView);

        init();
        loadSongs();
        // load nav menu header data
        loadNavHeader();

        /*MobileAds.initialize(this, "ca-app-pub-9325068791934346~6665696842");

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-9325068791934346/1692571768");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                }
            }
        });*/

        requestSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = "Feature for " + getResources().getString(R.string.app_name) + " app";
                String text = "Thanks for reaching out us. Request us a Song in this app or Feature You Want. We will add the song with in 24 hrs and feature on majority demand.";
                sendEmail(subject, text);
                Answers.getInstance().logCustom(new CustomEvent("Feature/Song Request"));
            }
        });
    }

    private void loadNavHeader() {
        db.collection("list").document(APP_NAME_ID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                Glide.with(getApplicationContext()).load(documentSnapshot.getString("cover")).into(coverImage);
            }
        });
    }

    private void init() {
        songsList = findViewById(R.id.songs_list);
        layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
        songsList.setLayoutManager(layoutManager);
        db = FirebaseFirestore.getInstance();
        adapter = new SongsAdapter(songs, getApplicationContext());
        requestSong = findViewById(R.id.request_song);

    }

    private void loadSongs() {
        songs.clear();
        Query query = db.collection(APP_NAME_ID).orderBy("rank", Query.Direction.DESCENDING);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                    Song newSong = new Song();
                    newSong.setName(snapshot.getString("name"));
                    String id = snapshot.getString("id");
                    String thumbnail = "http://img.youtube.com/vi/"+id+"/0.jpg";
                    newSong.setThumbnail(thumbnail);
                    newSong.setId(id);
                    newSong.setYear(snapshot.getString("year"));
                    newSong.setKey(snapshot.getId());
                    songs.add(newSong);
                }
                songsList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem search_item = menu.findItem(R.id.search_song);

        SearchView searchView = (SearchView) search_item.getActionView();
        searchView.setFocusable(false);
        searchView.setQueryHint("Search");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {


            @Override
            public boolean onQueryTextSubmit(String s) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //clear the previous data in search arraylist if exist
                searched_songs.clear();

                String keyword = s.toLowerCase();

                //checking language arraylist for items containing search keyword

                for(int i =0 ;i < songs.size();i++){
                    if(songs.get(i).getName().toLowerCase().contains(keyword)){
                        searched_songs.add(songs.get(i));
                    }
                }

                adapter = new SongsAdapter(searched_songs, MainActivity.this);
                songsList.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_rate_us) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName())));
            Answers.getInstance().logRating(new RatingEvent()
            .putContentName("Rate From Menu"));
            return true;
        } else if (id == R.id.action_feedback) {
            String subject = "Feedback for " + getResources().getString(R.string.app_name) + " app";
            String text = "Thanks for your valueable time. Please tell us what can we add or improve.";
            sendEmail(subject, text);
            Answers.getInstance().logCustom(new CustomEvent("Feedback From Menu"));
        } else if (id == R.id.action_request_singer) {
            String subject = "Request New Singer App";
            String text = "You can request any Singer. We will try to make it within two days Thanks!";
            sendEmail(subject, text);
            Answers.getInstance().logCustom(new CustomEvent("Requested Singer From Menu"));
        }else if (id == R.id.action_request_movie) {
            String subject = "Request New Movie App";
            String text = "You can request any Movie. We will try to make it within two days Thanks!";
            sendEmail(subject, text);
            Answers.getInstance().logCustom(new CustomEvent("Requested Movie From Menu"));
        }else if (id == R.id.action_request_artist) {
            String subject = "Request New Artist App";
            String text = "You can request any Artist. We will try to make it within two days Thanks!";
            sendEmail(subject, text);
            Answers.getInstance().logCustom(new CustomEvent("Requested Artist From Menu"));
        } else if (id == R.id.nav_share) {
            shareApp();
            Answers.getInstance().logInvite(new InviteEvent()
                    .putMethod("Main Menu Invite"));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.action_request_artist) {
            String subject = "Request New Artist App";
            String text = "You can request any Artist. We will try to make it within two days Thanks!";
            sendEmail(subject, text);
            Answers.getInstance().logCustom(new CustomEvent("Requested Artist From Navigation"));
        } else if (id == R.id.action_request_movie) {
            String subject = "Request New Movie App";
            String text = "You can request any Movie. We will try to make it within two days Thanks!";
            sendEmail(subject, text);
            Answers.getInstance().logCustom(new CustomEvent("Requested Movie From Navigation"));
        } else if (id == R.id.action_request_singer) {
            String subject = "Request New Singer App";
            String text = "You can request any Singer. We will try to make it within two days Thanks!";
            sendEmail(subject, text);
            Answers.getInstance().logCustom(new CustomEvent("Requested Singer From Navigation"));
        } else if (id == R.id.nav_rate) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName())));
            Answers.getInstance().logRating(new RatingEvent()
            .putContentName("Rating From Navigation"));
        } else if (id == R.id.nav_more) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=4685443282977689431")));
            Answers.getInstance().logCustom(new CustomEvent("Opened Developer Page"));
        } else if (id == R.id.nav_send) {
            String subject = "Feedback for " + getResources().getString(R.string.app_name) + " app";
            String text = "Thanks for your valueable time. Please tell us what can we add or improve.";
            sendEmail(subject, text);
            Answers.getInstance().logCustom(new CustomEvent("Feedback From Navigation"));
        }
        else if (id == R.id.nav_share) {
            shareApp();
            Answers.getInstance().logInvite(new InviteEvent()
                    .putMethod("Navigation Drawer Share"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void shareApp() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            String sAux =  getResources().getString(R.string.share_app);
            sAux = sAux + "https://play.google.com/store/apps/details?id="+getApplicationContext().getPackageName();
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        } catch(Exception e) {
            //e.toString();
        }
    }

    protected void sendEmail(String subject, String text) {
        Log.i("Send email", "");

        String[] TO = {"hackdeveloper1027@gmail.com"};
        //String[] CC = {"xyz@gmail.com"};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");


        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        //emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
            Log.i("Finished sending ...", "");
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this,
                    "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }
}
