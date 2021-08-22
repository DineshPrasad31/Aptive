package com.aptive.mediaplayer.ui

import android.os.Bundle
import com.aptive.mediaplayer.R
import com.aptive.mediaplayer.fragment.MediaFragment

class MediaPlayerActivity : BaseActivity() {

    private val mediaFragment = MediaFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initializeFragments()
    }

    private fun initializeFragments() {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.media_view, mediaFragment)
        fragmentTransaction.commit()

    }
}