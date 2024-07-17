package com.mrsneaker.ultrascheduler.ui.calendar;

import static com.mrsneaker.ultrascheduler.utils.DateUtils.monthYearFromDate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mrsneaker.ultrascheduler.databinding.FragmentCalendarBinding;
import com.mrsneaker.ultrascheduler.utils.DateUtils;

import java.time.LocalDate;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CalendarFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CalendarFragment extends Fragment {

    private FragmentCalendarBinding binding;

    public CalendarFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CalendarFragment.
     */
    public static CalendarFragment newInstance() {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        LocalDate currentDate = LocalDate.now();
        DateUtils.setSelectedDate(currentDate);

        binding.monthYearTV.setText(monthYearFromDate(currentDate));

        RecyclerView recyclerView = binding.calendarRecyclerView;
        recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 7));
        recyclerView.setAdapter(new CalendarAdapter(DateUtils.daysInWeekArray(currentDate), getParentFragmentManager()));

        initNextWeekBtn();
        initLastWeekBtn();

        return binding.getRoot();
    }

    private void initNextWeekBtn() {
        Button nextWeek = binding.nextWeekBtn;
        nextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView recyclerView = binding.calendarRecyclerView;
                LocalDate currentDate = DateUtils.getNextWeek();
                recyclerView.setAdapter(new CalendarAdapter(DateUtils.daysInWeekArray(currentDate), getParentFragmentManager()));
                updateMonthYearTitle();
            }
        });
    }

    private void initLastWeekBtn() {
        Button lastWeek = binding.previousWeekBtn;
        lastWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecyclerView recyclerView = binding.calendarRecyclerView;
                LocalDate currentDate = DateUtils.getLastWeek();
                recyclerView.setAdapter(new CalendarAdapter(DateUtils.daysInWeekArray(currentDate), getParentFragmentManager()));
                updateMonthYearTitle();
            }
        });
    }

    private void updateMonthYearTitle() {
        TextView monthYearTitle = binding.monthYearTV;
        String prevMonthYString = (String) monthYearTitle.getText();
        String newMonthYString = monthYearFromDate(DateUtils.getSelectedDate());
        if(!prevMonthYString.equals(newMonthYString)) {
            monthYearTitle.setText(newMonthYString);
        }
    }
}