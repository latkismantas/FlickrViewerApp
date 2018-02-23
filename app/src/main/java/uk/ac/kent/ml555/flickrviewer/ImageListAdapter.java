package uk.ac.kent.ml555.flickrviewer;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.toolbox.NetworkImageView;
import java.util.ArrayList;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    public class ViewHolder extends RecyclerView.ViewHolder{


        NetworkImageView mainImage;

        private View.OnClickListener onClick = new View.OnClickListener(){
            @Override
            public void onClick(View view){
                int position = ViewHolder.this.getLayoutPosition();
                Intent intent = new Intent(view.getContext(), DetailsActivity.class);
                intent.putExtra("PHOTO_POSITION", position);
                context.startActivity(intent);

            }
        };


        public ViewHolder(View itemView) {
            super(itemView);
            mainImage = (NetworkImageView) itemView.findViewById(R.id.mainImage);
            this.itemView.setOnClickListener(onClick);

        }
    }

    public ArrayList<ImageInfo> imageList = new ArrayList<ImageInfo>();
    Context context;
    public ImageListAdapter(Context context){
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cell_image_card,parent,false);

        ImageListAdapter.ViewHolder vh = new ImageListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageInfo imageInfo = imageList.get(position);
        holder.mainImage.setImageUrl(imageInfo.url_m, NetworkMgr.getInstance(context).imageLoader);
    }

    @Override
    public int getItemCount() {
        int x = imageList.size();
        return x;
    }

    public void clearList() {
        imageList.clear();

        notifyDataSetChanged();
    }


}
