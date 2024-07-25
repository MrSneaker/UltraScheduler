package com.mrsneaker.ultrascheduler.ui.event;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mrsneaker.ultrascheduler.databinding.FragmentEventCardBinding;
import com.mrsneaker.ultrascheduler.injection.ViewModelFactory;
import com.mrsneaker.ultrascheduler.model.event.GenericEvent;
import com.mrsneaker.ultrascheduler.utils.DateUtils;
import com.mrsneaker.ultrascheduler.viewmodel.EventViewModel;

import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventCardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventCardFragment extends Fragment {

    private static final String ARG_CARD_COLOR = "card_color";
    private static final String EVENT_ID = "id";
    private FragmentEventCardBinding binding;
    private ColorStateList cardColor;
    private long eventId;
    private EventViewModel evm;

    public EventCardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment EventCardFragment.
     */
    public static EventCardFragment newInstance(ColorStateList cardColor, long eventId) {
        EventCardFragment fragment = new EventCardFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CARD_COLOR, cardColor);
        args.putLong(EVENT_ID, eventId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            cardColor = getArguments().getParcelable(ARG_CARD_COLOR);
            eventId = getArguments().getLong(EVENT_ID);
        }
        evm = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(EventViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventCardBinding.inflate(inflater, container, false);
        CardView cardView = binding.eventCardView;
        if (cardColor != null) {
            cardView.setBackgroundTintList(cardColor);
        }

        LiveData<GenericEvent> eventLiveData = evm.getDetailedEventById(eventId);

        initBackButton();
        initMoreButton();
        initModifyBtn();
        initObserveEventLiveData(eventLiveData);

        return binding.getRoot();
    }

    private void initBackButton() {
        ImageButton backBtn = binding.backArrowBtn;
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getParentFragmentManager().popBackStack();
            }
        });
    }

    private void initMoreButton() {
        ImageButton moreBtn = binding.moreOptBtn;
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: display options
            }
        });
    }

    private void initModifyBtn() {
        ImageButton modifyBtn = binding.modifyBtn;
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: Open the event form filled with current info.
            }
        });
    }

    private void initObserveEventLiveData(LiveData<GenericEvent> event) {
        event.observe(getViewLifecycleOwner(), new Observer<GenericEvent>() {
            @Override
            public void onChanged(GenericEvent event) {
                TextView desc = binding.eventDescription;
                TextView sub = binding.eventSubject;
                TextView eventDate = binding.eventDate;

                desc.setText(event.getDescription());
                sub.setText(event.getSubject());
                eventDate.setText(getEventDateString(event));
            }
        });
    }


    private String getEventDateString(GenericEvent event) {
        Calendar startDate = event.getStartTime();
        Calendar endDate = event.getEndTime();

        Integer startDayOfMonth = startDate.get(Calendar.DAY_OF_MONTH);
        String startMonth = DateUtils.getMonthName(startDate);
        Integer startYear = startDate.get(Calendar.YEAR);
        Integer startHour = startDate.get(Calendar.HOUR_OF_DAY);
        Integer startMin = startDate.get(Calendar.MINUTE);

        Integer endDayOfMonth = endDate.get(Calendar.DAY_OF_MONTH);
        String endMonth = DateUtils.getMonthName(endDate);
        Integer endYear = endDate.get(Calendar.YEAR);
        Integer endHour = endDate.get(Calendar.HOUR_OF_DAY);
        Integer endMin = endDate.get(Calendar.MINUTE);

        String res = String.format("%d " + startMonth + " %d %d:%d - %d " + endMonth + " %d %d:%d",
                startDayOfMonth,
                startYear,
                startHour,
                startMin,
                endDayOfMonth,
                endYear,
                endHour,
                endMin);
        return res;
    }

}