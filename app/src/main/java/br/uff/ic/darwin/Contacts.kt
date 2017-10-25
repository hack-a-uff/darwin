package br.uff.ic.darwin

import android.content.DialogInterface
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.*
import br.uff.ic.darwin.user.UserManager
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

class Contacts : AppCompatActivity() {

    val update  = Channel<List<String>>(1)
    val manager = UserManager(update)
    val s  = X(update, this)
    class X(val update : Channel<List<String>>, val c: Contacts) : AsyncTask<Void, Void, List<String>>() {

        override fun onPostExecute(result: List<String>) {
            super.onPostExecute(result)
            c.updateContacts(result)
            X(update, c).execute()
        }

        override fun doInBackground(vararg params: Void?): List<String> {
            return runBlocking {
                update.receive()
            }
        }

    }

    fun updateContacts(result: List<String>) {

        val alertDialogBuilder = AlertDialog.Builder(
                this)

        val contactList = findViewById<ListView>(R.id.contactList)
        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,result)

        alertDialogBuilder.setView(contactList)
        contactList.setAdapter(adapter)



    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        s.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        val logged = intent.extras.getLong("logged",0)
        setContentView(R.layout.activity_contacts)
        Toast.makeText(this, logged.toString(),Toast.LENGTH_LONG).show()
        runBlocking {
            launch(CommonPool){
                val element = manager.getContactsName(logged.toString())
                s.update.send(element)
            }
        }

    }

}
