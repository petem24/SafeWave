package ljmu.cmp.safewave;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Pete on 07/02/2018.
 */

public class BeachAdapter extends RecyclerView.Adapter<BeachAdapter.ViewHolder>{

    private Context context;
    private List<BeachFragment> cardList;


    public BeachAdapter(Context context, List list) {
        this.context = context;
        this.cardList = list;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.card_layout_beach, null);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        final BeachFragment beachFragment = cardList.get(position);

        holder.textViewTitle.setText(beachFragment.getTitle());
        holder.textViewDesc.setText(beachFragment.getDesc());
        holder.imageView.setImageBitmap(getBitmapFromAssets("Images/"+beachFragment.getImg()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, BeachFullActivity.class);
                i.putExtra("BeachName", beachFragment.getTitle());
                i.putExtra("BeachShort", beachFragment.getDesc());
                i.putExtra("BeachDesc", SQLite.fullDescArray.get(position));
                context.startActivity(i);
            }
        });

    }

    private Bitmap getBitmapFromAssets(String fileName){

        AssetManager am = context.getAssets();
        InputStream is = null;
        try{

            is = am.open(fileName);
        }catch(IOException e){
            e.printStackTrace();
        }

        Bitmap bitmap = BitmapFactory.decodeStream(is);
        return bitmap;
    }

    @Override
    public int getItemCount() {

        return cardList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView, imageViewWeather;

        TextView textViewTitle, textViewDesc, textViewCond, textViewTemp;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgProfile);
            imageViewWeather = itemView.findViewById(R.id.currentWeatherImg1);
            textViewTitle = itemView.findViewById(R.id.cardViewTitle);
            textViewDesc = itemView.findViewById(R.id.cardViewDesc);
            textViewCond = itemView.findViewById(R.id.currentWeatherCon);
            textViewTemp = itemView.findViewById(R.id.currentWeatherTemp);
        }
    }





}