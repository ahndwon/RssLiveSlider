package xyz.thingapps.rssliveslider.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.DateTimeFormatter.RFC_1123_DATE_TIME
import org.threeten.bp.format.DateTimeParseException
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.dialog.FilterDialogFragment
import xyz.thingapps.rssliveslider.fragments.HomeFragment
import xyz.thingapps.rssliveslider.models.Cast
import xyz.thingapps.rssliveslider.utils.isValidFormat
import xyz.thingapps.rssliveslider.viewmodels.RssViewModel
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private val disposeBag = CompositeDisposable()
    private val filterDialog = FilterDialogFragment()
    private val viewModel: RssViewModel by lazy {
        ViewModelProviders.of(this@MainActivity).get(RssViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_search)

        viewModel.imageRecognizer.setClassifier(this)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, HomeFragment())
            .commit()

        parseDate()

        filterBar.clicks().throttleFirst(FILTER_DURATION, TimeUnit.MILLISECONDS)
            .subscribe({
                if (viewModel.castList.isNullOrEmpty()) {
                    return@subscribe
                }

                if (filterDialog.isAdded) {
                    return@subscribe
                }

                filterDialog.show(supportFragmentManager, FilterDialogFragment.TAG)
            }, { e ->
                Log.d(MainActivity::class.java.name, "e : ", e)
            }).addTo(disposeBag)


        buttonClearFilter.setOnClickListener {
            viewModel.getData()
            viewModel.sort = "Date Ascending"
        }

        viewModel.castListPublisher.observeOn(AndroidSchedulers.mainThread())
            .subscribe({ castList ->
                val currentRssText =
                    if (viewModel.castTitleList.size == castList.size) getString(R.string.all_rss)
                    else castList.map { it.title }.reversed().joinToString(separator = ", ")

                textCurrentSearch.text = currentRssText
                textCurrentSortBy.text = viewModel.sort
            }, { e ->
                Log.d(MainActivity::class.java.name, "e : ", e)
            })
            .addTo(disposeBag)
    }

    fun parseDate() {
//        AndroidThreeTen.init(this)
        val date = "Thu, 28 Mar 2019 14:00 EDT"
        val iso = "2019-07-28T22:55:28+09:00"


        val format = "E, dd MMM yyyy HH:mm zzz"
        val isoFormat = "yyyy-MM-dd'T'HH:mm:ssXXX"
        val locale = Locale.ENGLISH

        Log.d(
            MainActivity::class.java.name,
            "formatter -  ${isValidFormat("E, dd MMM yyyy HH:mm zzz", date, Locale.ENGLISH)}"
        )

//        println("isValid -  $date = " + isValidFormat("E, dd MMM yyyy HH:mm zzz", date, Locale.ENGLISH));
        val formatter = SimpleDateFormat(format, locale)
        formatter.applyLocalizedPattern(format)

        val isoFormatter = SimpleDateFormat(isoFormat, locale)
        formatter.applyLocalizedPattern(format)

        val form = DateTimeFormatter.ofPattern(format, locale)
//        val odt = OffsetDateTime.parse(date, form)
        Log.d(MainActivity::class.java.name, "formatter -  ${isValidFormat(format, date, locale)}")
        Log.d(
            MainActivity::class.java.name,
            "formatter formatter.parse -  ${formatter.parse(date)}"
        )
        Log.d(
            MainActivity::class.java.name,
            "formatter isoFormatter.parse -  ${isoFormatter.parse(iso)}"
        )

        val formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS zzz")
            .withZone(ZoneId.of("Asia/Seoul"))

        val formatter3 = DateTimeFormatter.ofPattern("E, dd MMM yyyy HH:mm Z")


        val now = Instant.now()
//        Log.d(MainActivity::class.java.name, "formatter -  ${formatter2.format(now)}")
//        Log.d(MainActivity::class.java.name, "formatter date -  ${formatter3.parse(date)}")
//        Log.d(MainActivity::class.java.name, "formatter local date -  ${LocalDateTime.parse(date, formatter3)}")

//        Log.d(MainActivity::class.java.name, "formatter ZoneIds -  ${ZoneId.getAvailableZoneIds()}")

        val rfcDate = "Thu, 28 Mar 2019 14:00 EDT"
        val rfcDate2 = "Thu, 25 Jul 2019 18:27 EDT"
        val gmtDate = "Sun, 28 Jul 2019 12:49:00 GMT"

        var cleanDate = ""
        if (rfcDate.contains("EDT")) cleanDate = rfcDate.replace("EDT", "GMT")

        try {
//            val zoned = ZonedDateTime.parse(cleanDate, formatter3)
//            val local = zoned.toLocalDateTime()

//            Log.d(MainActivity::class.java.name, "formats local - $local}")

        } catch (e: DateTimeParseException) {
            Log.d(MainActivity::class.java.name, "formats local format failed : ", e)

        }
        val strDate = "2015-08-04"
        val aLD = LocalDate.parse(cleanDate, RFC_1123_DATE_TIME)
        val dTF = DateTimeFormatter.ofPattern("dd MMM uuuu")



        Log.d(MainActivity::class.java.name, "formatter formats as aLD - $aLD}")

//        Log.d(MainActivity::class.java.name, "$aLD + formats as + ${dTF.format(aLD)}")
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.rss_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val list = ArrayList<Cast>()
                list.addAll(viewModel.castList)
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra(SearchActivity.CAST_LIST, list)
                startActivity(intent)
            }

            R.id.rss_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivityForResult(intent, RC_RSS_URL)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RC_RSS_URL -> viewModel.getData()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        const val RC_RSS_URL = 333
        const val FILTER_DURATION = 600L
        const val RFC_1123_FORMAT = "E, dd MMM yyyy HH:mm zzz"
        const val ISO_DATE_TIME_FORMAT = "E, dd MMM yyyy HH:mm zzz"
    }
}

