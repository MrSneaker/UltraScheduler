package com.mrsneaker.ultrascheduler;

import static com.mrsneaker.ultrascheduler.utils.DateUtils.monthYearFromDate;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mrsneaker.ultrascheduler.databinding.FragmentCalendarBinding;
import com.mrsneaker.ultrascheduler.utils.DateUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
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

        return binding.getRoot();
    }

    private List<String> getTimeSlots() {
        List<String> res = new ArrayList<>();
        for(int i = 0; i < 24; ++i) {
            res.add(i + ":00");
        }
        return res;
    }
}