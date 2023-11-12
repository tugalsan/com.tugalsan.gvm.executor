package com.tugalsan.gvm.executor;

import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.tuple.client.TGS_Tuple2;
import java.util.List;

sealed public abstract class Task permits TaskCopyFiles, TaskExecute {

    final public static String paramX(String name, String x) {
        return Task.class.getSimpleName() + " " + name + " " + x;
    }

    final public static String paramType(String name) {
        return paramX(name, "type");
    }

    protected Task(TS_ThreadSyncTrigger kill, String name) {
        this.kill = kill;
        this.name = name;
    }
    final public TS_ThreadSyncTrigger kill;
    final public String name;

    abstract public List<TGS_Tuple2<String, String>> toListTuple2();

    abstract public boolean execute();
}
