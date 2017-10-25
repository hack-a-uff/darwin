package br.uff.ic.darwin

import android.content.DialogInterface
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import br.uff.ic.darwin.user.UserManager
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import br.uff.ic.darwin.user.Student
import br.uff.ic.darwin.Card


class Contacts : AppCompatActivity() {

    val update  = Channel<List<Student>>(1)
    val manager = UserManager(update)
    val s  = X(update, this)
    class X(val update : Channel<List<Student>>, val c: Contacts) : AsyncTask<Void, Void, List<Student>>() {

        override fun onPostExecute(result: List<Student>) {
            super.onPostExecute(result)
            c.updateContacts(result)
            X(update, c).execute()
        }

        override fun doInBackground(vararg params: Void?): List<Student> {
            return runBlocking {
                update.receive()
            }
        }

    }

    fun updateContacts(result: List<Student>) {

        val context = this

        val contactList = findViewById<ListView>(R.id.contactList)
        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, result.map { it.name })

        contactList.setAdapter(adapter)

        contactList.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedFromList = result.get(position)

                // get prompts.xml view
                val li = LayoutInflater.from(context)
                val promptsView = li.inflate(R.layout.payment_view, null)

                val alertDialogBuilder = AlertDialog.Builder(context)

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView)

                val userInput = promptsView
                        .findViewById<EditText>(R.id.moneyInput)

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                DialogInterface.OnClickListener { dialog, id ->
                                    // get user input and set it to result
                                    transferMoney(userInput.text.toString(), selectedFromList)
                                })
                        .setNegativeButton("Cancel",
                                DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })

                // create alert dialog
                val alertDialog = alertDialogBuilder.create()

                // show it
                alertDialog.show()
            }
        }
    }


    fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this).replace(".", ",")

    fun transferMoney(money: String, targetID:Student){
        var oldValue = money
        oldValue = oldValue.replace(Regex("R\\$ "), "").replace(",", ".")
        var doubleValue = oldValue.toDouble()
        ACTUALSTUDENT!!.uffFunds -= doubleValue

//        findViewById<TextView>(R.id.ru).text = "R$ ${doubleValue.format(2)}"
        runBlocking {
            launch(CommonPool) {
                manager.updateFunds(ACTUALSTUDENT!!.cardNfcId, ACTUALSTUDENT!!.uffFunds)
            }
        }

        oldValue = targetID.uffFunds.toString()
        oldValue = oldValue.replace(Regex("R\\$ "), "").replace(",", ".")
        doubleValue = oldValue.toDouble()
        doubleValue += money.replace(Regex("R\\$ "), "").replace(",", ".").toDouble()

//        findViewById<TextView>(R.id.ruFundsView).text = "R$ ${doubleValue.format(2)}"
        runBlocking {
            launch(CommonPool) {
                manager.updateFunds(targetID.cardNfcId, doubleValue)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        s.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        val logged = intent.extras.getLong("logged",0)
        setContentView(R.layout.activity_contacts)
        Toast.makeText(this, logged.toString(),Toast.LENGTH_LONG).show()
        runBlocking {
            launch(CommonPool){
                val element = manager.getContacts(logged.toString())
                s.update.send(element)
            }
        }

    }

}
