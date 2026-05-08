package com.chordsense.chordapp


import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import android.content.res.Configuration
import android.content.res.Resources
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.transition.ChangeBounds
import android.transition.TransitionManager.beginDelayedTransition
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.preference.PreferenceManager
import com.chordsense.chordapp.GlobalMethods.Companion.hide
import com.chordsense.chordapp.GlobalMethods.Companion.show
import com.chordsense.chordapp.ui.CSPlusFragment
import com.chordsense.chordapp.ui.chords.ChordsFragment
import com.chordsense.chordapp.ui.progressions.ProgressionsFragment
import com.chordsense.chordapp.ui.settings.FragmentContainerSettings
import com.github.gzuliyujiang.wheelview.contract.OnWheelChangedListener
import com.github.gzuliyujiang.wheelview.widget.WheelView
import com.google.android.material.navigation.NavigationBarView
import com.revenuecat.purchases.LogLevel
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.PurchasesConfiguration
import com.revenuecat.purchases.getCustomerInfoWith
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.purchaseWith
import java.util.Timer
import kotlin.concurrent.schedule


class MainActivity : AppCompatActivity() {

    companion object {
        var whiteKeyWidth = 0
        var whiteKeyHeight = 0
        lateinit var sharedPref : SharedPreferences
        lateinit var securePref : SharedPreferences
        var screenWidth = 0
        var screenHeight = 0
        var dp = 0F
        lateinit var res: Resources
        var landscape=false
        var color:Int = 0
        var intervalLabels = false
        var scaleModes = false
        var scrolling = false
        lateinit var soundMenu: ConstraintLayout
        var preset:String = "grand_piano"
        var scaledDensity = 0F
        var premium = false
        var freeSaves:Int = 2
        lateinit var fm: FragmentManager
        lateinit var active: Fragment
        lateinit var fragment1: Fragment
        lateinit var fragment2: Fragment
        lateinit var fragment3: Fragment
        var fragment4: Fragment? = null
        lateinit var navView:NavigationBarView
        lateinit var restart:View
    }
    lateinit var csplus : Package

    lateinit var layout:ConstraintLayout


