package com.yunsu.common.service;

import com.yunsu.common.exception.BaseException;
import com.yunsu.common.exception.LocalGeneralException;
import com.yunsu.common.exception.NetworkNotAvailableException;
import com.yunsu.common.exception.ServerAuthException;
import com.yunsu.common.exception.ServerGeneralException;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;



public abstract class DataServiceImpl implements Runnable {

    public interface DataServiceDelegate {
        public void onRequestSucceeded(DataServiceImpl service, JSONObject data, boolean isCached);

        public void onRequestFailed(DataServiceImpl service, BaseException exception);
    }

    protected DataServiceDelegate delegate;

    public void start() {
        ServiceExecutor.getInstance().execute(this);
    }

    public void cancel() {
        ServiceExecutor.getInstance().cancel(this);
    }

    protected boolean isCached() {
        return false;
    }

    protected boolean forceLoad() {
        return true;
    }

    protected JSONObject cacheMethod() throws JSONException {
        return null;
    }

    protected abstract JSONObject method() throws ServerAuthException, ServerGeneralException, LocalGeneralException,
            NetworkNotAvailableException, Exception;

    public void setDelegate(DataServiceDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Please do not call this method directly. It is only used in thread pool.
     */
    @Override
    public void run() {

        JSONObject data;
        try {
            if (isCached()) {
                data = cacheMethod();
                if (data != null) {
                    delegate.onRequestSucceeded(this, data, true);
                }
            }
            if (forceLoad()) {
                data = method();
                if (data != null) {
                    delegate.onRequestSucceeded(this, data, false);
                }
            }

        } catch (ServerAuthException | ServerGeneralException | LocalGeneralException | NetworkNotAvailableException e) {
            Logger.getLogger(DataServiceImpl.class).error(e.getMessage());
            delegate.onRequestFailed(this, e);
        } catch (Exception e) {
            Logger.getLogger(DataServiceImpl.class).error(e.getMessage());
            delegate.onRequestFailed(this, new LocalGeneralException(e.getMessage()));
        }
    }

}
