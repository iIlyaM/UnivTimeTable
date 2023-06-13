package vsu.cs.univtimetable

import android.app.Application
import com.yandex.metrica.YandexMetrica
import com.yandex.metrica.YandexMetricaConfig

class Univtimetable : Application() {

    private var yandexMetrikaApi = "003f7283-eb34-4a52-9456-4559ece4d7a9"

    override fun onCreate() {
        super.onCreate()

        val config = YandexMetricaConfig.newConfigBuilder(yandexMetrikaApi).build()
        YandexMetrica.activate(applicationContext, config)
        YandexMetrica.enableActivityAutoTracking(this)
    }
}