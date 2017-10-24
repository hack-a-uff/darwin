package br.uff.ic.darwin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.content.DialogInterface
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
import android.graphics.Bitmap
import android.nfc.NfcAdapter
import android.util.Base64
import android.widget.ImageView
import android.widget.Toast


class Card : AppCompatActivity() {
    val update  = Channel<Student>(1)
    val userManager= UserManager(update)
    val s  = X(update, this)
    class X(val update : Channel<Student>, val c: Card) : AsyncTask<Void, Void, Student>() {

        override fun onPostExecute(result: Student) {
            super.onPostExecute(result)
            c.updateShit(result)
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        s.execute()
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
}
