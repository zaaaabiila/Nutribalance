package com.dicoding.appcapstone.ui.detail

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import coil.load
import coil.transform.RoundedCornersTransformation
import com.dicoding.appcapstone.R
import com.dicoding.appcapstone.response.ResultsItem
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.util.Log

class DetailFragment : Fragment() {

    private lateinit var viewHolder: DetailArticleViewHolder
    private var player: SimpleExoPlayer? = null

    companion object {
        fun newInstance(resultsItem: ResultsItem) = DetailFragment().apply {
            arguments = Bundle().apply {
                putParcelable("ResultsItem", resultsItem)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_detail, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewHolder = DetailArticleViewHolder(view)

        // Retrieve the ResultsItem from the arguments
        val resultsItem = arguments?.getParcelable<ResultsItem>("ResultsItem")

        // Use the data in resultsItem to populate the views
        viewHolder.thumbnailImageView.load(resultsItem?.thumbnailUrl) {
            crossfade(true)
            transformations(RoundedCornersTransformation(10f))
        }
        viewHolder.titleTextView.text = resultsItem?.name
        viewHolder.descriptionTextView.text = resultsItem?.description

        // Assuming Nutrition is a data class with properties for each nutrition fact
        val nutrition = resultsItem?.nutrition
        viewHolder.nutritionTextView.text = """
            Nutrition Facts:
            Carbohydrates: ${nutrition?.carbohydrates} g
            Fiber: ${nutrition?.fiber} g
            Protein: ${nutrition?.protein} g
            Fat: ${nutrition?.fat} g
            Calories: ${nutrition?.calories} C
            Sugar: ${nutrition?.sugar} g
        """.trimIndent()

        // Initialize the player
        player = SimpleExoPlayer.Builder(requireContext()).build()
        viewHolder.playerView.player = player

        // Set the thumbnail as the placeholder of the PlayerView
        val thumbnail = ImageView(requireContext())
        thumbnail.load(resultsItem?.thumbnailUrl) {
            crossfade(true)
            transformations(RoundedCornersTransformation(10f))
        }
        viewHolder.playerView.setShutterBackgroundColor(Color.TRANSPARENT)
        viewHolder.playerView.defaultArtwork = thumbnail.drawable

        // Add a Player.Listener to handle errors and loading states
        player?.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                Log.e("ExoPlayer", "Error occurred when loading media.", error)
                Toast.makeText(requireActivity(), "Failed to load media.", Toast.LENGTH_SHORT).show()
            }

            override fun onPlaybackStateChanged(state: Int) {
                super.onPlaybackStateChanged(state)
                when (state) {
                    Player.STATE_BUFFERING -> {
                        // Show loading indicator
                        viewHolder.progressBar.visibility = View.VISIBLE
                    }
                    Player.STATE_READY -> {
                        // Hide loading indicator
                        viewHolder.progressBar.visibility = View.GONE
                    }
                }
            }
        })

        viewHolder.playerView.setOnClickListener {
            // Prepare the player with the video URL only when the play button is clicked
            val mediaItem = MediaItem.fromUri(resultsItem?.originalVideoUrl!!)
            player?.setMediaItem(mediaItem)
            player?.prepare()
        }
    }

    override fun onStart() {
        super.onStart()
        // In case the activity was paused, this will restart the player
        player?.playWhenReady = false
    }

    override fun onResume() {
        super.onResume()
        // In case the activity was paused, this will restart the player
        player?.playWhenReady = false
    }

    override fun onPause() {
        super.onPause()
        // Pause the player when the activity is paused
        player?.pause()
    }

    override fun onStop() {
        super.onStop()
        // Pause the player when the activity is stopped
        player?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Release the player when the fragment's view is destroyed
        player?.release()
        player = null
    }
}
