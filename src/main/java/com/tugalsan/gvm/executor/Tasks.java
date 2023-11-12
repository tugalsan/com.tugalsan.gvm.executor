package com.tugalsan.gvm.executor;

import com.tugalsan.api.file.properties.server.TS_FilePropertiesUtils;
import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.file.server.TS_PathUtils;
import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.stream.client.TGS_StreamUtils;
import com.tugalsan.api.string.server.TS_StringUtils;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

public class Tasks {

    final private static TS_Log d = TS_Log.of(false, Tasks.class);

    public static Path pathDefault() {
        return TS_PathUtils.getPathCurrent_nio(Tasks.class.getPackageName() + ".properties");
    }

    public static String paramExecutionOrder() {
        return "executionOrder";
    }

    private Tasks(TS_ThreadSyncTrigger kill) {
        this.kill = kill;
    }
    final public TS_ThreadSyncTrigger kill;
    final public List<Task> list = new ArrayList();

    public static Tasks of(TS_ThreadSyncTrigger kill) {
        return new Tasks(kill);
    }

    public void load(Path propsFile) {
        var propsExists = TS_FileUtils.isExistFile(propsFile);
        if (!propsExists) {
            save(propsFile);
            return;
        }
        var props = TS_FilePropertiesUtils.read(propsFile);

        var executionOrderStr = TS_FilePropertiesUtils.getValue(props, paramExecutionOrder(), null);
        if (executionOrderStr == null) {
            save(propsFile);
            return;
        }
        var executionOrderNames = TS_StringUtils.toList_spc(executionOrderStr);

        executionOrderNames.forEach(name -> {
            var type = TS_FilePropertiesUtils.getValue(props, Task.paramType(name), null);
            if (type == null) {
                d.ce("load", propsFile, "ERROR: type not found for name", name);
                return;
            }
            if (Objects.equals(type, TaskCopyFiles.class.getSimpleName())) {
                List<Path> sourceFiles = new ArrayList();

                Path destinationDirectory = TS_PathUtils.of(TS_FilePropertiesUtils.getValue(props, Task.paramType(name), null));
                if (destinationDirectory == null) {
                    d.ce("load", propsFile, "name", name, "ERROR: destinationDirectory == null");
                    return;
                }
                list.add(TaskCopyFiles.of(kill, name, sourceFiles, destinationDirectory));
            }
            d.ce("load", propsFile, "ERROR: type not recognized for name", name, "type", type);
        });

        if (!props.equals(TS_FilePropertiesUtils.read(propsFile))) {
            save(propsFile);
        }
    }

    public void save(Path propsFile) {
        var props = new Properties();
        TS_FilePropertiesUtils.setValue(props, paramExecutionOrder(), String.join(" ", TGS_StreamUtils.toLst(list.stream().map(task -> task.name))));
        list.forEach(task -> {
            task.toListTuple2().forEach(tuple2 -> {
                TS_FilePropertiesUtils.setValue(props, tuple2.value0, tuple2.value1);
            });
        });
        TS_FilePropertiesUtils.write(props, propsFile);
    }

}
