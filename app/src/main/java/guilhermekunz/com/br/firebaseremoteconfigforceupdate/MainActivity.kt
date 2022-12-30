package guilhermekunz.com.br.firebaseremoteconfigforceupdate

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import guilhermekunz.com.br.firebaseremoteconfigforceupdate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val dialog = Dialog()

    private val firebaseRemoteConfig by lazy { FirebaseRemoteConfig.getInstance() }

    private val configSettings by lazy {
        FirebaseRemoteConfigSettings.Builder()
            .setMinimumFetchIntervalInSeconds((if (BuildConfig.DEBUG) 0 else 3600))
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        check()
    }

    init {
        firebaseRemoteConfig.setConfigSettingsAsync(configSettings)
    }

    private fun check() {
        val appVersion = ForceUpdateUtils.getAppVersion(this)
        firebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val forceUpdate = firebaseRemoteConfig.getBoolean(KEY_FORCE_UPDATE_REQUIRED)
                val currentVersion = firebaseRemoteConfig.getString(KEY_CURRENT_VERSION)
                if (forceUpdate) {
                    if (currentVersion > appVersion) {
                        showDialog()
                    }
                }
            }
        }
    }

    private fun showDialog() {
        dialog.show(supportFragmentManager, "Exit Dialog")
    }

    companion object {
        const val KEY_CURRENT_VERSION = "android_force_update_current_version"
        const val KEY_FORCE_UPDATE_REQUIRED = "android_force_update_required"
    }

}