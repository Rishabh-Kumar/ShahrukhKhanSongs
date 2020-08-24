package com.hackdevelopers.shahrukhkhansongs;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MoreAppsActivity extends AppCompatActivity {

    private RecyclerView rvAllApps;
    private AppsAdapter appsAdapter;
    private FirebaseFirestore db;

    private ArrayList<App> apps = new ArrayList<>();
    private ArrayList<App> searched_apps = new ArrayList<>();
    String label, category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_apps);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        db = FirebaseFirestore.getInstance();

        category = getIntent().getStringExtra("category");
        label = getIntent().getStringExtra("Category" + " Apps");

        getSupportActionBar().setTitle(label);

        rvAllApps = findViewById(R.id.rv_all_apps);
        rvAllApps.setHasFixedSize(false);


        appsAdapter = new AppsAdapter(getApplicationContext(), apps, "grid", label);

        Query queryApps = db.collection("list")
                .whereEqualTo("type", category);
                //.orderBy("name");
        queryApps.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (queryDocumentSnapshots != null && queryDocumentSnapshots.size() > 0) {
                    for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {
                        App newApp = new App();
                        newApp.setIcon(snapshot.getString("icon"));
                        newApp.setName(snapshot.getString("name") + " Songs");
                        apps.add(newApp);
                    }
                    rvAllApps.setAdapter(appsAdapter);
                    appsAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MoreAppsActivity.this, "Unable to load Data", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_more_apps, menu);

        MenuItem search_item = menu.findItem(R.id.search_app);

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
                searched_apps.clear();

                String keyword = s.toLowerCase();

                //checking language arraylist for items containing search keyword

                for(int i =0 ;i < apps.size();i++){
                    if(apps.get(i).getName().toLowerCase().contains(keyword)){
                        searched_apps.add(apps.get(i));
                    }
                }

                appsAdapter = new AppsAdapter(getApplicationContext(), searched_apps, "grid", label);
                rvAllApps.setAdapter(appsAdapter);
                appsAdapter.notifyDataSetChanged();
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
