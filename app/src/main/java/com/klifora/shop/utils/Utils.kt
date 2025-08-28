package com.klifora.shop.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.content.res.Configuration
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Parcelable
import android.os.SystemClock
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.DrawableCompat
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import com.klifora.shop.R
import com.klifora.shop.datastore.DataStoreKeys.LOGIN_DATA
import com.klifora.shop.datastore.DataStoreKeys.STORE_DETAIL
import com.klifora.shop.datastore.DataStoreUtil.clearDataStore
import com.klifora.shop.datastore.DataStoreUtil.removeKey
import com.klifora.shop.models.ItemReturn
import com.klifora.shop.ui.mainActivity.MainActivity
import com.razorpay.PayloadHelper
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import org.json.JSONArray
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.Serializable
import java.math.RoundingMode
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import kotlin.text.get


fun View.openKeyboard() = try {
    (MainActivity.context?.get() as Activity).apply {
        postDelayed({
            val inputMethodManager: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            this@openKeyboard.requestFocus()
            inputMethodManager.showSoftInput(currentFocus, InputMethodManager.SHOW_IMPLICIT)
        }, 200)
    }
} catch (e: Exception) {
    e.printStackTrace()
}


fun hideKeyboard() = try {
    (MainActivity.context?.get() as Activity).apply {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }
} catch (e: Exception) {
    e.printStackTrace()
}


/**
 * Show Snack Bar
 * */
@SuppressLint("CutPasteId")
fun showSnackBar(string: String, type: Int = 1, navController: NavController? = null) = try {
    hideKeyboard()
    MainActivity.context.get()?.let { context ->
        val message = if (string.contains("Unable to resolve host")) {
            context.getString(R.string.no_internet_connection)
        } else if (string.contains("DOCTYPE html")) {
            context.getString(R.string.something_went_wrong)
        } else if (string.contains("<script>")) {
            context.getString(R.string.something_went_wrong)
        } else if (string.contains("SQLSTATE")) {
            context.getString(R.string.something_went_wrong)
        } else {
            "" + string
        }
        Snackbar.make(
            (context as Activity).findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_LONG
        ).apply {
            setBackgroundTint(ContextCompat.getColor(context, R.color.white))
            animationMode = Snackbar.ANIMATION_MODE_SLIDE
            setTextColor(ContextCompat.getColor(context, R.color._2A3740))
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).textSize =
                (MainActivity.scale10 + 1).toFloat()
            view.findViewById<TextView>(com.google.android.material.R.id.snackbar_text).maxLines = 5
            if (type == 2) {
//                setAction(context.getString(R.string.subscribe_now), View.OnClickListener {
//                    navController?.navigate(R.id.subscription)
//                    this.dismiss()
//                }).setActionTextColor(ContextCompat.getColor(context, R.color._ffffff))
//                view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action ).textSize =
//                    (MainActivity.scale10 + 1).toFloat()
//                view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action ).isAllCaps = false
            }
            show()
        }
    }
} catch (e: Exception) {
    e.printStackTrace()
}


/**
 * Handle Error Messages
 * */
fun Any?.getErrorMessage(): String = when (this) {
    is Throwable -> this.message.getResponseError()
    else -> this.toString().getResponseError()
}

/**
 * Get Response error
 * */
fun String?.getResponseError(): String {
    if (this.isNullOrEmpty()) return ""
    return try {
        val jsonObject = JSONObject(this)
        if (jsonObject.has("message")) {
            jsonObject.getString("message")
        } else if (jsonObject.has("errors")) {
            val array = jsonObject.getJSONArray("errors")
            if (array.length() > 0) {
                array.getJSONObject(0)?.let {
                    if (it.has("message"))
                        return it.getString("message")
                }
            }
            this
        } else this
    } catch (e: Exception) {
        this
    }
}


fun Context.getRealPathFromUri(contentUri: Uri?): String? {
    var cursor: Cursor? = null
    return try {
        val proj = arrayOf<kotlin.String>(MediaStore.Images.Media.DATA)
        //val proj: Array<String>= arrayOf<kotlin.String>(MediaStore.Images.Media.DATA)
        cursor = contentResolver.query(contentUri!!, proj, null, null, null)
        assert(cursor != null)
        val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        cursor.getString(column_index)
    } finally {
        cursor?.close()
    }
}


fun Context.getMediaFilePathFor(uri: Uri): String {
    return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
//        val name = cursor.getString(nameIndex)
        File(filesDir, getImageName()).run {
            kotlin.runCatching {
                contentResolver.openInputStream(uri).use { inputStream ->
                    val outputStream = FileOutputStream(this)
                    var read: Int
                    val buffers = ByteArray(inputStream!!.available())
                    while (inputStream.read(buffers).also { read = it } != -1) {
                        outputStream.use {
                            it.write(buffers, 0, read)
                        }
                    }
                }
            }.onFailure {
                ///Logger.error("File Size %s", it.message.orEmpty())
            }
            return path
        }
    } ?: ""
}


@SuppressLint("CheckResult")
fun ImageView.loadImage(
    type: Int,
    url: () -> String,
    errorPlaceHolder: () -> Int = { if (type == 1) R.drawable.user_icon else R.drawable.no_image_banner }
) = try {
    val circularProgressDrawable = CircularProgressDrawable(this.context).apply {
        strokeWidth = 5f
        centerRadius = 30f
        start()
    }
    load(if (url().startsWith("http")) url() else File(url())) {
        placeholder(circularProgressDrawable)
        crossfade(true)
        error(errorPlaceHolder())
    }
} catch (e: Exception) {
    e.printStackTrace()
}


