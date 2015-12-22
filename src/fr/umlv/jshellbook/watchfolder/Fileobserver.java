package fr.umlv.jshellbook.watchfolder;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.*;

import java.util.Objects;

public class Fileobserver {

	private final Path dir;

	Fileobserver(Path dir) {
		this.dir = Objects.requireNonNull(dir);
	}

	public void observeDirectory() throws IOException, InterruptedException {
		WatchService myWatcher = FileSystems.getDefault().newWatchService();
		this.dir.register(myWatcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		WatchKey key;
		key = myWatcher.take();
		for (WatchEvent<?> event : key.pollEvents()) {
			WatchEvent.Kind<?> kind = event.kind();

			/* We guaranteed that event is typed as a WatchEvent<Path> */
			@SuppressWarnings("unchecked")
			WatchEvent<Path> ev = (WatchEvent<Path>) event;
			Path fileName = ev.context();

			if (kind == OVERFLOW) {
				continue;
			} else if (kind == ENTRY_CREATE) {

				System.out.println(fileName + " has been created.");
			} else if (kind == ENTRY_DELETE) {

				System.out.println(fileName + " has been deleted.");

			} else if (kind == ENTRY_MODIFY) {

				System.out.println(fileName + " has been modified.");

			}
		}
		key.reset();
	}

	@Override
	public String toString() {
		return "File : " + this.dir.getFileName();
	}
}
