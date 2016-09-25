package app.com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class MoviesFragment extends Fragment {

    ImageAdapter moviePosterAdapter;
    MovieListener mListener;

    public MoviesFragment() {
    }
    public void  onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //updateMovies();
    }
    public void onStart(){
        super.onStart();
        updateMovies();

    }
    public void onResume()
    {
        super.onResume();
        updateMovies();
    }

    public void updateMovies(){
        FetchMoviesTask movies=new FetchMoviesTask();
        boolean favorite= isFavorite(getActivity());
        boolean whichFilter= popularOrTop(getActivity());
        String filter;
        if(favorite)
            filter= "favorite";
        else if(whichFilter)
            filter= "popular";
        else
            filter= "top_rated";
        movies.execute(filter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.fragment_main, container, false);
        PreferenceManager.setDefaultValues(getActivity(), R.xml.pref_general, true);
        ArrayList<String> arrayOfMovies= new ArrayList<String>();
        moviePosterAdapter= new ImageAdapter(getActivity(), R.layout.fragment_main,arrayOfMovies);
        GridView gridView= (GridView) rootView.findViewById(R.id.gridView);
        gridView.setAdapter(moviePosterAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            public void onItemClick(AdapterView<?> parent, View v, int position, long id){

                String selectedMovie = (String) moviePosterAdapter.getItem(position);
                //Log.v("yara", selectedMovie);
                mListener.setSelectedMovie(selectedMovie);
            }

        });
        return rootView;
    }

    public void setNameListener(MovieListener movieListener) {
        mListener=movieListener;
    }

    //returns true if mostPopular and false if topRated
    public static boolean popularOrTop(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_mostPopular))
                .equals(context.getString(R.string.pref_sort_mostPopular));
    }

    public static boolean isFavorite(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_favorites))
                .equals(context.getString(R.string.pref_sort_favorites));
    }



    public class FetchMoviesTask extends AsyncTask<String,Void,String[]> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        protected void onPostExecute(String[] result) {
            if(result !=null) {
                moviePosterAdapter.clear();
                if(isFavorite(getActivity()) && result.length ==0)
                    Toast.makeText(getActivity(), "You do not have favorites",
                            Toast.LENGTH_LONG).show();

                for (String movieInfoStr : result) {
                    moviePosterAdapter.add(movieInfoStr);
                }

            }
            else if(!isOnline()) {
                Toast.makeText(getActivity(), "You are not connected to the internet",
                       Toast.LENGTH_LONG).show();
            }
        }
        protected String[] doInBackground(String... strings) {
            if(!isOnline()){
                return null;
            }
            if(strings == null)
                return null;
            Log.v("entered doInBackground", "...");
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String moviesJsonStr = null;
            try {
                if(strings[0].equals("favorite")) {
                    SharedPreferences prefFavoriteMovies = getActivity().getSharedPreferences("favorites", Context.MODE_PRIVATE);
                    Map<String, ?> allEntries =prefFavoriteMovies.getAll();
                    String[] favoriteMovies= new String[allEntries.size()];
                    int i =0;
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        favoriteMovies[i]=entry.getValue().toString();

                        //Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
                        i++;
                    }
                    return favoriteMovies;


                }

                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/";
                String addFilterToURL= FORECAST_BASE_URL + strings[0] + "?";
                //Log.v("yara", strings[0]);
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(addFilterToURL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.Movies_API_KEY)
                        .build();
                //Log.v("The url is",builtUri.toString() );
                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally

            {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            //Log.v(LOG_TAG,moviesJsonStr);
            try {
                return getMovieDataFromJson(moviesJsonStr);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        public String[] getMovieDataFromJson(String moviesJsonStr) throws JSONException {
            final String RESULTS= "results";
            final String POSTER = "poster_path";
            final String OVERVIEW = "overview";
            final String RATING = "vote_average";
            final String RELEASE_DATE = "release_date";
            final String ORIGINAL_TITLE = "original_title";
            final String ID= "id";
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            //Log.v("yara", String.valueOf(moviesJson));
            JSONArray movieArray = moviesJson.getJSONArray(RESULTS);
            String[] resultStrs= new String[movieArray.length()];
            for(int i = 0; i < movieArray.length(); i++) {
                String poster;
                String overview;
                Double rating;
                String originalTitle;
                String year;
                String id;
                JSONObject movieInfo = movieArray.getJSONObject(i);
                poster= movieInfo.getString(POSTER);
                overview= movieInfo.getString(OVERVIEW);
                rating=movieInfo.getDouble(RATING);
                originalTitle= movieInfo.getString(ORIGINAL_TITLE);
                year= movieInfo.getString(RELEASE_DATE).split("-")[0];
                id= movieInfo.getString(ID);
                resultStrs[i] = poster + "#" + overview + "#" + rating + "#" + originalTitle + "#" + year + "#" + id;
                //Log.v(i+"->", resultStrs[i]);
            }
            return resultStrs;
        }
    }
}

