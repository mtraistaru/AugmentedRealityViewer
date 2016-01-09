package com.ancestor.AugmentedRealityViewer.activity;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ancestor.AugmentedRealityViewer.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class Model3DSelectionActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AssetManager assetManager = getAssets();
        Vector<ModelListItem> modelVector = new Vector<>();
        try {
            String[] models = assetManager.list("models");
            List<String> modelFileList = Arrays.asList(models);
            for (String model : models) {
                if (model.endsWith(".obj")) {
                    ModelListItem modelListItem = new ModelListItem();
                    String trimmedFileName = model.substring(0, model.lastIndexOf(".obj"));
                    modelListItem.text = trimmedFileName;
                    modelVector.add(modelListItem);
                    if (modelFileList.contains(trimmedFileName + ".jpg")) {
                        InputStream is = assetManager.open("models/" + trimmedFileName + ".jpg");
                        modelListItem.icon = (BitmapFactory.decodeStream(is));
                    } else if (modelFileList.contains(trimmedFileName + ".png")) {
                        InputStream is = assetManager.open("models/" + trimmedFileName + ".png");
                        modelListItem.icon = (BitmapFactory.decodeStream(is));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        setListAdapter(new ModelSelectionAdapter(modelVector));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        ModelListItem ModelListItem = (ModelListItem) this.getListAdapter().getItem(position);
        String str = ModelListItem.text;
        Intent intent = new Intent(Model3DSelectionActivity.this, Model3DViewerActivity.class);
        intent.putExtra("name", str + ".obj");
        intent.putExtra("type", Model3DViewerActivity.TYPE_INTERNAL);
        intent.setAction(Intent.ACTION_VIEW);
        startActivity(intent);
    }

    private class ModelSelectionAdapter extends BaseAdapter {

        private Vector<ModelListItem> modelListItems;

        public ModelSelectionAdapter(Vector<ModelListItem> ModelListItems) {
            this.modelListItems = ModelListItems;
        }

        @Override
        public int getCount() {
            return modelListItems.size();
        }

        @Override
        public Object getItem(int position) {
            return modelListItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public int getItemViewType(int position) {
            return modelListItems.get(position).type;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ModelListItem ModelListItem = modelListItems.get(position);
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.item_model_selection, null);
            }
            if (ModelListItem != null) {
                Object iconImage = ModelListItem.icon;
                ImageView icon = (ImageView) v.findViewById(R.id.model_select_icon);
                if (icon != null) {
                    if (iconImage instanceof Integer) {
                        icon.setImageResource((Integer) iconImage);
                    } else if (iconImage instanceof Bitmap) {
                        icon.setImageBitmap((Bitmap) iconImage);
                    }
                }
                TextView text = (TextView) v.findViewById(R.id.model_select_text);
                if (text != null)
                    text.setText(ModelListItem.text);
            }
            return v;
        }
    }

    private class ModelListItem {
        public static final int LIST_ITEM_TYPE = 0;
        public int type = LIST_ITEM_TYPE;
        public Object icon = R.drawable.missing_model_image;
        public String text;
    }
}
