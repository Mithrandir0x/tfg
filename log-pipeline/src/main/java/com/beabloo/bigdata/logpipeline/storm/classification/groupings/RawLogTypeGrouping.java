package com.beabloo.bigdata.logpipeline.storm.classification.groupings;

import org.apache.storm.generated.GlobalStreamId;
import org.apache.storm.grouping.CustomStreamGrouping;
import org.apache.storm.task.WorkerTopologyContext;

import java.util.List;

public class RawLogTypeGrouping implements CustomStreamGrouping {

    @Override
    public void prepare(WorkerTopologyContext context, GlobalStreamId stream, List<Integer> targetTasks) {
    }

    @Override
    public List<Integer> chooseTasks(int taskId, List<Object> values) {
        return null;
    }

}
