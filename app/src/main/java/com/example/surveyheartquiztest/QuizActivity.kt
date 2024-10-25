package com.example.surveyheartquiztest

import android.content.Context
import com.example.surveyheartquiztest.RoomDB.QuestionEntity
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.view.children
import androidx.lifecycle.lifecycleScope
import com.example.surveyheartquiztest.API.ApiResponse
import com.example.surveyheartquiztest.API.ApiService
import com.example.surveyheartquiztest.API.Question
import com.example.surveyheartquiztest.API.RetrofitClient
import com.example.surveyheartquiztest.RoomDB.QuestionRepository
import com.example.surveyheartquiztest.RoomDB.QuizDb
import com.example.surveyheartquiztest.databinding.ActivityQuizBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch
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
    private var timeLeftInMillis: Long = 30000
    private var isQuizActive: Boolean = false
    private lateinit var questionRepository: QuestionRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (Build.VERSION.SDK_INT >= 21) {
            val window = this.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = this.resources.getColor(R.color.app_base)
        }

        questionRepository = QuestionRepository(QuizDb.getDatabase(this).questionDao())

        binding.progressBar.visibility = View.VISIBLE
        binding.timerTextView.text = getString(R.string.initial_timer_value)

        if (savedInstanceState != null) {
            restoreSavedState(savedInstanceState)
        } else {
            if (isInternetAvailable()) {
                fetchQuestions()
            } else {
                loadQuestionsFromDatabase()
            }
        }

        binding.submitBtn.isEnabled = false
        binding.submitBtn.setOnClickListener {
            checkAnswer()
        }
    }

    private fun isInternetAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private fun loadQuestionsFromDatabase() {
        lifecycleScope.launch {
            val questions = questionRepository.getAllQuestions()
            if (questions.isEmpty()) {
                Toast.makeText(this@QuizActivity, "No offline questions available.", Toast.LENGTH_SHORT).show()
            } else {
                this@QuizActivity.questions = questions.map { questionEntity ->
                    Question(
                        category = "",
                        type = "",
                        difficulty = "",
                        question = questionEntity.question,
                        correct_answer = questionEntity.correctAnswer,
                        incorrect_answers = questionEntity.incorrectAnswers.split(",")
                    )
                }
                displayQuestion()
                if (!isQuizActive) {
                    startTimer()
                }
            }
        }
    }

    private fun fetchQuestions() {
        val retrofit = RetrofitClient.getInstance().create(ApiService::class.java)
        val call = retrofit.getQuestions(20)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    val questions = response.body()?.results ?: emptyList()

                    lifecycleScope.launch {
                        questionRepository.clearQuestions()
                        val questionEntities = questions.map { question ->
                            QuestionEntity(
                                question = question.question,
                                correctAnswer = question.correct_answer,
                                incorrectAnswers = question.incorrect_answers.joinToString(",")
                            )
                        }
                        questionRepository.insertQuestions(questionEntities)
                    }

                    this@QuizActivity.questions = questions
                    displayQuestion()
                    if (!isQuizActive) {
                        startTimer()
                    }
                    binding.progressBar.visibility = View.GONE
                    startTimer()
                } else {
                    Toast.makeText(this@QuizActivity, "Failed to fetch questions.", Toast.LENGTH_SHORT).show()
                    loadQuestionsFromDatabase()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                binding.progressBar.visibility = View.GONE
                Toast.makeText(this@QuizActivity, "API Error: ${t.message}", Toast.LENGTH_SHORT).show()
                loadQuestionsFromDatabase()
            }
        })
    }

    private fun displayQuestion() {
        if (questions.isNotEmpty()) {
            val currentQuestion = questions[currentQuestionIndex]
            binding.questionTxtView.text = currentQuestion.question

            val allOptions = currentQuestion.incorrect_answers.toMutableList().apply {
                add(currentQuestion.correct_answer)
                shuffle()
            }

            binding.progressBar.visibility = View.GONE
            binding.answersRadioGroup.visibility = View.VISIBLE
            binding.answersRadioGroup.clearCheck()

            binding.answersRadioGroup.children.forEachIndexed { index, view ->
                val radioButton = view as RadioButton
                radioButton.text = if (index < allOptions.size) allOptions[index] else ""
                radioButton.visibility = if (index < allOptions.size) View.VISIBLE else View.GONE
            }

            binding.submitBtn.isEnabled = true
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
        isQuizActive = false
        val intent = Intent(this@QuizActivity, ResultActivity::class.java)
        intent.putExtra("score", score)
        startActivity(intent)
        finish()
    }

    private fun startTimer() {
        if (!isQuizActive) {
            isQuizActive = true
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
    }

    private fun updateTimer() {
        val seconds = (timeLeftInMillis / 1000).toInt()
        binding.timerTextView.text = String.format(Locale.getDefault(), "%02d", seconds)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("CURRENT_QUESTION_INDEX", currentQuestionIndex)
        outState.putInt("SCORE", score)
        outState.putLong("TIME_LEFT_IN_MILLIS", timeLeftInMillis)
        outState.putBoolean("IS_QUIZ_ACTIVE", isQuizActive)

        val gson = Gson()
        val json = gson.toJson(questions)
        outState.putString("QUESTIONS", json)
    }

    private fun restoreSavedState(savedInstanceState: Bundle) {
        currentQuestionIndex = savedInstanceState.getInt("CURRENT_QUESTION_INDEX", 0)
        score = savedInstanceState.getInt("SCORE", 0)
        timeLeftInMillis = savedInstanceState.getLong("TIME_LEFT_IN_MILLIS", 8000L)
        isQuizActive = savedInstanceState.getBoolean("IS_QUIZ_ACTIVE", false)

        val gson = Gson()
        val json = savedInstanceState.getString("QUESTIONS")
        val type = object : TypeToken<List<Question>>() {}.type
        questions = gson.fromJson(json, type)

        if (::questions.isInitialized && questions.isNotEmpty()) {
            displayQuestion()
            if (!isQuizActive) {
                startTimer()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (::timer.isInitialized && isQuizActive) {
            timer.cancel()
            isQuizActive = false
        }
    }

    override fun onResume() {
        super.onResume()
        if (::questions.isInitialized && timeLeftInMillis > 0) {
            displayQuestion()
            if (!isQuizActive) {
                startTimer()
            }
        }
    }
}
