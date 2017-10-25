package br.uff.ic.darwin

import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.nfc.NfcAdapter
import android.nfc.Tag
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
import android.widget.AdapterView.OnItemClickListener
import br.uff.ic.darwin.user.Student
import kotlin.experimental.and
import android.widget.TextView
import android.view.ViewGroup
import android.widget.ArrayAdapter




class Contacts : AppCompatActivity() {

    val update  = Channel<List<Student>>(1)
    val manager = UserManager(update)
    val s  = X(update, this)
    //lateinit var adapter: ArrayAdapter<String>
    lateinit var pendingIndent: PendingIntent
    lateinit var nfcAdapter: NfcAdapter
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
        return result + Math.pow(2.0, 16.0).toLong()
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
                        manager.update.send(manager.addContact(ACTUALSTUDENT!!.cardNfcId, v))
                    }
                }
                Toast.makeText(this,v,Toast.LENGTH_LONG).show()
            }
        } else {
        }
//        val qrbtn = findViewById<Button>(R.id.qrcodebutton)
//        qrbtn.callOnClick(){
//            val intent = Intent(applicationContext, Contacts::class.java)
//            intent.action = "com.google.zxing.client.android.SCAN"
//            intent.putExtra("SAVE_HISTORY", false)
//            startActivityForResult(intent, 0)
//        }

    }

    private fun getAdapter(): NfcAdapter? {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        return nfcAdapter
//        return startActivityForResult(this, 0, Intent(this,
//                javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)
    }


    fun updateContacts(result: List<Student>) {

        val context = this

        val contactList = findViewById<ListView>(R.id.contactList)

        val arrayAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, result.map { it.name }) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                // Get the Item from ListView
                val view = super.getView(position, convertView, parent)

                // Initialize a TextView for ListView each Item
                val tv = view.findViewById<TextView>(android.R.id.text1)

                // Set the text color of TextView (ListView Item)
                tv.setTextColor(Color.WHITE)

                // Generate ListView Item using TextView
                return view
            }
        }

        // DataBind ListView with items from ArrayAdapter
        contactList.setAdapter(arrayAdapter)

        contactList.onItemClickListener = OnItemClickListener { parent, view, position, id ->
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

    fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this).replace(".", ",")

    fun transferMoney(money: String, targetStudent:Student){
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

        oldValue = targetStudent.uffFunds.toString()
        oldValue = oldValue.replace(Regex("R\\$ "), "").replace(",", ".")
        doubleValue = oldValue.toDouble()
        doubleValue += money.replace(Regex("R\\$ "), "").replace(",", ".").toDouble()

//        findViewById<TextView>(R.id.ruFundsView).text = "R$ ${doubleValue.format(2)}"
        runBlocking {
            launch(CommonPool) {
                manager.updateFunds(targetStudent.cardNfcId, doubleValue)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        pendingIndent = PendingIntent.getActivity(this, 0, Intent(this,
                javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0)

        s.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        val logged = intent.extras.getLong("logged",0)
        setContentView(R.layout.activity_contacts)
        Toast.makeText(this, logged.toString(),Toast.LENGTH_LONG).show()
        runBlocking {
            launch(CommonPool){
                val element = manager.getContacts(logged.toString()).toSet()
                s.update.send(element.toList())
            }
        }

    }

    override fun onPause() {
        super.onPause()
        if (getAdapter() != null){
            getAdapter()!!.disableForegroundDispatch(this)
        }
    }

    override fun onResume() {
        super.onResume()
        getAdapter()!!.enableForegroundDispatch(this, pendingIndent, null, null)
    }

}