fun isValidPassword(password: String): Boolean {
    val pattern: Pattern
    val matcher: Matcher
    val specialCharacters = "-@%\\[\\}+'!/#$^?:;,\\(\"\\)~`.*=&\\{>\\]<_"
    val PASSWORD_REGEX =
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[$specialCharacters])(?=\\S+$).{8,20}$"
    pattern = Pattern.compile(PASSWORD_REGEX)
    matcher = pattern.matcher(password)
    return matcher.matches()
}


fun AppCompatEditText.focus() {
//    text?.let { setSelection(it.length) }
//    postDelayed({
//        requestFocus()
//        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//        imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
//    }, 100)
}


var runnable: Runnable? = null
fun ViewPager.autoScroll() {
    autoScrollStop()
    var scrollPosition = 0
    runnable = object : Runnable {
        override fun run() {
            val count = adapter?.count ?: 0
            setCurrentItem(scrollPosition++ % count, true)
            if (handler != null) {
                handler?.let {
                    postDelayed(this, 3000)
                }
            }
        }
    }
    if (handler != null) {
        if (runnable != null) {
            handler?.let {
                post(runnable as Runnable)
            }
        }
    }
}


fun ViewPager.autoScrollStop() {
    if (handler != null) {
        if (runnable != null) {
            runnable?.let { handler?.removeCallbacks(it) }
        }
    }
}


@SuppressLint("SimpleDateFormat")
fun String.changeDateFormat(inDate: String, outDate: String): String? {
    var str: String? = ""
    try {
        val inputFormat = SimpleDateFormat(inDate)
        val outputFormat = SimpleDateFormat(outDate)
        var date: Date? = null
        try {
            date = inputFormat.parse(this)
            str = date?.let { outputFormat.format(it) }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    } catch (e: Exception) {
        str = ""
    }
    return str
}


@SuppressLint("ClickableViewAccessibility")
fun AppCompatEditText.onRightDrawableClicked(onClicked: (view: EditText) -> Unit) {
    this.setOnTouchListener { v, event ->
        var hasConsumed = false
        if (v is EditText) {
            if (event.x >= v.width - v.totalPaddingRight) {
                if (event.action == MotionEvent.ACTION_UP) {
                    onClicked(this)
                }
                hasConsumed = true
            }
        }
        hasConsumed
    }
}


//fun ArrayList<String>.imageZoom(ivImage: ImageView, type: Int) {
//    StfalconImageViewer.Builder<String>(MainActivity.mainActivity.get()!!, this) { view, image ->
//        Glide.with(MainActivity.mainActivity.get()!!)
//            .load(image)
//            .apply(if (type == 1) myOptionsGlide else if (type == 2) myOptionsGlideUser else myOptionsGlide)
//            .into(view)
//    }
//        .withTransitionFrom(ivImage)
//        .withBackgroundColor(
//            ContextCompat.getColor(
//                MainActivity.mainActivity.get()!!,
//                R.color._D9000000
//            )
//        )
//        .show()
//}


fun getToken(callBack: String.() -> Unit) {
    FirebaseMessaging.getInstance().token.addOnSuccessListener { result ->
        if (result != null) {
            callBack(result)
        }
    }.addOnCanceledListener {
        callBack("")
    }
}


fun String.firstCharIfItIsLowercase() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}


fun Context.isTablet(): Boolean {
    return this.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
}


fun ViewPager2.updatePagerHeightForChild(view: View) {
    view.post {
        val wMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.width, View.MeasureSpec.EXACTLY)
        val hMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        view.measure(wMeasureSpec, hMeasureSpec)
        layoutParams = (layoutParams).also { lp -> lp.height = height }
        invalidate()
    }
}


//val myOptionsGlide: RequestOptions = RequestOptions()
////    .placeholder(R.drawable.no_image)
//    .diskCacheStrategy(DiskCacheStrategy.ALL)
//    .dontAnimate()
//    .apply(RequestOptions().placeholder(R.drawable.place_image))
//    .skipMemoryCache(true)
//
//val myOptionsGlideUser: RequestOptions = RequestOptions()
//    .placeholder(R.drawable.user_icon)
//    .diskCacheStrategy(DiskCacheStrategy.ALL)
//    .dontAnimate()
//    //  .apply( RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.no_image_2))
//    .skipMemoryCache(false)

fun String.glideImage(context: Context, ivMap: ShapeableImageView) {
//    Glide.with(context)
//        .load(this)
//        .apply(myOptionsGlide)
//        .into(ivMap)

//    Log.e("TAG", "thisXX "+this)

//    Glide.with(context)
//        .load(this.replace("https://", "http://"))
////                .transition(withCrossFade(factory))
//        .apply(RequestOptions().placeholder(R.drawable.place_image))
//        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
//        .into(ivMap)

    Picasso.get().load(this)
        .placeholder(R.drawable.place_image)
//        .memoryPolicy(MemoryPolicy.NO_CACHE)
        .into(ivMap)
}



fun String.glideImageWithoutPlace(context: Context, ivMap: ShapeableImageView) {
//    Glide.with(context)
//        .load(this)
////                .transition(withCrossFade(factory))
//        .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
//        .into(ivMap)

//    Picasso.get().load(this)
//        .placeholder(R.drawable.place_image)
////        .memoryPolicy(MemoryPolicy.NO_CACHE)
//        .into(ivMap)

    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 50f
    circularProgressDrawable.setColorSchemeColors(ContextCompat.getColor(context, R.color.app_color))
    circularProgressDrawable.start()

    Glide.with(context)
        .load(this)
        .placeholder(circularProgressDrawable)
        .into(ivMap)

}



