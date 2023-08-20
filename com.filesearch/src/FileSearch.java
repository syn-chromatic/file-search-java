import java.io.File;
import java.util.Optional;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;



public class FileSearch {
    private Optional<Path> root;
    private HashSet<String> exclusiveFilenames;
    private HashSet<String> exclusiveFileStems;
    private HashSet<String> exclusiveExts;
    private HashSet<Path> excludeDirs;
    private Boolean quitDirectoryOnMatch;


    public FileSearch() {
        this.root = Optional.empty();
        this.exclusiveFilenames = new HashSet<String>();
        this.exclusiveFileStems = new HashSet<String>();
        this.exclusiveExts = new HashSet<String>();
        this.excludeDirs = new HashSet<Path>();
        this.quitDirectoryOnMatch = false;
    }


    public void setRoot(String root) {
        this.root = Optional.of(Paths.get(root));
    }

    public void setExclusiveFilenames(String[] filenames) {
        HashSet<String> exclusiveFilenames = new HashSet<String>();
        for (String filename : filenames) {
            filename = filename.toLowerCase();
            exclusiveFilenames.add(filename);
        }
        this.exclusiveFilenames = exclusiveFilenames;
    }

    public void setExclusiveFileStems(String[] fileStems) {
        HashSet<String> exclusiveFileStems = new HashSet<String>();
        for (String fileStem : fileStems) {
            fileStem = fileStem.toLowerCase();
            exclusiveFileStems.add(fileStem);
        }
        this.exclusiveFileStems = exclusiveFileStems;
    }

    public void setExclusiveExtensions(String[] extensions) {
        HashSet<String> exclusiveExts = new HashSet<String>();
        for (String extension : extensions) {
            extension = formatExtension(extension);
            exclusiveExts.add(extension);
        }
        this.exclusiveExts = exclusiveExts;
    }

    public void setExcludeDirectories(String[] dirs) {
        HashSet<Path> excludeDirs = new HashSet<Path>();
        for (String dir : dirs) {
            excludeDirs.add(Paths.get(dir));
        }
        this.excludeDirs = excludeDirs;
    }

    public void setQuitDirectoryOnMatch(Boolean state) {
        this.quitDirectoryOnMatch = state;
    }

    public HashSet<Path> searchFiles() {
        HashSet<Path> files = new HashSet<Path>();
        LinkedList<Path> queue = new LinkedList<Path>();

        Path root = this.getRootPath();
        queue.addLast(root);

        while (!queue.isEmpty()) {
            Path dir = queue.poll();
            search(dir, files, queue);
        }
        return files;
    }

    private String formatExtension(String ext) {
        ext = ext.trim().toLowerCase();
        if (!ext.isEmpty() && !ext.startsWith(".")) {
            ext = "." + ext;
        }
        return ext;
    }

    private Path getAbsPath() {
        return Paths.get("").toAbsolutePath();
    }

    private Path getRootPath() {
        if (this.root.isPresent()) {
            return this.root.get();
        }
        return this.getAbsPath();
    }


    private Optional<Path> getCanonicalPath(Path path) {
        try {
            File file = path.toFile();
            Path path_canonical = Path.of(file.getCanonicalPath());
            return Optional.of(path_canonical);
        } catch (IOException e) {
            String str = String.format("Path Inaccessible: [%s]\n\n", path);
            System.out.println(str);
            return Optional.empty();
        }
    }


