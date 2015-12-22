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
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public class Fileobserver {

	private final Path dir;
	private final WatchService watcher;
	private final HashMap<Kind<Path>, Consumer<Path>> map = new HashMap<>();
	private final List<Kind<Path>> kinds = new ArrayList<>();

	/*Tool's method*/
	@SuppressWarnings("unchecked")
	private void takeEvent() throws InterruptedException {
		WatchKey key = this.watcher.take();

		for (WatchEvent<?> event : key.pollEvents()) {
			Kind<Path> kind = (Kind<Path>) event.kind();
			WatchEvent<Path> ev = (WatchEvent<Path>) event;
			System.out.println(kind);
			this.map.getOrDefault(kind, __ -> new IllegalStateException("Unknow kind event " + kind))
					.accept(ev.context());
		}
		key.reset();
	}
	
	
	
	/*Publics method*/
	public Fileobserver(Path dir) throws IOException {
		Objects.requireNonNull(dir);
		if (!Files.exists(dir) || !Files.isDirectory(dir)) {// implicite null
															// check
			throw new IllegalArgumentException(dir + " don't exist or is not a directory");
		}
		this.dir = dir;
		this.watcher = FileSystems.getDefault().newWatchService();
	}

	public void registerEvent(Kind<Path> kindEvent, Consumer<Path> consumer) {
		Objects.requireNonNull(kindEvent);
		this.map.put(kindEvent, consumer);
		this.kinds.add(kindEvent);
	}


	public void observeDirectory() throws InterruptedException, IOException {
		Kind<?>[] kinds = new Kind<?>[this.kinds.size()];
		for (int i = 0; i < this.kinds.size(); i++) {
			kinds[i] = this.kinds.get(i);
		}
		this.dir.register(this.watcher,  kinds);
		takeEvent();
	}
	


	@Override
	public String toString() {
		return "File : " + this.dir.getFileName();
	}
}
