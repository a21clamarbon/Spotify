package com.example.spotify
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var songListView: ListView
    private lateinit var songs: Array<String>
    private lateinit var seekBar: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        songListView = findViewById(R.id.songListView)
        seekBar = findViewById(R.id.seekBar)

        songs = resources.getStringArray(R.array.songs)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, songs)
        songListView.adapter = adapter

        mediaPlayer = MediaPlayer()

        val playButton: Button = findViewById(R.id.playButton)
        val pauseButton: Button = findViewById(R.id.pauseButton)
        val stopButton: Button = findViewById(R.id.stopButton)

        playButton.setOnClickListener {
            if (!mediaPlayer.isPlaying) {
                mediaPlayer.start()
            }
        }

        pauseButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
            }
        }

        stopButton.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.stop()
                mediaPlayer.prepareAsync()
            }
        }

        songListView.setOnItemClickListener { _, _, position, _ ->
            val songId = resources.getIdentifier(songs[position], "raw", packageName)
            playSong(songId)
        }

        mediaPlayer.setOnPreparedListener {
            seekBar.setMax(mediaPlayer.getDuration()/1000);

         //seekBar.max = mediaPlayer.duration
            Thread {
                while (mediaPlayer != null && mediaPlayer.isPlaying) {
                    val currentPosition = mediaPlayer.currentPosition
                    runOnUiThread {
                        seekBar.progress = currentPosition
                    }
                    Thread.sleep(1000)
                }
            }.start()
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer.seekTo(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}

        })
    }

    private fun playSong(songId: Int) {
        stopSong()
        mediaPlayer = MediaPlayer.create(this, songId)
        mediaPlayer.start()
    }

    private fun stopSong() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.release()
            mediaPlayer = MediaPlayer()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}
