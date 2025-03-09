package com.tugalsan.gvm.executor;

import com.tugalsan.api.log.server.TS_Log;
import com.tugalsan.api.network.server.TS_NetworkSSLUtils;
import com.tugalsan.api.thread.server.sync.TS_ThreadSyncTrigger;
import com.tugalsan.lib.license.server.TS_LibLicenseFileUtils;

public class Main {

    final private static TS_Log d = TS_Log.of(false, Main.class);

    //HOW TO EXECUTE
    //WHEN RUNNING IN NETBEANS, ALL DEPENDENCIES SHOULD HAVE TARGET FOLDER!
    //cd D:\git\gvm\com.tugalsan.gvm.executor
    //java --enable-preview --add-modules jdk.incubator.vector -jar target/com.tugalsan.gvm.executor-1.0-SNAPSHOT-jar-with-dependencies.jar    
    public static void main(String[] args) throws InterruptedException {
        //PREREQUESTS
        TS_NetworkSSLUtils.disableCertificateValidation();
        var kill = TS_ThreadSyncTrigger.of("main");
        var settings = TaskExamples.of(TaskExamples.pathDefault());
        if (settings == null) {
            d.ce("main", "ERROR: settings == null");
            return;
        }

        //LICENSE
        if (!TS_LibLicenseFileUtils.checkLicenseFromLicenseFile(Main.class)) {
            TS_LibLicenseFileUtils.createLicenseFileFromServer(Main.class);
        }
        if (!TS_LibLicenseFileUtils.checkLicenseFromLicenseFile(Main.class)) {
            TS_LibLicenseFileUtils.createRequestFile(Main.class);
            d.ce("main", "ERROR: license server cannot be reached", "request file created instead");
            return;
        }

    }

}
