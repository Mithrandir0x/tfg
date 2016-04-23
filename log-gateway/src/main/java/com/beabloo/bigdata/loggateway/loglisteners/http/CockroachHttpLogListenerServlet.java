package com.beabloo.bigdata.loggateway.loglisteners.http;

import com.beabloo.bigdata.loggateway.aspects.integration.FileStorage;
import com.beabloo.bigdata.model.RawLog;
import com.beabloo.bigdata.loggateway.aspects.integration.KafkaRawLogProducer;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Pattern;

@KafkaRawLogProducer
@FileStorage
@WebServlet(urlPatterns = {"/activityTracking/*"}, loadOnStartup = 1)
public class CockroachHttpLogListenerServlet extends AbstractHttpLogListenerServlet {

    private static Pattern validUri = Pattern.compile("^.*/activityTracking/[a-zA-Z_0-9]+.*$");

    @Override
    public void onIncomingLog(HttpServletRequest request) {
        System.out.println(String.format("URL [%s] URI [%s] queryString [%s]",
                request.getRequestURL().toString(),
                request.getRequestURI().toString(),
                request.getQueryString()));
    }

    public void onValidLog(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Valid log request");
    }

    public void onInvalidLog(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendError(400, "Invalid log request");
    }

    @Override
    public void onException(HttpServletRequest request, HttpServletResponse response, Throwable ex) throws ServletException, IOException {
        response.sendError(500, "Unexpected server error");
    }

    public boolean validateIncomingLog(HttpServletRequest request) {
        boolean isValid = true;

        isValid &= validUri.matcher(request.getRequestURI().toString()).matches();
        isValid &= request.getParameter("json") != null;

        return isValid;
    }

    public RawLog getLogObject(HttpServletRequest request) {
        RawLog rawLog = new RawLog();

        rawLog.setTimestamp((new Date()).getTime());
        rawLog.setType(request.getRequestURL().toString());
        rawLog.setData(request.getQueryString());

        return rawLog;
    }

    /**
     * Method is required to be defined as aspect won't weave the method,
     * if it is not explicitly implemented
     *
     * @param rawLog
     */
    public void onStoreLogObject(RawLog rawLog) {
        System.out.println("Storing message...");
    }

}
