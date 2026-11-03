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
     */
    private static List<Posters> createPosters() {
        List<Posters> posters = new ArrayList<>();

        Posters avatarMovie = new Posters();
        avatarMovie.id = "avatar";
        avatarMovie.image = R.drawable.avatar_aang_poster;
        avatarMovie.name = "Avatar Aang: The Last Airbender";
        avatarMovie.rating = 4f;
        avatarMovie.createdBy = "Lauren Montgomery";
        avatarMovie.story = "After the events Avatar: The Last Airbender, Avatar Aang looks for answers to help restore the Air Nation.";
        posters.add(avatarMovie);

        Posters forgottenMovie = new Posters();
        forgottenMovie.id = "forgotten";
        forgottenMovie.image = R.drawable.forgotten_island_poster;
        forgottenMovie.name = "Forgotten Island";
        forgottenMovie.rating = 4f;
        forgottenMovie.createdBy = "Joel Crawford & Januel Mercado";
        forgottenMovie.story = "Two best friends trying to recover their memories while traveling the forgotten islands.";
        posters.add(forgottenMovie);

        Posters backroomMovie = new Posters();
        backroomMovie.id = "backroom";
        backroomMovie.image = R.drawable.backroom_poster;
        backroomMovie.name = "Backrooms";
        backroomMovie.rating = 3.2f;
        backroomMovie.createdBy = "Kane Parsons";
        backroomMovie.story = "A furniture store owner discovers something in his walls.";
        posters.add(backroomMovie);

        Posters marioMovie = new Posters();
        marioMovie.id = "mario";
        marioMovie.image = R.drawable.mario_poster;
        marioMovie.name = "The Super Mario Galaxy Movie";
        marioMovie.rating = 3f;
        marioMovie.createdBy = "Aaron Horvath & Michael Jelenic";
        marioMovie.story = "Mario and Luigi travels the galaxy.";
        posters.add(marioMovie);

        Posters toyMovie = new Posters();
        toyMovie.id = "toy";
        toyMovie.image = R.drawable.toy_story_poster;
        toyMovie.name = "Toy Story 5";
        toyMovie.rating = 3.4f;
        toyMovie.createdBy = "Andrew Stanton";
        toyMovie.story = "Jesse battles with tech to find a friend for Bonnie.";
        posters.add(toyMovie);

        return posters;

    }


}
