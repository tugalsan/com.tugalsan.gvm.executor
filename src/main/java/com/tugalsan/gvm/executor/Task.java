package com.tugalsan.gvm.executor;

import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.tuple.client.TGS_Tuple2;
import java.util.ArrayList;
import java.util.List;

public abstract class Task {

    final public String paramX(String x) {
        return Task.class.getSimpleName() + " " + x + " " + "name";
    }

    final public String paramName() {
        return paramX("name");
    }

    final public String paramDescription() {
        return paramX("description");
    }

    protected Task(TS_ThreadSyncTrigger kill, int executionOrder, String name, String description) {
        this.kill = kill;
        this.executionOrder = executionOrder;
        this.name = name;
        this.description = description;
    }
    final public TS_ThreadSyncTrigger kill;
    final public int executionOrder;
    final public String name;
    final public String description;

    public List<TGS_Tuple2<String, String>> toListTuple2() {
        List<TGS_Tuple2<String, String>> lst = new ArrayList();
        lst.add(TGS_Tuple2.of(paramName(), name));
        lst.add(TGS_Tuple2.of(paramDescription(), description));
        return lst;
    }
    
    abstract public boolean execute();
}