//val myOptionsGlideUserChache: RequestOptions = RequestOptions()
//    .diskCacheStrategy(DiskCacheStrategy.ALL)
//    .dontAnimate()
//    //  .apply( RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.no_image_2))
//    .skipMemoryCache(true)

fun String.glideImageChache(context: Context, ivMap: ShapeableImageView) {
//    Glide.with(context)
//        .load(this)
//        .apply(myOptionsGlideUserChache)
//        .into(ivMap)

    Picasso.get().load(this)
        .placeholder(R.drawable.place_image)
//        .memoryPolicy(MemoryPolicy.NO_CACHE)
        .into(ivMap)
}


fun Int.glideImageChache(context: Context, ivMap: AppCompatImageView) {
//    Glide.with(context)
//        .load(this)
//        .apply(myOptionsGlideUserChache)
//        .into(ivMap)

    Picasso.get().load(this)
        .placeholder(R.drawable.place_image)
//        .memoryPolicy(MemoryPolicy.NO_CACHE)
        .into(ivMap)
}


fun String.glidePhotoView(context: Context, ivMap: PhotoView) {
//    Glide.with(context)
//        .load(this)
//        .apply(myOptionsGlide)
//        .into(ivMap)
    val circularProgressDrawable = CircularProgressDrawable(context)
    circularProgressDrawable.strokeWidth = 5f
    circularProgressDrawable.centerRadius = 50f
    circularProgressDrawable.setColorSchemeColors(ContextCompat.getColor(context, R.color.app_color))
    circularProgressDrawable.start()

    Glide.with(context)
        .load(this)
        .placeholder(circularProgressDrawable)
        .into(ivMap)

//        Picasso.get().load(this)
//        .placeholder(R.drawable.place_image)
////        .memoryPolicy(MemoryPolicy.NO_CACHE)
//        .into(ivMap)
}

//val myOptionsGlidePortrait: RequestOptions = RequestOptions()
//    .placeholder(R.drawable.no_image_modified)
//    .diskCacheStrategy(DiskCacheStrategy.ALL)
//    .dontAnimate()
//    //  .apply( RequestOptions().centerCrop().circleCrop().placeholder(R.drawable.no_image_2))
//    .skipMemoryCache(false)

//fun String.glideImagePortrait(context: Context, ivMap: ShapeableImageView) {
////    Glide.with(context)
////        .load(this)
////        .apply(myOptionsGlidePortrait)
////        .into(ivMap)
//
//    Picasso.get().load(this)
//        .placeholder(R.drawable.no_image_modified)
////        .memoryPolicy(MemoryPolicy.NO_CACHE)
//        .into(ivMap)
//}



//val myOptionsGlideLand: RequestOptions = RequestOptions()
//    .placeholder(R.drawable.no_image_long)
//    .diskCacheStrategy(DiskCacheStrategy.ALL)
//    .dontAnimate()
//    .skipMemoryCache(true)

fun glideImageBanner(context: Context, ivMap: ShapeableImageView, s: String) {
//    Glide.with(context)
//        .load(s)
//        .apply(myOptionsGlideLand)
//        .into(ivMap)

    Picasso.get().load(s)
        .placeholder(R.drawable.place_image_banner)
//        .memoryPolicy(MemoryPolicy.NO_CACHE)
        .into(ivMap)
}


fun View.singleClick(throttleTime: Long = 600L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0
        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < throttleTime) return
            else action()
            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}


fun Context.isAppIsInBackground(): Boolean {
    var isInBackground = true
    try {
        val am = this.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = am.runningAppProcesses
        for (processInfo in runningProcesses) {
            if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                for (activeProcess in processInfo.pkgList) {
                    if (activeProcess == this.packageName) {
                        isInBackground = false
                    }
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }
    return isInBackground
}


fun Context.px(@DimenRes dimen: Int): Int = resources.getDimension(dimen).toInt()

fun Context.dp(@DimenRes dimen: Int): Float = px(dimen) / resources.displayMetrics.density


fun String.relationType(array: Array<String>): String {
    "" + this.let {
        return when (it) {
            "father" -> {
                array[0]
            }

            "mother" -> {
                array[1]
            }

            "son" -> {
                array[2]
            }

            "daughter" -> {
                array[3]
            }

            "sister" -> {
                array[4]
            }

            "brother" -> {
                array[5]
            }

            "husband" -> {
                array[6]
            }

            "wife" -> {
                array[7]
            }

            else -> {
                ""
            }
        }
    }
}


@Throws(Exception::class)
fun String.parseResult(): String {
    var words = ""
    val jsonArray = JSONArray(this)
    val jsonArray2 = jsonArray[0] as JSONArray
    for (i in 0..jsonArray2.length() - 1) {
        val jsonArray3 = jsonArray2[i] as JSONArray
        words += jsonArray3[0].toString()
    }
    return words.toString()
}


var networkAlert: AlertDialog? = null

@SuppressLint("SuspiciousIndentation")
fun Context.callNetworkDialog() {
    if (networkAlert?.isShowing == true) {
        return
    }
    networkAlert = MaterialAlertDialogBuilder(this, R.style.LogoutDialogTheme)
        .setTitle(resources.getString(R.string.app_name))
        .setMessage(resources.getString(R.string.no_internet_connection))
        .setPositiveButton(resources.getString(R.string.ok)) { dialog, _ ->
            dialog.dismiss()
        }
//        .setCancelable(false)
        .show()
}


fun Context.isNetworkAvailable() =
    (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).run {
        getNetworkCapabilities(activeNetwork)?.run {
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } ?: false
    }


@RequiresApi(Build.VERSION_CODES.TIRAMISU)
inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}


fun String.parseOneTimeCode(): String {
    return if (this != null && this.length >= 6) {
        this.trim { it <= ' ' }.substring(0, 6)
    } else ""
}


@Suppress("DEPRECATION")
inline fun <reified P : Parcelable> Intent.getParcelable(key: String): P? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, P::class.java)
    } else {
        getParcelableExtra(key)
    }
}

