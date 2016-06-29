package edu.ub.bigdata.loggateway;

import edu.ub.bigdata.model.RawLog;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public interface LogListener<INPUT, OUTPUT> {

    void onIncomingLog(INPUT input) throws ServletException, IOException;

    void onValidLog(INPUT input, OUTPUT output) throws ServletException, IOException;

    void onInvalidLog(INPUT input, OUTPUT output) throws ServletException, IOException;

    void onException(INPUT input, OUTPUT output, Throwable ex) throws ServletException, IOException;

    boolean validateIncomingLog(INPUT input);

    RawLog getRawLog(INPUT input);

    void onStoreRawLog(RawLog rawLog) throws Exception;

}