    override fun onCreate(savedInstanceState: Bundle?) {

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val masterKey = MasterKey.Builder(this)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        securePref = EncryptedSharedPreferences.create(
            this,
            "chordsense_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        whiteKeyWidth = ((screenWidth -20* dp) / 17).toInt()
        whiteKeyHeight = ((screenHeight)*0.08).toInt()

        landscape = true


//        if (sharedPref.getBoolean("land", false)) {
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
//            landscape = true
//        } else {
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//        }

        super.onCreate(null)
        val scaledDensity: Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, resources.displayMetrics)

        screenWidth = resources.displayMetrics.widthPixels
        screenHeight = resources.displayMetrics.heightPixels
        dp = resources.displayMetrics.densityDpi/160F

//        if (sharedPref.getString("theme", "Light") == "Light") {
//            setTheme(R.style.Light)
//
//        }

        if (securePref.getBoolean("premium", false)) {
            premium = true
        }




        freeSaves = sharedPref.getInt("freeSaves", 0)


//        when (sharedPref.getString("theme", "Light")) {
//
//            "Light2" -> {
//                setTheme(R.style.Light2)
//
//            }
//
//            "Dark2" -> {
//                setTheme(R.style.Dark2)
//
//            }
//
//            "Dark3" -> {
//                setTheme(R.style.Dark3)
//
//            }
//
//            "Dark4" -> {
//                setTheme(R.style.Dark4)
//
//            }
//        }

        val typedValue = TypedValue()
        theme?.resolveAttribute(android.R.attr.colorPrimary, typedValue, true)
        color = typedValue.data


        res = resources
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {

            whiteKeyWidth = (((screenWidth -20* dp)*0.9)/36).toInt()

            whiteKeyHeight = ((screenWidth -66)*0.08).toInt()

        } else {
            whiteKeyWidth = ((screenWidth -20* dp) / 17).toInt()
            whiteKeyHeight = ((screenHeight)*0.08).toInt()

        }

        if (sharedPref.getBoolean("intervalLabels", false)) {
            intervalLabels = true

        }

        if (sharedPref.getBoolean("modes", false)) {
            scaleModes = true
        }

        if (sharedPref.getBoolean("scrolling", false)) {
            scrolling = true
        }
        try {
            Purchases.logLevel = LogLevel.DEBUG
            Purchases.configure(PurchasesConfiguration.Builder(this, "").build())
            Purchases.sharedInstance.getCustomerInfoWith(
                onError = { },
                onSuccess = { customerInfo ->
                    val isActive = customerInfo.entitlements["plus"]?.isActive == true
                    premium = isActive
                    securePref.edit().putBoolean("premium", isActive).apply()
                }
            )
            Purchases.sharedInstance.getOfferingsWith(
                onError = { error ->

                },
                onSuccess = { offerings ->
                    offerings.current

                    csplus = offerings["chordsense_plus"]?.lifetime!!


                })
        } catch(e:Exception) {

        }



        if (android.os.Build.VERSION.SDK_INT >= 34) {

        } else {
            actionBar?.hide()

        }


        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

        window.setDecorFitsSystemWindows(false)
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        decorView.systemUiVisibility = uiOptions
        setContentView(R.layout.activity_main)

        soundMenu = findViewById(R.id.sound_menu)
        val constraintSet = ConstraintSet()
        val container:ConstraintLayout= findViewById(R.id.container)
        constraintSet.clone(container)
        var navigating = false

        val loadingScreen :LinearLayout = findViewById(R.id.loading_screen)
        val loadingProgress : TextView = findViewById(R.id.loading_progress)
        Sounds.loadSounds(this, preset, loadingScreen, loadingProgress, this)

        val soundPresetDone = findViewById<ImageButton>(R.id.sound_preset_done)
        val soundPicker = findViewById<WheelView>(R.id.sound_preset_picker)
        val toggleNavViewButton = findViewById<Button>(R.id.toggle_nav_view)
        navView= findViewById(R.id.nav_view)
        val closeSoundMenu = findViewById<Button>(R.id.close_sound_menu)

        restart = findViewById(R.id.restart_menu)
        val ok = findViewById<ImageButton>(R.id.ok)

        ok.setOnClickListener {
            hide(restart)
        }


        fragment1= ChordsFragment()
        fragment2= ProgressionsFragment()
        fragment3 = FragmentContainerSettings()
        fm = supportFragmentManager
        active = fragment2

        fm.beginTransaction().add(R.id.nav_host, fragment3).hide(fragment3).commit()
        fm.beginTransaction().add(R.id.nav_host,fragment1).hide(fragment1).commit()
        fm.beginTransaction().add(R.id.nav_host, fragment2).commit()

        if (premium) {
            navView.menu.removeItem(R.id.navigation_csplus)

            navView.setOnItemSelectedListener { menuItem ->
                if (landscape) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        constraintSet.clear(R.id.nav_view, ConstraintSet.RIGHT)
                        constraintSet.connect(R.id.nav_view, ConstraintSet.LEFT, R.id.container, ConstraintSet.RIGHT)
                        val transition = ChangeBounds()
                        transition.setDuration(200)
                        beginDelayedTransition(container, transition)
                        constraintSet.applyTo(container)
                        show(toggleNavViewButton)

                    }, 200
                    )
                    navigating = !navigating

                }
                when (menuItem.itemId) {
                    R.id.navigation_chords-> {

                        active.let { fm.beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out).hide(it).show(fragment1).commit() }
                        active = fragment1
                        true
                    }
                    R.id.navigation_progressions-> {
                        active.let { fm.beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out).hide(it).show(fragment2).commit() }
                        active = fragment2
                        true
                    }
                    R.id.navigation_settings -> {
                        active.let { fm.beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out).hide(it).show(fragment3).commit() }
                        active = fragment3
                        true
                    }
                    else -> false
                }
            }


        } else {
            fragment4 = CSPlusFragment()
            fm.beginTransaction().add(R.id.nav_host, fragment4!!).hide(fragment4!!).commit()

            navView.setOnItemSelectedListener { menuItem ->
                if (landscape) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        constraintSet.clear(R.id.nav_view, ConstraintSet.RIGHT)
                        constraintSet.connect(R.id.nav_view, ConstraintSet.LEFT, R.id.container, ConstraintSet.RIGHT)
                        val transition = ChangeBounds()
                        transition.setDuration(250)
                        beginDelayedTransition(container, transition)
                        constraintSet.applyTo(container)
                        show(toggleNavViewButton)

                    }, 250
                    )
                    navigating = !navigating

                }
                when (menuItem.itemId) {
                    R.id.navigation_chords-> {
                        active.let { fm.beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out).hide(it).show(fragment1).commit() }
                        active = fragment1
                        true
                    }
                    R.id.navigation_progressions-> {
                        active.let { fm.beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out).hide(it).show(fragment2).commit() }
                        active = fragment2
                        true
                    }
                    R.id.navigation_settings -> {
                        active.let { fm.beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out).hide(it).show(fragment3).commit() }
                        active = fragment3
                        true
                    }
                    R.id.navigation_csplus-> {
                        active.let { fm.beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out).hide(it).show(fragment4!!).commit() }
                        active = fragment4!!
                        true
                    }
                    else -> false
                }
            }
        }

        if (landscape) {
//            val a = navView.layoutParams.width
//            val params = toggleNavViewButton.layoutParams as ConstraintLayout.LayoutParams
//            params.setMargins(0, 0, (54*dp).toInt(), (10*dp).toInt())
//            toggleNavViewButton.layoutParams = params

            val closeNavView =  findViewById<Button>(R.id.close_nav_view)


            toggleNavViewButton.setOnClickListener {
                constraintSet.clear(R.id.nav_view, ConstraintSet.LEFT)
                constraintSet.connect(R.id.nav_view, ConstraintSet.RIGHT, R.id.container, ConstraintSet.RIGHT)

                val transition = ChangeBounds()
                transition.setDuration(200)
                beginDelayedTransition(container, transition)
                constraintSet.applyTo(container)
                hide(toggleNavViewButton)

//                navigating = !navigating
//                if (navigating) {
//                    show(navView, Slide(Gravity.END), 300)
//
//                    toggleNavViewButton.setBackgroundResource(R.drawable.close)
//                } else {
//                    hide(navView, Slide(Gravity.END), 300)
//                    toggleNavViewButton.setBackgroundResource(R.drawable.north_west_24dp_5f6368_fill0_wght300_grad0_opsz24)
//
//                }

            }
            closeNavView.setOnClickListener {
                constraintSet.clear(R.id.nav_view, ConstraintSet.RIGHT)
                constraintSet.connect(R.id.nav_view, ConstraintSet.LEFT, R.id.container, ConstraintSet.RIGHT)
                val transition = ChangeBounds()
                transition.setDuration(200)
                beginDelayedTransition(container, transition)
                constraintSet.applyTo(container)
//                val transition1 = Fade(Visibility.MODE_IN)
//                transition1.duration=300
//                transition1.addTarget(toggleNavViewButton)
////                show(toggleNavViewButton, Fade(Visibility.MODE_IN), 300)
//                beginDelayedTransition(container, transition1)
                show(toggleNavViewButton)

            }


        }


        navView.menu.getItem(1).isChecked = true


        soundPresetDone.setOnClickListener {
            val newPreset:String=soundPicker.getCurrentItem()

            if (newPreset != preset) {
                preset = newPreset
                Sounds.loadSounds(this, MusicData.presetsMap.getValue(preset), loadingScreen, loadingProgress,this)

            }

            hide(soundMenu)

        }

        closeSoundMenu.setOnClickListener {

            hide(soundMenu)

        }

        if (premium) {
            soundPicker.data = MusicData.presets

        } else {
            soundPicker.data = MusicData.freePresets
        }
        soundPicker.typeface= resources.getFont(R.font.inter_light)
        soundPicker.itemSpace=40

        val player :MediaPlayer = MediaPlayer.create(this@MainActivity ,
            res.getIdentifier(preset +"_39", "raw", packageName))
        soundPicker.setOnWheelChangedListener(object: OnWheelChangedListener {
            override fun onWheelScrolled(view: WheelView?, offset: Int) {
            }

            override fun onWheelSelected(view: WheelView?, position: Int) {
                player.release()
                val preset:String = view!!.getCurrentItem()
                val player = MediaPlayer.create(this@MainActivity ,
                    res.getIdentifier(MusicData.presetsMap.getValue(preset)+"_39", "raw", packageName))
                player.start()
                Timer().schedule(2000) {
                    player.stop()
                    player.release()
                }

            }

            override fun onWheelScrollStateChanged(view: WheelView?, state: Int) {
            }

            override fun onWheelLoopFinished(view: WheelView?) {
            }

        })

    }

    fun purchase() {
        Purchases.sharedInstance.purchaseWith(
            PurchaseParams.Builder(this, csplus).build(),
            onError = { error, userCancelled -> /* No purchase */ },
            onSuccess = { storeTransaction, customerInfo ->
                if (customerInfo.entitlements["plus"]?.isActive == true) {
                    activate()
                }
            }
        )
    }
    private fun activate() {
        securePref.edit().putBoolean("premium", true).apply()
        show(restart)
//        ProcessPhoenix.triggerRebirth(this)
    }



}