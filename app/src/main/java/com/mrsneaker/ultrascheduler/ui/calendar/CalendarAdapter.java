package com.mrsneaker.ultrascheduler.ui.calendar;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mrsneaker.ultrascheduler.databinding.ItemCalendarBinding;

import java.time.LocalDate;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private final List<LocalDate> data;

    public CalendarAdapter(List<LocalDate> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCalendarBinding binding = ItemCalendarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LocalDate item = data.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemCalendarBinding binding;

        public ViewHolder(ItemCalendarBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(LocalDate date) {
            int dateNum  = date.getDayOfMonth();
            binding.calendarItemTextView.setText(String.format("%d", dateNum));
        }
    }
}