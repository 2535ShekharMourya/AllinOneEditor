package ja.burhanrashid52.photoeditor;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ImageFilterAdapter extends RecyclerView.Adapter<ImageFilterAdapter.ImageFilterMainHolder> {

    public List<FilterNew> colorList;
    public int currentItemPos;

    public ImageFilterAdapter(List<FilterNew> colorList) {
        this.colorList = colorList;

    }

    @NonNull
    @Override
    public ImageFilterMainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item_view,parent,false);
        if (viewType == 1){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_item_view_3,parent,false);
            return new ImageFilterGradient3Holder(view);
        }
        return new ImageFilterHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        if (colorList.get(position).getType().equals("gradient_3"))
            return 1;
        return 0;
    }

    @Override
    public void onBindViewHolder(@NonNull ImageFilterMainHolder holder1, int position) {
        final int i = position;
        Log.d("status","holder : ImageFilterHolder type :" +
                colorList.get(position).getType()+",position:"+position+",i:"+i);
        if (holder1 instanceof ImageFilterHolder){

            try {
                ImageFilterHolder holder = (ImageFilterHolder)holder1;
                if (colorList.get(i).getType()!=null && colorList.get(i).getType().equals("gradient")){
                    final int firstColor = Color.parseColor(colorList.get(i).getStartColor());
                    final int secondColor = Color.parseColor(colorList.get(i).getEndColor());

                    final MyGradientDrawable myGradBg = new MyGradientDrawable(firstColor, secondColor);
                    holder.view.setAlpha((colorList.get(i).getOpacity()>1)?(100/colorList.get(i).getOpacity()):0.5f);
                    holder.view.setBackground(myGradBg);
                }else if (colorList.get(i).getType()!=null && colorList.get(i).getType().equals("transparent")){
                    holder.view.setAlpha(0);
                }else{
                    final int firstColor = Color.parseColor(colorList.get(i).getStartColor());
                    holder.view.setBackgroundColor(firstColor);
                    holder.view.setAlpha((colorList.get(i).getOpacity()>1)?(100/colorList.get(i).getOpacity()):0.5f);
                }
            }catch (Exception e){
                Log.d("status","e : "+e.getMessage());
            }

        }
        else if (holder1 instanceof ImageFilterGradient3Holder){
            Log.d("status","holder1 : ImageFilterGradient3Holder");
            try {
                final int firstColor = Color.parseColor(colorList.get(i).getStartColor());
                ImageFilterGradient3Holder holder = (ImageFilterGradient3Holder)holder1;
                final int midColor = Color.parseColor(colorList.get(i).getMidColor());

                final MyGradientDrawable myGradBg = new MyGradientDrawable(firstColor, midColor);
                holder.view.setAlpha((colorList.get(i).getOpacity()>1)?(100/colorList.get(i).getOpacity()):0.5f);
                holder.view.setBackground(myGradBg);

                final int secondColor = Color.parseColor(colorList.get(i).getEndColor());
                final MyGradientDrawable myGradBg1 = new MyGradientDrawable(midColor, secondColor);
                holder.view1.setAlpha((colorList.get(i).getOpacity()>1)?(100/colorList.get(i).getOpacity()):0.5f);
                holder.view1.setBackground(myGradBg1);
            }catch (Exception e){
                Log.d("status","e : "+e.getMessage());
            }
        }
    }

    @Override
    public int getItemCount() {
        return colorList.size();
    }

    public class ImageFilterHolder extends ImageFilterMainHolder {
        View view;
        public ImageFilterHolder(@NonNull View itemView) {
            super(itemView);
            view=itemView.findViewById(R.id.view);
        }
    }
    public class ImageFilterGradient3Holder extends ImageFilterMainHolder {
        View view;
        View view1;
        public ImageFilterGradient3Holder(@NonNull View itemView) {
            super(itemView);
            view=itemView.findViewById(R.id.view);
            view1=itemView.findViewById(R.id.view1);
        }
    }

    public class ImageFilterMainHolder extends RecyclerView.ViewHolder {
        public ImageFilterMainHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

}
