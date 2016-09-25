package app.com.example.android.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

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

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    TrailersAdapter trailerAdapter;
    ReviewsAdapter reviewAdapter;
    String id;


    public DetailActivityFragment() {
    }

    public void onStart(){
        super.onStart();
        FetchTrailersTask trailers=new FetchTrailersTask();
        FetchReviewsTask reviews=new FetchReviewsTask();
        trailers.execute(id);
        reviews.execute(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView= inflater.inflate(R.layout.fragment_detail, container, false);
        ArrayList<String> arrayOfTrailers= new ArrayList<String>();
        ArrayList<String> arrayOfReviews= new ArrayList<String>();
        trailerAdapter= new TrailersAdapter(getActivity(), R.layout.fragment_detail,arrayOfTrailers);
        ListView trailerListView= (ListView) rootView.findViewById(R.id.trailersList);
        trailerListView.setAdapter(trailerAdapter);
        trailerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String videoId= (String) v.getTag();
                //Log.v("yara", (String) v.getTag());
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId)));

            }
        });
        reviewAdapter= new ReviewsAdapter(getActivity(), R.layout.fragment_detail,arrayOfReviews);
        ListView reviewListView= (ListView) rootView.findViewById(R.id.reviewsList);
        reviewListView.setAdapter(reviewAdapter);
        reviewListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            }
        });
        //Log.v("yara",getArguments().getString("movie") );
        String[] movieStr = getArguments().getString("movie").split("#");
        String poster= movieStr[0];
        String overview= movieStr[1];
        String rating= movieStr[2] + "/10";
        String originalTitle= movieStr[3];
        String year= movieStr[4];
        id= movieStr[5];


        ((TextView) rootView.findViewById(R.id.title))
                .setText(originalTitle);
        ((TextView) rootView.findViewById(R.id.title)).setTag(id);
        ((TextView) rootView.findViewById(R.id.description))
                .setText(overview);
        ((TextView) rootView.findViewById(R.id.year))
                .setText(year);
        ((TextView) rootView.findViewById(R.id.rating))
                .setText(rating);
        Picasso.with(getActivity()).load("http://image.tmdb.org/t/p/w185" + poster).into((ImageView)rootView.findViewById(R.id.poster));
        ((ImageView)rootView.findViewById(R.id.poster)).setTag(poster);

        return rootView;
    }

    public class FetchTrailersTask extends AsyncTask<String,Void,String[]> {

        private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

        protected void onPostExecute(String[] result) {
            if(result !=null) {
                trailerAdapter.clear();
                for (String trailerInfoStr : result) {
                    trailerAdapter.add(trailerInfoStr);
                }

            }
        }
        protected String[] doInBackground(String... strings) {
            if(strings == null)
                return null;
            //Log.v("entered doInBackground", "...");
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String trailersJsonStr = null;
            try {

                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/" + strings[0]+ "/videos";
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.Movies_API_KEY)
                        .build();
                Log.v("The url is",builtUri.toString() );
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

                trailersJsonStr = buffer.toString();
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
            //Log.v(LOG_TAG,trailersJsonStr);
            try {
                return getTrailerDataFromJson(trailersJsonStr);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        public String[] getTrailerDataFromJson(String trailersJsonStr) throws JSONException {
            final String RESULTS= "results";
            final String ID = "id";
            final String TRAILER_NAME = "name";
            final String KEY = "key";

            JSONObject trailersJson = new JSONObject(trailersJsonStr);
            JSONArray trailerArray = trailersJson.getJSONArray(RESULTS);
            String[] resultStrs= new String[trailerArray.length()];

            for(int i = 0; i < trailerArray.length(); i++) {
                String id;
                String trailerName;
                String key;

                JSONObject trailerInfo = trailerArray.getJSONObject(i);
                id= trailerInfo.getString(ID);
                trailerName= trailerInfo.getString(TRAILER_NAME);
                key= trailerInfo.getString(KEY);

                resultStrs[i] = id + ":" + trailerName + ":" + key;
            }
            return resultStrs;
        }
    }


    public class FetchReviewsTask extends AsyncTask<String,Void,String[]> {

        private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

        protected void onPostExecute(String[] result) {
            if (result != null) {
                reviewAdapter.clear();
                for (String reviewInfoStr : result) {
                    reviewAdapter.add(reviewInfoStr);
                }

            }
        }

        protected String[] doInBackground(String... strings) {
            if (strings == null)
                return null;
            Log.v("entered doInBackground", "...");
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String reviewsJsonStr = null;
            try {

                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/" + strings[0] + "/reviews";
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.Movies_API_KEY)
                        .build();
                //Log.v("The url is", builtUri.toString());
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

                reviewsJsonStr = buffer.toString();
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
            Log.v(LOG_TAG, reviewsJsonStr);
            try {
                return getReviewDataFromJson(reviewsJsonStr);

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        public String[] getReviewDataFromJson(String reviewsJsonStr) throws JSONException {
            final String RESULTS = "results";
            final String ID = "id";
            final String AUTHOR = "author";
            final String CONTENT = "content";

            JSONObject reviewsJson = new JSONObject(reviewsJsonStr);
            JSONArray reviewArray = reviewsJson.getJSONArray(RESULTS);
            String[] resultStrs = new String[reviewArray.length()];

            for (int i = 0; i < reviewArray.length(); i++) {
                String id;
                String author;
                String content;

                JSONObject reviewInfo = reviewArray.getJSONObject(i);
                id = reviewInfo.getString(ID);
                author = reviewInfo.getString(AUTHOR);
                content = reviewInfo.getString(CONTENT);

                resultStrs[i] = id + "#" + author + "#" + content;
            }
            return resultStrs;
        }
    }
}
