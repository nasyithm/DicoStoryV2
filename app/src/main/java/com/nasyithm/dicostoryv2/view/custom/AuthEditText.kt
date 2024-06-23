package com.nasyithm.dicostoryv2.view.custom

import android.content.Context
import android.graphics.Canvas
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.nasyithm.dicostoryv2.R

class AuthEditText @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    init {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                if (s.toString().isNotEmpty()) {
                    if (inputType == InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS) {
                        if (!Patterns.EMAIL_ADDRESS.matcher(s).matches()) {
                            setError(context.getString(R.string.invalid_email), null)
                        } else {
                            error = null
                        }
                    } else if (inputType == InputType.TYPE_CLASS_TEXT + InputType.TYPE_TEXT_VARIATION_PASSWORD) {
                        if (s.toString().length < 8) {
                            setError(context.getString(R.string.invalid_password), null)
                        } else {
                            error = null
                        }
                    } else {
                        error = null
                    }
                } else {
                    setError(context.getString(R.string.empty_edit_text), null)
                }
            }

            override fun afterTextChanged(s: Editable) {}

        })
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        setPadding(0, 16, 0, 16)
    }
}