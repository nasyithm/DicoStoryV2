package com.nasyithm.dicostoryv2.view.auth.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nasyithm.dicostoryv2.R
import com.nasyithm.dicostoryv2.data.Result
import com.nasyithm.dicostoryv2.databinding.ActivityRegisterBinding
import com.nasyithm.dicostoryv2.view.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        register()
        playAnimation()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    private fun register() {
        binding.btnRegister.setButtonText(getString(R.string.register))
        binding.btnRegister.addOnButtonClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            registerViewModel.register(name, email, password).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.succeed))
                            setMessage(getString(R.string.account_created, email))
                            setPositiveButton(getString(R.string.proceed)) { _, _ ->
                                finish()
                            }
                            create()
                            show()
                        }
                    }
                    is Result.Error -> {
                        showLoading(false)
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.failed))
                            setMessage(result.error)
                            setPositiveButton(getString(R.string.ok)) { _, _ -> }
                            create()
                            show()
                        }
                    }
                }
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.btnRegister.startLoading(getString(R.string.loading))
        } else {
            binding.btnRegister.stopLoading()
        }
    }

    private fun playAnimation() {
        val tvTitle = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(200)
        val ivRegister = ObjectAnimator.ofFloat(binding.ivRegister, View.ALPHA, 1f).setDuration(200)
        val tvName = ObjectAnimator.ofFloat(binding.tvName, View.ALPHA, 1f).setDuration(200)
        val letName = ObjectAnimator.ofFloat(binding.letName, View.ALPHA, 1f).setDuration(200)
        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(200)
        val letEmail = ObjectAnimator.ofFloat(binding.letEmail, View.ALPHA, 1f).setDuration(200)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(200)
        val letPassword = ObjectAnimator.ofFloat(binding.letPassword, View.ALPHA, 1f).setDuration(200)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(200)

        val togetherName = AnimatorSet().apply {
            playTogether(tvName, letName)
        }
        val togetherEmail = AnimatorSet().apply {
            playTogether(tvEmail, letEmail)
        }
        val togetherPassword = AnimatorSet().apply {
            playTogether(tvPassword, letPassword)
        }

        AnimatorSet().apply {
            playSequentially(tvTitle, ivRegister, togetherName, togetherEmail, togetherPassword, btnRegister)
            startDelay = 100
        }.start()
    }
}