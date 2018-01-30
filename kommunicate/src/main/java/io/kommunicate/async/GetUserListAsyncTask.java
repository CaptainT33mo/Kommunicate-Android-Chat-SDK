package io.kommunicate.async;

import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.List;

import io.kommunicate.KmUserResponse;
import io.kommunicate.callbacks.KMGetContactsHandler;
import io.kommunicate.services.KmUserService;

/**
 * Created by ashish on 30/01/18.
 */

public class GetUserListAsyncTask extends AsyncTask<Void, Void, KmUserResponse> {
    private WeakReference<Context> context;
    private List<String> userRoleList;
    private int startIndex;
    private int pageSize;
    private KMGetContactsHandler handler;

    public GetUserListAsyncTask(Context context, List<String> userRoleList, int startIndex, int pageSize, KMGetContactsHandler handler) {
        this.context = new WeakReference<Context>(context);
        this.userRoleList = userRoleList;
        this.startIndex = startIndex;
        this.pageSize = pageSize;
        this.handler = handler;
    }

    @Override
    protected KmUserResponse doInBackground(Void... voids) {

        KmUserResponse response;
        try {
            response = new KmUserService(context.get()).getUserList(userRoleList, startIndex, pageSize);
        } catch (Exception e) {
            e.printStackTrace();
            response = new KmUserResponse();
            response.setException(e);
        }
        return response;
    }

    @Override
    protected void onPostExecute(KmUserResponse kmUserResponse) {
        super.onPostExecute(kmUserResponse);
        if (handler == null) {
            return;
        }

        if (kmUserResponse != null) {
            if (kmUserResponse.isSuccess() && kmUserResponse.getContactList() != null) {
                handler.onSuccess(kmUserResponse.getContactList());
            } else {
                if (kmUserResponse.getErrorList() != null) {
                    handler.onFailure(kmUserResponse.getErrorList(), null);
                } else if (kmUserResponse.getException() != null) {
                    handler.onFailure(null, kmUserResponse.getException());
                }

            }
        } else {
            handler.onFailure(null, null);
        }
    }
}