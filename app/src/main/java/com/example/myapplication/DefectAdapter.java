package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

class ViewHolder {
    ImageView mImageHolder;
}

public class DefectAdapter extends BaseAdapter {

    private final Context mContext;
    private Defect[] defects;
    private View defectView;
    private ViewHolder mHolder;

    public DefectAdapter(Context context, Defect[] defects) {
        this.mContext = context;
        this.defects = defects;
    }

    @Override
    public int getCount() {
        return defects.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        mHolder = new ViewHolder();

        // Relate to the xml
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.defect_linearlayout, null);
            defectView = convertView;
        }

        //Set the card image on board - according to if it's clicked or not
        final ImageView imageView = (ImageView) convertView.findViewById(R.id.imageview_cover_art);
        Defect currDef = defects[position];
        imageView.setImageResource(currDef.GetImageResource());


        return convertView;
    }

}
