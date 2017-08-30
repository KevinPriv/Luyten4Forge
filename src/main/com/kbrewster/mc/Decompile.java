package com.kbrewster.mc;

import com.sun.org.apache.xpath.internal.SourceTree;
import us.deathmarine.luyten.Luyten;
import us.deathmarine.luyten.MainWindow;
import us.deathmarine.luyten.RecentFiles;

import java.io.File;
import java.util.Scanner;
import java.util.TreeMap;

@Metadata(name = "Luyten4Forge", version = 1.0)
public class Decompile implements Runnable {

    public static TreeMap<String, String> mappings = new TreeMap<>();

    /**
     * Cant use enums because they're numbers ._. so gay
     */
    public static String versions[] = {"1.7.10", "1.8", "1.8.9", "1.9", "1.10.2", "1.11"};

    public static String currentMapping = null;
    public static File currentFile;

    @Override
    public void run() {
        try {
            System.out.println(RecentFiles.paths.size());
            mappings = new TreeMap<>();

            String fileNames[] = {"fields.csv", "methods.csv", "params.csv"};

            for (String fileName : fileNames) {
                if(currentMapping == null)
                    return;
                File file = new File("mappings/" + currentMapping + "/" + fileName);;
                try (Scanner scanner = new Scanner(file)) {

                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        String linesplit[] = line.split(",");
                        mappings.put(linesplit[0], linesplit[1]);
                    }

                    scanner.close();

                }
            }

            if (currentFile != null) {
                System.out.println("[Open]: Opening " + currentFile.getAbsolutePath());

                Luyten.mainWindowRef.get().getModel().loadFile(currentFile);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void reloadMappings(String mappings) {
        currentMapping = mappings;
        new Thread(new Decompile()).start();
    }
}
