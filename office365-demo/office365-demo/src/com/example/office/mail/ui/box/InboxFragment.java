package com.example.office.mail.ui.box;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.widget.Toast;

import com.example.office.Configuration;
import com.example.office.Constants;
import com.example.office.Constants.UI;
import com.example.office.R;
import com.example.office.logger.Logger;
import com.example.office.mail.actions.MoveToArchiveMailAction;
import com.example.office.mail.actions.MoveToListsMailAction;
import com.example.office.mail.actions.RemindLaterMailAction;
import com.example.office.mail.actions.RemoveMailAction;
import com.example.office.mail.data.BoxedMailItem;
import com.example.office.mail.data.MailConfig;
import com.example.office.mail.data.NetworkState;
import com.example.office.mail.storage.MailConfigPreferences;
import com.example.office.ui.animate.actions.AnimationAction;
import com.example.office.ui.animate.actions.AnimationAction.Direction;
import com.example.office.utils.NetworkUtils;
import com.microsoft.opentech.office.core.action.async.IOperationCallback;
import com.microsoft.opentech.office.core.auth.method.AbstractBasicAuthenticator;
import com.microsoft.opentech.office.mail.data.odata.EmailMessage;
import com.microsoft.opentech.office.mail.network.MailsRequestOperation;

/**
 * 'Inbox' fragment containing logic related to managing inbox emails.
 */
public class InboxFragment extends BoxFragment implements IOperationCallback<List<EmailMessage>> {

    /**
     * Handler to process actions on UI thread when async task is finished.
     */
    private Handler mHandler;

    /**
     * Default constructor.
     */
    public InboxFragment() {
        super();
        mIsResumeEventHandled = false;
        mHandler = new Handler();
    }

    @Override
    protected UI.Screen getBox() {
        return UI.Screen.MAILBOX;
    }

    @Override
    protected List<AnimationAction> getAnimationActions() {
        try {
            List<AnimationAction> list = new ArrayList<AnimationAction>();
            list.add(new MoveToArchiveMailAction(mListener, Direction.RIGHT, Constants.SCREEN_PART_FOR_DEFAULT_ACTION));
            list.add(new RemoveMailAction(mListener, Direction.RIGHT, 1 - Constants.SCREEN_PART_FOR_DEFAULT_ACTION));
            list.add(new RemindLaterMailAction(mListener, Direction.LEFT, Constants.SCREEN_PART_FOR_DEFAULT_ACTION, this));
            list.add(new MoveToListsMailAction(mListener, Direction.LEFT, 1 - Constants.SCREEN_PART_FOR_DEFAULT_ACTION, this));
            return list;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + "getAnimationActions(): Error.");
        }
        return null;
    }

    @Override
    protected void initList() {
        try {
            List<BoxedMailItem> mails = getListData();
            boolean hasData = false;
            if (hasData = (mails != null && !mails.isEmpty())) {
                updateList(mails);
            }

            // Update list from the web.
            NetworkState nState = NetworkUtils.getNetworkState(getActivity());
            if (nState.getWifiConnectedState() || nState.getDataState() == NetworkUtils.NETWORK_UTILS_CONNECTION_STATE_CONNECTED) {
                showWorkInProgress(true, !hasData);
                com.microsoft.opentech.office.core.auth.Configuration.setServerBaseUrl(getInboxEndpoint());
                com.microsoft.opentech.office.core.auth.Configuration.setAuthenticator(new AbstractBasicAuthenticator() {
                    protected String getUsername() {
                        return Constants.USERNAME;
                    }

                    protected String getPassword() {
                        return Constants.PASSWORD;
                    }
                });
                new MailsRequestOperation(this, getActivity()).executeAsync();
            } else {
                Toast.makeText(getActivity(), R.string.data_connection_no_data_connection, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + "initList(): Error.");
        }
    }

    @Override
    public void onDone(List<EmailMessage> result) {
        MailConfig newConfig = new MailConfig(System.currentTimeMillis());
        final List<BoxedMailItem> boxedMails = new ArrayList<BoxedMailItem>();
        for (EmailMessage mail : result) {
            boxedMails.add(new BoxedMailItem(mail, UI.Screen.MAILBOX));
        }

        newConfig.setMails(boxedMails);
        MailConfigPreferences.updateConfiguration(newConfig);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                showWorkInProgress(false, false);
                updateList(boxedMails);
            }
        });
    }

    @Override
    public void onError(Throwable error) {
        Logger.logApplicationException(new Exception(error), getClass().getSimpleName() + ".onExecutionComplete(): Error.");
    }

    /**
     * Returns TEST or RELEASE version of end point to retrieve list of emails in the inbox depending on {@link Configuration#DEBUG}
     * constant value.
     * 
     * @return URL to retrieve list of emails in the inbox.
     */
    private String getInboxEndpoint() {
        if (Configuration.DEBUG) {
            return Constants.MAIL_MESSAGES_TEST;
        } else {
            return Constants.MAIL_MESSAGES;
        }
    }
}
