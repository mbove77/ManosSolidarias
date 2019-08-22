package com.bove.martin.manossolidarias.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bove.martin.manossolidarias.R;
import com.bove.martin.manossolidarias.activities.base.BaseActivity;
import com.bove.martin.manossolidarias.activities.utils.CirclePagerIndicatorDecoration;
import com.bove.martin.manossolidarias.activities.utils.DrawerUtil;
import com.bove.martin.manossolidarias.activities.utils.LinePagerIndicatorDecoration;
import com.bove.martin.manossolidarias.adapters.DonationAdapter;
import com.bove.martin.manossolidarias.adapters.NewsAdapter;
import com.bove.martin.manossolidarias.model.Donacion;
import com.bove.martin.manossolidarias.model.Noticia;
import com.ethanhua.skeleton.Skeleton;
import com.ethanhua.skeleton.SkeletonScreen;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kingfisher.easyviewindicator.RecyclerViewIndicator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeActivity extends BaseActivity implements NewsAdapter.OnItemClickListener {
    private final String TAG = "Home Activity";

    private ImageButton imageButtonOng;
    private ImageButton imageButtonDonation;
    private Boolean exitEnable = false;

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private SkeletonScreen skeletonScreen;

    private RecyclerViewIndicator recyclerViewIndicator;

    private List<Noticia> noticias = new ArrayList<Noticia>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Intent intentDonations = new Intent(this, DonationActivity.class);
        final Intent intentOngs = new Intent(this, OngListActivity.class);

        imageButtonOng = findViewById(R.id.imageButtonOngs);
        imageButtonDonation = findViewById(R.id.imageButtonDonations);

        // Toolbar
        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        // load NavDrawer
        DrawerUtil.getDrawer(this, myToolbar,1);

        imageButtonDonation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentDonations);
            }
        });

        // Instanciamos los elementos
        recyclerView = findViewById(R.id.recyclerViewNews);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        adapter = new NewsAdapter(noticias, R.layout.news_item, this, this);
        recyclerView.setAdapter(adapter);

        // Snap Helper
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);

        // RecyclerView Indicator
        recyclerViewIndicator = findViewById(R.id.circleIndicator);


        // Skeleton loader implementation
        skeletonScreen = Skeleton.bind(recyclerView)
                .adapter(adapter)
                .load(R.layout.news_item_skeleton)
                .color(R.color.skeletonShinColor)
                .show();

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(DB_NEWS)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Noticia noticia = document.toObject(Noticia.class);
                                noticias.add(noticia);
                            }
                            Collections.shuffle(noticias);
                            adapter.notifyDataSetChanged();
                            skeletonScreen.hide();
                            recyclerViewIndicator.setRecyclerView(recyclerView);
                        } else {
                            hideProgressDialog();
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        imageButtonOng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentOngs);
            }
        });
    }

    // Maneja la salida de al app
    @Override
    public void onBackPressed() {
        if(exitEnable) {
            this.moveTaskToBack(true);
        } else {
            Toast.makeText(this,  R.string.back_to_exit, Toast.LENGTH_SHORT).show();
            exitEnable = true;
        }

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                exitEnable = false;
            }
        }, 1500);
    }

    // Click de noticias
    @Override
    public void onItemClick(Noticia noticia, int posicion) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(noticia.getLink()));
        startActivity(i);
    }
}
