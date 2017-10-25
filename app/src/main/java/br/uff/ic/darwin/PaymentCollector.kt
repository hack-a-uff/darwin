package br.uff.ic.darwin

import br.uff.ic.darwin.wallet.PaymentsUtil
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.PaymentData
import com.google.android.gms.wallet.PaymentsClient

/**
 * Created by monique on 24/10/17.
 */

typealias Money = Long
fun Money.toMicroString(): String {
    return PaymentsUtil.microsToString(this)
}
class PaymentCollector(
        private val paymentsClient: PaymentsClient
) {
    fun collect(money: Money): Task<PaymentData>? {
        val price = money.toMicroString()

        val transaction = PaymentsUtil.createTransaction(price)
        val request = PaymentsUtil.createPaymentDataRequest(transaction)

        return paymentsClient.loadPaymentData(request)
    }
}