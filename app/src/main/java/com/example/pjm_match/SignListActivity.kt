package com.example.pjm_match

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import com.example.pjm_match.ui.Palette

class SignListActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, signs) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                if (view is TextView) {
                    view.setTextColor(Palette.text)
                    view.setBackgroundColor(Palette.background)
                    view.setPadding(32, 24, 32, 24)
                }
                return view
            }
        }

        val list = ListView(this).apply {
            setBackgroundColor(Palette.background)
            this.adapter = adapter
            setOnItemClickListener { _, _, position, _ ->
                startActivity(
                    Intent(this@SignListActivity, SignActivity::class.java)
                        .putExtra(SignActivity.EXTRA_SIGN, signs[position])
                )
            }
        }

        setContentView(list)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
