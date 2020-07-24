import java.io.IOException;
import java.io.File;
import java.nio.file.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Main {

    private static final String driveLocationPrefix = "/PATH/OF/BACKUP/DRIVE";
    private static final String localLocationPrefix = "/PATH/OF/LOCAL/SOURCE";

    private static final HashMap<Path, Path> toAdd = new HashMap<>();
    private static final HashMap<Path, Path> toReplace = new HashMap<>();

    private static List<File> listFolders(final Path entryPoint) throws IOException {
        return Files.walk(entryPoint).filter(Files::isDirectory).map(Path::toFile).collect(Collectors.toList());
    }

    private static List<File> listFiles(final Path entryPoint) throws IOException {
        return Files.walk(entryPoint).filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
    }

    private static void makeDirectories(final List<File> folders) {
        for (File folder : folders) {
            Path path = Paths.get(driveLocationPrefix + folder.toString().substring(localLocationPrefix.length()));
            if (Files.notExists(path)) path.toFile().mkdir();
        }
    }

    private static void compareFiles(final List<File> files, final int cutLength) {
        for (final File file : files) {
            final Path localPath = file.toPath();
            final Path remotePath = Paths.get(driveLocationPrefix + file.getPath().substring(cutLength));
            if (Files.notExists(remotePath)) {
                System.out.println("New File : " + file.getPath() + " -> " + remotePath.toString());
                toAdd.put(localPath, remotePath);
            }
            else {
                final long localVerTime = file.lastModified();
                final long remoteVerTime = remotePath.toFile().lastModified();
                if (localVerTime > remoteVerTime) {
                    System.out.println("New Version : " + file.getPath() + " -> " + remotePath.toString());
                    toReplace.put(localPath, remotePath);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException{
        System.out.println("Backup Process Initialized");

        final List<File> archiveDir = listFolders(Paths.get(localLocationPrefix + "/Documents/Archive"));
        final List<File> projectsDir = listFolders(Paths.get(localLocationPrefix + "/Documents/Project"));
        final List<File> studiesDir = listFolders(Paths.get(localLocationPrefix + "/Documents/Study"));
        final List<File> photoDir = listFolders(Paths.get(localLocationPrefix + "/Pictures"));
        System.out.println("Directories Loaded: " + (archiveDir.size() + projectsDir.size() + studiesDir.size()));

        makeDirectories(archiveDir);
        makeDirectories(projectsDir);
        makeDirectories(studiesDir);
        makeDirectories(photoDir);


        final List<File> archives = listFiles(Paths.get(localLocationPrefix + "/Documents/Archive"));
        final List<File> projects = listFiles(Paths.get(localLocationPrefix + "/Documents/Project"));
        final List<File> studies = listFiles(Paths.get(localLocationPrefix + "/Documents/Study"));
        final List<File> photos = listFiles(Paths.get(localLocationPrefix + "/Pictures"));
        System.out.println("Local Files Loaded: " + (archives.size() + projects.size() + studies.size()));

        compareFiles(archives, localLocationPrefix.length());
        compareFiles(projects, localLocationPrefix.length());
        compareFiles(studies, localLocationPrefix.length());
        compareFiles(photos, localLocationPrefix.length());
        System.out.println("Comparison Complete, Files To Add: " + toAdd.size());
        System.out.println("Comparison Complete, Files To Replace: " + toReplace.size());

        for (final Path localPath : toAdd.keySet()) {
            final Path remotePath = toAdd.get(localPath);

            System.out.print("Copying File : " + localPath.toString() + " -> " + remotePath.toString() + " | ");
            System.out.println(Files.copy(localPath, remotePath, StandardCopyOption.REPLACE_EXISTING).toFile().exists() ? "Success" : "Fail");
        }
        System.out.print(toAdd.isEmpty() ? "" : "Files Copied\n");

        for (final Path localPath : toReplace.keySet()) {
            final Path remotePath = toAdd.get(localPath);

            System.out.print("Updating File : " + localPath.toString() + " -> " + remotePath.toString() + " | ");
            System.out.println(Files.copy(localPath, remotePath, StandardCopyOption.REPLACE_EXISTING).toFile().exists() ? "Success" : "Fail");
        }
        System.out.print(toReplace.isEmpty() ? "" : "Files Updated\n");
        System.out.println("Backup Complete");
    }
}