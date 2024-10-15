package com.example.surveyheartquiztest

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.surveyheartquiztest.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private lateinit var preferenceManager: PreferenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(this@ResultActivity)

        val score = intent.getIntExtra("score", 0)
        val highScore = preferenceManager.loadHighScore()

        if(score>highScore){
            preferenceManager.saveHighScore(score)
            setResult(Activity.RESULT_OK)
        }

        binding.scoreTxtView.text = getString(R.string.your_score, score)
        binding.highScoreTxtView.text = getString(R.string.high_score, preferenceManager.loadHighScore())


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