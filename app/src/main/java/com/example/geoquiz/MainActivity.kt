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
import com.google.android.material.snackbar.Snackbar
private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
const val REQUEST_CODE_CHEAT = 0
const val USER_SHOW_ANSWER = 1


class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: Button
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView

    private fun toast(resId : Int){
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }
    private fun toast(resId : String){
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

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
                    toast("${(quizViewModel.score / quizViewModel.questionBank.size.toDouble()
                            * 100).toInt()}% is true.")
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
                    toast(R.string.correct_toast)
                    quizViewModel.score++
                } else
                    toast(R.string.incorrect_toast)
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
            val intent = Intent(this, CheatActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_CHEAT)
        }
        updateQuestion(true)
    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
             REQUEST_CODE_CHEAT -> {
                 when (resultCode) {
                     USER_SHOW_ANSWER -> toast(R.string.judgmental_message)
                 }
             }
        }
    }

}