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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Predicate;

public class FileObserver {

	private final Path dir;
	private final WatchService watcher;
	private final Predicate<Path> watchPredicate;
	private final Lock lock = new ReentrantLock();
	/* Publics method */
	public FileObserver(Path dir, Predicate<Path> watchPredicate) throws IOException {

		this.watchPredicate = Objects.requireNonNull(watchPredicate);
		if (!Files.exists(dir) || !Files.isDirectory(dir)) {// implicit null
															// check
			throw new IllegalArgumentException(dir + " don't exist or is not a directory");
		}
		this.dir = dir;
		this.watcher = FileSystems.getDefault().newWatchService();
	}

	
	public void registerKindEvent( Kind<?>... kindEvents) throws IOException {
		this.lock.lock();
		try{
		Objects.requireNonNull(kindEvents);
		this.dir.register(this.watcher, kindEvents);
		}
		finally{
			this.lock.unlock();
		}
	}

	@SuppressWarnings("unchecked")
	public List<WatchEvent<Path>> observeDirectory() throws InterruptedException {
		this.lock.lock();
		try{
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
		finally{
			this.lock.unlock();
		}
	}
}
