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
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

public class Fileobserver {

	private final Path dir;
	private final WatchService watcher;
	private final HashMap<Kind<Path>, Consumer<Path>> map = new HashMap<>();
	private final Set<Kind<Path>> kindsEvents  = new HashSet<>(); 

	
	
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
	
	public void addConsumerToKind(Kind<Path> kindEvent, Consumer<Path> consumer)  {
		Objects.requireNonNull(consumer);
		if(!this.kindsEvents.contains(kindEvent)){// implicite null check 
			throw new IllegalArgumentException("kind event "+kindEvent+" is not registred");
		}
		this.map.put(kindEvent, consumer);
		
	}
	
	public void registerKindEvent(final Kind<Path>... kindEvents) throws IOException{
		Objects.requireNonNull(kindEvents);
		this.dir.register(this.watcher, kindEvents);
		this.kindsEvents.addAll(Arrays.asList(kindEvents));
	}


	@SuppressWarnings("unchecked")
	public void observeDirectory() throws InterruptedException {
		
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
	


	@Override
	public String toString() {
		return "File : " + this.dir.getFileName();
	}
}
