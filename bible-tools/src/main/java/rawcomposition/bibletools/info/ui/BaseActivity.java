package rawcomposition.bibletools.info.ui;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.VersionUtils;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import rawcomposition.bibletools.info.BuildConfig;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.util.ThemeUtil;
import rawcomposition.bibletools.info.util.ToastUtil;
import rawcomposition.bibletools.info.util.billing.IabHelper;
import rawcomposition.bibletools.info.util.billing.IabResult;
import rawcomposition.bibletools.info.util.billing.Inventory;
import rawcomposition.bibletools.info.util.billing.Purchase;

/**
 * Created by tinashe
 */
public abstract class BaseActivity extends AppCompatActivity {

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    private static final String TAG = BaseActivity.class.getName();
    protected Toolbar mToolbar;
    protected View mHeaderView;
    // The helper object
    private IabHelper mHelper;

    // Listener that's called when we finish querying the items and subscriptions we own
    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                Log.d(TAG, "Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");


        }
    };
    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                //  showToast("There was an Error, please try again. ");
                // setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                //  showToast("There was an Error, please try again. ");
                return;
            }

            showToast("Donation successful :-)");


        }
    };

    protected abstract int getLayoutResource();

    @Override
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        ThemeUtil.setAppTheme(this, isSettings());

        setContentView(getLayoutResource());

        mToolbar = (Toolbar) findViewById(R.id.app_action_bar);
        mHeaderView = findViewById(R.id.header_view);
        if (mToolbar != null) {
            try {

                if (mHeaderView != null) {
                    ViewCompat.setTranslationZ(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
                    ViewCompat.setElevation(mHeaderView, getResources().getDimension(R.dimen.toolbar_elevation));
                }


                setSupportActionBar(mToolbar);

                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } catch (Throwable ex) {
                //
            }

        }

        mHelper = new IabHelper(this, getString(R.string.app_license_key));

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(false);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    Log.d(TAG, "Problem setting up in-app billing: " + result);
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

    public Toolbar getToolbar() {
        return mToolbar;
    }

    protected boolean isSettings() {
        return false;
    }

    @TargetApi(21)
    public void startAnActivity(Intent intent) {

        if (VersionUtils.isAtLeastL()) {
            startActivity(intent,
                    ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected void sendFeedBack() {

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

    protected void showHelp() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(getString(R.string.app_help)));
        startActivity(intent);
    }

    protected void showToast(String message) {
        ToastUtil.show(this, message);
    }

    // User clicked the "Donate" button
    public void onDonateButtonClicked() {

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        final String payload = "";

        final String[] skuArray = getResources().getStringArray(R.array.available_donations_sku);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        builder.title(R.string.title_make_donation)
                .items(getResources().getStringArray(R.array.available_donations_titles))
                .itemsCallbackSingleChoice(1, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog materialDialog, View view, int position, CharSequence charSequence) {
                        mHelper.launchPurchaseFlow(BaseActivity.this, skuArray[position], RC_REQUEST,
                                mPurchaseFinishedListener, payload);
                        return true;
                    }
                })
                .positiveText(R.string.action_donate)
                .negativeText(R.string.action_cancel)
                .build().show();


    }

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
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    /**
     * Verifies the developer payload of a purchase.
     */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        return true;

    }

}
