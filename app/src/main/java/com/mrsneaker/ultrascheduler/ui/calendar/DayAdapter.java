package com.mrsneaker.ultrascheduler.ui.calendar;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mrsneaker.ultrascheduler.databinding.ItemHourBinding;

import java.util.List;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {
    private final List<String> hourOfDay;
    private final FragmentManager fragmentManager;

    public DayAdapter(List<String> hourOfDay, FragmentManager fragmentManager) {
        this.hourOfDay = hourOfDay;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemHourBinding binding = ItemHourBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String hour = hourOfDay.get(position);
        holder.bind(hour, fragmentManager);
    }

    @Override
    public int getItemCount() {
        return hourOfDay.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemHourBinding binding;

        public ViewHolder(ItemHourBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(String hour, FragmentManager fragmentManager) {
            binding.hourTextView.setText(hour);
        }
    }


}
