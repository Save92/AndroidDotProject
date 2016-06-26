package com.sncf.itnovem.dotandroidapplication;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.sncf.itnovem.dotandroidapplication.Models.Commande;
import com.sncf.itnovem.dotandroidapplication.Models.Notification;
import com.sncf.itnovem.dotandroidapplication.Models.VoiceCommand;
import com.sncf.itnovem.dotandroidapplication.utils.CurrentUser;


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
    private TextView description;
    private Fragment fragment;

    public CommandListDetailFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_command_list_detail, container, false);
        title = (TextView) view.findViewById(R.id.title);
        title.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        description = (TextView) view.findViewById(R.id.description_content);
        toolbarTop = (Toolbar) super.getActivity().findViewById(R.id.app_bar);

        toolbarTop.setLogo(null);
        //toolbarTop.setLogo(android.R.color.transparent);
        toolbarTop.setNavigationIcon(R.drawable.ic_navigate_before_white_36dp);

        super.getActivity().setActionBar(toolbarTop);
        toolbarTop.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarTop.setNavigationIcon(null);
                ((ListCommandActivity) getActivity()).initToolbars();
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_out_right, R.anim.slide_out_right, R.anim.slide_out_right, R.anim.slide_out_right).remove(fragment).commit();
            }
        });
        setFragmentInfo();
        return view;
    }

    private void setFragmentInfo() {
            title.setText(currentCommand.getName());
            description.setText(currentCommand.getDescription());
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
