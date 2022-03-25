import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

public class FileWatcherServiceExample {
    private static String HOME = System.getProperty("user.home");
    private static String PLAY_WITH_NIO = "TempPlayGround";

    public void givenADirectoryWhenCheckListAllTheActivities() throws IOException {
        Path dir = Paths.get(HOME + "/" + PLAY_WITH_NIO);
        Files.list(dir).filter(Files::isRegularFile).forEach(System.out::println);
        new JavaWatcherServiceExample(dir).processEvents();
    }

    public class JavaWatcherServiceExample {
        private final WatchService watcher;
        private final Map<WatchKey, Path> dirWatchers;

        // creating a watchservice and registers the given directory
        public JavaWatcherServiceExample(Path dir) throws IOException {
            this.watcher = FileSystems.getDefault().newWatchService();
            this.dirWatchers = new HashMap<WatchKey, Path>();
            scanAndRegisterDirectories(dir);
        }

        // register the given directory with the watchservice;
        private void registerDirWatchers(Path dir) throws IOException {
            WatchKey key = dir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE,
                    StandardWatchEventKinds.ENTRY_MODIFY);
            dirWatchers.put(key, dir);
        }

          // register the given directory, and all its sub_directories, with the watchservice.
        private void scanAndRegisterDirectories(final Path start) throws IOException {
            Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                 //register directory and sub-directories
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) throws IOException {
                    registerDirWatchers(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        }

        //process all events for keys queued to the watcher
        @SuppressWarnings({"rawtypes", "unchecked"})
        void processEvents() {
            while (true) {
                WatchKey key; // wait for key to be signalled
                try {
                    key = watcher.take();
                } catch (InterruptedException x) {
                    return;
                }
                Path dir = dirWatchers.get(key);
                if (dir == null)
                    continue;
                for (WatchEvent<?> event : key.pollEvents()) {
                    WatchEvent.Kind kind = event.kind();
                    Path name = ((WatchEvent<Path>) event).context();
                    Path child = dir.resolve(name);
                    System.out.format("%s: %s\n", event.kind().name(), child); //print out event

                    // if directory is created, then register it and its sub_directories
                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        try {
                            if (Files.isDirectory(child))
                                scanAndRegisterDirectories(child);
                        } catch (IOException x) {
                        }
                    } else if (kind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                        if (Files.isDirectory(child))
                            dirWatchers.remove(key);
                    }
                }

                // reset key and remove from set if directory no longer accessible
                boolean valid = key.reset();
                if (!valid) {
                    dirWatchers.remove(key);
                    if (dirWatchers.isEmpty())
                        break; // all directoies are inaccessible
                }
            }
        }
    }
}