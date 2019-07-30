package xyz.thingapps.rssliveslider.activities

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.jakewharton.rxbinding3.widget.queryTextChanges
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_main.*
import org.threeten.bp.Instant
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.fragments.HomeFragment
import xyz.thingapps.rssliveslider.models.Cast
import xyz.thingapps.rssliveslider.models.RssUrl
import xyz.thingapps.rssliveslider.sharedApp
import xyz.thingapps.rssliveslider.utils.showKeyboard
import xyz.thingapps.rssliveslider.utils.validate
import xyz.thingapps.rssliveslider.viewmodels.RssViewModel
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private val disposeBag = CompositeDisposable()
    private val viewModel: RssViewModel by lazy {
        ViewModelProviders.of(this@MainActivity).get(RssViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_search)

        supportFragmentManager.beginTransaction()
            .replace(R.id.frameContainer, HomeFragment())
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        setupSearchView(menu)

        return true
    }

    private fun setupSearchView(menu: Menu) {
        val searchItem = menu.findItem(R.id.search)
        val searchView = searchItem.actionView as? SearchView

        searchView?.queryTextChanges()
            ?.debounce(500, TimeUnit.MILLISECONDS)
            ?.subscribe({ search ->
                viewModel.getData {
                    viewModel.castList = viewModel.castList.filter {
                        it.title?.contains(search) ?: false
                    }
                }
            }, { e ->
                e.printStackTrace()
            })?.addTo(disposeBag)

        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(menuItem: MenuItem?): Boolean {
                searchView?.isIconifiedByDefault = false
                searchView?.requestFocus()
                this@MainActivity.showKeyboard()
                return true
            }

            override fun onMenuItemActionCollapse(menuItem: MenuItem?): Boolean {
                viewModel.getData()
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
//                showUrlAddDialog()
                val list = ArrayList<Cast>()
                list.addAll(viewModel.castList)
                val intent = Intent(this, SearchActivity::class.java)
                intent.putExtra(SearchActivity.CAST_LIST, list)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showUrlAddDialog() {
        val frameLayout = FrameLayout(this)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        params.rightMargin = resources.getDimensionPixelSize(R.dimen.dialog_margin)
        val editText = EditText(this)
        editText.hint = getString(R.string.add_url_hint)
        editText.layoutParams = params
        editText.validate(getString(R.string.url_validation_message)) {
            it.isEmpty() || Patterns.WEB_URL.matcher(it).matches()
        }
        frameLayout.addView(editText)
        AlertDialog.Builder(
            this
        ).setTitle(getString(R.string.url_dialog_title))
            .setCancelable(true)
            .setView(frameLayout)
            .setPositiveButton(getString(R.string.alert_dialog_add)) { _, _ ->
                if (editText.error != null) {
                    Toast.makeText(
                        this,

                        getString(R.string.url_validation_message),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
//                    viewModel.urlList.add(editText.text.toString())
                    sharedApp.addRssUrl(
                        RssUrl(
                            editText.text.toString(),
                            Instant.now().toEpochMilli()
                        )
                    )
                    viewModel.getData()
                }
            }
            .setNegativeButton(getString(R.string.alert_dialog_cancel)) { _, _ -> }
            .show()
    }
}

