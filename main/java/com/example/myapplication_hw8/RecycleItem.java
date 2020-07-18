package com.example.myapplication_hw8;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RecycleItem extends RecyclerView.Adapter<RecycleItem.ViewHolder> {
    private static final String TAG = "CustomAdapter";

    private String[] mDataSet;

    // BEGIN_INCLUDE(recyclerViewSampleViewHolder)
    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final ImageView imageView;
        private final TextView shipping;
        private final TextView TopRated;
        private final TextView Condition;
        private final TextView Price;
        private final View view;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            title = (TextView) v.findViewById(R.id.recycle_item_title);
            imageView = (ImageView) v.findViewById(R.id.recycle_item_img);
            shipping = (TextView) v.findViewById(R.id.shipping);
            TopRated = (TextView) v.findViewById(R.id.TopRated);
            Condition = (TextView) v.findViewById(R.id.Condition);
            Price = (TextView) v.findViewById(R.id.price);
            view = v;
        }

        public TextView getTitleView() { return title; }
        public ImageView getImageView() { return imageView; }
        public TextView getShipping(){ return shipping;}
        public TextView getTopRated(){ return TopRated;}
        public TextView getCondition(){ return Condition;}
        public TextView getPrice(){ return Price;}
        public View getView(){return view;}
    }
    // END_INCLUDE(recyclerViewSampleViewHolder)

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public RecycleItem(String[] dataSet) {
        mDataSet = dataSet;
    }

    // BEGIN_INCLUDE(recyclerViewOnCreateViewHolder)
    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycle_item, viewGroup, false);

        return new ViewHolder(v);
    }
    // END_INCLUDE(recyclerViewOnCreateViewHolder)

    // BEGIN_INCLUDE(recyclerViewOnBindViewHolder)
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        // Get element from your dataset at this position and replace the contents of the view
        // with that element

        // read item from jsonobj
        JSONObject item;
        try {
            item = new JSONObject(mDataSet[position]);
            System.out.println(item);
            // set title
            viewHolder.getTitleView().setText(item.get("title").toString());
            // set image
            String imgUrl = item.get("img_url").toString();
            String ebay_deflat = "https://csci571.com/hw/hw8/images/ebayDefault.png";
            if (!imgUrl.equals(ebay_deflat)){
                Picasso.get().load(imgUrl)
                        .into(viewHolder.getImageView());
            }
            //set shipping
            String shippingcost = item.get("shippingCost").toString();
            if (shippingcost.equals("0"))
            {
                viewHolder.getShipping().setText("FREE shipping");
            }
            else
                viewHolder.getShipping().setText("Ships for $"+shippingcost);
            String toprate = item.get("top_rating").toString();
            if(toprate=="true")
                viewHolder.getTopRated().setText("Top Rated Listing");
            String cond=item.getString("conditon").toString();
            if(cond.equals(""))
                viewHolder.getCondition().setText("N/A");
            else
                viewHolder.getCondition().setText(cond);
            String  price = item.get("total_price").toString();
            viewHolder.getPrice().setText("$"+price);

            viewHolder.getView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("*********************************************");
                    System.out.println(mDataSet[position]);
                    Intent intent=new Intent(v.getContext(), ItemDetail.class);
                    intent.putExtra("data", mDataSet[position]);
                    v.getContext().startActivity(intent);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // END_INCLUDE(recyclerViewOnBindViewHolder)

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }
}
