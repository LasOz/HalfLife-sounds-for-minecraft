import java.io.File;

public class ResourcePackLocator
{
    public static File getResourcePackDir()
    {
        String os = System.getProperty("os.name").toLowerCase();
        String home = System.getProperty("user.home");

        if (os.contains("win"))
        {
            return new File(System.getenv("APPDATA"), ".minecraft/resourcepacks");
        }
        else if (os.contains("mac"))
        {
            return new File(home, "Library/Application Support/minecraft/resourcepacks");
        }
        else
        {
            return new File(home, ".minecraft/resourcepacks"); // Linux, BSD, etc.
        }
    }
}