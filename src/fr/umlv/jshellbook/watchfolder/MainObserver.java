package fr.umlv.jshellbook.watchfolder;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.Paths;

public class MainObserver {

	public static void main(String[] args) throws IOException {
		
		Fileobserver test = new Fileobserver(Paths.get("ressources"));
		test.registerEvent(ENTRY_CREATE);
		test.registerEvent(ENTRY_DELETE);
		test.registerEvent(ENTRY_MODIFY);
		while(true){
			try {
				test.observeDirectory();
			} catch (InterruptedException e) {
				System.out.println("Program has been interrupted");
				return;
			}
		}
	}

}
