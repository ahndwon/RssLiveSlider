package xyz.thingapps.rssliveslider.activities

import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_settings.*
import org.threeten.bp.Instant
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.adapters.UrlListAdapter
import xyz.thingapps.rssliveslider.models.RssUrl
import xyz.thingapps.rssliveslider.sharedApp
import xyz.thingapps.rssliveslider.utils.DividerItemDecoration
import xyz.thingapps.rssliveslider.utils.validate

class SettingsActivity : AppCompatActivity() {
    val adapter = UrlListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupActionBar()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        adapter.items = sharedApp.rssUrlList?.toList() ?: emptyList()
        adapter.onDeleteClick = { position ->
            showDeleteDialog {
                val urlList = sharedApp.rssUrlList ?: ArrayList()
                urlList.removeAt(position)
                sharedApp.rssUrlList = urlList
                adapter.items = urlList
                adapter.notifyDataSetChanged()
            }
        }
        rssRecyclerView.adapter = adapter
        rssRecyclerView.addItemDecoration(DividerItemDecoration(this, R.color.lightGray))
        rssRecyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, true).apply {
                stackFromEnd = true
            }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.add_rss_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.add_rss -> showUrlAddDialog()
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
                when {
                    editText.error != null -> Toast.makeText(
                        this,
                        getString(R.string.url_validation_message),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    editText.text.isNullOrBlank() -> Toast.makeText(
                        this,
                        getString(R.string.toast_no_url),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                    else -> {
                        addRss(editText.text.toString())
                    }
                }
            }
            .setNegativeButton(getString(R.string.alert_dialog_cancel)) { _, _ -> }
            .show()
    }

    private fun addRss(url: String) {
        sharedApp.addRssUrl(
            RssUrl(
                url,
                Instant.now().toEpochMilli()
            )
        )
        adapter.items = sharedApp.rssUrlList?.toList() ?: emptyList()
        adapter.notifyDataSetChanged()
        rssRecyclerView.smoothScrollToPosition(adapter.items.size)
    }

    private fun showDeleteDialog(onDelete: (() -> Unit)) {
        AlertDialog.Builder(
            this
        ).setTitle(getString(R.string.title_delete_rss))
            .setMessage(getString(R.string.message_delete_rss))
            .setCancelable(true)
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                onDelete.invoke()
            }
            .setNegativeButton(getString(R.string.alert_dialog_cancel)) { _, _ -> }
            .show()
    }
}
