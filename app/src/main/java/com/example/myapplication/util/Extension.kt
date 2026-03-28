package com.example.myapplication.util

import android.Manifest
import android.animation.Animator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.PowerManager
import android.provider.MediaStore
import android.provider.Settings
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.PixelCopy
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.findViewTreeLifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.myapplication.Application
import com.example.myapplication.R

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.sqrt


fun View.applyBackground(drawable: Drawable) {
    visible()
    background = drawable
}

fun initDenied(context: Context, isShouldDenied: Boolean) {
    if (!isShouldDenied) {
        openApplicationSetting(context)
    } else {
        toast(context)
    }
}

fun toast(context: Context) {
    Toast.makeText(context, "Rrror", Toast.LENGTH_SHORT).show()
}




fun ImageView.loadImage(url: String?) {
    Glide.with(context)
        .load(url)
        .apply(
            RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL) // cache cả original + resized
                .override(width.takeIf { it > 0 } ?: 800, height.takeIf { it > 0 } ?: 600) // không decode ảnh to hơn cần
                .centerCrop()
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
        )
        .transition(DrawableTransitionOptions.withCrossFade(150)) // fade mượt
        .into(this)
}


fun String.toDisplayDate(): String {
    return try {
        this.split("T")[0]
    } catch (e: Exception) {
        this
    }
}

fun openApplicationSetting(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", context.packageName, null)
    intent.data = uri
    context.startActivity(intent)
}
fun Activity.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Activity.checkGrandPermission(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Activity.requestPermission(permission: String, requestCode: Int) {
    ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
}

fun Long.convertDateToString(format: String): String {
    val date = Date(this)
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.format(date)
}
@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
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


fun getScreenShotFromView(v: View): Bitmap? {
    // create a bitmap object
    var screenshot: Bitmap? = null
    try {
        screenshot =
            Bitmap.createBitmap(v.measuredWidth, v.measuredHeight, Bitmap.Config.ARGB_8888)
        // Now draw this bitmap on a canvas
        val canvas = Canvas(screenshot)
        v.draw(canvas)
    } catch (e: Exception) {
        Log.e("GFG", "Failed to capture screenshot because:" + e.message)
    }
    // return the bitmap
    return screenshot
}

fun saveMediaToStorage(bitmap: Bitmap,context: Context) {
    // Generating a file name
    val filename = "${System.currentTimeMillis()}.jpg"

    // Output stream
    var fos: OutputStream? = null

    // For devices running android >= Q
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // getting the contentResolver
        context.contentResolver?.also { resolver ->

            // Content resolver will process the contentvalues
            val contentValues = ContentValues().apply {

                // putting file information in content values
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            }

            // Inserting the contentValues to
            // contentResolver and getting the Uri
            val imageUri: Uri? =
                resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

            // Opening an outputstream with the Uri that we got
            fos = imageUri?.let { resolver.openOutputStream(it) }
        }
    } else {
        // These for devices running on android < Q
        val imagesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image = File(imagesDir, filename)
        fos = FileOutputStream(image)
    }

    fos?.use {
        // Finally writing the bitmap to the output stream that we opened
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
//        Toast.makeText(context, context.getString(R.string.image_saved), Toast.LENGTH_SHORT).show()
    }
}
fun captureView(view: View, context: Context,window: Window) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Above Android O, use PixelCopy
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val location = IntArray(2)
        view.getLocationInWindow(location)
//        try {
        PixelCopy.request(window,
            Rect(location[0], location[1], location[0] + view.width, location[1] + view.height),
            bitmap,
            {
                if (it == PixelCopy.SUCCESS) {
                    saveMediaToStorage(bitmap, context)
                }else {
                    val toast = Toast.makeText(
                        context,
                        "Failed to copyPixels: $it", Toast.LENGTH_SHORT
                    )
                    toast.show()
                }
            },
            Handler(Looper.getMainLooper()) )
//        } catch (e: IllegalArgumentException) {
//            // PixelCopy may throw IllegalArgumentException, make sure to handle it
//            e.printStackTrace()
//        }
    } else {
        val tBitmap = Bitmap.createBitmap(
            view.width, view.height, Bitmap.Config.RGB_565
        )
        val canvas = Canvas(tBitmap)
        view.draw(canvas)
        canvas.setBitmap(null)

        saveMediaToStorage(tBitmap, context)
    }
}

