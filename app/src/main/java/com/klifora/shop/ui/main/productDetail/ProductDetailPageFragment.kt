package com.klifora.shop.ui.main.productDetail

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import com.klifora.shop.databinding.FragmentExoPlayerBinding
import com.klifora.shop.models.products.MediaGalleryEntry
import com.klifora.shop.networking.IMAGE_URL
import com.klifora.shop.utils.glideImageWithoutPlace
import com.klifora.shop.utils.singleClick
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.UnstableApi


@androidx.media3.common.util.UnstableApi
@AndroidEntryPoint
class ProductDetailPageFragment(
    private val activity: FragmentActivity,
    private val videoPath: MediaGalleryEntry,
    position: Int
) : Fragment() {

    private val TAG = "ExoPlayerFragment"

    private var _binding: FragmentExoPlayerBinding? = null
    private val binding get() = _binding!!

    private val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

    private var simpleExoPlayer: ExoPlayer ?= null

    private val positionOfFragment = position


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExoPlayerBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        _binding = FragmentExoPlayerBinding.bind(view)
        Log.d(TAG, "onViewCreated: Fragment Position : $positionOfFragment")
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun initializePlayer() {
        simpleExoPlayer = ExoPlayer.Builder(activity)
            .build()
        val mediaSource = getProgressiveMediaSource(videoPath.file)
        simpleExoPlayer!!.setMediaSource(mediaSource)
        simpleExoPlayer!!.prepare()

        binding!!.playerView.useController = false
        binding!!.playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM)
        binding!!.playerView.setRepeatToggleModes(Player.REPEAT_MODE_OFF)
        binding!!.playerView.player = simpleExoPlayer

        binding!!.playerView.player!!.playWhenReady =
            !binding!!.playerView.player!!.isPlaying

    }



    @OptIn(UnstableApi::class)
    private fun getProgressiveMediaSource(mediaUrl : String): MediaSource {
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(mediaUrl)))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        simpleExoPlayer?.release()
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: $positionOfFragment")
        if(videoPath.media_type.endsWith("image")){
            binding.ivIcon.visibility = View.VISIBLE
            binding.playerView.visibility = View.GONE
            (IMAGE_URL+videoPath.file).glideImageWithoutPlace(binding.ivIcon.context, binding.ivIcon)
            binding.ivIcon.singleClick {
                ProductDetail.callBackListener!!.onCallBack(0)
            }

            Log.e(TAG, "onResumeAA: "+(IMAGE_URL+videoPath.file))
        } else if(videoPath.file.endsWith(".mp4")) {
            binding.ivIcon.visibility = View.GONE
            binding.playerView.visibility = View.VISIBLE
            initializePlayer()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: $positionOfFragment")
//        if (binding != null) {
//            binding!!.playerView.player!!.playWhenReady =
//                !binding!!.playerView.player!!.isPlaying
//        }
//        simpleExoPlayer.release()

        if(videoPath.file.endsWith("mp4")) {
            Handler(Looper.getMainLooper()).postDelayed({
                binding!!.playerView.player!!.stop()
            }, 100)
        }
    }
}