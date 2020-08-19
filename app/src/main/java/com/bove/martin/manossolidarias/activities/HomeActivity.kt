package com.bove.martin.manossolidarias.activities

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.*
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.activities.base.BaseActivity
import com.bove.martin.manossolidarias.activities.utils.DrawerUtil.getDrawer
import com.bove.martin.manossolidarias.activities.utils.goToActivity
import com.bove.martin.manossolidarias.adapters.NewsAdapter
import com.bove.martin.manossolidarias.adapters.OtherAppsAdapter
import com.bove.martin.manossolidarias.model.App
import com.bove.martin.manossolidarias.model.Noticia
import com.ethanhua.skeleton.Skeleton
import com.ethanhua.skeleton.SkeletonScreen
import com.google.firebase.firestore.FirebaseFirestore
import com.kingfisher.easyviewindicator.RecyclerViewIndicator
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*

class HomeActivity : BaseActivity(), NewsAdapter.OnItemClickListener, OtherAppsAdapter.OnItemClickListener {
    private val TAG = "Home Activity"
    private var exitEnable = false

    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsLayoutManager: RecyclerView.LayoutManager
    private lateinit var newsSkeletonScreen: SkeletonScreen
    private lateinit var newsRecyclerViewIndicator: RecyclerViewIndicator
    private val noticias: MutableList<Noticia> = ArrayList()

    private lateinit var appsAdapter: OtherAppsAdapter
    private lateinit var appsLayoutManager: RecyclerView.LayoutManager
    private lateinit var recyclerViewAppsIdicator: RecyclerViewIndicator
    private lateinit var appSkeletonScreen: SkeletonScreen
    private val apps: MutableList<App> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val myToolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(myToolbar)

        getDrawer(this, myToolbar, 1)

        newsRecyclerInit()

        apsRecyclerInit()

        // Access a Cloud Firestore instance from your Activity
        val db = FirebaseFirestore.getInstance()
        db.collection(DB_NEWS)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val noticia = document.toObject(Noticia::class.java)
                            noticias.add(noticia)
                        }
                        Collections.shuffle(noticias)
                        newsAdapter.notifyDataSetChanged()
                        newsSkeletonScreen.hide()
                        newsRecyclerViewIndicator.setRecyclerView(recyclerViewNews)
                    } else {
                        hideProgressDialog()
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }
        db.collection(DB_APPS)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result!!) {
                            val app = document.toObject(App::class.java)
                            apps.add(app)
                        }
                        appsAdapter.notifyDataSetChanged()
                        appSkeletonScreen.hide()
                        if (apps.size > 1) recyclerViewAppsIdicator.setRecyclerView(recyclerViewApps)
                    } else {
                        // hideProgressDialog();
                        Log.w(TAG, "Error getting documents.", task.exception)
                    }
                }

        imageButtonDonations.setOnClickListener(View.OnClickListener { goToActivity<DonationActivity>() })
        imageButtonOngs.setOnClickListener(View.OnClickListener { goToActivity<OngListActivity>() })
    }

    private fun newsRecyclerInit() {
        newsLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewNews.layoutManager = newsLayoutManager
        recyclerViewNews.itemAnimator = DefaultItemAnimator()
        newsAdapter = NewsAdapter(noticias, R.layout.news_item, this, this)
        recyclerViewNews.adapter = newsAdapter

        val snapHelper: SnapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerViewNews)

        newsRecyclerViewIndicator = findViewById(R.id.circleIndicator)

        newsSkeletonScreen = Skeleton.bind(recyclerViewNews)
                .adapter(newsAdapter)
                .load(R.layout.news_item_skeleton)
                .color(R.color.skeletonShinColor)
                .show()
    }

    private fun apsRecyclerInit() {
        appsLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewApps.layoutManager = appsLayoutManager
        recyclerViewApps.itemAnimator = DefaultItemAnimator()
        appsAdapter = OtherAppsAdapter(apps, this)
        recyclerViewApps.adapter = appsAdapter

        recyclerViewAppsIdicator = findViewById(R.id.circleIndicatorForApps)

        appSkeletonScreen = Skeleton.bind(recyclerViewApps)
                .adapter(appsAdapter)
                .load(R.layout.apps_item_skeleton)
                .color(R.color.skeletonShinColor)
                .show()
    }

    // Maneja la salida de al app
    override fun onBackPressed() {
        if (exitEnable) {
            moveTaskToBack(true)
        } else {
            Toast.makeText(this, R.string.back_to_exit, Toast.LENGTH_SHORT).show()
            exitEnable = true
        }
        val handler = Handler()
        handler.postDelayed({ exitEnable = false }, 1500)
    }

    // Click de noticias
    override fun onItemClick(noticia: Noticia?, posicion: Int) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(noticia!!.link)
        startActivity(i)
    }

    // Click en other Apps
    override fun onAppItemClick(app: App, posicion: Int) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(app.url)
        try {
            startActivity(i)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, getString(R.string.error_no_intent), Toast.LENGTH_SHORT).show()
        }
    }
}