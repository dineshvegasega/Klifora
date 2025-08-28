package com.klifora.shop.ui.main.productDetail

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.OptIn
import androidx.databinding.DataBindingUtil
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_READY
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.recyclerview.widget.RecyclerView
import com.klifora.shop.R
import com.klifora.shop.databinding.ItemLoadingBinding
import com.klifora.shop.BR
import com.klifora.shop.databinding.ItemProductDetailImageBinding
import com.klifora.shop.utils.imageZoom
import com.klifora.shop.utils.loadImage
import com.klifora.shop.utils.singleClick

class ProductDetailAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var counter = 0

    var itemModels: ArrayList<String> = ArrayList()

    private var mediaUrl = ""
    private val mediaUrlHls = "http://sample.vodobox.net/skate_phantom_flex_4k/skate_phantom_flex_4k.m3u8"

    private var player: ExoPlayer?= null



    // Create a data source factory.
    private val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()

//    private var player: SimpleExoPlayer? = null
//    private val isPlaying get() = player?.isPlaying ?: false


    private val IMAGE: Int = 0
//    private val VIDEO: Int = 1
    private val LOADING: Int = 1

    private var isLoadingAdded: Boolean = false
    private var retryPageLoad: Boolean = false

    private var errorMsg: String? = ""


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return  if(viewType == IMAGE){
            val binding: ItemProductDetailImageBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_product_detail_image, parent, false)
            ImageVH(binding)
//        } else if(viewType == VIDEO){
//            val binding: ItemProductDetailVideoBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_product_detail_video, parent, false)
//            VideoVH(binding)
        } else{
            val binding: ItemLoadingBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_loading, parent, false)
            LoadingVH(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = itemModels[position]


        if(getItemViewType(position) == IMAGE){
            val myOrderVH: ImageVH = holder as ImageVH
            myOrderVH.bind(model, position)
//        } else if(getItemViewType(position) == VIDEO){
//            val myOrderVH: VideoVH = holder as VideoVH
//            myOrderVH.bind(model, position)
        } else {
            val loadingVH: LoadingVH = holder as LoadingVH
            if (retryPageLoad) {
                loadingVH.itemRowBinding.loadmoreProgress.visibility = View.GONE
            } else {
                loadingVH.itemRowBinding.loadmoreProgress.visibility = View.VISIBLE
            }
        }
    }

    override fun getItemCount(): Int {
        return if (itemModels.size > 0) itemModels.size else 0
    }

    override fun getItemViewType(position: Int): Int {
        Log.e("TAG", "getItemViewType " + position)
        return if(position == 0){
            IMAGE
//        }else if (position == 1) {
//            VIDEO
        } else {
            if (position == itemModels.size - 1 && isLoadingAdded) {
                LOADING
            } else {
//                if(position == 0){
//                    IMAGE
//                }else if (position == 1) {
//                    VIDEO
//                } else {
                    IMAGE
//                }
            }
        }
    }


    inner class ImageVH(binding: ItemProductDetailImageBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemRowBinding: ItemProductDetailImageBinding = binding

        @SuppressLint("NotifyDataSetChanged", "SetTextI18n", "ClickableViewAccessibility")
        fun bind(obj: Any?, position: Int) {
            itemRowBinding.setVariable(BR._all, obj)
            itemRowBinding.executePendingBindings()
            val model = obj as String


//            itemRowBinding.root.setOnClickListener {
//                it.findNavController().navigate(R.id.action_home_to_products)
//            }
            val images: ArrayList<String> = ArrayList()
            itemModels.filter {
                it.endsWith("jpg") || it.endsWith("jpeg") || it.endsWith("png")
            }.map {
                images.add(it)
            }
            if(model.endsWith("jpg") || model.endsWith("jpeg") || model.endsWith("png")){
                itemRowBinding.ivIcon.visibility = View.VISIBLE
                itemRowBinding.playerView.visibility = View.GONE

                itemRowBinding.ivIcon.loadImage(type = 1, url = { model })

                itemRowBinding.ivIcon.singleClick {
                    images.imageZoom(itemRowBinding.ivIcon, 2, counter)
                }
            } else if(model.endsWith("mp4")){
                itemRowBinding.ivIcon.visibility = View.GONE
                itemRowBinding.playerView.visibility = View.VISIBLE

                mediaUrl = model

//                if (player?.isPlaying == true) {
//                    stop()
//                }

                if (counter == position) {
                    initPlayer(itemRowBinding)
                    Log.e("TAG", "" + counter + " ::::::: " + position)

                }



//                Handler(Looper.getMainLooper()).postDelayed({
//                    if (player?.isPlaying == true) {
//                        restartPlayer()
//                    }
//                    play()
//                }, 50)
            }



        }


    }




    @OptIn(UnstableApi::class)
    private fun initPlayer(itemRowBinding: ItemProductDetailImageBinding) {
        player = ExoPlayer.Builder(itemRowBinding.root.context)
            .build()


        player?.apply {

                val source = if (mediaUrl.contains("m3u8"))
                    getHlsMediaSource()
                else
                    getProgressiveMediaSource()

                setMediaSource(source)
                prepare()
                addListener(object: Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        super.onPlaybackStateChanged(playbackState)
                        when(playbackState){
                            STATE_ENDED -> restartPlayer()
                            STATE_READY -> {
                                itemRowBinding.playerView.player = player
                                play()
                            }

                            Player.STATE_BUFFERING -> {

                            }

                            Player.STATE_IDLE -> {
//
                            }
                        }
                    }
                })


            }



