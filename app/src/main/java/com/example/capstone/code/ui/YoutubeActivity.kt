package com.example.capstone.code.ui

//import android.os.Bundle
//import com.example.capstone.R
//import com.google.android.youtube.player.YouTubeBaseActivity
//import com.google.android.youtube.player.YouTubePlayer
//import com.google.android.youtube.player.YouTubePlayerFragment
//
//class YoutubeActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_playback)
//    }
//
//    private fun initYoutube() {
//        (youtube_fragment as YouTubePlayerFragment).initialize(
//            getString(R.string.google_api_key),
//            this
//        )
//    }
//    override fun onInitializationSuccess(
//        provider: YouTubePlayer.Provider,
//        player: YouTubePlayer,
//        wasRestored: Boolean
//    ) {
//        this.youtubePlayer = player
//        loadDoTheDogAvc()
//    }
//    override fun onInitializationFailure(
//        provider: YouTubePlayer.Provider,
//        error: YouTubeInitializationResult
//    ) {
//    }
//}