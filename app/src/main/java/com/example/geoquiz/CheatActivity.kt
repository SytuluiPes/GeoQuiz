package com.example.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

private const val EXTRA_ANSWER_IS_TRUE = "com.example.geoquiz.answer_is_true"
const val EXTRA_ANSWER_SHOWN = ""
class CheatActivity : AppCompatActivity() {

    private lateinit var showAnswerButton: Button
    private lateinit var answerTextView: TextView
    private lateinit var apiVersionView: TextView

    private fun setAnswerShownResult(isAnswerShown: Boolean){
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)
        val answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        showAnswerButton = findViewById(R.id.show_answer_button)
        answerTextView = findViewById(R.id.answer_text_view)
        apiVersionView = findViewById(R.id.api_version_view)

        showAnswerButton.setOnClickListener {
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)
            setAnswerShownResult(true)
        }
        apiVersionView.setText(getString(R.string.api_level, Build.VERSION.SDK_INT))
    }

    companion object{
        fun newIntent(packageContext: Context, answerIsTrue : Boolean): Intent{
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}