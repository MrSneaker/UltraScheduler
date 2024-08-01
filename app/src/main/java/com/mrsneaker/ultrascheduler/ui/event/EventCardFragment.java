package com.mrsneaker.ultrascheduler.ui.event;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.mrsneaker.ultrascheduler.R;
import com.mrsneaker.ultrascheduler.databinding.FragmentEventCardBinding;
import com.mrsneaker.ultrascheduler.injection.ViewModelFactory;
import com.mrsneaker.ultrascheduler.model.event.DetailedEvent;
import com.mrsneaker.ultrascheduler.model.event.GenericEvent;
import com.mrsneaker.ultrascheduler.model.event.TaskEvent;
import com.mrsneaker.ultrascheduler.utils.DateUtils;
import com.mrsneaker.ultrascheduler.viewmodel.EventViewModel;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

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
    private GenericEvent currentEvent;

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
        currentEvent = new GenericEvent();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEventCardBinding.inflate(inflater, container, false);
        CardView cardView = binding.eventCardView;
        if (cardColor != null) {
            cardView.setBackgroundTintList(cardColor);
        }

        LiveData<GenericEvent> eventLiveData = evm.getEventById(eventId);

        initBackButton();
        initMoreButton();
        initModifyBtn();
        initObserveEventLiveData(eventLiveData);

        return binding.getRoot();
    }

    private void initNotificationDisplay() {
        TextView notificationsTextView = binding.notificationTextView;

        StringBuilder finalDisplay = new StringBuilder();

        for(Map.Entry<Long, Calendar> entry : currentEvent.getNotificationDate().entrySet()) {
            if(entry.getValue() == currentEvent.getStartTime()) {
                continue;
            }
            finalDisplay.append(getFormattedTimeDifference(currentEvent.getStartTime(), entry.getValue()));
            finalDisplay.append(" before.\n");
        }

        notificationsTextView.setText(finalDisplay);
    }

    private String getFormattedTimeDifference(Calendar start, Calendar end) {
        long diffInMillis = end.getTimeInMillis() - start.getTimeInMillis();

        long diffMinutes = diffInMillis / (60 * 1000) % 60;
        long diffHours = diffInMillis / (60 * 60 * 1000) % 24;
        long diffDays = diffInMillis / (24 * 60 * 60 * 1000);

        return String.format(Locale.getDefault(), "%d days, %d hours, %d minutes", diffDays, diffHours, diffMinutes);
    }

    private void initBackButton() {
        ImageButton backBtn = binding.backArrowBtn;
        backBtn.setOnClickListener(view -> getParentFragmentManager().popBackStack());
    }

    private void initMoreButton() {
        ImageButton moreBtn = binding.moreOptBtn;
        moreBtn.setOnClickListener(this::showPopupMenu);
    }

    private void initModifyBtn() {
        ImageButton modifyBtn = binding.modifyBtn;
        modifyBtn.setOnClickListener(view -> {
            FragmentManager fragmentManager = getParentFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            EventFormFragment eventFormFragment =  EventFormFragment.newInstance(eventId);
            fragmentTransaction.replace(R.id.fragment_container_view, eventFormFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
    }

    private void initObserveEventLiveData(LiveData<GenericEvent> event) {
        event.observe(getViewLifecycleOwner(), event1 -> {
            if(event1 != null)  {
                currentEvent = event1;
                TextView desc = binding.eventDescription;
                TextView sub = binding.eventSubject;
                TextView eventDate = binding.eventDate;

                desc.setText(event1.getDescription());
                sub.setText(event1.getSubject());
                eventDate.setText(getEventDateString(event1));
                initNotificationDisplay();
            }
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(requireContext(), view);
        popupMenu.getMenuInflater().inflate(R.menu.more_card_options, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int id = menuItem.getItemId();
            if(id == R.id.deleteCardOpt) {
                evm.deleteEvent(currentEvent, requireContext());
                getParentFragmentManager().popBackStack();
                return true;
            } else if(id == R.id.duplicateCardOpt) {
                //TODO: duplicate action
                return true;
            } else if (id == R.id.shareCardOpt) {
                //TODO: share action
                return true;
            }
            return false;
        });
        popupMenu.show();
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

        return String.format("%02d " + startMonth + " %04d %02d:%02d - %02d " + endMonth + " %04d %02d:%02d",
                startDayOfMonth,
                startYear,
                startHour,
                startMin,
                endDayOfMonth,
                endYear,
                endHour,
                endMin);
    }

}