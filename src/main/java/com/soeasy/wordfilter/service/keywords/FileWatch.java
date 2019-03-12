package com.soeasy.wordfilter.service.keywords;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.*;

/**
 * 敏感词文件监听器
 * 文件改动重新加载 敏感库
 */
public class FileWatch {

    private static Logger logger = LoggerFactory.getLogger(FileWatch.class);

    private WatchService watchService;

    public FileWatch(String pathStr) {
        try {
            Path path = Paths.get(pathStr);
            watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
        } catch (Exception e) {
            System.out.println("watch create fail");
            e.printStackTrace();
        }
    }


    public void handleEvents(KWContext bannerFilter) throws InterruptedException {
        while (true) {
            WatchKey key = watchService.take();
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                if (kind == StandardWatchEventKinds.OVERFLOW) {
                    continue;
                }

                WatchEvent<Path> e = (WatchEvent<Path>) event;
                Path fileName = e.context();

                logger.info("事件{}触发,文件名为{}", kind.name(), fileName);
                KWContext.init();
            }
            if (!key.reset()) {
                break;
            }
        }
    }
}