@RequiresApi(Build.VERSION_CODES.N)
fun usePixelCopy(videoView: SurfaceView,context: Context, callback: (Bitmap?) -> Unit) {
    val bitmap: Bitmap = Bitmap.createBitmap(
        videoView.width,
        videoView.height,
        Bitmap.Config.ARGB_8888
    );
    try {
        // Create a handler thread to offload the processing of the image.
        val handlerThread = HandlerThread("PixelCopier");
        handlerThread.start();

        PixelCopy.request(
            videoView, bitmap,
            { copyResult ->
                if (copyResult === PixelCopy.SUCCESS) {
                    val toast = Toast.makeText(
                        context  ,
                        bitmap.toString(), Toast.LENGTH_LONG
                    )
                    saveMediaToStorage(bitmap, context)
                } else {
                    val toast = Toast.makeText(
                        context,
                        "Failed to copyPixels: $copyResult", Toast.LENGTH_LONG
                    )
                    toast.show()
                }
                handlerThread.quitSafely()
            },
            Handler(handlerThread.looper)
        )

    } catch (e: IllegalArgumentException) {
        callback(null)
        // PixelCopy may throw IllegalArgumentException, make sure to handle it
        e.printStackTrace()
    }
}
fun getFulllScreenshot(view: View): Bitmap? {
    val v = view.rootView
    v.isDrawingCacheEnabled = true
    v.measure(
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
    )
    v.layout(0, 0, v.measuredWidth, v.measuredHeight)
    v.buildDrawingCache(true)
    val b = Bitmap.createBitmap(v.drawingCache)
    v.isDrawingCacheEnabled = false // clear drawing cache
    return b
}
fun takeScreenshot(view: View): Bitmap? {
    val rootView = view.rootView
    rootView.isDrawingCacheEnabled = true
    return rootView.drawingCache
}
//change bitmap to uri
fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
    val bytes = ByteArrayOutputStream()
    inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path =
        MediaStore.Images.Media.insertImage(inContext.contentResolver, inImage, "Title", null)
    return Uri.parse(path)
}
fun getBitmapFromView(view: View): Bitmap? {
    var bitmap =
        Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    view.draw(canvas)
    return bitmap
}
fun setMargin(context: Context?, view: View, left: Int, top: Int, right: Int, bottom: Int) {
    val params = view.layoutParams as ViewGroup.MarginLayoutParams
    params.setMargins(
        left * density(context!!),
        top * density(context),
        right * density(context),
        bottom * density(context),
    )
    view.layoutParams = params
}

//firebase ml kit


//chuyen uri thanh bitmap
fun decodeUriToBitmap(context: Context,uri: Uri): Bitmap? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(inputStream)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        null
    }
}
fun drawableToString(resources: Resources, drawableResId: Int): String {
    val bitmap = BitmapFactory.decodeResource(resources, drawableResId)
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
    val byteArray = byteArrayOutputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.DEFAULT)
}

fun stringToDrawable(resources: Resources, drawableString: String): Bitmap {
    val byteArray = Base64.decode(drawableString, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}
//chuyen drawable to bitmap
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
fun density(context: Context): Int {
    val density = context.resources.displayMetrics.density
    return density.toInt()
}
fun View.visible() {
    this.visibility = View.VISIBLE
    this.isEnabled = true
}
fun Activity.hideNavBar() {
    window.decorView.apply {
        systemUiVisibility =
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }
}
fun View.hidden() {
    this.visibility = View.INVISIBLE
    this.isEnabled = false
}

fun View.gone() {
    this.visibility = View.GONE
    this.isEnabled = false
}

fun View.animeFade(isShow: Boolean, duration: Long = 0) {
    if (isShow == isVisible) {
        return
    }
    val toAlpha = if (isShow) 1f else 0f
    this.visible()
    this.alpha = if (isShow) 0f else 1f
    animate()
        .alpha(toAlpha)
        .setDuration(duration)
        .setComplete { if (isShow) visible() else gone() }
        .start()
}

fun Context.showSingleActionAlert(
    title: String, message: String,
    actionTitle: String = "OK",
    completion: () -> Unit
) {
    AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(actionTitle) { _, _ ->
            completion()
        }
        .setCancelable(false)
        .create()
        .apply {
            setCanceledOnTouchOutside(false)
            show()
        }
}

fun Activity.hideKeyboard() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    currentFocus?.let {
        inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
    }
}
fun isDeviceLocked(context: Context): Boolean {

    // First we check the locked state
    val keyguardManager = context.getSystemService(AppCompatActivity.KEYGUARD_SERVICE) as KeyguardManager
    val inKeyguardRestrictedInputMode = keyguardManager.isKeyguardLocked
    val isLocked = if (inKeyguardRestrictedInputMode) {
        true
    } else {
        // If password is not set in the settings, the inKeyguardRestrictedInputMode() returns false,
        // so we need to check if screen on for this case
        val powerManager = context.getSystemService(AppCompatActivity.POWER_SERVICE) as PowerManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            !powerManager.isInteractive
        } else {
            !powerManager.isScreenOn
        }
    }
    Log.d("ziko",String.format("Now device is %s.", if (isLocked) "locked" else "unlocked"))
    return isLocked
}
fun Context.showTwoActionAlert(
    title: String, message: String,
    positiveTitle: String = "OK",
    negativeTitle: String = "Cancel",
    positiveAction: (() -> Unit)? = null,
    negativeAction: (() -> Unit)? = null
) {
    CoroutineScope(Dispatchers.Main).launch {
        AlertDialog.Builder(this@showTwoActionAlert)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveTitle) { _, _ ->
                positiveAction?.let { it() }
            }
            .setNegativeButton(negativeTitle) { _, _ ->
                negativeAction?.let { it() }
            }
            .setCancelable(false)
            .create()
            .apply {
                setCanceledOnTouchOutside(false)
                show()
            }
    }
}

