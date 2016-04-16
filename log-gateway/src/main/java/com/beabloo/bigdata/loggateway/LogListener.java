package com.beabloo.bigdata.loggateway;

import com.beabloo.bigdata.model.RawLog;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface LogListener {

    void onIncomingLog(HttpServletRequest request) throws ServletException, IOException;

    void onValidLog(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    void onInvalidLog(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    void onException(HttpServletRequest request, HttpServletResponse response, Throwable ex) throws ServletException, IOException;

    boolean validateIncomingLog(HttpServletRequest request);

    RawLog getLogObject(HttpServletRequest request);

    void onStoreLogObject(RawLog rawLog);

}
