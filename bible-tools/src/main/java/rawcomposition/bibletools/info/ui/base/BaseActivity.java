package rawcomposition.bibletools.info.ui.base;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import rawcomposition.bibletools.info.BuildConfig;
import rawcomposition.bibletools.info.R;
import rawcomposition.bibletools.info.util.ThemeUtil;
import rawcomposition.bibletools.info.util.ToastUtil;
import rawcomposition.bibletools.info.util.VersionUtils;
import rawcomposition.bibletools.info.util.billing.IabHelper;
import rawcomposition.bibletools.info.util.billing.IabResult;
import rawcomposition.bibletools.info.util.billing.Inventory;
import rawcomposition.bibletools.info.util.billing.Purchase;

/**
 * Created by tinashe on 2016/08/31.
 */
public abstract class BaseActivity extends AppCompatActivity implements BaseView {

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    private static final String TAG = BaseActivity.class.getName();

    @Nullable
    @BindView(R.id.app_action_bar)
    public Toolbar toolbar;

    private AlertDialog alertDialog;
    private DefaultProgressDialog progressDialog;

    public static void navigateUpOrBack(Activity currentActivity,
                                        Class<? extends Activity> syntheticParentActivity) {
        // Retrieve parent activity from AndroidManifest.
        Intent intent = NavUtils.getParentActivityIntent(currentActivity);

        // Synthesize the parent activity when a natural one doesn't exist.
        if (intent == null && syntheticParentActivity != null) {
            try {
                intent = NavUtils.getParentActivityIntent(currentActivity, syntheticParentActivity);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        if (intent == null) {
            // No parent defined in manifest. This indicates the activity may be used by
            // in multiple flows throughout the app and doesn't have a strict parent. In
            // this case the navigation up button should act in the same manner as the
            // back button. This will result in users being forwarded back to other
            // applications if currentActivity was invoked from another application.
            currentActivity.onBackPressed();
        } else {
            if (NavUtils.shouldUpRecreateTask(currentActivity, intent)) {
                // Need to synthesize a backstack since currentActivity was probably invoked by a
                // different app. The preserves the "Up" functionality within the app according to
                // the activity hierarchy defined in AndroidManifest.xml via parentActivity
                // attributes.
                TaskStackBuilder builder = TaskStackBuilder.create(currentActivity);
                builder.addNextIntentWithParentStack(intent);
                builder.startActivities();
            } else {
                // Navigate normally to the manifest defined "Up" activity.
                NavUtils.navigateUpTo(currentActivity, intent);
            }
        }
    }

    // The helper object
    private IabHelper mHelper;

    private int mSelected = 1;

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

    protected abstract int layoutRes();

    protected abstract boolean showHomeAsUp();

    protected boolean isSettings() {
        return false;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtil.setAppTheme(this, isSettings());
        setContentView(layoutRes());
        ButterKnife.bind(this);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (showHomeAsUp() && getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        mHelper = new IabHelper(this, getString(R.string.app_license_key));

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(false);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(result -> {
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
        });

        hookUpPresenter();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                navigateUpOrBack(this, null);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void switchToFragment(Fragment fragment, int containerResId) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(containerResId, fragment, fragment.getClass().getName())
                .commit();
    }


    @Override
    public void showProgressIndicator() {
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }

        if (progressDialog == null) {
            progressDialog = new DefaultProgressDialog(this);
            progressDialog.setMessage("Please wait...");
        }

        progressDialog.show();
    }

    @Override
    public void showProgressIndicator(String msg) {
        if (progressDialog != null && progressDialog.isShowing()) {
            return;
        }

        if (progressDialog == null) {
            progressDialog = new DefaultProgressDialog(this);
            progressDialog.setMessage(msg);
        }

        progressDialog.show();
    }

    @Override
    public void hideProgressIndicator() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        progressDialog = null;
    }

    @Override
    public void hideAlert() {
        if (alertDialog != null && alertDialog.isShowing()) {
            alertDialog.hide();
        }

        alertDialog = null;
    }

    @Override
    public void showAlert(String message) {
        showAlert(message, false);
    }

    @Override
    public void showAlert(String message, boolean finishActivity) {
        if (alertDialog != null) {
            hideAlert();
        }

        alertDialog = new AlertDialog.Builder(this)
                .setTitle("Alert")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> {
                    if (finishActivity) {
                        finish();
                    }
                })
                .create();

        alertDialog.show();
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

        //Not necessary ATM
        return true;

    }

    public void sendFeedBack() {

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

        // MaterialDialog.Builder builder = new MaterialDialog.Builder(this);
        new AlertDialog.Builder(this)
                .setTitle(R.string.title_make_donation)
                .setSingleChoiceItems(getResources().getStringArray(R.array.available_donations_titles), mSelected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int position) {
                        mSelected = position;
                    }
                })
                .setPositiveButton(R.string.action_donate, (dialogInterface, i) -> mHelper.launchPurchaseFlow(BaseActivity.this, skuArray[mSelected], RC_REQUEST,
                        mPurchaseFinishedListener, payload))
                .setNegativeButton(R.string.action_cancel, null)
                .create().show();


    }

    public void showWebUrl(String url) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder()
                .setToolbarColor(ContextCompat.getColor(this, R.color.theme_primary))
                .setShowTitle(true)
                .enableUrlBarHiding();
        if (VersionUtils.isAtLeastL()) {
            builder.setStartAnimations(this, R.anim.slide_up, android.R.anim.fade_out)
                    .setExitAnimations(this, android.R.anim.fade_in, R.anim.slide_down);
        }
        CustomTabsIntent intent = builder.build();
        intent.launchUrl(this, Uri.parse(url));
    }
}
