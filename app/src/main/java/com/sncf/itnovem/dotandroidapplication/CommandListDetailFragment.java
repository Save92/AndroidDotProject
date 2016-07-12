package com.sncf.itnovem.dotandroidapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toolbar;

import com.sncf.itnovem.dotandroidapplication.Adapters.ListCommandDetailRecyclerAdapter;
import com.sncf.itnovem.dotandroidapplication.Adapters.SimpleDividerItemDecoration;
import com.sncf.itnovem.dotandroidapplication.Models.VoiceCommand;

import java.util.ArrayList;

/**
 * Created by Journaud Nicolas on 20/04/16.
 */
/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CommandListDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class CommandListDetailFragment extends Fragment {

    private Toolbar toolbarTop;
    private VoiceCommand currentCommand;
    private TextView title;
    private RecyclerView descriptions;
    private TextView description;
    private Fragment fragment;
    private ListCommandDetailRecyclerAdapter adapter;

    public CommandListDetailFragment() {
    }

    public static CommandListDetailFragment newInstance(VoiceCommand c)
    {
        CommandListDetailFragment f = new CommandListDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("command", c);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle inState)
    {
        super.onCreate(inState);
        fragment = this;
        if(getArguments() != null) {
            currentCommand = getArguments().getParcelable("command");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void initToolbar() {

        toolbarTop = (Toolbar) getActivity().findViewById(R.id.app_bar);
        TextView titleBar = (TextView) toolbarTop.findViewById(R.id.app_bar_title);
        titleBar.setText(currentCommand.getName());
        titleBar.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        toolbarTop.setLogo(null);
        titleBar.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        super.getActivity().setActionBar(toolbarTop);
        toolbarTop.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);
        toolbarTop.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarTop.setNavigationIcon(null);
                ((ListCommandActivity) getActivity()).initToolbars();
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_out_right, R.anim.slide_out_right, R.anim.slide_out_right, R.anim.slide_out_right).remove(fragment).commit();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        initToolbar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_command_list_detail, container, false);
        title = (TextView) view.findViewById(R.id.title);
        title.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        descriptions = (RecyclerView) view.findViewById(R.id.commandesDetailRecycleView);
        descriptions.setLayoutManager(new LinearLayoutManager(this.getContext()));
        descriptions.addItemDecoration(new SimpleDividerItemDecoration(this.getContext()));
        setFragmentInfo();
        return view;
    }

    private void setFragmentInfo() {
        title.setText(currentCommand.getName());
        ArrayList<String> listDescription;
        listDescription = currentCommand.getDescription();
        adapter = new ListCommandDetailRecyclerAdapter(this.getContext(), listDescription);
        descriptions.setAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