    private Optional<DirectoryStream<Path>> getDirectoryEntries(Path root) {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(root);
            return Optional.of(stream);
        } catch (IOException e) {
            String str = String.format("Path Inaccessible: [%s]\n\n", root);
            System.out.println(str);
            return Optional.empty();
        }
    }

    private Boolean isSameDirectory(Path path, Path dir) {
        if (Files.exists(dir)) {
            while (path != path.getRoot()) {
                if (path.equals(dir)) {
                    return true;
                }
                path = path.getParent();
            }
        }
        return false;
    }



    private String getFileExtension(Path path) {
        String file_name = path.getFileName().toString();
        int ext_idx = file_name.lastIndexOf('.');

        if (ext_idx > 0 && ext_idx < file_name.length() - 1) {
            return file_name.substring(ext_idx);
        }
        return "";
    }

    private String getFileStem(Path path) {
        String fileName = path.getFileName().toString();
        int extIdx = fileName.lastIndexOf('.');

        if (extIdx > 0) {
            return fileName.substring(0, extIdx);
        }
        return fileName;
    }

    private Boolean getFilterValidation(Path path) {
        Boolean isExclusiveFilename = this.isExclusiveFilename(path);
        Boolean isExclusiveFileStem = this.isExclusiveFileStem(path);
        Boolean isExclusiveExtension = this.isExclusiveExtension(path);

        Boolean filterValidation =
                isExclusiveFilename && isExclusiveFileStem && isExclusiveExtension;
        return filterValidation;
    }

    private Boolean isExclusiveFilename(Path path) {
        if (this.exclusiveFilenames.isEmpty()) {
            return true;
        }

        String filename = path.getFileName().toString();
        filename = filename.toLowerCase();

        if (this.exclusiveFilenames.contains(filename)) {
            return true;
        }
        return false;

    }

    private Boolean isExclusiveFileStem(Path path) {
        if (this.exclusiveFileStems.isEmpty()) {
            return true;
        }

        String fileStem = this.getFileStem(path);
        fileStem = fileStem.toLowerCase();

        if (this.exclusiveFileStems.contains(fileStem)) {
            return true;
        }

        return false;

    }

    private Boolean isExclusiveExtension(Path path) {
        if (this.exclusiveExts.isEmpty()) {
            return true;
        }

        String fileExt = this.getFileExtension(path);
        fileExt = fileExt.toLowerCase();
        fileExt = this.formatExtension(fileExt);

        if (this.exclusiveExts.contains(fileExt)) {
            return true;
        }
        return false;
    }

    private Boolean isExcludedDirectory(Path path) {
        if (this.excludeDirs.isEmpty()) {
            return false;
        }

        for (Path dir : this.excludeDirs) {
            boolean is_same_directory = this.isSameDirectory(path, dir);
            if (is_same_directory) {
                return true;
            }
        }
        return false;
    }

    private Boolean handleFile(Path path, HashSet<Path> files) {
        Boolean filterValidation = this.getFilterValidation(path);

        if (!files.contains(path) && filterValidation) {
            files.add(path);
            return true;
        }
        return false;
    }

    private void handleEntry(Path entry, HashSet<Path> files,
            LinkedList<Path> additional_directories) {
        File file = entry.toFile();
        if (file.isFile()) {
            Boolean is_match = this.handleFile(entry, files);
            if (is_match && this.quitDirectoryOnMatch) {
                return;
            }
        } else if (file.isDirectory()) {
            additional_directories.addLast(entry);
        }
    }

    private void walker(DirectoryStream<Path> entries, HashSet<Path> files,
            LinkedList<Path> queue) {

        LinkedList<Path> additional_directories = new LinkedList<Path>();
        for (Path entry : entries) {
            this.handleEntry(entry, files, additional_directories);
        }

        queue.addAll(additional_directories);
    }

    private void search(Path root, HashSet<Path> files, LinkedList<Path> queue) {
        Optional<Path> root_canonical_op = this.getCanonicalPath(root);
        if (root_canonical_op.isPresent()) {
            Path root_canonical = root_canonical_op.get();
            if (this.isExcludedDirectory(root_canonical)) {
                return;
            }

            Optional<DirectoryStream<Path>> entries_op = this.getDirectoryEntries(root_canonical);
            if (entries_op.isPresent()) {
                DirectoryStream<Path> entries = entries_op.get();
                this.walker(entries, files, queue);
            }
        }
    }
}
