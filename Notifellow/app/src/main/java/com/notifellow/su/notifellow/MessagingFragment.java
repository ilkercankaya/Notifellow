package com.notifellow.su.notifellow;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class MessagingFragment extends Fragment {

    FloatingActionButton fab;
    private SharedPreferences shared;

    List<Task> userList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_messaging, container, false);

        fab = (FloatingActionButton) view.findViewById(R.id.fabNewMessage);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(getActivity(), "Send New Message!", Toast.LENGTH_LONG).show();

//                    GroupChannel.createChannelWithUserIds(userList,false,);
                    connectToSendBird(shared.getString("email", "null"), shared.getString("username", "null"));
                }
            });
        }

        shared = getContext().getSharedPreferences("shared", MODE_PRIVATE);
//        final String email = shared.getString("email", "null");//GET EMAIL FROM SHARED
//        final String username = shared.getString("username", "null");//GET EMAIL FROM SHARED
        return view;
    }


    /**
     * Attempts to connect a user to SendBird.
     * @param userId    The unique ID of the user.
     * @param userNickname  The user's nickname, which will be displayed in chats.
     */
    private void connectToSendBird(final String userId, final String userNickname) {


        SendBird.connect(userId, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {

                if (e != null) {
                    // Error!
                    Toast.makeText(getContext(), "" + e.getCode() + ": " + e.getMessage(), Toast.LENGTH_SHORT).show();

                    // Show login failure snackbar
//                    mConnectButton.setEnabled(true);
                    return;
                }

                // Update the user's nickname
                updateCurrentUserInfo(userNickname);

                Intent intent = new Intent(getContext(), ChatActivity.class);
                startActivity(intent);
//                finish();
            }
        });
    }
    /**
     * Updates the user's nickname.
     * @param userNickname  The new nickname of the user.
     */
    private void updateCurrentUserInfo(String userNickname) {
        SendBird.updateCurrentUserInfo(userNickname, null, new SendBird.UserInfoUpdateHandler() {
            @Override
            public void onUpdated(SendBirdException e) {
                if (e != null) {
                    // Error!
                    Toast.makeText( getContext(), "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

            }
        });
    }
}
