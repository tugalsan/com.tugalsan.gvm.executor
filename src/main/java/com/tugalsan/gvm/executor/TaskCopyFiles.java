package com.tugalsan.gvm.executor;

import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.tuple.client.TGS_Tuple2;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

public class TaskCopyFiles extends Task {

    final public List<Path> sourceFiles;
    final public Path destinationDirectory;

    final public String paramSourceFilesSize() {
        return paramX("sourceFiles.size()");
    }

    final public String paramSourceFileI(int i) {
        return paramX("sourceFiles.get(" + i + ")");
    }

    final public String paramdestinationDirectory() {
        return paramX("destinationDirectory");
    }

    private TaskCopyFiles(TS_ThreadSyncTrigger kill, int executionOrder, String name, String description, List<Path> sourceFiles, Path destinationDirectory) {
        super(kill, executionOrder, name, description);
        this.sourceFiles = sourceFiles;
        this.destinationDirectory = destinationDirectory;
    }

    public static TaskCopyFiles of(TS_ThreadSyncTrigger kill, int executionOrder, String name, String description, List<Path> sourceFiles, Path destinationDirectory) {
        return new TaskCopyFiles(kill, executionOrder, name, description, sourceFiles, destinationDirectory);
    }

    @Override
    public List<TGS_Tuple2<String, String>> list2tuple() {
        var lst = super.list2tuple();
        lst.add(TGS_Tuple2.of(paramSourceFilesSize(), String.valueOf(sourceFiles.size())));
        IntStream.range(0, sourceFiles.size()).forEachOrdered(i -> {
            lst.add(TGS_Tuple2.of(paramSourceFileI(i), sourceFiles.get(i).toString()));
        });
        return lst;
    }

    @Override
    public boolean execute() {
        return sourceFiles.stream()
                .map(sourceFile -> TS_FileUtils.copyToFolder(destinationDirectory, destinationDirectory, true))
                .filter(sourceFile -> sourceFile == null).findAny().isEmpty();
    }
}
