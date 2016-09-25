package app.com.example.android.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Yarayehia on 9/23/16.
 */

public class ReviewsAdapter extends ArrayAdapter{

    private Context mContext;
    private int mResource;
    private ArrayList<String> mThumbIds;

    public ReviewsAdapter(Context context, int resource, ArrayList<String>items) {
        super(context,resource,items);
        mContext = context;
        mResource=resource;
        mThumbIds= items;
    }

    public int getCount() {
        return mThumbIds.size();
    }


    public View getView(int position, View convertView, ViewGroup parent) {

        TextView textView;
        String trailer= (String) getItem(position);
        //Log.v("The result of getItem",trailer);
        if (convertView == null) {
            //Log.v("inside if in getITEM", "..");
            convertView = LayoutInflater.from(mContext).inflate(mResource, parent, false);
            textView = new TextView(mContext);

        } else {
            //Log.v("inside else in getITEM", "..");
            textView= new TextView(mContext);
        }
        Log.v("yara", mThumbIds.get(position));
        textView.setText(mThumbIds.get(position).split("#")[2] + "/n" + "Written by: " + mThumbIds.get(position).split("#")[1] );

        return textView;
    }

}
