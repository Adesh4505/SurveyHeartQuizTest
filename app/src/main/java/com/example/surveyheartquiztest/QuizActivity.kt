import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import com.example.surveyheartquiztest.API.ApiResponse
import com.example.surveyheartquiztest.API.ApiService
import com.example.surveyheartquiztest.API.Question
import com.example.surveyheartquiztest.API.RetrofitClient
import com.example.surveyheartquiztest.ResultActivity
import com.example.surveyheartquiztest.databinding.ActivityQuizBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0
    private var score = 0
    private lateinit var timer: CountDownTimer
    private var timeLeftInMillis: Long = 30000
    private var isQuizActive: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Restore previous state if available
        if (savedInstanceState != null) {
            restoreState(savedInstanceState)
        } else {
            fetchQuestions()
        }

        binding.submitBtn.setOnClickListener {
            checkAnswer()
        }
    }

    private fun restoreState(savedInstanceState: Bundle) {
        currentQuestionIndex = savedInstanceState.getInt("CURRENT_QUESTION_INDEX", 0)
        score = savedInstanceState.getInt("SCORE", 0)
        timeLeftInMillis = savedInstanceState.getLong("TIME_LEFT_IN_MILLIS", 30000)
        isQuizActive = savedInstanceState.getBoolean("IS_QUIZ_ACTIVE", false)

        // Restore questions using Gson
        val gson = Gson()
        val json = savedInstanceState.getString("QUESTIONS")
        val type = object : TypeToken<List<Question>>() {}.type
        questions = gson.fromJson(json, type)

        // Display question if available
        if (questions.isNotEmpty()) {
            displayQuestion()
            if (!isQuizActive) {
                startTimer()
            }
        }
    }

    private fun fetchQuestions() {
        val retrofit = RetrofitClient.getInstance().create(ApiService::class.java)
        val call = retrofit.getQuestions(10)
        call.enqueue(object : Callback<ApiResponse> {
            override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                if (response.isSuccessful) {
                    questions = response.body()?.results ?: emptyList()
                    if (questions.isNotEmpty()) {
                        displayQuestion()
                        if (!isQuizActive) {
                            startTimer()
                        }
                    } else {
                        Toast.makeText(this@QuizActivity, "No questions available.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@QuizActivity, "Failed to fetch questions.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                Toast.makeText(this@QuizActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
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

    private fun displayQuestion() {
        if (questions.isNotEmpty()) {
            binding.submitBtn.isEnabled = true
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
        isQuizActive = false
        val intent = Intent(this@QuizActivity, ResultActivity::class.java)
        intent.putExtra("score", score)
        startActivity(intent)
        finish()
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
            displayQuestion() // Ensure question is displayed
            if (!isQuizActive) {
                startTimer() // Restart the timer if the quiz was previously active
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::timer.isInitialized) {
            timer.cancel()
        }
    }
}
