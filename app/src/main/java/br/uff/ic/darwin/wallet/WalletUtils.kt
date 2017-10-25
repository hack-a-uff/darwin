package br.uff.ic.darwin.wallet

import android.app.Activity
import br.uff.ic.darwin.Constants
import com.google.android.gms.tasks.Task
import com.google.android.gms.wallet.*
import java.math.BigDecimal
import java.math.RoundingMode

object WalletUtils {
    private val precision = BigDecimal(1000000.0)

    fun createPaymentsClient(activity: Activity): PaymentsClient {
        val walletOptions = Wallet.WalletOptions.Builder()
                .setEnvironment(Constants.PAYMENTS_ENVIRONMENT)
                .build()
        return Wallet.getPaymentsClient(activity, walletOptions)
    }

    fun createPaymentDataRequest(transactionInfo: TransactionInfo): PaymentDataRequest {
        val paramsBuilder = PaymentMethodTokenizationParameters.newBuilder()
                .setPaymentMethodTokenizationType(
                        WalletConstants.PAYMENT_METHOD_TOKENIZATION_TYPE_PAYMENT_GATEWAY)
                .addParameter("gateway", Constants.GATEWAY_TOKENIZATION_NAME)
        for (param in Constants.GATEWAY_TOKENIZATION_PARAMETERS) {
            paramsBuilder.addParameter(param.first, param.second)
        }

        return createPaymentDataRequest(transactionInfo, paramsBuilder.build())
    }

    private fun createPaymentDataRequest(transactionInfo: TransactionInfo, params: PaymentMethodTokenizationParameters): PaymentDataRequest {

        return PaymentDataRequest.newBuilder()
                .setPhoneNumberRequired(false)
                .setEmailRequired(true)
                .setShippingAddressRequired(false)
                .setTransactionInfo(transactionInfo)
                .addAllowedPaymentMethods(Constants.SUPPORTED_METHODS)
                .setCardRequirements(
                        CardRequirements.newBuilder()
                                .addAllowedCardNetworks(Constants.SUPPORTED_NETWORKS)
                                .setAllowPrepaidCards(true)
                                .setBillingAddressRequired(true)

                                // Omitting this parameter will result in the API returning
                                // only a "minimal" billing address (post code only).
                                .setBillingAddressFormat(WalletConstants.BILLING_ADDRESS_FORMAT_FULL)
                                .build())
                .setPaymentMethodTokenizationParameters(params)

                // If the UI is not required, a returning user will not be asked to select
                // a card. Instead, the card they previously used will be returned
                // automatically (if still available).
                // Prior whitelisting is required to use this feature.
                .setUiRequired(true)
                .build()
    }

    fun isReadyToPay(client: PaymentsClient): Task<Boolean> {
        val request = IsReadyToPayRequest.newBuilder()
        for (allowedMethod in Constants.SUPPORTED_METHODS) {
            request.addAllowedPaymentMethod(allowedMethod!!)
        }
        return client.isReadyToPay(request.build())
    }

    fun createTransaction(price: String): TransactionInfo {
        return TransactionInfo.newBuilder()
                .setTotalPriceStatus(WalletConstants.TOTAL_PRICE_STATUS_FINAL)
                .setTotalPrice(price)
                .setCurrencyCode(Constants.CURRENCY_CODE)
                .build()
    }

    fun microsToString(micros: Long): String =
            BigDecimal(micros).divide(precision).setScale(2, RoundingMode.HALF_EVEN).toString()
}
