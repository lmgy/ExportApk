package com.lmgy.exportapk.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatCheckBox
import com.lmgy.exportapk.R

/**
 * @author lmgy
 * @date 2019/10/16
 */
class RoundCheckBox: AppCompatCheckBox {

    constructor(context: Context): this(context, null)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, R.attr.radioButtonStyle)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int): super(context, attrs, defStyleAttr)

}