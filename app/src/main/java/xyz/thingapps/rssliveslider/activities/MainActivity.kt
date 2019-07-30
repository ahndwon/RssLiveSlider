package xyz.thingapps.rssliveslider.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*
import xyz.thingapps.rssliveslider.R
import xyz.thingapps.rssliveslider.fragments.HomeFragment
import xyz.thingapps.rssliveslider.models.Cast
import xyz.thingapps.rssliveslider.viewmodels.RssViewModel


class MainActivity : AppCompatActivity() {

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
    }
}

