package cn.ddh.simulatedclick.javaimpl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.ddh.simulatedclick.R;
import cn.ddh.simulatedclick.event.EventBase;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {

    private Context context;
    private List<EventBase> eventBaseList;

    public EventsAdapter(Context context, List<EventBase> eventBaseList){
        this.context = context;
        this.eventBaseList = eventBaseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_event,null);
        return new ViewHolder(itemView);
    }

//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EventBase eventBase = eventBaseList.get(position);
        holder.textView.setText(eventBase.getName());
        if(eventBase.getTasking()){
            holder.textView.setTextColor(context.getResources().getColor(R.color.teal_200));
        }else{
            holder.textView.setTextColor(context.getResources().getColor(R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return eventBaseList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_content);
        }
    }
}
