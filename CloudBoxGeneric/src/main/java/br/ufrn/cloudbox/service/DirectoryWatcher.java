package br.ufrn.cloudbox.service;
 
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

import br.ufrn.cloudbox.exception.ConnectionException;
 
public abstract class DirectoryWatcher extends Thread {
 
    private Path rootPath;
    private WatchService watchService;
    private boolean watching = true;
 
    public DirectoryWatcher(String absoluteRootDirectory) throws IOException {
        this.rootPath = Paths.get(absoluteRootDirectory);
        this.watchService = FileSystems.getDefault().newWatchService();
        this.rootPath.register(this.watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    }
 
    @Override
    public void run() {
        while (this.watching) {
            try {
                WatchKey watchKey = watchService.take();
 
                List<WatchEvent<?>> events = watchKey.pollEvents();
                for (WatchEvent<?> event : events) {
                    WatchEvent.Kind<?> kind = event.kind();
 
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path localRelativePath = ev.context();
                     
                    if (kind == OVERFLOW) {
                        System.out.println("Overflow on directory watcher.");
                        continue;
                    } else if (kind == ENTRY_CREATE) {
                        processCreateEvent(localRelativePath.toString());
                    } else if (kind == ENTRY_DELETE) {
                        processDeleteEvent(localRelativePath.toString());
                    } else if (kind == ENTRY_MODIFY) {
                        processModifyEvent(localRelativePath.toString());
                    }
                }
 
                boolean valid = watchKey.reset();
                if(!valid) {
                	System.out.println("Returned 'false' on watchKey.reset()");
                    break;
                }
            } catch (ConnectionException e) {
            	System.out.println("Error while doing operation. Error: " + e.toString());
                e.printStackTrace();
            } catch (InterruptedException e) {
            	System.out.println("Directory watcher interrupted.");
            }
        }
    }
 
    public abstract void processCreateEvent(String localRelativePath) throws ConnectionException;
    public abstract void processDeleteEvent(String localRelativePath) throws ConnectionException;
    public abstract void processModifyEvent(String localRelativePath) throws ConnectionException;
 
    public void stopWatching() {
        watching = false;
    }
}