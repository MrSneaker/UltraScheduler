package com.mrsneaker.ultrascheduler.ui.calendar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.mrsneaker.ultrascheduler.R;
import com.mrsneaker.ultrascheduler.databinding.ItemCalendarBinding;
import com.mrsneaker.ultrascheduler.utils.DateUtils;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.ViewHolder> {

    private final List<Calendar> data;
    private final FragmentManager fragmentManager;

    public CalendarAdapter(List<Calendar> data, FragmentManager fragmentManager) {
        this.data = data;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCalendarBinding binding = ItemCalendarBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Calendar item = data.get(position);
        holder.bind(item, fragmentManager);
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

        public void bind(Calendar date, FragmentManager fragmentManager) {
            int dateNum  = date.get(Calendar.DAY_OF_MONTH);
            binding.calendarItemTextView.setText(String.format("%d", dateNum));
            CardView dayCard = binding.calendarItemCardView;
            dayCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    DayFragment dayFragment = DayFragment.newInstance(date);
                    fragmentTransaction.replace(R.id.fragment_container_view, dayFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            });
        }
    }
}