fun Context.determineScreenDensityCode(): String {
    return when (resources.displayMetrics.densityDpi) {
        DisplayMetrics.DENSITY_LOW -> "ldpi"
        DisplayMetrics.DENSITY_MEDIUM -> "mdpi"
        DisplayMetrics.DENSITY_HIGH -> "hdpi"
        DisplayMetrics.DENSITY_XHIGH, DisplayMetrics.DENSITY_280 -> "xhdpi"
        DisplayMetrics.DENSITY_XXHIGH, DisplayMetrics.DENSITY_360, DisplayMetrics.DENSITY_400, DisplayMetrics.DENSITY_420 -> "xxhdpi"
        DisplayMetrics.DENSITY_XXXHIGH, DisplayMetrics.DENSITY_560 -> "xxxhdpi"
        else -> "Unknown code ${resources.displayMetrics.densityDpi}"
    }
}


fun Context.getDensityName(): String {
    val density = resources.displayMetrics.density
    if (density >= 4.0) {
        return "xxxhdpi"
    }
    if (density >= 3.0) {
        return "xxhdpi"
    }
    if (density >= 2.0) {
        return "xhdpi"
    }
    if (density >= 1.5) {
        return "hdpi"
    }
    return if (density >= 1.0) {
        "mdpi"
    } else "ldpi"
}


@RequiresApi(Build.VERSION_CODES.P)
fun Context.getSignature(): String {
    var info: PackageInfo? = null
    try {
        info = packageManager.getPackageInfo(
            packageName,
            PackageManager.GET_SIGNING_CERTIFICATES
        )
        val sigHistory: Array<Signature> = info.signingInfo!!.signingCertificateHistory
        val signature: ByteArray = sigHistory[0].toByteArray()
        val md = MessageDigest.getInstance("SHA1")
        val digest = md.digest(signature)
        val sha1Builder = StringBuilder()
        for (b in digest) sha1Builder.append(String.format("%02x", b))
        return sha1Builder.toString()
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }
    return ""
}


fun Double.decimal2Digits(): String {
    return String.format("%.2f", this)
}

fun Double.roundOffDecimal(): String { //here, 1.45678 = 1.46
    val df = DecimalFormat("####0.00", DecimalFormatSymbols(Locale.ENGLISH))
    df.roundingMode = RoundingMode.HALF_UP
    return "" + df.format(this)
}

//fun roundOffDecimal(number: Double): Double? { //here, 1.45678 = 1.45
//    val df = DecimalFormat("#.##")
//    df.roundingMode = RoundingMode.FLOOR
//    return df.format(number).toDouble()
//}

fun Activity.getCameraPath(callBack: Uri.() -> Unit) {
    runOnUiThread() {
        val directory = File(filesDir, "camera_images")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val uriReal = FileProvider.getUriForFile(
            this,
            packageName + ".provider",
            File(directory, "${Calendar.getInstance().timeInMillis}.png")
        )
        callBack(uriReal)
    }
}


fun getImageName(): String {
    return "${"klifora_" + SimpleDateFormat("HHmmss").format(Date())}.png"
}


@SuppressLint("SimpleDateFormat")
fun getPdfName(): String {
    return "${"klifora_" + SimpleDateFormat("HHmmss").format(Date())}.pdf"
}


//fun Activity.showOptions(callBack: Int.() -> Unit) = try {
//    val dialogView = layoutInflater.inflate(R.layout.dialog_choose_image_option, null)
//    val btnCancel = dialogView.findViewById<AppCompatButton>(R.id.btnCancel)
//    val tvPhotos = dialogView.findViewById<AppCompatTextView>(R.id.tvPhotos)
//    val tvPhotosDesc = dialogView.findViewById<AppCompatTextView>(R.id.tvPhotosDesc)
//    val tvCamera = dialogView.findViewById<AppCompatTextView>(R.id.tvCamera)
//    val tvCameraDesc = dialogView.findViewById<AppCompatTextView>(R.id.tvCameraDesc)
//    val dialog = BottomSheetDialog(this, R.style.TransparentDialog)
//    dialog.setContentView(dialogView)
//    dialog.show()
//
//    btnCancel.singleClick {
//        dialog.dismiss()
//    }
//    tvCamera.singleClick {
//        dialog.dismiss()
//        callBack(1)
//    }
//    tvCameraDesc.singleClick {
//        dialog.dismiss()
//        callBack(1)
//    }
//
//    tvPhotos.singleClick {
//        dialog.dismiss()
//        callBack(2)
//    }
//    tvPhotosDesc.singleClick {
//        dialog.dismiss()
//        callBack(2)
//    }
//} catch (e: Exception) {
//    e.printStackTrace()
//}


