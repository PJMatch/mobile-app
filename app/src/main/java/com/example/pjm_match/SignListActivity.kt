package com.example.pjm_match

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.ListView

class SignListActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        actionBar?.setDisplayHomeAsUpEnabled(true)

        val list = ListView(this).apply {
            adapter = ArrayAdapter(this@SignListActivity, android.R.layout.simple_list_item_1, signs)
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
