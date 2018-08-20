package rawcomposition.bibletools.info.ui.base

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.view.MenuItem
import android.widget.Toast
import com.android.billingclient.api.*
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.PurchaseEvent
import com.crashlytics.android.answers.StartCheckoutEvent
import dagger.android.AndroidInjection
import rawcomposition.bibletools.info.BuildConfig
import rawcomposition.bibletools.info.R
import rawcomposition.bibletools.info.data.prefs.AppPrefs
import timber.log.Timber
import java.math.BigDecimal
import java.util.*
import javax.inject.Inject

abstract class BaseThemedActivity : AppCompatActivity(), PurchasesUpdatedListener, BillingClientStateListener {

    @Inject
    lateinit var appPrefs: AppPrefs

    private lateinit var billingClient: BillingClient

    private var selectedPos = 0
    private var skuDetailsList: List<SkuDetails>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidInjection.inject(this)

        delegate.setLocalNightMode(if (appPrefs.isNightMode()) {
            AppCompatDelegate.MODE_NIGHT_YES
        } else {
            AppCompatDelegate.MODE_NIGHT_NO
        })

        billingClient = BillingClient.newBuilder(this).setListener(this).build()
        billingClient.startConnection(this)
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            android.R.id.home -> {
                supportFinishAfterTransition()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    fun sendFeedback() {
        val subject = "BibleTools (Android - ${BuildConfig.VERSION_NAME})"

        val intent = Intent(Intent.ACTION_SENDTO)
        intent.data = Uri.parse("mailto:")
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.app_email)))
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(Intent.createChooser(intent, getString(R.string.label_email)))
        }
    }

    fun donateClicked() {

        AlertDialog.Builder(this)
                .setTitle(R.string.title_make_donation)
                .setSingleChoiceItems(resources.getStringArray(R.array.available_donations_titles), selectedPos) { _, position ->
                    selectedPos = position
                }
                .setPositiveButton(R.string.action_donate) { _, _ ->

                    skuDetailsList?.let {
                        purchaseDonation(it[selectedPos].sku)
                    } ?: purchaseDonation(SKU_LIST[selectedPos])
                }
                .create().show()
    }

    fun showWebUrl(url: String) {
        val builder = CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.theme_primary))
                .setShowTitle(true)
                .enableUrlBarHiding()
                .setStartAnimations(this, R.anim.slide_up, android.R.anim.fade_out)
                .setExitAnimations(this, android.R.anim.fade_in, R.anim.slide_down)

        try {
            val intent = builder.build()
            intent.launchUrl(this, Uri.parse(url))
        } catch (ex: Exception) {

        }
    }

    private fun purchaseDonation(sku: String) {
        val flowParams = BillingFlowParams.newBuilder()
                .setSku(sku)
                .setType(BillingClient.SkuType.INAPP)
                .build()

        Answers.getInstance().logStartCheckout(StartCheckoutEvent()
                .putItemCount(1)
                .putTotalPrice(getAmount(sku))
                .putCurrency(Currency.getInstance("USD"))
                .putCustomAttribute("sku", sku))

        val responseCode = billingClient.launchBillingFlow(this, flowParams)
    }

    override fun onPurchasesUpdated(responseCode: Int, purchases: MutableList<Purchase>?) {
        Timber.d("onPurchasesUpdated: RC $responseCode, $purchases")

        if (responseCode == BillingClient.BillingResponse.OK && purchases != null) {
            val purchasesResult = billingClient.queryPurchases(BillingClient.SkuType.INAPP)

            Timber.d("Purchase result: $purchasesResult")

            val sku = purchasesResult.purchasesList.first().sku
            Answers.getInstance().logPurchase(PurchaseEvent()
                    .putSuccess(true)
                    .putItemId(sku))

            Toast.makeText(this, "Thank You!", Toast.LENGTH_LONG).show()

        } else if (responseCode == BillingClient.BillingResponse.ITEM_ALREADY_OWNED) {
            Toast.makeText(this, "Already Purchased.\nPlease select another amount.", Toast.LENGTH_SHORT).show()
        } else {
            Answers.getInstance().logPurchase(PurchaseEvent().putSuccess(false))
        }
    }

    private fun getAmount(sku: String): BigDecimal {
        return when(sku){
            SKU_LIST[0] -> BigDecimal.ONE
            SKU_LIST[1] -> BigDecimal("5.00")
            SKU_LIST[2] -> BigDecimal.TEN
            SKU_LIST[3] -> BigDecimal("20.00")
            SKU_LIST[4] -> BigDecimal("50.00")
            SKU_LIST[5] -> BigDecimal("100.00")
            else -> BigDecimal.ZERO
        }
    }

    override fun onBillingServiceDisconnected() {
    }

    override fun onBillingSetupFinished(responseCode: Int) {

        if (responseCode == BillingClient.BillingResponse.OK) {
            // The billing client is ready. You can query purchases here.

            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(SKU_LIST).setType(BillingClient.SkuType.INAPP)
            billingClient.querySkuDetailsAsync(params.build()) { code, skuDetailsList ->
                // Process the result.
                if (code == BillingClient.BillingResponse.OK && skuDetailsList.isNotEmpty()) {
                    //skuDetails = skuDetailsList.find { it.sku == PREMIUM_SKU_ID }
                    Timber.d("Products: $skuDetailsList")
                    this.skuDetailsList = skuDetailsList
                }
            }
        }

    }

    companion object {
        private val SKU_LIST = arrayListOf("donate_id_1", "donate_id_5", "donate_id_10",
                "donate_id_20", "donate_id_50", "donate_id_100")
    }
}