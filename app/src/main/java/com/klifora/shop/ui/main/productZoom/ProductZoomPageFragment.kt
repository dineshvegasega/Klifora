package com.klifora.shop.ui.main.productZoom

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
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
import com.klifora.shop.databinding.ProductZoomPageBinding
import com.klifora.shop.networking.IMAGE_URL
import com.klifora.shop.utils.glidePhotoView
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.UnstableApi

@androidx.media3.common.util.UnstableApi
@AndroidEntryPoint
class ProductZoomPageFragment(
    private val activity: FragmentActivity,
    private val videoPath: String,
    position: Int
) : Fragment() {

    private val TAG = "ProductZoomPageFragment"

    private var _binding: ProductZoomPageBinding? = null
    private val binding get() = _binding!!

    private val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

    private var simpleExoPlayer: ExoPlayer?= null

    private val positionOfFragment = position


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProductZoomPageBinding.inflate(inflater)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        _binding = FragmentExoPlayerBinding.bind(view)
        Log.d(TAG, "onViewCreated: Fragment Position : $positionOfFragment")

        (IMAGE_URL +videoPath).glidePhotoView(binding.ivIcon.context, binding.ivIcon)
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun initializePlayer() {
        simpleExoPlayer = ExoPlayer.Builder(activity)
            .build()
        val mediaSource = getProgressiveMediaSource(videoPath)
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

//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//        simpleExoPlayer?.release()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        Log.d(TAG, "onResume: $positionOfFragment")
////        if (binding != null) {
////            binding!!.playerView.player!!.playWhenReady =
////                !binding!!.playerView.player!!.isPlaying
////        }
//
//        if(videoPath.endsWith("jpg") || videoPath.endsWith("jpeg") || videoPath.endsWith("png")){
//            binding.ivIcon.visibility = View.VISIBLE
//            binding.playerView.visibility = View.GONE
//            videoPath.glideImage(binding.ivIcon.context, binding.ivIcon)
//            // binding.ivIcon.loadImage(type = 1, url = { videoPath })
//
////            binding.root.setOnClickListener {
////                it.findNavController().navigate(R.id.productZoom)
////            }
//
//            binding.ivIcon.singleClick {
//                // images.imageZoom(itemRowBinding.ivIcon, 2, counter)
////                findNavController().navigate(R.id.productZoom)
//                ProductDetail.callBackListener!!.onCallBack(0)
//            }
//        } else if(videoPath.endsWith("mp4")) {
//            binding.ivIcon.visibility = View.GONE
//            binding.playerView.visibility = View.VISIBLE
//            initializePlayer()
//
//        }
//
//
//    }
//
//    override fun onPause() {
//        super.onPause()
//        Log.d(TAG, "onPause: $positionOfFragment")
////        if (binding != null) {
////            binding!!.playerView.player!!.playWhenReady =
////                !binding!!.playerView.player!!.isPlaying
////        }
////        simpleExoPlayer.release()
//
//        if(videoPath.endsWith("mp4")) {
//            Handler(Looper.getMainLooper()).postDelayed({
//                binding!!.playerView.player!!.stop()
//            }, 100)
//        }
//
//
//
//    }

}