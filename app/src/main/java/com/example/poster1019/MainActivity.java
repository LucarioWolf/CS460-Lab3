package com.example.poster1019;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_CURRENT_PAGE = "current_page";

    //Tags to find each page's Fragment again in the FragmentManager.
    private static final String TAG_PAGE_MAIN = "page_main";
    private static final String TAG_PAGE_FAVORITES = "page_favorites";
    private static final String TAG_PAGE_CONTACT = "page_contact";

    //Added the navigator tag for celebrity page.
    private static final String TAG_PAGE_CELEBRITY = "page_celebrity";

    //drawer the side menu inside it, and the bottom bar.
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigation;

    private int currentPageId = R.id.nav_main;

    private PosterRepository posterRepository;
    private final PosterRepository.FavoritesListener favoritesBadgeListener = this::updateFavoritesBadge;

    private void updateFavoritesBadge() {

        int count = posterRepository.getFavoriteCount();
        BadgeDrawable badge = bottomNavigation.getOrCreateBadge(R.id.nav_favorites);
        badge.setBackgroundColor(ContextCompat.getColor(this, R.color.colorBadge));
        badge.setBadgeTextColor(ContextCompat.getColor(this, R.color.colorTextIcons));
        badge.setNumber(count);
        badge.setVisible(count > 0);
    }


    /**
     * On the setting the fragments if statement, I added the 4th icon and page to the app.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //find by id
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        //The drawer and the bottom bar both just call showPage().
        // Returning true tells them to show the tapped item as selected.
        navigationView.setNavigationItemSelectedListener(item -> {
            showPage(item.getItemId());
            return true;
        });
        bottomNavigation.setOnItemSelectedListener(item -> {
            showPage(item.getItemId());
            return true;
        });

        //Window insets: the sizes of the status bar, navigation bar and camera cut-out.
        final AppBarLayout appBarLayout = findViewById(R.id.appBarLayout);
        final View fragmentContainer = findViewById(R.id.fragmentContainer);

        final View drawerHeader = navigationView.getHeaderView(0);
        final int drawerHeaderPaddingTop = drawerHeader.getPaddingTop();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            appBarLayout.setPadding(bars.left, bars.top, bars.right, 0);
            fragmentContainer.setPadding(bars.left, 0, bars.right, 0);
            drawerHeader.setPadding(drawerHeader.getPaddingLeft(), drawerHeaderPaddingTop + bars.top,
                    drawerHeader.getPaddingRight(), drawerHeader.getPaddingBottom());
            return insets;
        });

        //Added the CelebrityFragment to the fragment manager
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragmentContainer, new PostersFragment(), TAG_PAGE_MAIN)
                    .add(R.id.fragmentContainer, new FavoritesFragment(), TAG_PAGE_FAVORITES)
                    .add(R.id.fragmentContainer, new ContactFragment(), TAG_PAGE_CONTACT)
                    .add(R.id.fragmentContainer, new CelebrityFragment(), TAG_PAGE_CELEBRITY)
                    .commitNow();
        } else {
            currentPageId = savedInstanceState.getInt(KEY_CURRENT_PAGE, R.id.nav_main);
        }
        showPage(currentPageId);

        //Favorites badge: show the current count now, then follow every change.
        posterRepository = PosterRepository.getInstance(this);
        posterRepository.addFavoritesListener(favoritesBadgeListener);
        updateFavoritesBadge();
    }

    @Override
    protected void onDestroy() {
        posterRepository.removeFavoritesListener(favoritesBadgeListener);
        super.onDestroy();
    }

    //Remember the current page, so a rotation doesn't jump back to the Main Page.
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_CURRENT_PAGE, currentPageId);
    }

    /**
     * Added the Celebrity page to the string array so that when the celebrity icon is pressed
     * it switches to the celebrity page.
     * @param pageId an integer id to get the page selected.
     */
    private void showPage(int pageId) {
        currentPageId = pageId;

        //Show this page's Fragment and hide the other two. show/hide (instead of
        // replace) keeps each page alive, with its scroll position and selections.
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        String visibleTag = tagForPage(pageId);
        for (String tag : new String[]{TAG_PAGE_MAIN, TAG_PAGE_FAVORITES, TAG_PAGE_CONTACT, TAG_PAGE_CELEBRITY}) {
            Fragment page = fragmentManager.findFragmentByTag(tag);
            if (page == null) {
                continue;
            }
            if (tag.equals(visibleTag)) {
                transaction.show(page);
            } else {
                transaction.hide(page);
            }
        }
        transaction.commit();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(titleForPage(pageId));
        }

        navigationView.setCheckedItem(pageId);
        bottomNavigation.getMenu().findItem(pageId).setChecked(true);

        drawerLayout.closeDrawer(GravityCompat.START);

    }

    /**
     * Added the if statement for the celebrity page to show the title of the page
     * @param pageId A int that determines which page number we are.
     * @return The title of the page.
     */
    private int titleForPage(int pageId) {
        if (pageId == R.id.nav_favorites) {
            return R.string.title_favorites;
        } else if (pageId == R.id.nav_contact) {
            return R.string.title_contact;
        }
        //Added the celebrity page title
        else if(pageId == R.id.nav_celebrity){
            return R.string.nav_celebrity;
        }
        return R.string.posters;
    }

    /**
     * Added the celebrity navigator tag
     * @param pageId The int that determines the page
     * @return A string containing the tag for the navigator.
     */
    private String tagForPage(int pageId) {
        if (pageId == R.id.nav_favorites) {
            return TAG_PAGE_FAVORITES;
        } else if (pageId == R.id.nav_contact) {
            return TAG_PAGE_CONTACT;
        }
        //Added the celebrity navigator tag
        else if (pageId == R.id.nav_celebrity){
            return TAG_PAGE_CELEBRITY;
        }
        return TAG_PAGE_MAIN;
    }


}