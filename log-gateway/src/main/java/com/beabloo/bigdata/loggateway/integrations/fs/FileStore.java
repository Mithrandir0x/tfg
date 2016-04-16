package com.beabloo.bigdata.loggateway.integrations.fs;

import com.beabloo.bigdata.loggateway.integrations.kafka.serdes.RawLogSerDe;
import com.beabloo.bigdata.model.RawLog;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

public class FileStore {

    private ScheduledExecutorService service;
    private RawLogSerDe serDe;
    private LinkedBlockingQueue<RawLog> logs;

    private static FileStore instance = new FileStore();

    private FileStore() {
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(new SaveFile(), 0, 1, TimeUnit.MINUTES);

        logs = new LinkedBlockingQueue<>();

        serDe = new RawLogSerDe();
    }

    public static FileStore getInstance() {
        return instance;
    }

    public void send(RawLog rawLog) {
        logs.add(rawLog);
    }

    protected void saveRawLog(RawLog rawLog) throws Exception {
        byte[] serializedRawLog = serDe.serialize(null, rawLog);

        String filename = String.format("/tmp/log-gateway/%s-%s", rawLog.getType().replace("/", "_"),rawLog.getTimestamp());
        System.out.println(String.format("Saving to [%s]", filename));
        FileUtils.writeByteArrayToFile(new File(filename), serializedRawLog);
    }

    protected void saveRawLogList(List<RawLog> rawLogs) throws Exception {
        String filename = String.format("/tmp/log-gateway/batch-%s", new Date().getTime());
        System.out.println(String.format("Saving [%s] logs to [%s]", rawLogs.size(), filename));
        for ( RawLog rawLog : rawLogs ) {
            FileUtils.writeByteArrayToFile(new File(filename), serDe.serialize(null, rawLog), true);
        }
    }

    private class SaveFile implements Runnable {

        @Override
        public void run() {
            try {
                ArrayList<RawLog> drainedLogs = new ArrayList<>();
                int totalDrainedLogs = logs.drainTo(drainedLogs, 1000);
                if ( totalDrainedLogs > 0 ) {
                    saveRawLogList(drainedLogs);
                }
            } catch ( Throwable ex ) {
                ex.printStackTrace();
            }
        }

    }

}
