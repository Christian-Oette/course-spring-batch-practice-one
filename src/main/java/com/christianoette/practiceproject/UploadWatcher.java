package com.christianoette.practiceproject;

import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationListenerAdaptor;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class UploadWatcher {

    private static final Logger LOGGER = LogManager.getLogger(UploadWatcher.class);
    private final Anonymizer anonymizer;

    public UploadWatcher(Anonymizer anonymizer) {
        this.anonymizer = anonymizer;
    }

    @EventListener(value = ApplicationReadyEvent.class)
    public void watch() throws Exception {
        LOGGER.info("Watcher enabled");
        FileAlterationObserver observer = new FileAlterationObserver("C:\\development\\course-spring-batch-practice-one\\public\\upload");
        FileAlterationMonitor monitor = new FileAlterationMonitor(10_000);
        observer.addListener(new FileAlterationListenerAdaptor() {
            @Override
            public void onFileCreate(File file) {
                anonymizer.runAnonymizationJob(file);
            }
        });
        monitor.addObserver(observer);
        monitor.start();
    }
}
