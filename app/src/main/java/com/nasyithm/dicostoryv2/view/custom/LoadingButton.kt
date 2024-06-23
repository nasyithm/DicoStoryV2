package com.nasyithm.dicostoryv2.view.custom

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.nasyithm.dicostoryv2.databinding.LoadingButtonBinding

class LoadingButton : ConstraintLayout {
    private lateinit var binding: LoadingButtonBinding
    private var buttonText: String = ""
    private var buttonClickAction: () -> Unit = {}

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }

    private fun init() {
        binding = LoadingButtonBinding.inflate(LayoutInflater.from(context), this, true)
        binding.btnLoading.setOnClickListener { buttonClickAction() }
    }

    fun setButtonText(text: String) {
        binding.btnLoading.text = text
        buttonText = text
    }

    fun startLoading(text: String) {
        binding.progressBar.visibility = VISIBLE
        binding.btnLoading.isEnabled = false
        binding.btnLoading.text = text
    }

    fun stopLoading() {
        binding.progressBar.visibility = GONE
        binding.btnLoading.isEnabled = true
        binding.btnLoading.text = buttonText
    }

    fun addOnButtonClickListener(onClick: () -> Unit) {
        buttonClickAction = onClick
    }
}