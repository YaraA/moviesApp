package app.com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MovieListener {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    boolean mTwoPane;
    public static ArrayList<String> favorites= new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        FrameLayout flPanel2 = (FrameLayout) findViewById(R.id.flPanel_Two);
        if (null == flPanel2) {
            mTwoPane = false;
        } else {
            mTwoPane = true;
        }
        if (null == savedInstanceState) {
            MoviesFragment movieFragment = new MoviesFragment();
            movieFragment.setNameListener(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.flPanel_One, movieFragment).commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    @Override
    public void setSelectedMovie(String movie) {
        if (mTwoPane) {
            DetailActivityFragment detailsFragment= new DetailActivityFragment();
            Bundle extras= new Bundle();
            extras.putString("movie", movie);
            detailsFragment.setArguments(extras);
            getSupportFragmentManager().beginTransaction().replace(R.id.flPanel_Two,detailsFragment).commit();
        } else {
            Intent i = new Intent(this, DetailActivity.class);
            i.putExtra("movie", movie);
            startActivity(i);
        }

    }


    public void addToFavorites(View view){
        String poster= (String) ((ImageView) this.findViewById(R.id.poster)).getTag();
        String overview= (String) ((TextView) this.findViewById(R.id.description)).getText();
        String rating=(String) ((TextView) this.findViewById(R.id.rating)).getText();
        rating= rating.split("/")[0];
        String originalTitle= (String) ((TextView) this.findViewById(R.id.title)).getText();
        String year= (String) ((TextView) this.findViewById(R.id.year)).getText();
        String id= (String) ((TextView) this.findViewById(R.id.title)).getTag();

        String movie= poster + "#" + overview + "#" + rating + "#" + originalTitle + "#" + year + "#" + id;
        int index= favorites.indexOf(movie);
        if(index == -1)
            favorites.add(poster + "#" + overview + "#" + rating + "#" + originalTitle + "#" + year + "#" + id);
        SharedPreferences sharedFavorites = this.getSharedPreferences("favorites", Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedFavoritesEditor = sharedFavorites.edit();
        sharedFavoritesEditor.clear();
        sharedFavoritesEditor.putInt("favorites_size", favorites.size());
        for(int i=0;i<favorites.size();i++)
            sharedFavoritesEditor.putString("favorites_" + i, favorites.get(i));
        sharedFavoritesEditor.commit();

    }
}
