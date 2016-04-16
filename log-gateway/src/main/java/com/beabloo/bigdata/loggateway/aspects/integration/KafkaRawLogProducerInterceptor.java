package com.beabloo.bigdata.loggateway.aspects.integration;

import com.beabloo.bigdata.model.RawLog;
import com.beabloo.bigdata.loggateway.integrations.kafka.RawLogProducer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class KafkaRawLogProducerInterceptor {

    @Around("execution(* (@KafkaRawLogProducer com.beabloo.bigdata.loggateway.LogListener+).onStoreLogObject(..))")
    public void sendToKafka(ProceedingJoinPoint pjp) throws Throwable {
        pjp.proceed();

        RawLog rawLog = (RawLog) pjp.getArgs()[0];

        System.out.println(String.format("rawLog [%s]", rawLog));
        RawLogProducer.getInstance().send(rawLog);
    }

}
