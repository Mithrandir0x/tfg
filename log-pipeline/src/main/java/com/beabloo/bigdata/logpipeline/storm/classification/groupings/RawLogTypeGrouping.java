package com.beabloo.bigdata.logpipeline.storm.classification.groupings;

import backtype.storm.generated.GlobalStreamId;
import backtype.storm.grouping.CustomStreamGrouping;
import backtype.storm.task.WorkerTopologyContext;

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
