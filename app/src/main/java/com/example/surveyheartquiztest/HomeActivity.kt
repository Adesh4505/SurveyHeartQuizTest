package com.example.surveyheartquiztest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.surveyheartquiztest.databinding.ActivityHomeBinding

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var quizActivityLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        preferenceManager = PreferenceManager(this@HomeActivity)

        quizActivityLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val score = result.data?.getIntExtra("score", 0) ?: 0
                val highScore = preferenceManager.loadHighScore()

                if (score > highScore) {
                    preferenceManager.saveHighScore(score)
                    binding.highScoreTxtView.text = "High Score: $score"
                } else {
                    binding.highScoreTxtView.text = "High Score: $highScore"
                }
            }
        }

        binding.startQuizBtn.setOnClickListener {
            val intent = Intent(this@HomeActivity, QuizActivity::class.java)
            quizActivityLauncher.launch(intent)
        }
        binding.highScoreTxtView.text = "High Score: ${preferenceManager.loadHighScore()}"

    }
}