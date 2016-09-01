package com.jszczygiel.foundation.views;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.MenuItem;

import com.jszczygiel.R;

/**
 * Class responsible for displaying fragment inside. Passes all extras sent in intent to fragment.
 */
public abstract class SimpleFragmentActivityImpl<T extends BaseFragmentImpl> extends AppCompatActivity {

    private T fragment;

    public T getFragment() {
        return fragment;
    }

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();

            fragment = newFragmentInstance();
            boolean noArguments = false;
            Bundle fragmentExtras = fragment.getArguments();
            if (fragmentExtras == null) {
                noArguments = true;
                fragmentExtras = new Bundle();
            }
            if (extras != null) {
                fragmentExtras.putAll(extras);
            }
            if (noArguments) {
                fragment.setArguments(fragmentExtras);
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.activity_simple_root, fragment);
            transaction.commitNow();
        } else {
            fragment = (T) getSupportFragmentManager().getFragments().get(0);
        }

    }

    public int getLayoutId() {
        return R.layout.activity_simple;
    }

    public abstract T newFragmentInstance();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (upIntent != null) {
            if (NavUtils.shouldUpRecreateTask(this, upIntent) || isTaskRoot()) {
                // This activity is NOT part of this app's task, so create a new task
                // when navigating up, with a synthesized back stack.
                TaskStackBuilder.create(this)
                        // Add all of this activity's parents to the back stack
                        .addNextIntentWithParentStack(upIntent)
                        // Navigate up to the closest parent
                        .startActivities();
            } else {
                // This activity is part of this app's task, so simply
                // navigate up to the logical parent activity.
                if (!fragment.onBackPressed()) {
                    ActivityCompat.finishAfterTransition(this);
                }
            }
        } else {
            if (!fragment.onBackPressed()) {
                ActivityCompat.finishAfterTransition(this);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setIntent(null);
    }

    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_simple_root, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        getFragment().onNewIntent(intent);
    }
}
