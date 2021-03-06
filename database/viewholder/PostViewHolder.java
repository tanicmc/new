package com.example.database.viewholder;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.database.R;
import com.example.database.models.Post;

public class PostViewHolder extends RecyclerView.ViewHolder {
	public ImageView starView;
	private TextView authorView;
	private TextView bodyView;
	private TextView numStarsView;
	private TextView titleView;
	private TextView priceView;

	public PostViewHolder(View itemView) {
		super(itemView);
		titleView = itemView.findViewById(R.id.post_title);
		authorView = itemView.findViewById(R.id.post_author);
		starView = itemView.findViewById(R.id.star);
		numStarsView = itemView.findViewById(R.id.post_num_stars);
		bodyView = itemView.findViewById(R.id.post_body);
		priceView = itemView.findViewById(R.id.post_price);
	}

	public void bindToPost(Post post, View.OnClickListener starClickListener) {
		titleView.setText(post.title);
		authorView.setText(post.author);
		numStarsView.setText(String.valueOf(post.starCount));
		bodyView.setText(post.body);
		priceView.setText(post.cfprice);
		starView.setOnClickListener(starClickListener);
	}
}