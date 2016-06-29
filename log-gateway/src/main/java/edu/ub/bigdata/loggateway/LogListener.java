package edu.ub.bigdata.loggateway;

import edu.ub.bigdata.model.RawLog;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface LogListener {

    void onIncomingLog(HttpServletRequest input) throws ServletException, IOException;

    void onValidLog(HttpServletRequest input, HttpServletResponse output) throws ServletException, IOException;

    void onInvalidLog(HttpServletRequest input, HttpServletResponse output) throws ServletException, IOException;

    void onException(HttpServletRequest input, HttpServletResponse output, Throwable ex) throws ServletException, IOException;

    boolean validateIncomingLog(HttpServletRequest input);

    RawLog getRawLog(HttpServletRequest input);

    void onStoreRawLog(RawLog rawLog) throws Exception;

}
