package com.example.poster1019;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The one place that owns the app's movie data: all posters, the selection, and the favorites.
 */
public class PosterRepository {

    // Notified whenever favorites are added or removed (Favorite List, badge).
    public interface FavoritesListener{
        void onFavoritesChanged();
    }

    private static PosterRepository instance;

    private final List<Posters> allPosters;
    private final FavoritesStore favoritesStore;
    private final List<FavoritesListener> favoritesListeners = new ArrayList<>();

    // Creates the single instance on first use. It keeps the application context,
    // never an Activity: holding an Activity in a singleton would leak it.
    public static synchronized PosterRepository getInstance(Context context) {
        if (instance == null) {
            instance = new PosterRepository(context.getApplicationContext());
        }
        return instance;
    }

    private PosterRepository(Context appContext) {
        allPosters = Collections.unmodifiableList(createPosters());
        favoritesStore = new FavoritesStore(appContext);

        // Forget saved favorites whose poster no longer exists in the app.
        List<String> validIds = new ArrayList<>();
        for (Posters poster : allPosters) {
            validIds.add(poster.id);
        }
        favoritesStore.retainOnly(validIds);
    }

    public List<Posters> getAllPosters(){
        return allPosters;
    }

    public Posters findById(String id){
        for(Posters posters:allPosters){
            if(posters.id.equals(id)){
                return posters;
            }
        }
        return null;
    }

    // deselects every poster
    public void clearSelection(){
        for(Posters poster : allPosters){
            poster.isSelected = false;
        }
    }

    //get favorite posters, in the order they were added
    public List<Posters> getFavorites(){
        List<Posters> favorites = new ArrayList<>();
        for (String id : favoritesStore.getIds()) {
            Posters poster = findById(id);
            if (poster != null) {
                favorites.add(poster);
            }
        }
        return favorites;
    }

    public int getFavoriteCount(){
        return favoritesStore.getIds().size();
    }

    // Adds posters to favorites, skipping ones already there.
    // Returns how many were new, so the page can show the right message.
    public int addFavorites(List<Posters> posters) {
        List<String> ids = new ArrayList<>();
        for (Posters poster : posters) {
            ids.add(poster.id);
        }
        int added = favoritesStore.addAll(ids);
        if (added > 0) {
            notifyFavoritesChanged();
        }
        return added;
    }

    // Removes one favorite. Returns true if it was a favorite.
    public boolean removeFavorite(String id) {
        boolean removed = favoritesStore.remove(id);
        if (removed) {
            notifyFavoritesChanged();
        }
        return removed;
    }

    // The listener (observer) pattern: pages register to hear about changes.
    // Whoever adds a listener must remove it again (onDestroyView / onDestroy).
    public void addFavoritesListener(FavoritesListener listener) {
        if (!favoritesListeners.contains(listener)) {
            favoritesListeners.add(listener);
        }
    }

    public void removeFavoritesListener(FavoritesListener listener) {
        favoritesListeners.remove(listener);
    }

    private void notifyFavoritesChanged() {
        for(FavoritesListener listener : new ArrayList<>(favoritesListeners)){
            listener.onFavoritesChanged();
        }
    }

    /**
     * Changed the posters to fit my top 5 movies
     * TODO: Change Posters
     */
    private static List<Posters> createPosters() {
        List<Posters> posters = new ArrayList<>();

        Posters the100 = new Posters();
        the100.id = "vietnam";
        the100.image = R.drawable.poster3;
        the100.name = "Vietnam Movie";
        the100.rating = 5f;
        the100.createdBy = "Cuong Ngo";
        the100.story = "This is an Vietnam Movie with a young lady";
        posters.add(the100);

        Posters chineseMovie = new Posters();
        chineseMovie.id = "chinese";
        chineseMovie.image = R.drawable.poster4;
        chineseMovie.name = "Chinese Movie";
        chineseMovie.rating = 4f;
        chineseMovie.createdBy = "Zhang San";
        chineseMovie.story = "This is an Chinese Movie with a young lady.";
        posters.add(chineseMovie);

        Posters japanMovie = new Posters();
        japanMovie.id = "japanese";
        japanMovie.image = R.drawable.poster1;
        japanMovie.name = "Japanese Movie";
        japanMovie.rating = 3f;
        japanMovie.createdBy = "Juzo Itami";
        japanMovie.story = "This is an Japanese Movie.";
        posters.add(japanMovie);

        Posters americanMovie = new Posters();
        americanMovie.id = "american";
        americanMovie.image = R.drawable.poster2;
        americanMovie.name = "American Movie";
        americanMovie.rating = 5f;
        americanMovie.createdBy = "Tim Burton";
        americanMovie.story = "This is an American Movie.";
        posters.add(americanMovie);

        return posters;

    }


}
