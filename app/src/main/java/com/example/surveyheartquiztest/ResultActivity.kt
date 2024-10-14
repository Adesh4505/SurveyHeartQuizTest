package com.example.surveyheartquiztest

import QuizActivity
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
        updateHighScore(score)

        binding.scoreTxtView.text = "Your Score: $score"
        binding.highScoreTxtView.text = "High Score: ${preferenceManager.loadHighScore()}"

        binding.retryBtn.setOnClickListener {
            restartQuiz()
        }
        binding.HomeBtn.setOnClickListener {
            navigateToHome()
        }
    }

    private fun updateHighScore(score: Int) {
        val highScore = preferenceManager.loadHighScore()
        if (score > highScore) {
            preferenceManager.saveHighScore(score)
            setResult(Activity.RESULT_OK) // Notify previous activity of new high score
        }
    }

    private fun restartQuiz() {
        val intent = Intent(this, QuizActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
