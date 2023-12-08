package com.example.geoquiz

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: Button
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    private var isNotClicked = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)
        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        fun updateQuestion(create: Boolean = false) {
            if (create) {
                val questionTextResId = quizViewModel.currentQuestionText
                questionTextView.setText(questionTextResId)
            } else {
                if (quizViewModel.currentIndex == 0) {
                    Toast.makeText(
                        this,
                        "${(quizViewModel.score / quizViewModel.questionBank.size.toDouble() * 100).toInt()}% is true.",
                        Toast.LENGTH_SHORT
                    ).show()
                    quizViewModel.score = 0
                }
                val questionTextResId = quizViewModel.currentQuestionText
                questionTextView.setText(questionTextResId)

            }
        }

        fun checkAnswer(userAnswer: Boolean) {
            if (isNotClicked) {
                isNotClicked = false
                if (userAnswer == quizViewModel.currentQuestionAnswer) {
                    Toast.makeText(this, R.string.correct_toast, Toast.LENGTH_SHORT).show()
                    quizViewModel.score++
                } else
                    Toast.makeText(this, R.string.incorrect_toast, Toast.LENGTH_SHORT).show()
            }
        }
        questionTextView.setOnClickListener {
            nextButton.callOnClick()
        }
        trueButton.setOnClickListener {
            checkAnswer(true)

        }
        falseButton.setOnClickListener {
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            isNotClicked = true
            quizViewModel.moveToNext()
            updateQuestion()
        }
        cheatButton.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            startActivity(intent)
            updateQuestion(true)
        }
    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

}