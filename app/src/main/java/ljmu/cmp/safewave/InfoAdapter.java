package ljmu.cmp.safewave;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Pete on 07/02/2018.
 */

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ViewHolder>{

    private Context context;
    private List<InfoFragment> cardList;

    public InfoAdapter(Context context, List list) {
        this.context = context;
        this.cardList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.card_layout, null);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {


        final InfoFragment infoFragment = cardList.get(position);
        holder.textViewTitle.setText(infoFragment.getTitle());
        holder.textViewDesc.setText(infoFragment.getDesc());
        holder.imageView.setImageBitmap(getBitmapFromAssets("Images/"+infoFragment.getImg()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = LayoutInflater.from(context);
                final View dialogView = inflater.inflate(R.layout.card_info, null);

                ImageView image = dialogView.findViewById(R.id.imgProfile);
                TextView title = dialogView.findViewById(R.id.cardViewTitle);
                TextView fullDesc = dialogView.findViewById(R.id.cardViewDesc);

                image.setImageBitmap(getBitmapFromAssets("Images/"+infoFragment.getImg()));
                title.setText(infoFragment.getTitle());
                fullDesc.setText(infoFragment.getFullDesc());

                dialogBuilder.setView(dialogView);
                final AlertDialog b = dialogBuilder.create();
                b.show();
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

        ImageView imageView;

        TextView textViewTitle, textViewDesc;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imgProfile);
            textViewTitle = itemView.findViewById(R.id.cardViewTitle);
            textViewDesc = itemView.findViewById(R.id.cardViewDesc);
        }
    }



}
