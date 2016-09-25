package app.com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DetailActivity extends AppCompatActivity {

    public static ArrayList<String> favorites= new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
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
        Bundle extras = getIntent().getExtras();
        if (null == savedInstanceState) {
            DetailActivityFragment mDetailsFragment = new DetailActivityFragment();
            //Log.v("yara", String.valueOf(extras));
            mDetailsFragment.setArguments(extras);
            //Log.v("yara", mDetailsFragment.getArguments().toString());
            getSupportFragmentManager().beginTransaction().add(R.id.llDetailsContainer, mDetailsFragment).commit();
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
