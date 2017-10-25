package br.uff.ic.darwin

import android.Manifest
import android.app.PendingIntent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.widget.EditText
import android.view.LayoutInflater
import android.support.v7.app.AlertDialog
import android.widget.TextView
import br.uff.ic.darwin.user.Student
import br.uff.ic.darwin.user.UserManager
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.runBlocking
import android.graphics.BitmapFactory
import android.nfc.NfcAdapter
import android.util.Base64
import android.widget.ImageView
import android.widget.Toast
import android.nfc.Tag
import android.support.v4.app.ActivityCompat
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import java.lang.Math.pow
import java.nio.charset.Charset
import kotlin.experimental.and


class Card : AppCompatActivity() {
    val update  = Channel<Student>(1)
    val userManager= UserManager(update)
    lateinit var pendingIndent: PendingIntent
    lateinit var nfcAdapter: NfcAdapter
    val s  = X(update, this)
    class X(val update : Channel<Student>, val c: Card) : AsyncTask<Void, Void, Student>() {

        override fun onPostExecute(result: Student) {
            super.onPostExecute(result)
            c.updateShit(result)
            X(update, c).execute()
        }

        override fun doInBackground(vararg params: Void?): Student {
            return runBlocking {
                update.receive()
            }
        }

    }

    fun updateShit(student: Student){
        val name = findViewById(R.id.nameView) as TextView
        val registration = findViewById(R.id.registrationView) as TextView
        val course = findViewById(R.id.courseView) as TextView
        val expiresIn = findViewById(R.id.expireInView) as TextView
        val enrolled = findViewById(R.id.isEnrolledView) as TextView
        val buFund = findViewById(R.id.buFundsView) as TextView
        val avatar = findViewById(R.id.imageView) as ImageView
        val ruFunds = findViewById(R.id.ruFundsView) as TextView
        name.text = student.name
        registration.text = student.uffRegistrationNumber
        course.text = student.course
        expiresIn.text = student.expiresAt
        enrolled.text = "Inscrito"
        buFund.text = "R$ ${student.rioCardFunds}"
        ruFunds.text = "R$ ${student.uffFunds}"
        val decodedString = Base64.decode(student.picture, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        avatar.setImageBitmap(decodedByte)

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Toast.makeText(this,"LI NFC",Toast.LENGTH_LONG).show()
        val action = intent.action

        if (NfcAdapter.ACTION_TAG_DISCOVERED == action) {

            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            if (tag == null) {
                Toast.makeText(this, "NULL TAG", Toast.LENGTH_SHORT).show()
            } else {
                val v = toDec(tag.id).toString()
                runBlocking {
                    launch(CommonPool) {
                        userManager.update.send(userManager.getUser(v))
                    }
                }
                Toast.makeText(this,v,Toast.LENGTH_LONG).show()
            }
        } else {
        }

    }

    private fun toDec(bytes: ByteArray): Long {
        var result: Long = 0
        var factor: Long = 1
        for (i in bytes.indices) {
            val value = bytes[i].and(0xFF.toByte())
            result += value * factor
            factor *= 256L
        }
        if (result<0){
            result *= -1
        }
        return result + pow(2.0, 16.0).toLong()
    }

    override fun onResume() {
        super.onResume()
        getAdapter()!!.enableForegroundDispatch(this, pendingIndent, null, null)
    }

    override fun onPause() {
        super.onPause()
        if (getAdapter() != null){
            getAdapter()!!.disableForegroundDispatch(this)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        s.execute()

        //intencao de capturar o NFC
        pendingIndent = PendingIntent.getActivity(this, 0, Intent(this,
            javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
        setContentView(R.layout.activity_card)

        window.decorView.setBackgroundColor(resources.getColor(R.color.material_blue_grey_800))
        val ruAddFundButton = findViewById(R.id.button2)
        val context = this
        ruAddFundButton.setOnClickListener(object : View.OnClickListener {

            override fun onClick(arg0: View) {

                // get prompts.xml view
                val li = LayoutInflater.from(context)
                val promptsView = li.inflate(R.layout.payment_view, null)

                val alertDialogBuilder = AlertDialog.Builder(
                    context)

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView)

                val userInput = promptsView
                    .findViewById<EditText>(R.id.moneyInput)

                val result = findViewById(R.id.ruFundsView) as TextView

                // set dialog message
                alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialog, id ->
                            // get user input and set it to result
                            // edit text
                            result.text = userInput.text
                        })
                    .setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })

                // create alert dialog
                val alertDialog = alertDialogBuilder.create()

                // show it
                alertDialog.show()

            }
        })

    }

    private fun getAdapter(): NfcAdapter? {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        return nfcAdapter
    }
}
