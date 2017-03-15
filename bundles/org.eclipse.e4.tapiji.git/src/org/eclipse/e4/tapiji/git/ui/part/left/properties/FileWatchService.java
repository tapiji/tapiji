package org.eclipse.e4.tapiji.git.ui.part.left.properties;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Singleton;
import org.eclipse.e4.core.di.annotations.Creatable;


@Creatable
@Singleton
public final class FileWatchService {

    private WatchService watcher;
    private ExecutorService executor;
    private HashSet<String> fileQueue = new HashSet<String>();
    private Timer processTimer;

    @PostConstruct
    public void create() {
        executor = Executors.newSingleThreadExecutor();
    }

    @PreDestroy
    public void destroy() {
        closeWatcher();
        executor.shutdown();
    }

    public void closeWatcher() {
        try {
            if (watcher != null) {
                watcher.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startWatcher(final Path path, FileWatcher listener) {
        if (path == null || listener == null) {
            throw new IllegalArgumentException("Path and FileWatcher may not be null");
        }
        try {
            watcher = path.getFileSystem().newWatchService();
            path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            executeService(listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeService(FileWatcher listener) {
        executor.submit(() -> {
            while (true) {
                final WatchKey key;
                try {
                    key = watcher.take();
                } catch (InterruptedException ex) {
                    return;
                }
                key.pollEvents()
                    .stream()
                    .filter(event -> event.kind() != StandardWatchEventKinds.OVERFLOW)
                    .map(e -> ((WatchEvent<Path>) e).context())
                    .forEach(path -> processFile(listener, path));

                if (!key.reset()) {
                    break;
                }
            }
        });
    }

    private void processFile(FileWatcher listener, Path path) {
        fileQueue.add(path.toFile().getAbsolutePath());
        if (processTimer != null) {
            processTimer.cancel();
        }
        processTimer = new Timer();
        processTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                for (Iterator<String> it = fileQueue.iterator(); it.hasNext();) {
                    it.next();
                    listener.onFileChanged(path);
                    it.remove();
                }
            }
        }, 150);
    }

    public interface FileWatcher {

        void onFileChanged(Path path);
    }
}