fun ImageView.loadImage(source: Int) {
    this.post {
        fun isValidContextForGlide(context: Context): Boolean {
            if (context is Activity) {
                if (context.isDestroyed || context.isFinishing) {
                    return false;
                }
            }
            return true
        }
        if (isValidContextForGlide(Application.appContext!!.applicationContext))
            Glide.with(Application.appContext!!.applicationContext).asBitmap()
                .override(this.width, this.height)
                .load(source).into(this)
    }
}


var lastEvent: FloatArray? = null
var d = 0f
var newRot = 0f
var isZoomAndRotate = false
var isOutSide = false
val NONE = 0
val DRAG = 1
val ZOOM = 2
var mode = NONE
val start = PointF()
val mid = PointF()
var oldDist = 1f
var xCoOrdinate = 0f
var yCoOrdinate = 0f
var initialX = 0f
var initialY = 0f
var rotationAngle = 0f
fun setSelectedView(view: View,any : Any){

    when(view){

    }


}
fun viewTransformation(view: View, event: MotionEvent) {
    when (event.action and MotionEvent.ACTION_MASK) {
        MotionEvent.ACTION_DOWN -> {
            xCoOrdinate = view.x - event.rawX
            yCoOrdinate = view.y - event.rawY
            start[event.x] = event.y
            isOutSide = false
            mode = DRAG
            lastEvent = null
        }

        MotionEvent.ACTION_POINTER_DOWN -> {
            oldDist = spacing(event)
            if (oldDist > 10f) {
                midPoint(mid, event)
                mode = ZOOM
            }
            lastEvent = FloatArray(4)
            lastEvent!![0] = event.getX(0)
            lastEvent!![1] = event.getX(1)
            lastEvent!![2] = event.getY(0)
            lastEvent!![3] = event.getY(1)
            d = rotation(event)

            if (event.x < view.width / 4 && event.y < view.height / 4) {
                initialX = event.rawX
                initialY = event.rawY
                rotationAngle = view.rotation
            }
        }

        MotionEvent.ACTION_UP -> {
            isZoomAndRotate = false
            if (mode == DRAG) {
                val x = event.x
                val y = event.y
            }
            isOutSide = true
            mode = NONE
            lastEvent = null
            mode = NONE
            lastEvent = null
        }

        MotionEvent.ACTION_OUTSIDE -> {
            isOutSide = true
            mode = NONE
            lastEvent = null
            mode = NONE
            lastEvent = null
        }

        MotionEvent.ACTION_POINTER_UP -> {
            mode = NONE
            lastEvent = null
        }

        MotionEvent.ACTION_MOVE -> if (!isOutSide) {
            if (mode == DRAG) {
                isZoomAndRotate = false
                view.animate().x(event.rawX + xCoOrdinate).y(event.rawY + yCoOrdinate)
                    .setDuration(0).start()
            }
            if (mode == ZOOM && event.pointerCount == 2) {
                val newDist1 = spacing(event)
                if (newDist1 > 10f) {
                    val scale = newDist1 / oldDist * view.scaleX
                    view.scaleX = scale
                    view.scaleY = scale
                }
                if (lastEvent != null) {
                    newRot = rotation(event)
                    view.rotation = (view.rotation + (newRot - d))
                }
            }
            if (initialX != 0f && initialY != 0f) {
                val deltaX = event.rawX - initialX
                val deltaY = event.rawY - initialY
                val newRotationAngle = rotationAngle + (deltaX + deltaY) * 0.1f
                view.rotation = newRotationAngle
            }
        }
    }
}