@SuppressLint("SuspiciousIndentation")
fun Activity.callPermissionDialog(callBack: Intent.() -> Unit) {
    MaterialAlertDialogBuilder(this, R.style.LogoutDialogTheme)
        .setTitle(resources.getString(R.string.app_name))
        .setMessage(resources.getString(R.string.required_permissions))
        .setPositiveButton(resources.getString(R.string.yes)) { dialog, _ ->
            dialog.dismiss()
            val i = Intent()
            i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            i.addCategory(Intent.CATEGORY_DEFAULT)
            i.data = Uri.parse("package:" + packageName)
            callBack(i)
        }
        .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
            Handler(Looper.getMainLooper()).postDelayed({
//                MainActivity.binding.drawerLayout.close()
            }, 500)
        }
        .setCancelable(false)
        .show()
}


fun String.orderId(): String {
    return if (this.length > 3) this.substring(
        4,
        this.length
    ) + "-" + SimpleDateFormat(
        "yyMMdd-HHmmss",
        Locale.ENGLISH
    ).format(Calendar.getInstance().time) else ""
}

fun dateTime(): String {
    return SimpleDateFormat(
        "dd-MM-yyyy HH:mm:ss",
        Locale.ENGLISH
    ).format(Calendar.getInstance().time)
}


fun gen(): Int {
    val r = Random(System.currentTimeMillis())
    return 1000000000 + r.nextInt(2000000000)
}


fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap {
    var drawable = ContextCompat.getDrawable(context!!, drawableId)
    if (SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        drawable = DrawableCompat.wrap(drawable!!).mutate()
    }
    val bitmap = Bitmap.createBitmap(
        drawable!!.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
    )
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}


fun saveImageInQ(context: Context?, bitmap: Bitmap): Uri {
    val filename = getImageName()
    var fos: OutputStream? = null
    var imageUri: Uri? = null
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        put(MediaStore.Video.Media.IS_PENDING, 1)
    }

    val contentResolver = context!!.contentResolver
    contentResolver.also { resolver ->
        imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        fos = imageUri?.let { resolver.openOutputStream(it) }
    }
    fos?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 70, it) }
    contentValues.clear()
    contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
    contentResolver.update(imageUri!!, contentValues, null, null)
    return imageUri!!
}


@RequiresApi(Build.VERSION_CODES.O)
fun getDateToLongTime(amount: String): Long {
    val current1 =
        LocalDate.parse(amount, DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH))
    val millis1: Long = SimpleDateFormat("yyyy-MM-dd").parse(current1.toString())?.time ?: 0
    return millis1
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDateToLongTimeNow(): Long {
    val current1 =
        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH))
    val millis1: Long = SimpleDateFormat("yyyy-MM-dd").parse(current1.toString())?.time ?: 0
    return millis1
}

fun String.toDate(dateFormat: String = "yyyy-MM-dd HH:mm:ss", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date? {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)
}

fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    formatter.timeZone = timeZone
    return formatter.format(this)
}

fun String.getDate(): String {
    try {
        val formatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        formatter.timeZone = TimeZone.getTimeZone("UTC")
        val value: Date? = formatter.parse(this)

        val dateFormatter: SimpleDateFormat =
            SimpleDateFormat("hh:mm a") //this format changeable
        dateFormatter.setTimeZone(TimeZone.getDefault())
        return dateFormatter.format(value!!)

        //Log.d("ourDate", ourDate);
    } catch (e: Exception) {
        return "0000-00-00 00:00:00"
    }
}


fun Double.round(decimals: Int = 2): Double = "%.${decimals}f".format(this).toDouble()

fun dpToPx(dp: Int): Int {
    val displayMetrics: DisplayMetrics =
        MainActivity.context.get()?.getResources()!!.getDisplayMetrics()
    return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}


fun pxToDp(px: Int): Int {
    val displayMetrics: DisplayMetrics =
        MainActivity.context.get()?.getResources()!!.getDisplayMetrics()
    return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
}


fun ArrayList<String>.imageZoom(ivImage: ImageView, type: Int, position: Int) {
    StfalconImageViewer.Builder<String>(MainActivity.mainActivity.get()!!, this) { view, image ->
//        Glide.with(MainActivity.mainActivity.get()!!)
//            .load(image)
//            .apply(if (type == 1) myOptionsGlide else if (type == 2) myOptionsGlideUser else myOptionsGlide)
//            .into(view)
        if (type == 1) {
            Picasso.get().load(image)
                .placeholder(R.drawable.place_image)
                .into(view)

        } else if (type == 2) {
            Picasso.get().load(image)
                .placeholder(R.drawable.user_icon)
                .into(view)
        } else {

            Picasso.get().load(image)
                .placeholder(R.drawable.place_image)
                .into(view)
        }


    }
        .withTransitionFrom(ivImage)
        .withBackgroundColor(
            ContextCompat.getColor(
                MainActivity.mainActivity.get()!!,
                R.color._D9000000
            )
        ).withStartPosition(position)
        .show()
}


