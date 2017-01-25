package com.hsrt.euebt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;


public class TrainingAdapter extends RecyclerView.Adapter<TrainingAdapter.ViewHolder> {

    private final List<Training> trainings;
    private final Context context;
    private final OnObjectClickListener listener;

    public TrainingAdapter(Context context, List<Training> trainingList, OnObjectClickListener listener) {
        this.trainings = trainingList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public TrainingAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View workoutDetailView = inflater.inflate(R.layout.item_training, parent, false);
        TrainingAdapter.ViewHolder viewHolder = new TrainingAdapter.ViewHolder(workoutDetailView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final TrainingAdapter.ViewHolder viewHolder, int position) {
        Training training = trainings.get(position);
        //Training Name wird ausgelesen und gesetzt
        viewHolder.trainingNameTextView.setText(training.getName());
        //Training TimeStamp wird ausgelesen und gesetzt
        /* [Johannes]
         * Die Formatierung würde ich so ändern, dass man auch das Datum und dafür keine Sekunden sieht:
         * viewHolder.timeStamp.setText(new SimpleDateFormat("d.M.y h:m a").format(new Date(training.getTimestamp() * 1000)));
          */
        viewHolder.timeStamp.setText(new SimpleDateFormat("d.M.y - h:m a").format(new Date(training.getTimestamp() * 1000)));
        System.out.println("AMK" + new SimpleDateFormat("d.M.y - h:m a").format(new Date(training.getTimestamp() * 1000)));
        //viewHolder.timeStamp.setText((DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date(training.getTimestamp()*1000)))+"");
        System.out.println("===================TRAININGADAPTER: "+(DateFormat.getTimeInstance(DateFormat.MEDIUM).format(new Date(training.getTimestamp()*1000)))+"");
        System.out.println("===================Training.getTimestamp(): "+training.getTimestamp());
        viewHolder.bind(training, listener);
    }

    @Override
    public int getItemCount() {
        return trainings != null ? trainings.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView trainingNameTextView;
        public TextView timeStamp;

        public ViewHolder(View itemView) {
            super(itemView);
            trainingNameTextView = (TextView) itemView.findViewById(R.id.training_name);
            timeStamp = (TextView) itemView.findViewById(R.id.timeStamp);

        }

        public void bind(final Training training, final OnObjectClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onObjectClick(training);
                }
            });
        }


    }
}