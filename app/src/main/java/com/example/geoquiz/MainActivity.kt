package com.example.geoquiz

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
const val REQUEST_CODE_CHEAT = 0
var USER_SHOW_ANSWER : Int = 0
var helpRemaining = 3

class MainActivity : AppCompatActivity() {

    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: Button
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView
    private lateinit var helpRemainingView: TextView

    private fun toast(resId : Int){
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }
    private fun toast(resId : String){
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show()
    }

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)
        helpRemainingView = findViewById(R.id.help_remaining)

        var correctAnswer : Boolean = quizViewModel.currentQuestionAnswer

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        fun updateQuestion() {
            val questionTextResId = quizViewModel.currentQuestionText
            correctAnswer = quizViewModel.currentQuestionAnswer
            questionTextView.setText(questionTextResId)
            if (quizViewModel.isCheater)
                quizViewModel.isCheater = false
        }

        fun checkAnswer(userAnswer: Boolean) {
            val messageResId = when (userAnswer){
                correctAnswer -> R.string.correct_toast
                else -> { R.string.incorrect_toast}
            }
            toast(messageResId)
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
            quizViewModel.moveToNext()
            updateQuestion()
        }
        cheatButton.setOnClickListener {
            if (helpRemaining > 0){
                val answerIsTrue = quizViewModel.currentQuestionAnswer
                val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val options = ActivityOptions.makeClipRevealAnimation(
                        it, 0, 0, it.width, it.height
                    )
                    startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
                }
                else{
                    startActivityForResult(intent, REQUEST_CODE_CHEAT)
                }
            } else toast(R.string.help_remaining_is_null)
        }
        updateQuestion()
        helpRemainingView.setText(getString(R.string.help_remaining, helpRemaining))
    }
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
             REQUEST_CODE_CHEAT ->{
                 quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
                 helpRemaining--
                 helpRemainingView.setText(getString(R.string.help_remaining, helpRemaining))
             }
        }
    }

}