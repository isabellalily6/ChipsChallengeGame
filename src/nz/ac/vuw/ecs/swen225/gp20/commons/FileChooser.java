package nz.ac.vuw.ecs.swen225.gp20.commons;

import nz.ac.vuw.ecs.swen225.gp20.application.GUI;

import javax.json.Json;
import javax.json.JsonArray;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * This is a common file chooser for the choosing of JSON files
 * This can be used to choose files for either recording, or to play a saved game
 *
 * @author callum mckay 300496765
 */
public class FileChooser {


    /**
     * @param g         The gui (parent component)
     * @param jsonArray The json array to save to the file
     * @param directory The directory to save
     */
    public static void saveToFile(GUI g, JsonArray jsonArray, String directory) {
        var pathString = Paths.get(".", directory).toAbsolutePath().normalize().toString();
        var fileChooser = new JFileChooser(pathString);
        var result = fileChooser.showOpenDialog(g);

        if (result == JFileChooser.APPROVE_OPTION) {
            var writer = new StringWriter();
            Json.createWriter(writer).write(jsonArray);
            try {
                var fileName = fileChooser.getSelectedFile().getName();
                if (!fileName.endsWith(".json")) {
                    fileName += ".json";
                }
                var bw = new BufferedWriter(new FileWriter(pathString + File.separator + fileName, StandardCharsets.UTF_8));
                bw.write(writer.toString());
                bw.close();
            } catch (IOException e) {
                throw new Error("Game was not able to be saved due to an exception");
            }
        }
    }

    /**
     * @param g         The gui to add this file chooser to
     * @param directory the directory to open with this file chooser
     * @return the json file that is found
     */
    public static File getJsonFileToLoad(GUI g, String directory) {
        var fileChooser = new JFileChooser(Paths.get(".", directory).toAbsolutePath().normalize().toString());
        fileChooser.setFileFilter(new FileNameExtensionFilter("json files only", "json"));
        var result = fileChooser.showOpenDialog(g);

        if (result == JFileChooser.APPROVE_OPTION) {
            File jsonFile = fileChooser.getSelectedFile();
            if (!jsonFile.getName().endsWith(".json")) return null;

            return jsonFile;
        }

        return null;
    }
}
