package com.kbrewster.mc;

import com.sun.org.apache.xpath.internal.SourceTree;
import us.deathmarine.luyten.Luyten;
import us.deathmarine.luyten.MainWindow;
import us.deathmarine.luyten.RecentFiles;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * Bad naming convention as its nothing to do with Decompiling but i cba to rename
 */
@Metadata(name = "Luyten4Forge", version = 1.0)
public class Decompile implements Runnable {

    /**
     * Stores all the old mappings and what to replace it with
     */
    public static TreeMap<String, String> mappings = new TreeMap<>();

    /**
     * Cant use enums because they're numbers ._. so heartbreaking
     */
    public static String versions[] = {"1.7.10", "1.8", "1.8.9", "1.9", "1.10.2", "1.11"};

    public static String currentMapping = null;
    public static File currentFile;

    /**
     * Iterates through the needed mapping putting them in a map and reloads the current project
     */
    @Override
    public void run() {
        try {
            mappings = new TreeMap<>();

            String fileNames[] = {"fields.csv", "methods.csv", "params.csv"};

            for (String fileName : fileNames) {
                if(currentMapping == null)
                    return;

                File file = getResourceAsFile("mapping/" + currentMapping + "/" + fileName);

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

    /**
     * Gave the up on the trying to get the directory of the mappings, it was painful ;(
     * https://stackoverflow.com/questions/14089146/file-loading-by-getclass-getresource
     * @param resourcePath
     * @return
     */
    public static File getResourceAsFile(String resourcePath) {
        try {
            InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
            if (in == null) {
                return null;
            }

            File tempFile = File.createTempFile(String.valueOf(in.hashCode()), ".tmp");
            tempFile.deleteOnExit();

            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                //copy stream
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Reloads mapping
     * @param mappings the version
     */
    public static void reloadMappings(String mappings) {
        currentMapping = mappings;
        new Thread(new Decompile()).start();
    }
}
