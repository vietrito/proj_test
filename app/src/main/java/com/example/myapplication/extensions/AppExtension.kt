package com.example.myapplication.extensions

import android.app.Activity
import android.app.ActivityManager
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.MediaMetadataRetriever
import android.media.RingtoneManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.net.toUri
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.DecimalFormat


fun String?.formatStringNumber(format: String): String {
    return try {
        this?.run {
            DecimalFormat(format).format(this.toInt())
        } ?: ""
    } catch (ex: NumberFormatException) {
        this ?: ""
    }
}

fun drawableToBitmap(context: Context, drawable: Drawable): Bitmap {
    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }

    val bitmap = Bitmap.createBitmap(
        drawable.intrinsicWidth,
        drawable.intrinsicHeight,
        Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}

fun setPaddingEx(
    context: Context,
    view: View,
    resId: Int? = null,
    resetPadding: Boolean = false
) {
    if (resetPadding) {
        view.setPadding(0, 0, 0, 0)
    } else if (resId != null) {
        val paddingLeft = context.resources.getDimensionPixelSize(resId)
        val paddingTop = context.resources.getDimensionPixelSize(resId)
        val paddingRight = context.resources.getDimensionPixelSize(resId)
        val paddingBottom = context.resources.getDimensionPixelSize(resId)

        view.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
    }
}

fun setPaddingHorEx(
    context: Context,
    view: View,
    resId: Int? = null,
    resetPadding: Boolean = false
) {
    if (resetPadding) {
        view.setPadding(0, 0, 0, 0)
    } else if (resId != null) {
        val paddingLeft = context.resources.getDimensionPixelSize(resId)
        val paddingRight = context.resources.getDimensionPixelSize(resId)

        view.setPadding(paddingLeft, 0, paddingRight, 0)
    }
}

fun setPaddingVerEx(
    context: Context,
    view: View,
    resId: Int? = null,
    resetPadding: Boolean = false
) {
    if (resetPadding) {
        view.setPadding(0, 0, 0, 0)
    } else if (resId != null) {
        val paddingTop = context.resources.getDimensionPixelSize(resId)
        val paddingBottom = context.resources.getDimensionPixelSize(resId)

        view.setPadding(0, paddingTop, 0, paddingBottom)
    }
}


fun hasWriteSettingsPermission(context: Context): Boolean {
    return Settings.System.canWrite(context)
}

fun TextView.setColorText(color: Int) {
    this.setTextColor(ContextCompat.getColor(context, color))
}

fun TextView.setStyleText(@FontRes font: Int) {
    this.typeface = ResourcesCompat.getFont(context, font)
}

fun TextView.setTextColorGradient(colorGradient1: String, colorGradient2: String) {
    val paint = this.paint
    val width = paint.measureText(this.text.toString())
    val textShader: Shader = LinearGradient(
        0f, 0f, width, this.textSize, intArrayOf(
            Color.parseColor(colorGradient1),
            Color.parseColor(colorGradient2)
        ), null, Shader.TileMode.REPEAT
    )
    this.paint.shader = textShader
}

fun View.setBackGroundDrawable(drawableRes: Int) {
    this.background = ContextCompat.getDrawable(context, drawableRes)
}

fun showActivity(context: Context, activity: Class<*>, bundle: Bundle? = null) {
    val intent = Intent(context, activity)
    intent.putExtras(bundle ?: Bundle())
    context.startActivity(intent)
}

fun Class<*>.isMyServiceRunning(context: Context): Boolean {
    val manager = context.getSystemService(AppCompatActivity.ACTIVITY_SERVICE) as ActivityManager
    for (service in manager.getRunningServices(Int.MAX_VALUE)) {
        if (this.name == service.service.className) {
            return true
        }
    }
    return false
}

fun View.hide() {
    try {
        this.run {
            this.visibility = View.GONE
        }
    } catch (e: java.lang.Exception) {
        this.run {
            this.visibility = View.GONE
        }
    }
}

fun View.show() {
    try {
        this.run {
            this.visibility = View.VISIBLE
        }
    } catch (e: java.lang.Exception) {
        this.run {
            this.visibility = View.GONE
        }
    }
}

fun View.invisible() {
    try {
        this.run {
            this.visibility = View.INVISIBLE
        }
    } catch (e: java.lang.Exception) {
        this.run {
            this.visibility = View.GONE
        }
    }
}

fun <T : View> T.onClickDelay(block: T.() -> Unit) {

    onClick(200, block)
}

private var lastClick = 0L
fun <T : View> T.onClick(delayBetweenClick: Long = 0, block: T.() -> Unit) {
    setOnClickListener {
        when {
            delayBetweenClick <= 0 -> {
                block()
            }

            System.currentTimeMillis() - lastClick > delayBetweenClick -> {
                lastClick = System.currentTimeMillis()
                block()
            }

            else -> {

            }
        }
    }
}

fun Activity.hideKeyboard() {
    val inputMethodManager =
        this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(this.currentFocus?.windowToken, 0)
}

fun Activity.showKeyboard(editText: View) {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}

fun isNetworkAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val nw = connectivityManager.activeNetwork ?: return false
    val actNw = connectivityManager.getNetworkCapabilities(nw) ?: return false
    return when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> true
        else -> false
    }
}


fun getRoundedCornerBitmap(bitmap: Bitmap, cornerRadius: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(output)
    val paint = Paint()
    val rect = Rect(0, 0, width, height)
    val rectF = RectF(rect)

    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    canvas.drawRoundRect(rectF, cornerRadius.toFloat(), cornerRadius.toFloat(), paint)

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, rect, rect, paint)

    return output
}

fun getRoundedCornerBitmap2(bitmap: Bitmap, cornerRadius: Float): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val canvas = Canvas(output)
    val paint = Paint()
    paint.isAntiAlias = true

    val path = Path()
    path.moveTo(0f, cornerRadius)
    path.lineTo(0f, height - cornerRadius)
    path.quadTo(0f, height.toFloat(), cornerRadius, height.toFloat())
    path.lineTo(width.toFloat(), height.toFloat())
    path.lineTo(width.toFloat(), 0f)
    path.lineTo(cornerRadius, 0f)
    path.quadTo(0f, 0f, 0f, cornerRadius)

    canvas.drawPath(path, paint)

    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(bitmap, 0f, 0f, paint)

    return output
}

fun drawableToBitmap(drawable: Drawable): Bitmap {
    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }

    val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 1
    val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 1
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}

fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


private fun getMIMEType(url: String?): String? {
    var mType: String? = null
    val mExtension = MimeTypeMap.getFileExtensionFromUrl(url)
    if (mExtension != null) {
        mType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mExtension)
    }
    return mType
}

fun AppCompatActivity.iS10Band(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
}

fun AppCompatActivity.volatility(): Int {
    return if (iS10Band()) 15 else 1500
}