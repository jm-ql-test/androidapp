package com.aexp.jmai.qlexam;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.aexp.jmai.qlexam.domain.Game;
import com.squareup.picasso.Picasso;
import java.util.List;

public class SearchResultAdapter extends ArrayAdapter<Game> {

    private final Context    context;
    private final int        layoutResId;
    private final List<Game> data;

    public SearchResultAdapter(final Context context, final int layoutResId, final List<Game> data) {
        super(context, layoutResId, data);
        this.layoutResId = layoutResId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GameHolder holder = null;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            convertView = inflater.inflate(layoutResId, parent, false);

            holder = new GameHolder();
            holder.imageIcon = (ImageView) convertView.findViewById(R.id.icon);
            holder.textTitle = (TextView) convertView.findViewById(R.id.title);

            convertView.setTag(holder);
        } else {
            holder = (GameHolder) convertView.getTag();
        }

        Game game = data.get(position);
        holder.textTitle.setText(game.getName());
        if (game.getIconUrl() != null) {
            Picasso.with(this.context).load(game.getIconUrl()).error(R.drawable.thumbnail_not_available).into(holder.imageIcon);
        } else {
            Picasso.with(this.context).load(R.drawable.thumbnail_not_available).into(holder.imageIcon);
        }

        return convertView;
    }

    static class GameHolder {
        ImageView imageIcon;
        TextView  textTitle;
    }
}