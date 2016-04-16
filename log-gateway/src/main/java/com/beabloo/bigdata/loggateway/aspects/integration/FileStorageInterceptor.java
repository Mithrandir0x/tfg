package com.beabloo.bigdata.loggateway.aspects.integration;

import com.beabloo.bigdata.loggateway.integrations.fs.FileStore;
import com.beabloo.bigdata.model.RawLog;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class FileStorageInterceptor {

    @Around("execution(* (@FileStorage com.beabloo.bigdata.loggateway.LogListener+).onStoreLogObject(..))")
    public void saveToFileSystem(ProceedingJoinPoint pjp) throws Throwable {
        pjp.proceed();

        RawLog rawLog = (RawLog) pjp.getArgs()[0];

        System.out.println(String.format("rawLog [%s]", rawLog));
        FileStore.getInstance().send(rawLog);

    }

}
