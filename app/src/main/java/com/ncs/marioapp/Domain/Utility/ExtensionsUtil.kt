package com.ncs.marioapp.Domain.Utility

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewPropertyAnimator
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.BaseTransientBottomBar.ANIMATION_MODE_SLIDE
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.ncs.marioapp.R
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


object ExtensionsUtil {

    fun getEligibilityScore(eligibility: String): Int{
        return when(eligibility){
            "NOOBIE"-> 1
            "INTERMEDIATE" -> 2
            "PRO"-> 3
            else -> 1
        }
    }

    fun getUserScoreForEligibilty(score:Int) : Int{
        return if (score<100){
            1
        }
        else if (score<400){
            2
        }
        else {
            3
        }
    }

    fun generateShareLink(postId:String, link:(Uri?) -> Unit){
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://ncsmario.page.link/share/${postId}"))
            .setDomainUriPrefix("https://ncsmario.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder("com.ncs.marioapp")
                    .setMinimumVersion(1)
                    .build()
            )
            .buildDynamicLink()
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLongLink(dynamicLink.uri)
            .buildShortDynamicLink()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val shortLink = task.result?.shortLink
                    link(shortLink!!)
                } else {
                    link(null)
                }
            }
    }

    fun generateEventShareLink(eventId:String, link:(Uri?) -> Unit){
        val dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLink(Uri.parse("https://ncsmario.page.link/event/${eventId}"))
            .setDomainUriPrefix("https://ncsmario.page.link")
            .setAndroidParameters(
                DynamicLink.AndroidParameters.Builder("com.ncs.marioapp")
                    .setMinimumVersion(1)
                    .build()
            )
            .buildDynamicLink()
        FirebaseDynamicLinks.getInstance().createDynamicLink()
            .setLongLink(dynamicLink.uri)
            .buildShortDynamicLink()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val shortLink = task.result?.shortLink
                    link(shortLink!!)
                } else {
                    link(null)
                }
            }
    }

    //Logging extensions

    fun Any?.printToLog(tag: String = "Debug Log") {
        Timber.tag(tag).d(toString())
    }



    fun showProgressDialog(context: Context, message: String): Dialog {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.progress_dialog, null)

        val lottieAnimationView: LottieAnimationView = view.findViewById(R.id.animationView)
        val textView: TextView = view.findViewById(R.id.textView)

        lottieAnimationView.setAnimation(R.raw.loading)
        lottieAnimationView.playAnimation()
        textView.text = message

        val dialog = AlertDialog.Builder(context)
            .setView(view)
            .setCancelable(false)
            .create()

        dialog.show()
        return dialog
    }


    // Visibililty Extensions

    fun View.gone() = run { visibility = View.GONE }
    fun View.visible() = run { visibility = View.VISIBLE }
    fun View.invisible() = run { visibility = View.INVISIBLE }

    infix fun View.visibleIf(condition: Boolean) =
        run { visibility = if (condition) View.VISIBLE else View.GONE }

    infix fun View.goneIf(condition: Boolean) =
        run { visibility = if (condition) View.GONE else View.VISIBLE }

    infix fun View.invisibleIf(condition: Boolean) =
        run { visibility = if (condition) View.INVISIBLE else View.VISIBLE }


    fun View.progressGone(context: Context, duration: Long = 1500L) = run {
        animFadeOut(context, duration)
        visibility = View.GONE

    }

    fun View.progressVisible(context: Context, duration: Long = 1500L) = run {
        visibility = View.VISIBLE
        animFadein(context, duration)
    }


    fun View.progressGoneSlide(context: Context, duration: Long = 1500L) = run {
        animSlideUp(context, duration)
        visibility = View.GONE

    }

    fun View.progressVisibleSlide(context: Context, duration: Long = 1500L) = run {
        visibility = View.VISIBLE
        animSlideDown(context, duration)
    }

    // Toasts

    fun Fragment.toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    fun Fragment.toast(@StringRes message: Int) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    fun Activity.toast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    fun Activity.toast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }


    //Snackbar

    fun View.snackbar(message: String, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).setAnimationMode(ANIMATION_MODE_SLIDE).show()
    }

    fun View.snackbar(@StringRes message: Int, duration: Int = Snackbar.LENGTH_LONG) {
        Snackbar.make(this, message, duration).show()
    }


    fun Activity.hideKeyboard() {
        val imm: InputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun Fragment.hideKeyboard() {
        activity?.apply {
            val imm: InputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            val view = currentFocus ?: View(this)
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun Fragment.showKeyboard(editBox: EditText) {
        activity?.apply {
            val imm: InputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInputFromInputMethod(editBox.windowToken, InputMethodManager.SHOW_FORCED)
        }
    }

    fun EditText.showKeyboardB() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        requestFocus()
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    }


    // Convert px to dp
    val Int.dp: Int
        get() = (this / Resources.getSystem().displayMetrics.density).toInt()

    //Convert dp to px
    val Int.px: Int
        get() = (this * Resources.getSystem().displayMetrics.density).toInt()


    val String.isDigitOnly: Boolean
        get() = matches(Regex("^\\d*\$"))

    val String.isAlphabeticOnly: Boolean
        get() = matches(Regex("^[a-zA-Z]*\$"))

    val String.isAlphanumericOnly: Boolean
        get() = matches(Regex("^[a-zA-Z\\d]*\$"))


    //Null check
    val Any?.isNull get() = this == null

    fun Any?.ifNull(block: () -> Unit) = run {
        if (this == null) {
            block()
        }
    }

    /**
     * Set Drawable to the left of EditText
     * @param icon - Drawable to set
     */
    fun EditText.setDrawable(icon: Drawable) {
        this.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null)
    }


    /**
     * Function to run a delayed function
     * @param millis - Time to delay
     * @param function - Function to execute
     */
    fun runDelayed(millis: Long, function: () -> Unit) {
        Handler(Looper.getMainLooper()).postDelayed(function, millis)
    }

    /**
     * Show multiple views
     */
    fun showViews(vararg views: View) {
        views.forEach { view -> view.visible() }
    }


    /**
     * Hide multiple views
     */
    fun hideViews(vararg views: View) {
        views.forEach { view -> view.gone() }
    }


    //Date formatting
    fun String.toDate(format: String = "yyyy-MM-dd HH:mm:ss"): Date? {
        val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
        return dateFormatter.parse(this)
    }

    fun Date.toStringFormat(format: String = "yyyy-MM-dd HH:mm:ss"): String {
        val dateFormatter = SimpleDateFormat(format, Locale.getDefault())
        return dateFormatter.format(this)
    }


    //Network check
    fun Context.isNetworkAvailable(): Boolean {
        val manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = manager.getNetworkCapabilities(manager.activeNetwork)
        return if (capabilities != null) {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                    || capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
        } else false
    }


    //Permission
    fun Context.isPermissionGranted(permission: String) = run {
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }


    fun ImageView.load(url: Any, placeholder: Drawable) {
        Glide.with(context)
            .setDefaultRequestOptions(RequestOptions().placeholder(placeholder))
            .load(url)
            .thumbnail(0.05f)
            .error(placeholder)
            .into(this)
    }


    fun isValidContext(context: Context?): Boolean {
        if (context == null) {
            return false
        }
        if (context is Activity) {
            val activity = context as Activity
            if (activity.isDestroyed || activity.isFinishing) {
                return false
            }
        }
        return true
    }


    fun ImageView.loadProfileImg(url: Any) {
        if (isValidContext(context)) {

            Glide.with(context)
                .load(url)
                .listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }
                })
                .encodeQuality(80)
                .override(40, 40)
                .apply(
                    RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .error(R.drawable.profile_pic_placeholder)
                .into(this)
        }
    }


    /**
     * Load image to ImageView
     * @param url - Url of the image, can be Int, drawable or String
     * @param placeholder - Placeholder to show when loading image
     * @param thumbnail - Image thumbnail url
     */
    fun ImageView.load(url: Any, placeholder: Int, thumbnail: String) {
        Glide.with(context)
            .setDefaultRequestOptions(
                RequestOptions()
                    .placeholder(placeholder)
            )
            .load(url)
            .thumbnail(Glide.with(context).asDrawable().load(thumbnail).thumbnail(0.1f))
            .into(this)
    }

    /**
     * Load image to ImageView
     * @param url - Url of the image, can be Int, drawable or String
     * @param placeholder - Placeholder to show when loading image
     * @param thumbnail - Image thumbnail url
     */
    fun ImageView.load(url: Any, placeholder: Drawable, thumbnail: String) {
        Glide.with(context)
            .setDefaultRequestOptions(
                RequestOptions()
                    .placeholder(placeholder)
            )
            .load(url)
            .thumbnail(Glide.with(context).asDrawable().load(thumbnail).thumbnail(0.1f))
            .into(this)
    }

    //Animation
    fun View.animSlideUp(context: Context, animDuration: Long = 1500L) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_bottom_to_up)
            .apply {
                duration = animDuration
            }
        this.startAnimation(animation)
    }

    fun View.animSlideDown(context: Context, animDuration: Long = 1500L) = run {
        this.clearAnimation()
        val animation =
            AnimationUtils.loadAnimation(context, me.shouheng.utils.R.anim.slide_top_to_bottom)
                .apply {
                    duration = animDuration
                }
        this.startAnimation(animation)
        this.gone()
    }


    fun View.animSlideUpVisible(context: Context, animDuration: Long = 1500L) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_bottom_to_up)
            .apply {
                duration = animDuration
            }
        this.startAnimation(animation)
        this.visible()
    }

    fun View.slideDownAndVisible(
        duration: Long = 300L,
        onEndAction: (() -> Unit)? = null
    ): ViewPropertyAnimator {
        return animate()
            .translationYBy(height.toFloat())
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                visibility = View.VISIBLE
                onEndAction?.invoke()
            }
    }

    fun View.slideUpAndGone(
        duration: Long = 300L,
        onEndAction: (() -> Unit)? = null
    ): ViewPropertyAnimator {
        visibility = View.GONE
        return animate()
            .translationYBy(-height.toFloat())
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                onEndAction?.invoke()
            }
    }

    fun View.slideDownAndGone(
        duration: Long = 300L,
        onEndAction: (() -> Unit)? = null
    ): ViewPropertyAnimator {
        return animate()
            .translationYBy(height.toFloat())
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                visibility = View.GONE
                onEndAction?.invoke()
            }
    }

    fun View.slideUpAndVisible(
        duration: Long = 300L,
        onEndAction: (() -> Unit)? = null
    ): ViewPropertyAnimator {
        visibility = View.VISIBLE
        return animate()
            .translationYBy(-height.toFloat())
            .setDuration(duration)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                onEndAction?.invoke()
            }
    }

    fun View.animSlideLeft(context: Context, animDuration: Long = 1500L) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_left)
            .apply {
                duration = animDuration
            }
        this.startAnimation(animation)
    }

    fun View.animSlideRight(context: Context, animDuration: Long = 1500L) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.slide_in_right)
            .apply {
                duration = animDuration
            }
        this.startAnimation(animation)
    }

    fun View.animFadein(context: Context, animDuration: Long = 200L) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_in)
            .apply {
                duration = animDuration
            }
        this.startAnimation(animation)
    }

    fun ImageView.loadImage(
        url: String,
        placeholder: Int = R.drawable.placeholder_image,
        skipMemoryCache: Boolean = false
    ) {
        val options = RequestOptions()
            .placeholder(placeholder)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(skipMemoryCache)

        Glide.with(this.context)
            .load(url)
            .apply(options)
            .into(this)
    }


    fun View.rotate180(context: Context, animDuration: Long = 500L) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.rotate180)
            .apply {
                duration = animDuration
            }
        this.startAnimation(animation)
    }

    fun View.set180(context: Context, animDuration: Long = 200L) {
        clearAnimation()
        val currentRotation = tag as? Float ?: 0f
        val targetRotation = if (currentRotation == 0f) 180f else 0f
        val rotationProperty =
            PropertyValuesHolder.ofFloat(View.ROTATION, currentRotation, targetRotation)
        val animator = ObjectAnimator.ofPropertyValuesHolder(this, rotationProperty)
            .apply {
                duration = animDuration
            }
        tag = targetRotation

        animator.start()
    }


    fun View.rotateInfinity(context: Context) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.rotateinfi)
        this.startAnimation(animation)
    }


    fun View.popInfinity(context: Context) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.popinfi)
        this.startAnimation(animation)
    }


    fun View.blink(context: Context) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.blink)
        this.startAnimation(animation)
    }

    fun View.blinkinfi(context: Context) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.blinkinf)
        this.startAnimation(animation)
    }

    fun View.rotateInfinityReverse(context: Context) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.rotateinfirev)
        this.startAnimation(animation)
    }

    fun View.animFadeOut(context: Context, animDuration: Long = 1500L) = run {
        this.clearAnimation()
        val animation =
            AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_out)
                .apply {
                    duration = animDuration
                }
        this.startAnimation(animation)

    }

    fun View.fadeIn(duration: Long = 300, startAlpha: Float = 0f, endAlpha: Float = 1f) {
        this.visibility = View.VISIBLE
        val fadeInAnimation = AlphaAnimation(startAlpha, endAlpha).apply {
            this.duration = duration
            fillAfter = true
        }
        this.startAnimation(fadeInAnimation)
    }

    fun View.popIn(duration: Long = 300, startScale: Float = 0.7f, endScale: Float = 1f) {
        this.visibility = View.VISIBLE

        // Scale animation to make the view grow from a smaller scale to its normal size
        val scaleAnimation = ScaleAnimation(
            startScale, endScale, // Start and end scale in X
            startScale, endScale, // Start and end scale in Y
            AnimationSet.RELATIVE_TO_SELF, 0.5f, // Pivot X
            AnimationSet.RELATIVE_TO_SELF, 0.5f  // Pivot Y
        ).apply {
            interpolator = DecelerateInterpolator()
        }

        // Fade-in animation for added effect
        val alphaAnimation = AlphaAnimation(0f, 1f).apply {
        }

        // Combine both animations
        val animationSet = AnimationSet(true).apply {
            addAnimation(scaleAnimation)
            addAnimation(alphaAnimation)
        }

        this.startAnimation(animationSet)
    }


    fun TextView.startBlinking() {
        val fadeOut = ObjectAnimator.ofFloat(this, "alpha", 1f, 0.5f)
        fadeOut.duration = 800

        val fadeIn = ObjectAnimator.ofFloat(this, "alpha", 0.5f, 1f)
        fadeIn.duration = 800

        // Set the blink to repeat infinitely
        fadeOut.repeatCount = ObjectAnimator.INFINITE
        fadeIn.repeatCount = ObjectAnimator.INFINITE

        // Reverse the order of animation
        fadeOut.repeatMode = ObjectAnimator.REVERSE
        fadeIn.repeatMode = ObjectAnimator.REVERSE

        fadeOut.start()
        fadeIn.start()
    }

    fun View.pulseEffect(duration: Long = 200, scaleFactor: Float = 1.2f) {
        val scaleUp = ObjectAnimator.ofFloat(this, "scaleX", 1f, scaleFactor).apply { }
        val scaleDown = ObjectAnimator.ofFloat(this, "scaleX", scaleFactor, 1f).apply { }
        val scaleYUp = ObjectAnimator.ofFloat(this, "scaleY", 1f, scaleFactor).apply { }
        val scaleYDown = ObjectAnimator.ofFloat(this, "scaleY", scaleFactor, 1f).apply { }

        val animatorSet = AnimatorSet().apply {
            play(scaleUp).with(scaleYUp)
            play(scaleDown).with(scaleYDown).after(scaleUp)
        }
        animatorSet.start()
    }

    fun ImageView.animateHeartFill(duration: Long = 300) {
        val scaleUp = ObjectAnimator.ofFloat(this, "scaleX", 1f, 1.3f).apply { }
        val scaleYUp = ObjectAnimator.ofFloat(this, "scaleY", 1f, 1.3f).apply { }
        val scaleDown = ObjectAnimator.ofFloat(this, "scaleX", 1.3f, 1f).apply { }
        val scaleYDown = ObjectAnimator.ofFloat(this, "scaleY", 1.3f, 1f).apply { }

        val animatorSet = AnimatorSet().apply {
            play(scaleUp).with(scaleYUp)
            play(scaleDown).with(scaleYDown).after(scaleUp)
        }

        animatorSet.start()
    }

    fun View.bubblePopIn(
        duration: Long = 500,
        startScale: Float = 0.2f,
        endScale: Float = 1f,
        bounceHeight: Float = 50f
    ) {
        this.visibility = View.VISIBLE

        val scaleAnimation = ScaleAnimation(
            startScale, endScale,
            startScale, endScale,
            android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f,
            android.view.animation.Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            interpolator = DecelerateInterpolator()
        }

        val alphaAnimation = AlphaAnimation(0f, 1f).apply {
        }

        val translateAnimation = TranslateAnimation(
            0f, 0f,
            -bounceHeight, 0f
        ).apply {
            interpolator = DecelerateInterpolator()
        }

        val animationSet = AnimationSet(true).apply {
            addAnimation(scaleAnimation)
            addAnimation(alphaAnimation)
            addAnimation(translateAnimation)
        }

        this.startAnimation(animationSet)
    }


    fun View.bounce(context: Context, animDuration: Long = 500L) = run {
        this.clearAnimation()
        val animation = AnimationUtils.loadAnimation(context, R.anim.bounce)
            .apply {
                duration = animDuration
            }
        this.startAnimation(animation)
    }
    private const val SHORT_HAPTIC_FEEDBACK_DURATION = 5L
    @RequiresApi(Build.VERSION_CODES.O)
    fun Context.performHapticFeedback() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val vibrationEffect = VibrationEffect.createOneShot(5L, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(vibrationEffect)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Context.performShakeHapticFeedback() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val timings = longArrayOf(0, 500)
        val amplitudes = intArrayOf(VibrationEffect.DEFAULT_AMPLITUDE, VibrationEffect.EFFECT_HEAVY_CLICK,)
        val vibrationEffect = VibrationEffect.createWaveform(timings, amplitudes, -1)
        vibrator.vibrate(vibrationEffect)
    }





    fun TextInputEditText.appendTextAtCursor(textToAppend: String) {
        val start = selectionStart
        val end = selectionEnd

        val editable = text

        editable?.replace(start, end, textToAppend)
        setSelection(start + textToAppend.length)
    }


    fun View.setMargins(left: Int, top: Int, right: Int, bottom: Int) {
        if (this.layoutParams is ViewGroup.MarginLayoutParams) {
            val params = this.layoutParams as ViewGroup.MarginLayoutParams
            params.setMargins(left, top, right, bottom);
        }
    }

    fun TextInputEditText.appendTextAtCursorMiddleCursor(textToAppend: String, type: Int) {
        val position = this.selectionStart
        val text = this.text
        val newText = StringBuilder(text).insert(position, textToAppend).toString()
        this.setText(newText)
        if (type == 2) this.setSelection(position + 1)
        else this.setSelection(position + 2)
    }


    fun View.setOnClickThrottleBounceListener(throttleTime: Long = 600L, onClick: () -> Unit) {

        this.setOnClickListener(object : View.OnClickListener {

            private var lastClickTime: Long = 0
            override fun onClick(v: View) {
                context?.let {
                    v.bounce(context)
                    if (SystemClock.elapsedRealtime() - lastClickTime < throttleTime) return
                    else onClick()
                    lastClickTime = SystemClock.elapsedRealtime()
                }

            }
        })
    }


    fun View.fadeOutAndGone(duration: Long = 300L) {
        val fadeOutAnimator = ObjectAnimator.ofFloat(this, "alpha", 1f, 0f)
        fadeOutAnimator.duration = duration

        fadeOutAnimator.addListener(object : android.animation.Animator.AnimatorListener {

            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                this@fadeOutAndGone.gone()
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })

        fadeOutAnimator.start()
    }

    fun View.fadeInAndVisible(duration: Long = 100L) {
        val fadeOutAnimator = ObjectAnimator.ofFloat(this, "alpha", 0f, 1f)
        fadeOutAnimator.duration = duration

        fadeOutAnimator.addListener(object : android.animation.Animator.AnimatorListener {

            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                this@fadeInAndVisible.visibility = View.VISIBLE

            }

            override fun onAnimationCancel(animation: Animator) {
                this@fadeInAndVisible.visibility = View.VISIBLE

            }

            override fun onAnimationRepeat(animation: Animator) {
            }
        })

        fadeOutAnimator.start()
    }

    fun View.setOnDoubleClickListener(listener: () -> Unit) {
        val doubleClickInterval = 500 // Adjust this value as needed (in milliseconds)
        var lastClickTime: Long = 0

        this.setOnClickListener { view ->
            val clickTime = SystemClock.uptimeMillis()
            if (clickTime - lastClickTime < doubleClickInterval) {
                // Double click detected
                context?.let {
                    context.performHapticFeedback()
                    listener.invoke()
                }

            }

            lastClickTime = clickTime
        }
    }





    fun deleteDownloadedFile(downloadID : Long, context: Context) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.remove(downloadID)
    }

    fun getVersionName(context: Context): String? {
        return try {
            val packageInfo: PackageInfo =
                context.packageManager.getPackageInfo(context.packageName, 0)
            packageInfo.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            null
        }
    }

    fun String.isGreaterThanVersion(otherVersion: String): Boolean {
        val thisParts = this.split(".").map { it.toInt() }
        val otherParts = otherVersion.split(".").map { it.toInt() }

        for (i in 0 until maxOf(thisParts.size, otherParts.size)) {
            val thisPart = thisParts.getOrNull(i) ?: 0
            val otherPart = otherParts.getOrNull(i) ?: 0

            if (thisPart != otherPart) {
                return thisPart > otherPart
            }
        }

        return false
    }


    fun View.setOnClickSingleTimeBounceListener(onClick: () -> Unit) {

        this.setOnClickListener(object : View.OnClickListener {
            private var clicked: Boolean = false
            override fun onClick(v: View) {
                //context.performHapticFeedback()
                v.bounce(context)
                if (clicked) return
                else onClick()
                clicked = true
            }
        })
    }

    inline fun View.setOnClickFadeInListener(crossinline onClick: () -> Unit) {
        setOnClickListener {
            // context.performHapticFeedback()
            it.animFadein(context, 100)
            onClick()
        }
    }


    fun View.setSingleClickListener(throttleTime: Long = 600L, action: () -> Unit) {
        this.setOnClickListener(object : View.OnClickListener {
            private var lastClickTime: Long = 0

            override fun onClick(v: View) {
                if (SystemClock.elapsedRealtime() - lastClickTime < throttleTime) return
                else action()
                lastClickTime = SystemClock.elapsedRealtime()
            }
        })
    }


    fun View.setBackgroundColorRes(colorResId: Int) {
        val color = context.resources.getColor(colorResId)
        setBackgroundColor(color)
    }

    fun View.setBackgroundColor(color: Int) {
        background = ColorDrawable(color)
    }

    fun View.setBackgroundDrawable(drawable: Drawable) {
        background = drawable
    }
}

