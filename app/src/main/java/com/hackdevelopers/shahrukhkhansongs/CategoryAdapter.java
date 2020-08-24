package com.hackdevelopers.shahrukhkhansongs;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VHCategory> {

    private Context context;
    public ArrayList<Category> categories;
    private FirebaseFirestore db;

    public CategoryAdapter(Context context, ArrayList<Category> categories) {
        this.context = context;
        this.categories = categories;
    }

    @NonNull
    @Override
    public VHCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_category, parent, false);
        db = FirebaseFirestore.getInstance();

        return new VHCategory(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VHCategory holder, final int position) {
        holder.setLabel(categories.get(position).getLabel());
        holder.setRvSuggestions(categories.get(position).getId(), categories.get(position).getLabel());
        holder.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MoreAppsActivity.class);
                intent.putExtra("category", categories.get(position).getId());
                intent.putExtra("Category", categories.get(position).getLabel());
                holder.itemView.getContext().startActivity(intent);
                Answers.getInstance().logCustom(new CustomEvent("Clicked More")
                .putCustomAttribute("Category", categories.get(position).getLabel()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class VHCategory extends RecyclerView.ViewHolder {

        private TextView label, more;
        private RecyclerView rvSuggestions;
        private AppsAdapter appsAdapter;

        public VHCategory(View itemView) {
            super(itemView);

            label = itemView.findViewById(R.id.label);
            more = itemView.findViewById(R.id.more);
            rvSuggestions = itemView.findViewById(R.id.rv_suggestions);
        }

        public void setLabel(String label_s) {
            label.setText(label_s);
        }

        public void setRvSuggestions(String query, final String category) {
            final ArrayList<App> apps = new ArrayList<>();
            appsAdapter = new AppsAdapter(context, apps, "horizontal", category);

            Log.d("CHECK", "setRvSuggestions: "+category+query);

            Query queryApps = db.collection("list")
                    .whereEqualTo("type",query);


            queryApps.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                    if (queryDocumentSnapshots !=null && queryDocumentSnapshots.size() > 0 ) {
                        for (QueryDocumentSnapshot snapshot : queryDocumentSnapshots) {

                            App newApp = new App();
                            newApp.setIcon(snapshot.getString("icon"));
                            newApp.setName(snapshot.getString("name") + " Songs");
							if (snapshot.getString("package_name").equals(context.getPackageName())) {
                                continue;
                            }
                            newApp.setPackageName(snapshot.getString("package_name"));
                            apps.add(newApp);
                        }
                        if (apps.size() >= 10) {
                            more.setVisibility(View.VISIBLE);
                        }
                        rvSuggestions.setAdapter(appsAdapter);
                        appsAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }
}
