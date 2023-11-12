package com.tugalsan.gvm.executor;

import com.tugalsan.api.charset.client.TGS_CharSetCast;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.os.server.TS_OsProcess;
import com.tugalsan.api.thread.server.async.TS_ThreadAsyncAwait;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.tuple.client.TGS_Tuple2;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

final public class TaskExecute extends Task {

    final private static TS_Log d = TS_Log.of(false, TaskExecute.class);

    final public String command;
    final public int timoutSeconds;

    final public static String paramCommand(String name) {
        return paramX(name, "command");
    }

    final public static String paramTimoutSeconds(String name) {
        return paramX(name, "timoutSeconds");
    }

    private TaskExecute(TS_ThreadSyncTrigger kill, String name, int timoutSeconds, String command) {
        super(kill, name);
        this.command = command;
        this.timoutSeconds = timoutSeconds;
    }

    public static TaskExecute of(TS_ThreadSyncTrigger kill, String name, int timoutSeconds, String command) {
        return new TaskExecute(kill, name, timoutSeconds, command);
    }

    @Override
    public List<TGS_Tuple2<String, String>> toListTuple2() {
        List<TGS_Tuple2<String, String>> lstTuple2 = new ArrayList();
        lstTuple2.add(TGS_Tuple2.of(paramType(name), TaskExecute.class.getSimpleName()));
        lstTuple2.add(TGS_Tuple2.of(paramCommand(name), command));
        lstTuple2.add(TGS_Tuple2.of(paramTimoutSeconds(name), String.valueOf(timoutSeconds)));
        return lstTuple2;
    }

    @Override
    public boolean execute() {
        var await = TS_ThreadAsyncAwait.callSingle(kill, Duration.ofSeconds(timoutSeconds), kt -> {
            var process = TS_OsProcess.of(command);
            if (process.exception != null) {
                process.exception.printStackTrace();
                return false;
            }
            return !TGS_CharSetCast.containsLocaleIgnoreCase(command, "error");
        });
        if (await.hasError()) {
            d.ct("execute", await.exceptionIfFailed.get());
            return false;
        }
        return await.resultIfSuccessful.get();
    }
}
