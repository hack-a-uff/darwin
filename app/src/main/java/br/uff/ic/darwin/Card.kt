package br.uff.ic.darwin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class Card : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card)
        window.decorView.setBackgroundColor(resources.getColor(R.color.material_blue_grey_800))
    }
}
