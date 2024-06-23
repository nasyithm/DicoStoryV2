package com.nasyithm.dicostoryv2.view.auth.login

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.nasyithm.dicostoryv2.R
import com.nasyithm.dicostoryv2.data.local.pref.UserModel
import com.nasyithm.dicostoryv2.databinding.ActivityLoginBinding
import com.nasyithm.dicostoryv2.data.Result
import com.nasyithm.dicostoryv2.view.ViewModelFactory
import com.nasyithm.dicostoryv2.view.auth.register.RegisterActivity
import com.nasyithm.dicostoryv2.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        login()
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

    private fun login() {
        binding.btnLogin.setButtonText(getString(R.string.login))
        binding.btnLogin.addOnButtonClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            loginViewModel.login(email, password).observe(this) { result ->
                when (result) {
                    is Result.Loading -> {
                        showLoading(true)
                    }
                    is Result.Success -> {
                        showLoading(false)
                        loginViewModel.saveSession(
                            UserModel(
                            result.data.loginResult?.userId.toString(),
                            result.data.loginResult?.name.toString(),
                            email,
                            result.data.loginResult?.token.toString())
                        )
                        AlertDialog.Builder(this).apply {
                            setTitle(getString(R.string.succeed))
                            setMessage(getString(R.string.login_succeed))
                            setPositiveButton(getString(R.string.proceed)) { _, _ ->
                                val intent = Intent(context, MainActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
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

    private fun register() {
        binding.btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.btnLogin.startLoading(getString(R.string.loading))
        } else {
            binding.btnLogin.stopLoading()
        }
    }

    private fun playAnimation() {
        val tvTitle = ObjectAnimator.ofFloat(binding.tvTitle, View.ALPHA, 1f).setDuration(200)
        val ivLogin = ObjectAnimator.ofFloat(binding.ivLogin, View.ALPHA, 1f).setDuration(200)
        val tvEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(200)
        val letEmail = ObjectAnimator.ofFloat(binding.letEmail, View.ALPHA, 1f).setDuration(200)
        val tvPassword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(200)
        val letPassword = ObjectAnimator.ofFloat(binding.letPassword, View.ALPHA, 1f).setDuration(200)
        val btnLogin = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(200)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(200)

        val togetherEmail = AnimatorSet().apply {
            playTogether(tvEmail, letEmail)
        }
        val togetherPassword = AnimatorSet().apply {
            playTogether(tvPassword, letPassword)
        }

        AnimatorSet().apply {
            playSequentially(tvTitle, ivLogin, togetherEmail, togetherPassword, btnLogin, btnRegister)
            startDelay = 100
        }.start()
    }
}