package pl.ownvision.fastpocket

import android.app.Application
import com.airbnb.mvrx.Mavericks
import com.chimerapps.niddler.core.AndroidNiddler
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class MyApplication : Application() {

    @Inject
    lateinit var niddler: AndroidNiddler

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())
        Mavericks.initialize(this)

        niddler.attachToApplication(this)
    }
}