package edu.ub.bigdata.loggateway.aspects.integration;

import edu.ub.bigdata.loggateway.integrations.fs.FileStore;
import edu.ub.bigdata.model.RawLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class FileStorageInterceptor {

    @Around("execution(* (@edu.ub.bigdata.loggateway.aspects.integration.FileStorage edu.ub.bigdata.loggateway.LogListener+).onStoreRawLog(..))")
    public void saveToFileSystem(ProceedingJoinPoint pjp) throws Throwable {
        pjp.proceed();

        RawLog rawLog = (RawLog) pjp.getArgs()[0];

        System.out.println(String.format("rawLog [%s]", rawLog));
        FileStore.getInstance().send(rawLog);

    }

}
