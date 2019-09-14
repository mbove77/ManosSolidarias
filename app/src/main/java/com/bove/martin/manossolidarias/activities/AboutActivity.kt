package com.bove.martin.manossolidarias.activities

import android.os.Bundle

import mehdi.sakout.aboutpage.AboutPage
import com.bove.martin.manossolidarias.R
import com.bove.martin.manossolidarias.activities.base.BaseActivity

import mehdi.sakout.aboutpage.Element

// TODO cambiar el usuario de gitHub cuando suba al repo la app.
class AboutActivity : BaseActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val appVersion: String = packageManager.getPackageInfo(packageName, 0).versionName

        val aboutPage = AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.ic_logo_vert)
                .setDescription(getString(R.string.aboutDesc))
                .addItem(Element().setTitle("Versión $appVersion"))
                .addPlayStore(getString(R.string.playStoreId))
                .addFacebook(getString(R.string.fbPageId))
                .addGitHub(getString(R.string.gitHubId))
                .addWebsite(getString(R.string.appUrl))
                .addEmail(getString(R.string.emailAddr))
                .create()

        setContentView(aboutPage)
    }

}
