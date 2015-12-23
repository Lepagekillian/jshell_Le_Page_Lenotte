package fr.umlv.jshellbook.watchfolder;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

public class FileObserverTest {

	@Test(expected = NullPointerException.class)
	@SuppressWarnings({ "static-method", "unused" }) // test method can't be
														// static
	public void testDirNull() throws IOException {
		new FileObserver(null, p -> true);
	}

	@Test(expected = NullPointerException.class)
	@SuppressWarnings({ "static-method", "unused" }) // test method can't be
														// static
	public void testPredicateNull() throws IOException {

		new FileObserver(Paths.get("ressources"), null);
	}

	@Test
	@SuppressWarnings("static-method") // test method can't be static
	public void testOtherObserver() throws IOException, InterruptedException {
		Path ressources = Paths.get("ressources");

		FileObserver test = new FileObserver(ressources, p -> p.toString().endsWith(".mkdown"));
		test.registerKindEvent(ENTRY_MODIFY, ENTRY_CREATE, ENTRY_DELETE);
		Path pathCreate = Paths.get(ressources.toString(), "test.txt");

		try (BufferedWriter bw = Files.newBufferedWriter(pathCreate, StandardOpenOption.CREATE_NEW,
				StandardOpenOption.DELETE_ON_CLOSE)) {
			bw.write("test");
			bw.close();
		}
		List<WatchEvent<Path>> watchables = test.observeDirectory();
		assertEquals(0, watchables.size());
	}

	@Test
	@SuppressWarnings("static-method") // test method can't be static
	public void testModifyObserver() throws IOException, InterruptedException {
		Path ressources = Paths.get("ressources");

		FileObserver test = new FileObserver(ressources, p -> p.toString().endsWith(".mkdown"));
		test.registerKindEvent(ENTRY_MODIFY);
		Path pathCreate = Paths.get(ressources.toString(), "testModif.mkdown");
		try (BufferedWriter bw = Files.newBufferedWriter(pathCreate, StandardOpenOption.CREATE_NEW,
				StandardOpenOption.DELETE_ON_CLOSE)) {
			bw.write("test");
		}
		List<WatchEvent<Path>> watchables = test.observeDirectory();
		List<Kind<Path>> kinds = watchables.stream().map(w -> w.kind()).collect(Collectors.toList());

		assertEquals(1, watchables.size());
		assertTrue(kinds.contains(ENTRY_MODIFY));
		
	}

	@Test
	@SuppressWarnings("static-method") // test method can't be static
	public void testDeletteObserver() throws IOException, InterruptedException {
		Path ressources = Paths.get("ressources");

		FileObserver test = new FileObserver(ressources, p -> p.toString().endsWith(".mkdown"));
		test.registerKindEvent(ENTRY_DELETE);
		Path pathCreate = Paths.get(ressources.toString(), "testDelette.mkdown");

		try (BufferedWriter bw = Files.newBufferedWriter(pathCreate, StandardOpenOption.CREATE_NEW,
				StandardOpenOption.DELETE_ON_CLOSE)) {
			bw.write("test");
		}
		List<WatchEvent<Path>> watchables = test.observeDirectory();
		assertEquals(1, watchables.size());
		List<Kind<Path>> kinds = watchables.stream().map(w -> w.kind()).collect(Collectors.toList());

		assertEquals(1, watchables.size());
		assertTrue(kinds.contains(ENTRY_DELETE));
	}

	@Test
	@SuppressWarnings("static-method") // test method can't be static
	public void testCreateObserver() throws IOException, InterruptedException {
		Path ressources = Paths.get("ressources");

		FileObserver test = new FileObserver(ressources, p -> p.toString().endsWith(".mkdown"));
		test.registerKindEvent(ENTRY_CREATE);
		Path pathCreate = Paths.get(ressources.toString(), "testCreate.mkdown");

		try (BufferedWriter bw = Files.newBufferedWriter(pathCreate, StandardOpenOption.CREATE_NEW,
				StandardOpenOption.DELETE_ON_CLOSE)) {
			bw.write("test");
		}
		List<WatchEvent<Path>> watchables = test.observeDirectory();
		List<Kind<Path>> kinds = watchables.stream().map(w -> w.kind()).collect(Collectors.toList());

		assertEquals(1, watchables.size());
		assertTrue(kinds.contains(ENTRY_CREATE));
	}
}
