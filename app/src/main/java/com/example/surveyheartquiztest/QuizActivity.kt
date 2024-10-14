package com.example.surveyheartquiztest

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.children
import com.example.surveyheartquiztest.API.ApiResponse
import com.example.surveyheartquiztest.API.ApiService
import com.example.surveyheartquiztest.API.Question
import com.example.surveyheartquiztest.API.RetrofitClient
import com.example.surveyheartquiztest.databinding.ActivityQuizBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Locale

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0
    private var score = 0
    private lateinit var timer: CountDownTimer
    private var timeLeftInMillis: Long = 8000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchQuestions()
//        binding.progressCircular.visibility = View.VISIBLE

        binding.submitBtn.setOnClickListener {
            checkAnswer()
        }
    }

    private fun fetchQuestions() {
        val retrofit = RetrofitClient.getInstance().create(ApiService::class.java)

        val call = retrofit.getQuestions(10)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
//                    binding.progressCircular.visibility = View.GONE
                    questions = response.body()?.results ?: emptyList()
                    if (questions.isNotEmpty()) {
                        displayQuestion()
                        startTimer()
                    } else {
                        Toast.makeText(
                            this@QuizActivity,
                            "No questions available.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
//                    binding.progressCircular.visibility = View.GONE
                    Toast.makeText(
                        this@QuizActivity,
                        "Failed to fetch questions.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
//                binding.progressCircular.visibility = View.GONE
                Toast.makeText(this@QuizActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun startTimer() {
        timer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimer()
            }

            override fun onFinish() {
                endQuiz()
            }
        }.start()
    }

    private fun updateTimer() {
        val seconds = (timeLeftInMillis / 1000).toInt()
        binding.timerTextView.text = String.format(Locale.getDefault(), "%02d", seconds)
    }

    private fun displayQuestion() {
        if (questions.isNotEmpty()) {
            val currentQuestion = questions[currentQuestionIndex]
            binding.questionTxtView.text = currentQuestion.question

            val allOptions = currentQuestion.incorrect_answers.toMutableList().apply {
                add(currentQuestion.correct_answer)
                shuffle()
            }

            binding.answersRadioGroup.clearCheck()

            binding.answersRadioGroup.children.forEachIndexed { index, view ->
                val radioButton = view as RadioButton
                radioButton.text = if (index < allOptions.size) allOptions[index] else ""
                radioButton.visibility = if (index < allOptions.size) View.VISIBLE else View.GONE
            }
        }
    }

    private fun checkAnswer() {

        val selectedRadioButtonId = binding.answersRadioGroup.checkedRadioButtonId
        if (selectedRadioButtonId == -1) {
            Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedRadioButton = findViewById<RadioButton>(selectedRadioButtonId)
        val selectedAnswer = selectedRadioButton.text.toString()
        val correctAnswer = questions[currentQuestionIndex].correct_answer

        if (selectedAnswer == correctAnswer) {
            score++
        }

        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
            displayQuestion()
        } else {
            endQuiz()
        }
    }

    private fun endQuiz() {
        timer.cancel()
        val intent = Intent(this@QuizActivity, ResultActivity::class.java)
        intent.putExtra("score", score)
        startActivity(intent)
        finish()
    }
}