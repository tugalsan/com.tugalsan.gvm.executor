package com.tugalsan.gvm.executor;

import com.tugalsan.api.file.server.TS_FileUtils;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.api.tuple.client.TGS_Tuple2;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

final public class TaskCopyFiles extends Task {

    final public List<Path> sourceFiles;
    final public Path destinationDirectory;

    final public static String paramSourceFilesSize(String name) {
        return paramX(name, "sourceFiles.size()");
    }

    final public static String paramSourceFileI(String name, int i) {
        return paramX(name, "sourceFiles.get(" + i + ")");
    }

    final public static String paramdestinationDirectory(String name) {
        return paramX(name, "destinationDirectory");
    }

    private TaskCopyFiles(TS_ThreadSyncTrigger kill, String name, List<Path> sourceFiles, Path destinationDirectory) {
        super(kill, name);
        this.sourceFiles = sourceFiles;
        this.destinationDirectory = destinationDirectory;
    }

    public static TaskCopyFiles of(TS_ThreadSyncTrigger kill, String name, List<Path> sourceFiles, Path destinationDirectory) {
        return new TaskCopyFiles(kill, name, sourceFiles, destinationDirectory);
    }

    @Override
    public List<TGS_Tuple2<String, String>> toListTuple2() {
        List<TGS_Tuple2<String, String>> lstTuple2 = new ArrayList();
        lstTuple2.add(TGS_Tuple2.of(paramType(name), TaskCopyFiles.class.getSimpleName()));
        lstTuple2.add(TGS_Tuple2.of(paramSourceFilesSize(name), String.valueOf(sourceFiles.size())));
        IntStream.range(0, sourceFiles.size()).forEachOrdered(i -> {
            lstTuple2.add(TGS_Tuple2.of(paramSourceFileI(name, i), sourceFiles.get(i).toString()));
        });
        return lstTuple2;
    }

    @Override
    public boolean execute() {
        return sourceFiles.stream()
                .map(sourceFile -> TS_FileUtils.copyToFolder(destinationDirectory, destinationDirectory, true))
                .noneMatch(sourceFile -> sourceFile == null);
    }
}
