package pt.IPG.messenger.recyclerview;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import pt.IPG.messenger.R;

public class ProductAdapter extends SelectableAdapter<ProductAdapter.ViewHolder> {

    private List<Product> mArrayList;
    private List<View> itemViews = new ArrayList<>();
    private Context mContext;
    private ProductAdapter.ViewHolder.ClickListener clickListener;
    private static int SELECTION_COLOR = Color.CYAN;



    public ProductAdapter (Context context, List<Product> arrayList, ProductAdapter.ViewHolder.ClickListener clickListener) {
        this.mArrayList = arrayList;
        this.mContext = context;
        this.clickListener = clickListener;

    }

    public List<Product> getProducts(){
        return mArrayList;
    }

    public List<Product> getSelected(){
        List<Product> mSelected = new ArrayList<>();
        List<Integer> values = this.getSelectedItems();
        for(int i = 0; i< mArrayList.size(); i++) {
            if(values.contains(i)){
                mSelected.add(mArrayList.get(i));
            }
        }
        return mSelected;
    }

    @Override
    public ProductAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                        int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.list_item_product, null);
        itemViews.add(itemLayoutView);
        ProductAdapter.ViewHolder viewHolder = new ProductAdapter.ViewHolder(itemLayoutView, clickListener);

        return viewHolder;
    }

    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ViewHolder viewHolder, int position) {
        viewHolder.tvProduct.setText(mArrayList.get(position).getName());
        viewHolder.ivProductPhoto.setImageResource(mArrayList.get(position).getImage());
        viewHolder.tvPrice.setText(String.valueOf(mArrayList.get(position).getPrice()));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener, View.OnLongClickListener {

        public TextView tvProduct;
        public TextView tvPrice;
        public ImageView ivProductPhoto;
        private ProductAdapter.ViewHolder.ClickListener listener;
        protected View view;

        public ViewHolder(View itemLayoutView, ProductAdapter.ViewHolder.ClickListener listener) {
            super(itemLayoutView);
            this.view = itemLayoutView;
            this.listener = listener;

            tvProduct = (TextView) itemLayoutView.findViewById(R.id.tv_product_name);
            ivProductPhoto = (ImageView) itemLayoutView.findViewById(R.id.iv_product_photo);
            tvPrice = (TextView) itemLayoutView.findViewById(R.id.tv_product_price);
            itemLayoutView.setOnClickListener(this);

            itemLayoutView.setOnLongClickListener (this);
        }

        public View getView(){
            return view;
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                int val = getAdapterPosition ();
                listener.onItemClicked(val);
            }
        }



        @Override
        public boolean onLongClick (View view) {

            if (listener != null) {
                return listener.onItemLongClicked(getAdapterPosition ());
            }
            return false;
        }

        public interface ClickListener {
            void onItemClicked(int position);
            boolean onItemLongClicked(int position);
            boolean onCreateOptionsMenu(Menu menu);
        }
    }
}
