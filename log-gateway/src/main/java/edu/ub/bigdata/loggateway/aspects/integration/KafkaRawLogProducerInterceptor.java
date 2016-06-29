package edu.ub.bigdata.loggateway.aspects.integration;

import edu.ub.bigdata.model.RawLog;
import edu.ub.bigdata.loggateway.integrations.kafka.RawLogProducer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class KafkaRawLogProducerInterceptor {

    @Around("execution(* (@edu.ub.bigdata.loggateway.aspects.integration.KafkaRawLogProducer edu.ub.bigdata.loggateway.LogListener+).onStoreRawLog(..))")
    public void sendToKafka(ProceedingJoinPoint pjp) throws Throwable {
        pjp.proceed();

        RawLog rawLog = (RawLog) pjp.getArgs()[0];

        System.out.println(String.format("rawLog [%s]", rawLog));
        RawLogProducer.getInstance().send(rawLog);
    }

}