fun rotation(event: MotionEvent): Float {
    val delta_x = (event.getX(0) - event.getX(1)).toDouble()
    val delta_y = (event.getY(0) - event.getY(1)).toDouble()
    val radians = atan2(delta_y, delta_x)
    return Math.toDegrees(radians).toFloat()
}

fun spacing(event: MotionEvent): Float {
    val x = event.getX(0) - event.getX(1)
    val y = event.getY(0) - event.getY(1)
    return sqrt((x * x + y * y).toDouble()).toInt().toFloat()
}

fun midPoint(point: PointF, event: MotionEvent) {
    val x = event.getX(0) + event.getX(1)
    val y = event.getY(0) + event.getY(1)
    point[x / 2] = y / 2
}

suspend fun Context.showTwoActionAlert(
    title: String, message: String,
    positiveTitle: String = "OK",
    negativeTitle: String = "Cancel"
) = suspendCoroutine<Boolean> { continuation ->
    showTwoActionAlert(title, message, positiveTitle, negativeTitle, positiveAction = {
        continuation.resume(true)
    })
}

fun View.jumping(translationY: Float = 20F, duration: Long, loop: Boolean = true) {
    animate()
        .translationY(translationY)
        .setDuration(duration / 2)
        .setComplete {
            animate()
                .translationY(-translationY)
                .setDuration(1500L)
                .setComplete {
                    if (loop) {
                        jumping(translationY, duration, loop)
                    }
                }
        }
}

fun View.toggleSelected() {
    isSelected = !isSelected
}

fun View.togleVisible() {
    if (isVisible) {
        gone()
    } else {
        visible()
    }
}

fun View.animeRotate(rotation: Float) {
    animate()
        .rotation(rotation)
        .setDuration(200)
        .start()
}

fun Context.convertDpToPixel(dp: Float): Float {
    return dp * (resources
        .displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}

fun Context.isInternetAvailable(): Boolean {
    var result = false
    val connectivityManager =
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networkCapabilities = connectivityManager.activeNetwork ?: return false
    val actNw =
        connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
    result = when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
        else -> false
    }
    return result
}

@RequiresApi(Build.VERSION_CODES.O)
fun LocalDateTime.string(pattern: String = "yyyy-MM-dd HH:mm"): String {
    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(pattern)
    return this.format(formatter)
}

fun Context.hasPermissions(permissions: Array<String>): Boolean = permissions.all {
    ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
}
fun isBasicPermissionGranted(context: Context): Boolean {
    return PermissionChecker.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
    ) == PermissionChecker.PERMISSION_GRANTED
}

fun ViewPropertyAnimator.setComplete(completion: (Animator?) -> Unit): ViewPropertyAnimator {
    return setListener(object : Animator.AnimatorListener {

        override fun onAnimationStart(p0: Animator) {

        }

        override fun onAnimationEnd(p0: Animator) {
            completion(p0)
        }

        override fun onAnimationCancel(p0: Animator) {

        }

        override fun onAnimationRepeat(p0: Animator) {

        }
    })
}

fun View.setPaddingAsDP(left: Int = 0, top: Int = 0, right: Int = 0, bottom: Int = 0) {
    setPadding(asPixels(left), asPixels(top), asPixels(right), asPixels(bottom))
}

fun View.asPixels(value: Int): Int {
    val scale = resources.displayMetrics.density
    val dpAsPixels = (value * scale + 0.5f)
    return dpAsPixels.toInt()
}
@SuppressLint("DiscouragedApi")
fun getTypeFace(context: Context): Typeface? {
    val typefaceId = context.resources.getIdentifier("lexend_regular", "font", context.packageName)
    return ResourcesCompat.getFont(context, typefaceId)
}
internal fun Context.getDrawableCompat(@DrawableRes drawable: Int): Drawable =
    requireNotNull(ContextCompat.getDrawable(this, drawable))

internal fun Context.getColorCompat(@ColorRes color: Int) =
    ContextCompat.getColor(this, color)

internal fun TextView.setTextColorRes(@ColorRes color: Int) =
    setTextColor(context.getColorCompat(color))

