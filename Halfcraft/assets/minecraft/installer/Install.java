import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Optional;
import javax.swing.*;

public class Install
{
    public File hlLocation;
    public File hcLocation;
    public File projectDirectory;
    public String FolderName = "Halfcraft";
    public File defaultLocation = new File(System.getProperty("user.dir"));

    public Install() throws IOException
    {
        hlLocation = SteamLocator.findGamePath(70);
        hcLocation = ResourcePackLocator.getResourcePackDir();

        var window = new Window(this, 
        (actionEvent) -> 
        {
            hlLocation = Optional.ofNullable(Selector(hlLocation)).orElse(defaultLocation);
        },
        (actionEvent) -> 
        {
            var result = Selector(hcLocation);
            if (result == null)
            {
                return;
            }
            hcLocation = new File(result, FolderName);
        }
        ,
        (actionEvent) -> 
        {

        }
        ,
        (actionEvent) -> 
        {
            projectDirectory = defaultLocation.getParentFile().getParentFile().getParentFile();

            if (!hcLocation.exists())
            {
                hcLocation.mkdirs();
            }
        });
    }

    public static void main(String[] args) throws IOException
    {
        var install = new Install();
    }

    public static void copyDirectory(Path source, Path target) throws IOException
    {
            // Create target directory if it doesn't exist
            if (!Files.exists(target))
            {
                Files.createDirectories(target);
            }

            // Copy files and subdirectories
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(source))
            {
                for (Path entry : stream)
                {
                    Path newTarget = target.resolve(source.relativize(entry));
                    if (Files.isDirectory(entry))
                    {
                        copyDirectory(entry, newTarget); // Recursive call for subdirectories
                    }
                    else
                    {
                        Files.copy(entry, newTarget, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
    }

    public static File Selector(File start)
    {
        JFileChooser chooser = new JFileChooser(start);

        // Set it to only allow directory selection
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        // Show the dialog
        int result = chooser.showOpenDialog(null);

        // Check if the user selected a directory
        if (result == JFileChooser.APPROVE_OPTION)
        {
            File selectedDir = chooser.getSelectedFile();
            System.out.println("Selected directory: " + selectedDir.getAbsolutePath());
        }
        else
        {
            System.out.println("No directory selected.");
            return null;
        }
        return chooser.getSelectedFile();
    }


}