//        binding.playerView.player.setShowPreviousButton(false);
    }


    @OptIn(UnstableApi::class)
    private fun getHlsMediaSource(): MediaSource {
        // Create a HLS media source pointing to a playlist uri.
        return HlsMediaSource.Factory(dataSourceFactory).
        createMediaSource(MediaItem.fromUri(mediaUrl))
    }

    @OptIn(UnstableApi::class)
    private fun getProgressiveMediaSource(): MediaSource {
        // Create a Regular media source pointing to a playlist uri.
        return ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri(Uri.parse(mediaUrl)))
    }

    private fun releasePlayer(){
        player?.apply {
            playWhenReady = false
            release()
        }
        player = null
    }

    private fun pause(){
        player?.playWhenReady = false
    }

    private fun stop(){
        player?.stop()
    }

    private fun play(){
        player?.playWhenReady = true
    }

    private fun restartPlayer(){
        player?.seekTo(0)
        player?.playWhenReady = true
    }



//
//    inner class VideoVH(binding: ItemProductDetailVideoBinding) : RecyclerView.ViewHolder(binding.root) {
//        var itemRowBinding: ItemProductDetailVideoBinding = binding
//
//        @SuppressLint("NotifyDataSetChanged", "SetTextI18n", "ClickableViewAccessibility")
//        fun bind(obj: Any?, position: Int) {
//            itemRowBinding.setVariable(BR._all, obj)
//            itemRowBinding.executePendingBindings()
//            val model = obj as String
//
//
//            itemRowBinding.root.setOnClickListener {
//                it.findNavController().navigate(R.id.action_home_to_products)
//            }
//
//        }
//
//
//    }
//


    inner class LoadingVH(binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root) {
        var itemRowBinding: ItemLoadingBinding = binding
    }

    fun showRetry(show: Boolean, errorMsg: String) {
        retryPageLoad = show
        notifyItemChanged(itemModels.size - 1)
        this.errorMsg = errorMsg
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addAllSearch(movies: MutableList<String>) {
        itemModels.clear()
        itemModels.addAll(movies)
//        for(movie in movies){
//            add(movie)
//        }
        notifyDataSetChanged()
    }

    fun addAll(movies: MutableList<String>) {
        for(movie in movies){
            add(movie)
        }
    }

    fun add(moive: String) {
        itemModels.add(moive)
        notifyItemInserted(itemModels.size - 1)
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
//        add(ItemLiveScheme())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false

//        val position: Int =itemModels.size -1
//        val movie: ItemLiveScheme = itemModels[position]
//
//        if(movie != null){
//            itemModels.removeAt(position)
//            notifyItemRemoved(position)
//        }
    }


    fun submitData(itemMainArray: ArrayList<String>) {
        itemModels = itemMainArray
    }


    @SuppressLint("NotifyDataSetChanged")
    fun updatePosition(position: Int) {
        counter = position
        Log.e("TAG", "updatePosition " + position)

//        Handler(Looper.getMainLooper()).postDelayed({
//            stop()
//            releasePlayer()
//        }, 50)


//        if(itemModels[counter].endsWith("mp4")){
//            Handler(Looper.getMainLooper()).postDelayed({
//                if (player?.isPlaying == true) {
//                    restartPlayer()
//                }
//                play()
//            }, 50)
//        }






//        notifyItemChanged(position)

    }



}