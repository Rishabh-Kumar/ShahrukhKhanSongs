package com.hackdevelopers.shahrukhkhansongs;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;

import java.util.ArrayList;

public class AppsAdapter extends RecyclerView.Adapter<AppsAdapter.VHApps> {

    private Context context;
    private ArrayList<App> appsList;
    private String listType;
    private String category;

    public AppsAdapter(Context context, ArrayList<App> appsList, String listType, String category) {
        this.context = context;
        this.appsList = appsList;
        this.listType = listType;
        this.category = category;
    }

    @NonNull
    @Override
    public VHApps onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (listType.equals("horizontal")) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_apps, parent, false);
        } else if (listType.equals("grid")) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_apps_grid, parent, false);
        }
        return new VHApps(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final VHApps holder, final int position) {
        holder.setIcon(appsList.get(position).getIcon());
        holder.setName(appsList.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Use package name which we want to check
                boolean isAppInstalled = appInstalledOrNot(appsList.get(position).getPackageName());

                if(isAppInstalled) {
                    //This intent will help you to launch if the package is already installed
                    Intent LaunchIntent = context.getPackageManager()
                            .getLaunchIntentForPackage(appsList.get(position).getPackageName());
                    holder.itemView.getContext().startActivity(LaunchIntent);

                } else {
                    try {
                        holder.itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appsList.get(position).getPackageName())));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        holder.itemView.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appsList.get(position).getPackageName())));
                    }
                    Answers.getInstance().logCustom(new CustomEvent("Clicked Suggestion")
                            .putCustomAttribute("App Name", appsList.get(position).getName())
                            .putCustomAttribute("Category", category));
                }
            }
        });
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    @Override
    public int getItemCount() {
        return appsList.size();
    }

    class VHApps extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView name;

        public VHApps(View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
        }

        public void setIcon(String icon_s) {
            Glide.with(context).load(icon_s).into(icon);
        }

        public void setName(String name_s) {
            name.setText(name_s);
        }
    }
}
