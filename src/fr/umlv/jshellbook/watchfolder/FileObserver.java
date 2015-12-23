package fr.umlv.jshellbook.watchfolder;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class FileObserver {

	private final Path dir;
	private final WatchService watcher;
	private final Predicate<Path> watchPredicate;

	/* Publics method */
	public FileObserver(Path dir, Predicate<Path> watchPredicate) throws IOException {

		this.watchPredicate = Objects.requireNonNull(watchPredicate);
		if (!Files.exists(dir) || !Files.isDirectory(dir)) {// implicite null
															// check
			throw new IllegalArgumentException(dir + " don't exist or is not a directory");
		}
		this.dir = dir;
		this.watcher = FileSystems.getDefault().newWatchService();
	}

	
	public void registerKindEvent( Kind<?>... kindEvents) throws IOException {
		Objects.requireNonNull(kindEvents);
		this.dir.register(this.watcher, kindEvents);
	}

	@SuppressWarnings("unchecked")
	public List<WatchEvent<Path>> observeDirectory() throws InterruptedException {
		WatchKey key = this.watcher.take();
		List<WatchEvent<Path>> watchEvents = new ArrayList<>();

		for (WatchEvent<?> event : key.pollEvents()) {
			WatchEvent<Path> ev = (WatchEvent<Path>) event;
			if (this.watchPredicate.test(ev.context())) {
				watchEvents.add(ev);
			}
		}
		key.reset();
		return watchEvents;
	}
}
