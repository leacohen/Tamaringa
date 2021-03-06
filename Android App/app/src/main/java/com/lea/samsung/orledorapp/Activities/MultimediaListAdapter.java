package com.lea.samsung.orledorapp.Activities;

import java.lang.Object;
import java.lang.Override;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.style.UpdateLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lea.samsung.orledorapp.Common.UserContext;
import com.lea.samsung.orledorapp.Inerfaces.IRecommended;
import com.lea.samsung.orledorapp.Logic.MultimediaLogic;
import com.lea.samsung.orledorapp.Models.Multimedia;
import com.lea.samsung.orledorapp.Models.MultimediaType;
import com.lea.samsung.orledorapp.R;

public class MultimediaListAdapter extends BaseAdapter {
    private static List<Integer> signColors = new ArrayList<Integer>() {{
        add(Color.RED);
        add(Color.BLUE);
        add(Color.YELLOW);
    }};
    private Activity activity;
    private LayoutInflater inflater;
    private List<? extends IRecommended> multimedias;

    public MultimediaListAdapter(Activity activity, List<? extends IRecommended> multimedias) {
        this.activity = activity;
        this.multimedias = multimedias;
    }

    @Override
    public int getCount() {
        return multimedias.size();
    }

    @Override
    public Object getItem(int location) {
        return multimedias.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_raw, null);

        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView lblLanguages = (TextView)convertView.findViewById(R.id.multimedia_language);
        TextView lblDate = (TextView)convertView.findViewById(R.id.multimedia_date);
        ImageView ivSign = (ImageView)convertView.findViewById(R.id.lvSign);
        ImageView ivLike = (ImageView) convertView.findViewById(R.id.lvLike);
        ImageView ivDislike = (ImageView) convertView.findViewById(R.id.lvDislike);

        // getting movie data for the row
        final IRecommended m = multimedias.get(position);

        // title
        title.setText(m.get_name());

        ivSign.setColorFilter(signColors.get(position % signColors.size()));
        if(m.get_type() == MultimediaType.Song) {
            ivSign.setImageResource(R.drawable.music_sign);
        }
        else if(m.get_type() == MultimediaType.Movie) {
            ivSign.setImageResource(R.drawable.movie_sign);
        }
        else {
            ivSign.setImageResource(R.drawable.other_sign);
        }

        if(m instanceof  Multimedia) {
            Multimedia multimedia = (Multimedia) m;

            setLikeClicks(
                    ivLike,
                    ivDislike,
                    multimedia);

            setLikeColors(
                    ivLike,
                    ivDislike,
                    multimedia.get_name());

            lblLanguages.setVisibility(View.VISIBLE);
            lblLanguages.setText(activity.getString(R.string.lvLanguage) + ": " + multimedia.get_language());

            if(multimedia.get_publishDate() != null) {
                lblDate.setVisibility(View.VISIBLE);
                lblDate.setText(activity.getString(R.string.lvDate) + ": " +
                        android.text.format.DateFormat.format("yyyy/MM/dd", multimedia.get_publishDate()));
            }
        }
        else {
            ivLike.setVisibility(View.INVISIBLE);
            ivDislike.setVisibility(View.INVISIBLE);
            lblDate.setVisibility(View.INVISIBLE);
            lblLanguages.setVisibility(View.INVISIBLE);
        }


        return convertView;
    }

    private void setLikeColors(
            ImageView ivLike,
            ImageView ivDislike,
            String mediaName) {
        HashMap userLikes = UserContext.getLoggedUser().getLikes();

        if(!userLikes.containsKey(mediaName)) {
            ivLike.setColorFilter(Color.BLACK);
            ivDislike.setColorFilter(Color.BLACK);

            return;
        }

        int userLikeValue = (int)userLikes.get(mediaName);

        if(userLikeValue == 1) {
            ivLike.setColorFilter(Color.GREEN);
            ivDislike.setColorFilter(Color.BLACK);
        }

        else if(userLikeValue == -1) {
            ivLike.setColorFilter(Color.BLACK);
            ivDislike.setColorFilter(Color.RED);
        }
    }

    private void setLikeClicks(
            final ImageView like,
            final ImageView dislike,
            final Multimedia media) {



        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MultimediaLogic().Like(media);
                notifyDataSetChanged();
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MultimediaLogic().Dislike(media);
                notifyDataSetChanged();
            }
        });
    }
}