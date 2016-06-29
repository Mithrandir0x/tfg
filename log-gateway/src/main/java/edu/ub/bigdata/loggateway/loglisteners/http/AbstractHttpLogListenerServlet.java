package edu.ub.bigdata.loggateway.loglisteners.http;

import edu.ub.bigdata.loggateway.LogListener;
import edu.ub.bigdata.model.RawLog;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public abstract class AbstractHttpLogListenerServlet extends HttpServlet implements LogListener<HttpServletRequest, HttpServletResponse> {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RawLog rawLog = null;

        try {
            onIncomingLog(request);

            if ( validateIncomingLog(request) ) {
                rawLog = getRawLog(request);
                onStoreRawLog(rawLog);
            }
        } catch ( Throwable t ) {
            t.printStackTrace();
            onException(request, response, t);
        } finally {
            if ( rawLog == null ) {
                onInvalidLog(request, response);
            } else {
                onValidLog(request, response);
            }
        }
    }

    @Override
    public void onIncomingLog(HttpServletRequest request) {
    }

}
