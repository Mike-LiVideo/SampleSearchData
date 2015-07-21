package com.themtgdeckgenius.sampleapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.themtgdeckgenius.sampleapplication.R;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

public class AudioAdapter
        extends BaseAdapter{

    private final ArrayList<File> mFiles = new ArrayList<>();
    private final Context mContext;

    public AudioAdapter(final String aDir,
                        final FilenameFilter aFilter,
                        final Context aContext
    ){
        mContext = aContext.getApplicationContext();
        File dir = new File(aDir);
        if(dir.exists()){
            final String[] files = dir.list(aFilter);
            for(String file : files){
                mFiles.add(new File(dir, file));
            }
        }
        else{
            //noinspection ResultOfMethodCallIgnored
            dir.mkdirs();
        }
    }

    @Override
    public int getCount(){
        return mFiles.size();
    }

    @Override
    public File getItem(final int position){
        return mFiles.get(position);
    }

    @Override
    public long getItemId(final int position){
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent){
        final File file = mFiles.get(position);
        if(convertView == null){
            final LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.audio_list_item, parent, false);
            convertView.setTag(R.id.image, convertView.findViewById(R.id.image));
        }

        //noinspection ConstantConditions
        View btnRemove = convertView.findViewById(R.id.btnDeleteItem);
        btnRemove.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(final View v){
                Integer position = (Integer) v.getTag(R.id.tvCiteNo);
                File file = getItem(position);
                //noinspection ResultOfMethodCallIgnored
                removeFile(file);
                notifyDataSetChanged();
            }
        });
        btnRemove.setTag(R.id.tvCiteNo, position);

        ImageView imageView = (ImageView) convertView.getTag(R.id.image);
        imageView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(final View v){
                final Uri uri = Uri.fromFile(file);
                Intent i = getActionIntent();
                i.setDataAndType(uri, "audio/mp4");
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });

        showMedia(file, imageView);
        return convertView;
    }

    protected Intent getActionIntent(){
        return new Intent(Intent.ACTION_VIEW);
    }

    protected void removeFile(final File aFile){
        aFile.delete();
        mFiles.remove(aFile);
    }

    protected void showMedia(final File aFile, final ImageView aImageView){
        aImageView.setImageResource(R.drawable.ic_audio_preview);
        aImageView.setBackgroundColor(getResources().getColor(android.R.color.white));
        aImageView.setScaleType(ImageView.ScaleType.CENTER);
    }

    public void add(final File aFile){
        if(!mFiles.contains(aFile)){
            mFiles.add(aFile);
            notifyDataSetChanged();
        }
    }

}
