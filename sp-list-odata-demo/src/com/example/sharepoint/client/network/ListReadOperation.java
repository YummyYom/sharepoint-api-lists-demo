package com.example.sharepoint.client.network;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import android.content.Context;

import com.example.sharepoint.client.Constants;
import com.example.sharepoint.client.logger.Logger;
import com.example.sharepoint.client.network.auth.AuthType;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataEntityRequest;
import com.msopentech.odatajclient.engine.communication.request.retrieve.ODataRetrieveRequestFactory;
import com.msopentech.odatajclient.engine.data.ODataEntity;
import com.msopentech.odatajclient.engine.data.ODataProperty;
import com.msopentech.odatajclient.engine.uri.ODataURIBuilder;

public class ListReadOperation extends HttpOperation {

    private String guid;
    
    /**
     * Number of fields in returned entity.
     */
    private int result = 0;
    
    public ListReadOperation(OnOperaionExecutionListener listener, AuthType authType, Context context, String guid) {
        super(listener, authType, context);        
        this.guid = guid;
    }
    
    @Override
    protected List<Header> getRequestHeaders() {
        List<Header> headers = super.getRequestHeaders();
        try {
            headers.add(new BasicHeader("Accept", "application/json; odata=verbose"));

            if (authType == AuthType.Office365) {
                headers.add(new BasicHeader("Cookie", Constants.COOKIE_RT_FA + "; " + Constants.COOKIE_FED_AUTH));
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".getRequestHeaders(): Error.");
        }
        return headers;
    }

    @Override
    protected String getServerUrl() {
        return Constants.SP_BASE_URL;
    }
    
    @Override
    public void execute() {
        try {
            ODataURIBuilder builder = new ODataURIBuilder(getServerUrl()).appendEntityTypeSegment("Web/Lists").appendKeySegment(guid);
            ODataEntityRequest req = ODataRetrieveRequestFactory.getEntityRequest(builder.build());
            for (Header h : getRequestHeaders()) {
                req.addCustomHeader(h.getName(), h.getValue());
            }

            ODataEntity res = req.execute().getBody();

            boolean isSucceeded = handleServerResponse(res);

            if (mListener != null) {
                mListener.onExecutionComplete(this, isSucceeded);
            }
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".execute(): Error.");
        }
    }

    private boolean handleServerResponse(ODataEntity res) {
        try {
            result = 0;
            for (ODataProperty p: res.getProperties()) {
                result += p.getComplexValue().size();
            }
            
            return true;
        } catch (Exception e) {
            Logger.logApplicationException(e, getClass().getSimpleName() + ".handleServerResponse(): Error.");
        }
        
        return false;
    }

    public int getResult() {
        return result;
    }
}