fun ArrayList<String>.imageZoom(ivImage: ImageView, type: Int) {
    StfalconImageViewer.Builder<String>(MainActivity.mainActivity.get()!!, this) { view, image ->
        if (type == 1) {
//            Glide.with(MainActivity.mainActivity.get()!!)
//                .load(image)
//                .apply(RequestOptions().placeholder(R.drawable.place_image))
//                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
//                .into(view)
            Picasso.get().load(image)
                .placeholder(R.drawable.place_image)
                .into(view)
        } else if (type == 2) {
//            Glide.with(MainActivity.mainActivity.get()!!)
//                .load(image)
//                .apply(myOptionsGlideUser)
//                .into(view)
            Picasso.get().load(image)
                .placeholder(R.drawable.user_icon)
                .into(view)
        } else {
//            Glide.with(MainActivity.mainActivity.get()!!)
//                .load(image)
//                .apply(myOptionsGlide)
//                .into(view)

            Picasso.get().load(image)
                .placeholder(R.drawable.place_image)
                .into(view)
        }
    }
        .withTransitionFrom(ivImage)
        .withBackgroundColor(
            ContextCompat.getColor(
                MainActivity.mainActivity.get()!!,
                R.color._D9000000
            )
        )
        .show()
}




fun ViewPager2.getRecyclerView(): RecyclerView {
    val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
    recyclerViewField.isAccessible = true
    return recyclerViewField.get(this) as RecyclerView
}


inline fun <reified T : java.io.Serializable> Bundle.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getSerializable(key) as? T
}

inline fun <reified T : Serializable> Intent.serializable(key: String): T? = when {
    Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> getSerializableExtra(
        key,
        T::class.java
    )

    else -> @Suppress("DEPRECATION") getSerializableExtra(key) as? T
}


fun sessionExpired() {
    MaterialAlertDialogBuilder(MainActivity.mainActivity.get()!!, R.style.LogoutDialogTheme)
        .setTitle(MainActivity.mainActivity.get()!!.resources.getString(R.string.app_name))
        .setMessage(MainActivity.mainActivity.get()!!.resources.getString(R.string.sessionExpired))
        .setPositiveButton(MainActivity.mainActivity.get()!!.resources.getString(R.string.yes)) { dialog, _ ->
            dialog.dismiss()
            removeKey(LOGIN_DATA) {}
            removeKey(STORE_DETAIL) {}
            clearDataStore { }
//            findNavController().navigate(R.id.action_profile_to_login)
            MainActivity.mainActivity.get()!!.adminToken()
        }
        .setNegativeButton(MainActivity.mainActivity.get()!!.resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        .setCancelable(false)
        .show()
}


fun Double.getPatternFormat(type: String): Double {
//    var defaultPattern : Double = 0.0
    if (type.equals("0", ignoreCase = true)) {
        val nf: NumberFormat = NumberFormat.getInstance(Locale("en", "US"))
        nf.setMinimumFractionDigits(2)
        nf.setMaximumFractionDigits(2)
        return nf.format(this).toDouble()
        //            defaultPattern = "###,###,###,##0.00";
    } else if (type.equals("1", ignoreCase = true)) {
        val nf: NumberFormat = NumberFormat.getInstance(Locale("en", "IN"))
        nf.setMinimumFractionDigits(2)
        nf.setMaximumFractionDigits(2)
        return nf.format(this).toDouble()
        //            defaultPattern = "##,##,##,##0.00";
    } else if (type.equals("2", ignoreCase = true)) {
        val nf: NumberFormat = NumberFormat.getInstance(Locale("da", "DK"))
        nf.setMinimumFractionDigits(2)
        nf.setMaximumFractionDigits(2)
        return nf.format(this).toDouble()
        //            defaultPattern = "###.###.###.##0,00";
    } else if (type.equals("3", ignoreCase = true)) {
        val nf: NumberFormat = NumberFormat.getInstance(Locale("sk", "SK"))
        nf.setMinimumFractionDigits(2)
        nf.setMaximumFractionDigits(2)
        return nf.format(this).toDouble()
        //            defaultPattern = "### ### ### ###,00";
    }
    return 1.0
}


fun getPatternFormat(s: String, value: Double?): String {
    var defaultPattern = "0.0"
    if (s.equals("0", ignoreCase = true)) {
        val nf = NumberFormat.getInstance(Locale("en", "US"))
        nf.minimumFractionDigits = 2
        nf.maximumFractionDigits = 2
        defaultPattern = nf.format(value)
        //            defaultPattern = "###,###,###,##0.00";
    } else if (s.equals("1", ignoreCase = true)) {
        val nf = NumberFormat.getInstance(Locale("en", "IN"))
        nf.minimumFractionDigits = 2
        nf.maximumFractionDigits = 2
        defaultPattern = nf.format(value)
        //            defaultPattern = "##,##,##,##0.00";
    } else if (s.equals("2", ignoreCase = true)) {
        val nf = NumberFormat.getInstance(Locale("da", "DK"))
        nf.minimumFractionDigits = 2
        nf.maximumFractionDigits = 2
        defaultPattern = nf.format(value)
        //            defaultPattern = "###.###.###.##0,00";
    } else if (s.equals("3", ignoreCase = true)) {
        val nf = NumberFormat.getInstance(Locale("sk", "SK"))
        nf.minimumFractionDigits = 2
        nf.maximumFractionDigits = 2
        defaultPattern = nf.format(value)
        //            defaultPattern = "### ### ### ###,00";
    }
    return defaultPattern
}



fun getIntValue(vararg any: Any) : Int {
    return when(val tmp = any.first()) {
        is Number -> tmp.toInt()
        else -> throw Exception("not a number") // or do something else reasonable for your case
    }
}



fun getSize(size: Int) : Int {
    return when(size){
        4 -> 6
        5 -> 7
        6 -> 8
        7 -> 9
        27 -> 10
        24 -> 11
        31 -> 12
        32 -> 13
        33 -> 14
        34 -> 15
        35 -> 16
        36 -> 17
        37 -> 18
        38 -> 19
        39 -> 20
        40 -> 21
        41 -> 22
        42 -> 23
        43 -> 24
        44 -> 25
        45 -> 26
        46 -> 27
        47 -> 28
        48 -> 29
        49 -> 30
        else -> 0
    }

//    return when(val tmp = any.first()) {
//        is Number -> tmp.toInt()
//        else -> throw Exception("not a number") // or do something else reasonable for your case
//    }
}



fun AppCompatTextView.endDrawable(@DrawableRes id: Int = 0) {
    this.setCompoundDrawablesWithIntrinsicBounds(0, 0, id, 0)
}





fun Activity.showDropDownDialog(
    type: Int = 0,
    arrayList: Array<String?> = emptyArray(),
    callBack: ItemReturn.() -> Unit
) {
    hideKeyboard()

    when (type) {
        1 -> {
            val list=resources.getStringArray(R.array.type_array)
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.select_your_choice))
                .setItems(list) {_,which->
                    callBack(ItemReturn(which, list[which]))
                }.show()
        }
        2 -> {
            val list=resources.getStringArray(R.array.type_array_priority)
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.select_priority))
                .setItems(list) {_,which->
                    callBack(ItemReturn(which, list[which]))
                }.show()
        }
        3 -> {
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.select_order))
                .setItems(arrayList) { _, which ->
                    callBack(ItemReturn(which, arrayList[which]!!))
                }.show()
        }
        4 -> {
            val list=resources.getStringArray(R.array.type_array_subject)
            MaterialAlertDialogBuilder(this, R.style.DropdownDialogTheme)
                .setTitle(resources.getString(R.string.select_subject))
                .setItems(list) {_,which->
                    callBack(ItemReturn(which, list[which]))
                }.show()
        }
    }
}





