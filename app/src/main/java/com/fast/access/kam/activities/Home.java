package com.fast.access.kam.activities;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.fast.access.kam.R;
import com.fast.access.kam.global.adapter.AppsAdapter;
import com.fast.access.kam.global.loader.ApplicationsLoader;
import com.fast.access.kam.global.model.AppsModel;
import com.fast.access.kam.widget.impl.OnItemClickListener;
import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class Home extends AppCompatActivity implements SearchView.OnQueryTextListener,
        NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<List<AppsModel>> {
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.appbar)
    AppBarLayout appbar;
    @Bind(R.id.recycler)
    RecyclerView recycler;
    @Bind(R.id.main_content)
    CoordinatorLayout mainContent;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.progress)
    ProgressBar progress;
    private GridLayoutManager manager;
    private AppsAdapter adapter;
    private final String APP_LIST = "AppsList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        ButterKnife.bind(this);
        manager = new GridLayoutManager(this, getResources().getInteger(R.integer.num_row));
        recycler.setItemAnimator(new DefaultItemAnimator());
        recycler.setHasFixedSize(true);
        recycler.setLayoutManager(manager);
        adapter = new AppsAdapter(onClick, new ArrayList<AppsModel>());
        recycler.setAdapter(adapter);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            ab.setDisplayHomeAsUpEnabled(true);
        }
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(this);
    }


    private OnItemClickListener onClick = new OnItemClickListener() {
        @Override
        public void onItemClickListener(View v, int position) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("AppsModel", adapter.getModelList().get(position));
            Intent intent = new Intent(Home.this, AppDetailsActivity.class);
            intent.putExtras(bundle);
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(Home.this, v, getString(R.string.app_icon_transition));
            startActivity(intent, options.toBundle());
        }
    };


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.isEmpty()) {
            adapter.getFilter().filter("");
        } else {
            adapter.getFilter().filter(newText.toLowerCase());
        }

        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        mDrawerLayout.closeDrawers();
        switch (menuItem.getItemId()) {
            case R.id.settings:
                new LibsBuilder()
                        .withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR)
                        .withAboutIconShown(true)
                        .withAboutVersionShown(true)
                        .withAnimations(true)
                        .withActivityTheme(R.style.AboutActivity)
                        .start(Home.this);
                return true;
            case R.id.refresh:
                refresh();
                return true;
            case R.id.team:
                startActivity(new Intent(this, TeamActivity.class));
                return true;

        }
        return true;
    }

    @Override
    public Loader<List<AppsModel>> onCreateLoader(int id, Bundle args) {
        progress.setVisibility(View.VISIBLE);
        return new ApplicationsLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<AppsModel>> loader, List<AppsModel> data) {
        adapter.insert(data);
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<AppsModel>> loader) {
        adapter.clearAll();
    }

    private void refresh() {
        getSupportLoaderManager().restartLoader(0, null, this);
    }
}
