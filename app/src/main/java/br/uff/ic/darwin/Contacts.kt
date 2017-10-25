package br.uff.ic.darwin

import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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

        val contactList = findViewById<ListView>(R.id.contactList)
        val adapter = ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, result.map { it.name })

        contactList.setAdapter(adapter)

        contactList.onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedFromList = result.get(position)
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
