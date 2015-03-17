package rawcomposition.bibletools.info.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.afollestad.materialdialogs.MaterialDialog;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.ArrayList;

import rawcomposition.bibletools.info.BuildConfig;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.util.ToastUtil;
import rawcomposition.bibletools.info.util.billing.IabHelper;
import rawcomposition.bibletools.info.util.billing.IabResult;
import rawcomposition.bibletools.info.util.billing.Inventory;
import rawcomposition.bibletools.info.util.billing.Purchase;

/**
 * Created by tinashe
 */
public abstract class BaseActivity extends ActionBarActivity{

    private static final String TAG = BaseActivity.class.getName();

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;

    // Durations for certain animations we use:
    private static final int HEADER_HIDE_ANIM_DURATION = 300;

    protected abstract int getLayoutResource();

    protected Toolbar mToolbar;

    protected View mHeaderView;

    // When set, these components will be shown/hidden in sync with the action bar
    // to implement the "quick recall" effect (the Action Bar and the header views disappear
    // when you scroll down a list, and reappear quickly when you scroll up).
    private ArrayList<View> mHideableHeaderViews = new ArrayList<View>();

    // The helper object
    private IabHelper mHelper;

    @Override
    public void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);

        setContentView(getLayoutResource());

        mToolbar = (Toolbar) findViewById(R.id.app_action_bar);
        mHeaderView = findViewById(R.id.header_view);
        if (mToolbar != null) {
            try{

                if(mHeaderView != null){
                    ViewCompat.setTranslationZ(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
                    ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
                }


                setSupportActionBar(mToolbar);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }catch (Throwable ex){
                //
            }

        }

        mHelper = new IabHelper(this, getString(R.string.app_license_key));

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    showToast("Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });
    }


    public Toolbar getToolbar(){
        return mToolbar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void hideActionBar(){
        onActionBarAutoShowOrHide(false);
    }

    public void showActionBar(){
        onActionBarAutoShowOrHide(true);
    }

    protected void registerHideableHeaderView(View hideableHeaderView) {
        if (!mHideableHeaderViews.contains(hideableHeaderView)) {
            mHideableHeaderViews.add(hideableHeaderView);
        }
    }

    protected void deregisterHideableHeaderView(View hideableHeaderView) {
        if (mHideableHeaderViews.contains(hideableHeaderView)) {
            mHideableHeaderViews.remove(hideableHeaderView);
        }
    }

    protected void onActionBarAutoShowOrHide(boolean shown) {

        for (View view : mHideableHeaderViews) {
            if (shown) {
                ViewPropertyAnimator.animate(view)
                        .translationY(0)
                        .alpha(1)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            } else {
                ViewPropertyAnimator.animate(view)
                        .translationY(-view.getBottom())
                        .alpha(0)
                        .setDuration(HEADER_HIDE_ANIM_DURATION)
                        .setInterpolator(new DecelerateInterpolator());
            }
        }
    }


    protected void sendFeedBack(){

        String subject = getString(R.string.app_full_name)
                + " (Android - " + BuildConfig.VERSION_NAME + ")";

        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.app_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(intent, getString(R.string.label_email)));
        }
    }

    protected void showHelp(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.app_help)));
        startActivity(intent);
    }

    protected void showToast(String message){
        ToastUtil.show(this, message);
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                showToast("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");



            // Check for gas delivery -- if we own gas, we should fill up the tank immediately
         /*   Purchase gasPurchase = inventory.getPurchase(SKU_DONATION);
            if (gasPurchase != null && verifyDeveloperPayload(gasPurchase)) {
                Log.d(TAG, "We have gas. Consuming it.");
                mHelper.consumeAsync(inventory.getPurchase(SKU_DONATION), mConsumeFinishedListener);
                return;
            }*/

            //updateUi();
            //setWaitScreen(false);
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }
    };

    // User clicked the "Donate" button
    public void onDonateButtonClicked() {
        Log.d(TAG, "Donate button clicked.");

        Log.d(TAG, "Launching donate.");

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        final String payload = "";

        final String[] skuArray = getResources().getStringArray(R.array.available_donations_sku);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(R.string.title_make_donation)
                .items(getResources().getStringArray(R.array.available_donations_titles))
                .itemsCallbackSingleChoice(1, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int position, CharSequence charSequence) {

                        mHelper.launchPurchaseFlow(BaseActivity.this, skuArray[position], RC_REQUEST,
                                mPurchaseFinishedListener, payload);
                    }
                })
                .positiveText(R.string.action_donate)
                .build().show();



    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                showToast("Error purchasing: " + result);
               // setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                showToast("Error purchasing. Authenticity verification failed.");
                //setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");

           /* if (purchase.getSku().equals(SKU_DONATION)) {
                // bought 1/4 tank of gas. So consume it.
                Log.d(TAG, "Purchase is gas. Starting gas consumption.");
                mHelper.consumeAsync(purchase, mConsumeFinishedListener);
            }*/

        }
    };

    // Called when consumption is complete
    IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
        public void onConsumeFinished(Purchase purchase, IabResult result) {
            Log.d(TAG, "Consumption finished. Purchase: " + purchase + ", result: " + result);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            // We know this is the "gas" sku because it's the only one we consume,
            // so we don't check which sku was consumed. If you have more than one
            // sku, you probably should check...
            if (result.isSuccess()) {
                // successfully consumed, so we apply the effects of the item in our
                // game world's logic, which in our case means filling the gas tank a bit
                Log.d(TAG, "Consumption successful. Provisioning.");

            }
            else {
                showToast("Error while consuming: " + result);
            }

            Log.d(TAG, "End consumption flow.");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        return true;

    }

}
