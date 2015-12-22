package fr.umlv.jshellbook.watchfolder;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.Paths;

public class MainObserver {

	public static void main(String[] args) throws IOException {

		Fileobserver test = new Fileobserver(Paths.get("ressources"));
		test.registerKindEvent(ENTRY_MODIFY,ENTRY_CREATE,ENTRY_DELETE);
		test.addConsumerToKind(ENTRY_MODIFY, p -> System.out.println(p + " has been modified."));
		test.addConsumerToKind(ENTRY_CREATE, p -> System.out.println(p + " has been create."));
		test.addConsumerToKind(ENTRY_DELETE, p -> System.out.println(p + " has been deleted."));
		
		while (true) {
			try {
				test.observeDirectory();
			} catch (InterruptedException e) {
				System.out.println("Program has been interrupted");
				return;
			}
		}
	}

}