fun Activity.showOptions(callBack: Int.() -> Unit) = try {
    val dialogView = layoutInflater.inflate(R.layout.dialog_choose_image_option, null)
    val btnCancel = dialogView.findViewById<AppCompatButton>(R.id.btnCancel)
    val tvPhotos = dialogView.findViewById<AppCompatTextView>(R.id.tvPhotos)
    val tvPhotosDesc = dialogView.findViewById<AppCompatTextView>(R.id.tvPhotosDesc)
    val tvCamera = dialogView.findViewById<AppCompatTextView>(R.id.tvCamera)
    val tvCameraDesc = dialogView.findViewById<AppCompatTextView>(R.id.tvCameraDesc)
    val dialog = BottomSheetDialog(this, R.style.TransparentDialog)
    dialog.setContentView(dialogView)
    dialog.show()

    btnCancel.singleClick {
        dialog.dismiss()
    }
    tvCamera.singleClick {
        dialog.dismiss()
        callBack(1)
    }
    tvCameraDesc.singleClick {
        dialog.dismiss()
        callBack(1)
    }

    tvPhotos.singleClick {
        dialog.dismiss()
        callBack(2)
    }
    tvPhotosDesc.singleClick {
        dialog.dismiss()
        callBack(2)
    }
} catch (e: Exception) {
    e.printStackTrace()
}



fun RecyclerView.isLastItemDisplaying(): Boolean {
    if (this.adapter!!.itemCount != 0) {
        val lastVisibleItemPosition =
            (this.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
        if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == this.adapter!!
                .itemCount - 1
        ) return true
    }
    return false
}



//fun getLocationFromAddress11(context: Context?, strAddress: String?): Location? {
//    val coder = Geocoder(context!!)
//    var address: List<Address>? = ArrayList()
//    val p1: Location = Location("pickup")
//    try {
//        address = coder.getFromLocationName(strAddress!!, 5)
//        if (address == null) {
//            return null
//        }
//        Log.e(TAG, "address111 $address")
//
//        if (address.size > 0) {
//            val location = address[0]
//            location.latitude
//            location.longitude
//            p1.setLatitude(location.latitude)
//            p1.setLongitude(location.longitude)
//        }
//    } catch (ex: IOException) {
//        ex.printStackTrace()
//        Log.e(TAG, "getMessage111 " + ex.message)
//    }
//    return p1
//}

public fun String.getLocationFromAddress(): LatLng {
    val coder = Geocoder(MainActivity.mainActivity.get()!!, Locale.getDefault())
    var p1: LatLng = LatLng(0.0, 0.0)
    try {
        val address: MutableList<Address>? = coder.getFromLocationName(this, 5)
        address?.let {
            if (it.size > 0) {
                address[0].let { add ->
                    p1 = LatLng(
                        (add.latitude),
                        (add.longitude)
                    )
                }
            }
            Log.d("locationFRR", "getLocationFromAddress: $p1")
        }
    } catch (e: IOException) {
        e.printStackTrace()
        Log.d("locationFRR", "getLocationFromAddress: ${e.message}")
    }
    return p1
}


fun isNumeric(str: String): Boolean {
    for (c in str.toCharArray()) {
        if (!Character.isDigit(c)) return false
    }
    return true
}


