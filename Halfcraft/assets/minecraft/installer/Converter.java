import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class Converter {

    // Change this to your ffmpeg path if it's not in PATH
    private static final String FFMPEG = "ffmpeg";

    public static void processFile(Path file, Path inputDir, Path outputDir, Window ref) throws IOException, InterruptedException {
        // Create relative output path
        Path relative = inputDir.relativize(file);
        Path outFile = outputDir.resolve(relative.toString().toLowerCase().replaceAll("'", "").replaceFirst("!", "_l").replaceFirst("\\.(wav|mp3)", ".ogg"));
        Files.createDirectories(outFile.getParent());

        // Wrangle paths to be minecraft compliant
        String outputFileName = outFile.toString();

        ProcessBuilder pb = new ProcessBuilder(
                FFMPEG, "-i", file.toString(),
                "-c:a", "libvorbis", outputFileName, "-y"
        );

        //pb.inheritIO(); // show ffmpeg output in console for debugging
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode == 0)
        {
            ref.updateOutput("Processed: " + file + " → " + outputFileName);
        }
        else
        {
            ref.updateOutput("Failed: " + file);
        }
    }

    public static void ProcessFolder(Path inputDir, Path outputDir, Window ref)
    {
        if (!Files.exists(inputDir) || !Files.isDirectory(inputDir))
        {
            ref.updateOutput("Input folder does not exist or is not a directory.");
            System.exit(1);
        }

        try
        {
            if (!Files.exists(outputDir))
            {
                Files.createDirectories(outputDir);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.exit(1);
        }

        try (Stream<Path> paths = Files.walk(inputDir))
        {
            paths.filter(Files::isRegularFile).forEach(file ->
            {
                if (!(file.toString().endsWith(".wav") || file.toString().endsWith(".mp3")))
                {
                    return;
                }
                try
                {
                    processFile(file, inputDir, outputDir, ref);
                }
                catch (IOException | InterruptedException e)
                {
                    System.err.println("Error processing file " + file + ": " + e.getMessage());
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
