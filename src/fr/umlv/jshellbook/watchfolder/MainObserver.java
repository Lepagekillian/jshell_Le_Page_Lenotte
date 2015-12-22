package fr.umlv.jshellbook.watchfolder;

import java.io.IOException;
import java.nio.file.Paths;

public class MainObserver {

	public static void main(String[] args) {
		Fileobserver test = new Fileobserver(Paths.get("ressources"));
		while(true){
			try {
				test.alphaOne();
			} catch (IOException e) {
				System.out.println(test + "does not exist");
				return;
			} catch (InterruptedException e) {
				System.out.println("Program has been interrupted");
				return;
			}
		}
	}

}