fun encodeImage(path: String): String {
    val imagefile = File(path)
    var fis: FileInputStream? = null
    try {
        fis = FileInputStream(imagefile)
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
    }
    val bm = BitmapFactory.decodeStream(fis)
    val baos = ByteArrayOutputStream()
    bm.compress(Bitmap.CompressFormat.JPEG, 20, baos)
    val b = baos.toByteArray()
    val encImage: String = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT)
    //Base64.de
    return encImage
}




fun isLocationEnabled(context: Context): Boolean {
    val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}


fun showGPSNotEnabledDialog(context: Context) {
    AlertDialog.Builder(context)
        .setTitle(context.getString(R.string.enable_gps))
        .setMessage(context.getString(R.string.required_for_this_app))
        .setCancelable(false)
        .setPositiveButton(context.getString(R.string.enable_now)) { _, _ ->
            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
        .show()
}


@SuppressLint("SuspiciousIndentation")
fun Activity.callPermissionDialogGPS(callBack: Intent.() -> Unit) {
    MaterialAlertDialogBuilder(this, R.style.LogoutDialogTheme)
        .setTitle(resources.getString(R.string.enable_gps))
        .setMessage(resources.getString(R.string.required_for_this_app))
        .setPositiveButton(resources.getString(R.string.enable_now)) { dialog, _ ->
            dialog.dismiss()
            callBack(Intent().apply {
                action = Settings.ACTION_LOCATION_SOURCE_SETTINGS
            })
        }
        .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }
        .setCancelable(false)
        .show()
}



fun String.getChannelName(): String {
    return if (this == "Sample") {
        "Sample"
    } else if (this == "notice") {
        "Notice"
    } else if (this == "training") {
        "Training"
    } else if (this == "Vendor Details" || this == "VendorDetails") {
        "Vendor Details"
    } else if (this == "information") {
        "Information Center"
    } else if (this == "Feedback") {
        "Complaints/Feedback"
    } else if (this == "membership") {
        "Membership"
    } else {
        "Others"
    }
}

fun String.getNotificationId(): Int {
    return if (this == "Sample") {
        1
    } else if (this == "notice") {
        2
    } else if (this == "training") {
        3
    } else if (this == "Vendor Details" || this == "VendorDetails") {
        4
    } else if (this == "information") {
        5
    } else if (this == "Feedback") {
        6
    } else if (this == "membership") {
        7
    } else {
        8
    }
}



fun Context.loadHtml(type: Int, callBack: String.() -> Unit) {
    var inputStream: InputStream = assets.open("web/aboutUs/about.html")
    when (type) {
        1 -> {
            inputStream = assets.open("web/aboutUs/about.html")
        }

        2 -> {
            inputStream = assets.open("web/privacyPolicy/privacy.html")
        }

        3 -> {
            inputStream = assets.open("web/termsConditions/terms.html")
        }
    }

    val size: Int = inputStream.available()
    val buffer = ByteArray(size)
    inputStream.read(buffer)
    inputStream.close()
    val str = String(buffer)
    callBack(str)
}


//fun convertToBase64(attachment: File): String {
//    return Base64.encodeToString(attachment.readBytes(), Base64.NO_WRAP)
//}



// filters -
// gender - male, female, kids ,
// category
// material - 14, 18, Platinum
// price - 1000 - 10000

//sort
//default
//price - low to high
//price - high to low


//seach api - search by sku and name, color, purity, ring size


//https://shop.vegasega.com/rest/default/V1/products/?searchCriteria[sortOrders][0][field]=price&searchCriteria[sortOrders][0][direction]=ASC

//not visibility 1
//visibility 2,3,4

//gold 12
//platinum 13

//rg 25
//yg 19
//gw 20


//26 9
//14 14
//15 18
//16 22
//17 24
//18 95



//val payloadHelper = PayloadHelper("INR", 100, "order_XXXXXXXXX")
//payloadHelper.name = "Gaurav Kumar"
//payloadHelper.description = "Description"
//payloadHelper.prefillEmail = "gaurav.kumar@example.com"
//payloadHelper.prefillContact = "9000090000"
//payloadHelper.prefillCardNum = "4386289407660153"
//payloadHelper.prefillCardCvv = "111"
//payloadHelper.prefillCardExp = "11/24"
//payloadHelper.prefillMethod = "card"
//payloadHelper.prefillName = "MerchantName"
//payloadHelper.sendSmsHash = true
//payloadHelper.retryMaxCount = 4
//payloadHelper.retryEnabled = true
//payloadHelper.color = "#000000"
//payloadHelper.allowRotation = true
//payloadHelper.rememberCustomer = true
//payloadHelper.timeout = 10
//payloadHelper.redirect = true
//payloadHelper.recurring = "1"
//payloadHelper.subscriptionCardChange = true
//payloadHelper.customerId = "cust_XXXXXXXXXX"
//payloadHelper.callbackUrl = "https://accepts-posts.request"
//payloadHelper.subscriptionId = "sub_XXXXXXXXXX"
//payloadHelper.modalConfirmClose = true
//payloadHelper.backDropColor = "#ffffff"
//payloadHelper.hideTopBar = true
//payloadHelper.notes = JSONObject("{\"remarks\":\"Discount to customer\"}")
//payloadHelper.readOnlyEmail = true
//payloadHelper.readOnlyContact = true
//payloadHelper.readOnlyName = true
//payloadHelper.image = "https://www.razorpay.com"
//// these values are set mandatorily during object initialization. Those values can be overridden like this
//payloadHelper.amount=100
//payloadHelper.currency="INR"
//payloadHelper.orderId = "order_XXXXXXXXXXXXXX"
