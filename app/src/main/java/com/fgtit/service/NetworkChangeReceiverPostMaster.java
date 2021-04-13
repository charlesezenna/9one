package com.fgtit.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.fgtit.data.GlobalData;
import com.fgtit.data.UserItem;
import com.fgtit.network.ApiHelper;
import com.fgtit.network.Response;
import com.fgtit.utils.Threading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class NetworkChangeReceiverPostMaster extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        int status = PostMasterNetworkUtil.getConnectivityStatusString(context);
        Log.e("Sulod network reciever", "Sulod sa network reciever");
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(intent.getAction())) {
            if (status == PostMasterNetworkUtil.NETWORK_STATUS_NOT_CONNECTED) {
                //new ForceExitPause(context).execute();
            } else {
                //new ResumeForceExitPause(context).execute();
                //private void loadUsersAsync() {
                    Threading.async(new Callable<List<UserItem>>() {
                        @Override
                        public List<UserItem> call() throws Exception {
                            GlobalData.getInstance().LoadUsersList();
                            if (GlobalData.getInstance().userList == null)
                                return new ArrayList<UserItem>();
                            else
                                return GlobalData.getInstance().userList;
                        }
                    }, new Consumer<List<UserItem>>() {
                        @Override
                        public void accept(List<UserItem> userItems) throws Exception {
                            syncUsers();
                        }
                    });
                //}
            }
        }
    }

    Map<UserItem, UserItem> userMap = new HashMap<UserItem, UserItem>();

    private void syncUsers() {
        List<UserItem> userItems = GlobalData.getInstance().userList;
        int i = 0;
        if (userItems != null && userItems.size() != 0) {
            for (final UserItem userItem : userItems) {
                userMap.put(userItem, userItems.get(i));
                i++;
                if (userItem.isSyncWithBackend == false)
                    Threading.async(ApiHelper.getEnrollObservable(userItem).map(new Function<Response, Response>() {
                        @Override
                        public Response apply(Response response) throws Exception {
                            userMap.get(userItem).isSyncWithBackend = true;
                            GlobalData.getInstance().SaveUsersList();
                            Log.d("", "");
                            return response;
                        }
                    }), new Consumer<Response>() {
                        @Override
                        public void accept(Response response) throws Exception {
                            Log.d("", "");
                        }
                    });
            }
        }
    }
}
