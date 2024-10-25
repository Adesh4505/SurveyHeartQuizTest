package com.example.surveyheartquiztest

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import com.example.surveyheartquiztest.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.app_base)
        }

        preferenceManager = PreferenceManager(this@ResultActivity)

        val score = intent.getIntExtra("score", 0)
        val highScore = preferenceManager.loadHighScore()

        if(score>highScore){
            preferenceManager.saveHighScore(score)
            setResult(Activity.RESULT_OK)
        }

        binding.scoreTxtView.text = getString(R.string.your_score)
        binding.totalScoreTxtView.text = score.toString()
//        binding.highScoreTxtView.text = getString(R.string.high_score, preferenceManager.loadHighScore())


        binding.retryBtn.setOnClickListener {
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.HomeBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}