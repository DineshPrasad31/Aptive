package com.aptive.mediaplayer.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.aptive.mediaplayer.R
import com.aptive.mediaplayer.databinding.MediaFragmentBinding
import com.aptive.mediaplayer.viewmodel.MediaViewModel
import kotlinx.android.synthetic.main.media_fragment.view.*


class MediaFragment : BaseFragment<MediaViewModel>(), MediaViewModel.ServiceConnected {

    private lateinit var layoutView: View
    private lateinit var mediaFragmentBinding: MediaFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        mediaFragmentBinding =
            DataBindingUtil.inflate(inflater, R.layout.media_fragment, container, false)
        mediaFragmentBinding.viewmodel = viewModel
        mediaFragmentBinding.lifecycleOwner = this
        layoutView = mediaFragmentBinding.root

        return mediaFragmentBinding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initialize(this)
        onClick()
        mediaFragmentBinding.seekBar.setOnTouchListener(OnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                viewModel.clearDisposal()
                mediaFragmentBinding.seekBar.setProgress(mediaFragmentBinding.seekBar.getProgress())
                return@OnTouchListener false
            } else if (event.action == MotionEvent.ACTION_UP) {
                viewModel.setSeekPosition(mediaFragmentBinding.seekBar.progress)
                viewModel.updateMediaSeekBar()
                return@OnTouchListener false
            }
            true
        })
    }


    override fun getViewModel(): Class<MediaViewModel> {
        return MediaViewModel::class.java
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearDisposal()
        viewModel.stopService()
    }

    override fun isSerViceConnected() {
        viewModel.getSeekBarValue().observe(viewLifecycleOwner, Observer {
            mediaFragmentBinding.seekBarData = it
        })

        viewModel.getInitialData().observe(viewLifecycleOwner, Observer {
            mediaFragmentBinding.initialData = it
        })

        viewModel.getPlayPauseData().observe(viewLifecycleOwner, Observer {
            mediaFragmentBinding.playPause = it
        })

        viewModel.getSongInfo().observe(viewLifecycleOwner, Observer {
            mediaFragmentBinding.songInfo = it
        })

    }

    private fun onClick() {
        layoutView.play_pause_check.setOnClickListener {
            viewModel.playPauseMedia()
        }
        layoutView.play_next_img.setOnClickListener {
            viewModel.forwardSong()
        }

        layoutView.play_previous_img.setOnClickListener {
            viewModel.backwardSong()
        }
    }

}

