import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class SteamLocator {

    public static String getSteamPath() {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");

        if (os.contains("win")) {
            // Try default
            return "C:\\Program Files (x86)\\Steam";
            // (Optional: read registry for accuracy)
        } else if (os.contains("mac")) {
            return home + "/Library/Application Support/Steam";
        } else {
            return home + "/.steam/steam";
        }
    }

    public static List<String> getLibraryFolders(String steamPath) throws IOException {
        List<String> libraries = new ArrayList<>();
        Path libFile = Paths.get(steamPath, "steamapps", "libraryfolders.vdf");

        List<String> lines = Files.readAllLines(libFile);
        Pattern p = Pattern.compile("\"\\d+\"\\s*\"([^\"]+)\"");
        for (String line : lines) {
            Matcher m = p.matcher(line);
            if (m.find()) {
                libraries.add(m.group(1).replace("\\\\", "\\"));
            }
        }
        // Always add main steamapps folder
        libraries.add(steamPath);
        return libraries;
    }

    public static File findGamePath(int appId) throws IOException {
        String steamPath = getSteamPath();
        List<String> libraries = getLibraryFolders(steamPath);

        for (String lib : libraries) {
            Path manifest = Paths.get(lib, "steamapps", "appmanifest_" + appId + ".acf");
            if (Files.exists(manifest)) {
                // Parse the .acf to find installdir
                for (String line : Files.readAllLines(manifest)) {
                    if (line.contains("\"installdir\"")) {
                        String folder = line.split("\"")[3];
                        return new File(Paths.get(lib, "steamapps", "common", folder).toString());
                    }
                }
            }
        }
        return null; // Not found
    }
}
