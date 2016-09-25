package app.com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Yarayehia on 8/12/16.
 */
public class ImageAdapter extends ArrayAdapter {
    private Context mContext;
    private int mResource;
    private ArrayList<String> mThumbIds;

    public ImageAdapter(Context context, int resource, ArrayList<String>items) {
        super(context,resource,items);
        mContext = context;

        mResource=resource;
        mThumbIds= items;
    }

    public int getCount() {
        return mThumbIds.size();
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        String movie= (String) getItem(position);
        //Log.v("The result of getItem",movie);
        if (convertView == null) {
            //Log.v("inside if in getITEM", "..");
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
            imageView = new ImageView(mContext);
        } else {
            //Log.v("inside else in getITEM", "..");
            imageView= new ImageView(mContext);
        }
//        Log.v("yara", mThumbIds.get(position));
//        Log.v("path", "http://image.tmdb.org/t/p/w185" + mThumbIds.get(position).split("#")[0]);
//        Log.v("poster path",mThumbIds.get(position).split("#")[0]);
//        Log.v("imageView", String.valueOf(imageView));
        Picasso.with(mContext).load("http://image.tmdb.org/t/p/w185" + mThumbIds.get(position).split("#")[0]).into(imageView);
        return imageView;
    }

}
