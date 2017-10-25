package br.uff.ic.darwin

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import br.uff.ic.darwin.user.Student
import br.uff.ic.darwin.user.UserManager
import br.uff.ic.darwin.wallet.PaymentsUtil
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.kittinunf.fuel.android.extension.jsonDeserializer
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wallet.AutoResolveHelper
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentsClient
import com.google.android.gms.wallet.TransactionInfo
import com.stripe.android.model.Token
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.runBlocking
import java.util.HashMap
import java.util.concurrent.Callable


class Card : AppCompatActivity() {
    val update  = Channel<Student>(1)
    val userManager= UserManager(update)
    val s  = X(update, this)
    var MEUCACHEDEAMMOUNT: Long = 0
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
        val name = findViewById<TextView>(R.id.nameView)
        val registration = findViewById<TextView>(R.id.registrationView)
        val course = findViewById<TextView>(R.id.courseView)
        val expiresIn = findViewById<TextView>(R.id.expireInView)
        val enrolled = findViewById<TextView>(R.id.isEnrolledView)
        val buFund = findViewById<TextView>(R.id.buFundsView)
        val avatar = findViewById<ImageView>(R.id.imageView)
        val ruFunds = findViewById<TextView>(R.id.ruFundsView)
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
        val ruAddFundButton = findViewById<Button>(R.id.button2)
        val context = this
        mPaymentsClient = PaymentsUtil.createPaymentsClient(this)

        PaymentsUtil.isReadyToPay(mPaymentsClient).addOnCompleteListener { task ->
            try {
                val result = task.getResult(ApiException::class.java)
            } catch (exception: ApiException) {
                // Process error
                Log.w("isReadyToPay failed", exception)
            }
        }
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

                val result = findViewById<TextView>(R.id.ruFundsView)

                // set dialog message
                alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",
                        DialogInterface.OnClickListener { dialog, id ->
                            // get user input and set it to result
                            requestPayment(userInput.text.toString())
                        })
                    .setNegativeButton("Cancel",
                        DialogInterface.OnClickListener { dialog, id -> dialog.cancel() })

                // create alert dialog
                val alertDialog = alertDialogBuilder.create()

                // show it
                alertDialog.show()

            }
        })


        // It's recommended to create the PaymentsClient object inside of the onCreate method.
    }


    // Arbitrarily-picked result code.
    private val LOAD_PAYMENT_DATA_REQUEST_CODE = 991

    private var mPaymentsClient: PaymentsClient? = null

    private var mPwgButton: View? = null
    private var mPwgStatusText: TextView? = null


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        when (requestCode) {
            LOAD_PAYMENT_DATA_REQUEST_CODE -> {
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        val paymentData = PaymentData.getFromIntent(data)
                        if (paymentData != null) {
                            handlePaymentSuccess(paymentData)
                        }
                    }
                    Activity.RESULT_CANCELED -> {
                    }
                    AutoResolveHelper.RESULT_ERROR -> {
                        val status = AutoResolveHelper.getStatusFromIntent(data)
                        handleError(status!!.statusCode)
                    }
                }// Nothing to here normally - the user simply cancelled without selecting a
                // payment method.

            }
        }
    }

    private fun handlePaymentSuccess(paymentData: PaymentData) {
        // PaymentMethodToken contains the payment information, as well as any additional
        // requested information, such as billing and shipping address.
        //
        // Refer to your processor's documentation on how to proceed from here.

        val token = paymentData.getPaymentMethodToken()
        val stripeToken = Token.fromString(token.getToken())
        // getPaymentMethodToken will only return null if PaymentMethodTokenizationParameters was
        // not set in the PaymentRequest.
        if (stripeToken != null) {

            chargeRequest(stripeToken.id)
            val billingName = paymentData.cardInfo.billingAddress?.name
            // Use token.getToken() to get the token string.
            Log.d("PaymentData", "PaymentMethodToken received")
        }
    }

    private fun chargeRequest(stripeToken: String) {

        val params = HashMap<String, Any>()
        params.put("amount", MEUCACHEDEAMMOUNT)
        params.put("currency", "brl")
        params.put("description", "Example charge")
        params.put("source", stripeToken)

        val data = requester.get("https://api.stripe.com/v1/charges", params)
        if (data != "deu ruim"){
            val ruFunds = findViewById<TextView>(R.id.ruFundsView)
            var oldValue = ruFunds.text
            oldValue = oldValue.replace(Regex("R\\$ "), "")
            var doubleValue = oldValue.toString().toDouble()
            doubleValue += (MEUCACHEDEAMMOUNT / 100.0)  

            findViewById<TextView>(R.id.ruFundsView).text = doubleValue.toString()
        }
    }

    private fun handleError(statusCode: Int) {
        // At this stage, the user has already seen a popup informing them an error occurred.
        // Normally, only logging is required.
        // statusCode will hold the value of any constant from CommonStatusCode or one of the
        // WalletConstants.ERROR_CODE_* constants.
        Log.w("loadPaymentData failed", String.format("Error code: %d", statusCode))
    }

    // This method is called when the Pay with Google button is clicked.
    fun requestPayment(value: String) {
        // Disables the button to prevent multiple clicks.

        // The price provided to the API should include taxes and shipping.
        // This price is not displayed to the user.
        val money = value.toDouble() * 1000000
        MEUCACHEDEAMMOUNT = (value.toDouble() * 100).toLong()
        val price = PaymentsUtil.microsToString(money.toLong())

        val transaction = PaymentsUtil.createTransaction(price)
        val request = PaymentsUtil.createPaymentDataRequest(transaction)
        val futurePaymentData = mPaymentsClient!!.loadPaymentData(request)

        // Since loadPaymentData may show the UI asking the user to select a payment method, we use
        // AutoResolveHelper to wait for the user interacting with it. Once completed,
        // onActivityResult will be called with the result.
        AutoResolveHelper.resolveTask(futurePaymentData, this, LOAD_PAYMENT_DATA_REQUEST_CODE)
    }


}
