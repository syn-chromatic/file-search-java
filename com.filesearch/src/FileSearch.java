import java.io.File;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Optional;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;


public class FileSearch {
    Optional<Path> root;
    ArrayList<String> exclusive_filenames;
    ArrayList<String> exclusive_exts;
    ArrayList<Path> exclude_dirs;


    public FileSearch() {
        this.root = Optional.empty();
        this.exclusive_filenames = new ArrayList<>();
        this.exclusive_exts = new ArrayList<>();
        this.exclude_dirs = new ArrayList<>();
    }

    public void setRoot(String root) {
        this.root = Optional.of(Paths.get(root));
    }

    public void setExclusiveFilenames(String[] filenames) {
        List<String> filenames_list = Arrays.asList(filenames);
        this.exclusive_filenames = new ArrayList<>(filenames_list);
    }

    public void setExclusiveExtensions(String[] exts) {
        ArrayList<String> exclusive_exts = new ArrayList<>();

        for (String ext : exts) {
            ext = this.formatExtension(ext);
            exclusive_exts.add(ext);
        }
        this.exclusive_exts = exclusive_exts;
    }

    public void setExcludeDirectories(String[] dirs) {
        List<String> dir_list = Arrays.asList(dirs);
        ArrayList<String> dir_array = new ArrayList<>(dir_list);

        ArrayList<Path> exclude_dirs = new ArrayList<>();
        exclude_dirs.ensureCapacity(dir_array.size());

        for (String dir : dir_array) {
            exclude_dirs.add(Paths.get(dir));
        }
        this.exclude_dirs = exclude_dirs;
    }

    public ArrayList<Path> searchFiles() {
        ArrayList<Path> roots = new ArrayList<>();
        ArrayList<Path> files = new ArrayList<>();

        Path root = this.getRootPath();
        this.search(root, roots, files);
        return files;
    }

    private String formatExtension(String ext) {
        ext = ext.strip();
        ext = ext.toLowerCase();

        if (!ext.isEmpty() && ext.charAt(0) != '.') {
            ext = "." + ext;
        }
        return ext;
    }

    private String getFileStem(Path path) {
        String file_name = path.getFileName().toString();
        int ext_idx = file_name.lastIndexOf('.');

        if (ext_idx > 0 && ext_idx < file_name.length() - 1) {
            return file_name.substring(0, ext_idx);
        }
        return "";
    }

    private String getFileExtension(Path path) {
        String file_name = path.getFileName().toString();
        int ext_idx = file_name.lastIndexOf('.');

        if (ext_idx > 0 && ext_idx < file_name.length() - 1) {
            return file_name.substring(ext_idx);
        }
        return "";
    }

    private boolean getFilterValidation(Path path) {
        boolean is_exclusive_filename = this.isExclusiveFilename(path);
        boolean is_exclusive_extension = this.isExclusiveExtension(path);
        boolean filter_validation = is_exclusive_filename && is_exclusive_extension;
        return filter_validation;
    }

    private Optional<Path> getCanonicalPath(Path path) {
        try {
            File file = path.toFile();
            Path path_canonical = Path.of(file.getCanonicalPath());
            return Optional.of(path_canonical);
        } catch (IOException e) {
            System.out.println("Path Inaccessible: " + path + "\n\n");
            return Optional.empty();
        }
    }

    private Optional<DirectoryStream<Path>> getDirectoryEntries(Path root) {
        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(root);
            return Optional.of(stream);
        } catch (IOException e) {
            System.out.println("Path Inaccessible: " + root + "\n\n");
            return Optional.empty();
        }
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

    private boolean isSameDirectory(Path path, Path dir) {
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

    private boolean isExclusiveFilename(Path path) {
        if (this.exclusive_filenames.isEmpty()) {
            return true;
        }

        String file_stem = this.getFileStem(path);

        for (String file_name : this.exclusive_filenames) {
            if (file_name.equals(file_stem)) {
                return true;
            }
        }
        return false;
    }

    private boolean isExclusiveExtension(Path path) {
        if (this.exclusive_exts.isEmpty()) {
            return true;
        }

        for (String ext : this.exclusive_exts) {
            ext = this.formatExtension(ext);
            String file_ext = this.getFileExtension(path);
            if (file_ext.equals(ext)) {
                return true;
            }
        }
        return false;
    }

    private boolean isExcludedDirectory(Path path) {
        if (this.exclude_dirs.isEmpty()) {
            return false;
        }

        for (Path dir : this.exclude_dirs) {
            boolean is_same_directory = this.isSameDirectory(path, dir);
            if (is_same_directory) {
                return true;
            }
        }
        return false;


    }

    private void handleFile(Path path, ArrayList<Path> files) {
        boolean filter_validation = this.getFilterValidation(path);

        if (!files.contains(path) && filter_validation) {
            files.add(path);
        }
    }

    private void handleFolder(Path path, ArrayList<Path> roots, ArrayList<Path> files) {
        if (!roots.contains(path)) {
            roots.add(path);
            this.search(path, roots, files);
        }
    }

    private void walker(DirectoryStream<Path> entries, ArrayList<Path> roots,
            ArrayList<Path> files) {
        for (Path entry : entries) {
            Optional<Path> op_path = this.getCanonicalPath(entry);
            if (op_path.isPresent()) {
                Path path = op_path.get();

                if (Files.isRegularFile(path)) {
                    this.handleFile(path, files);
                } else if (Files.isDirectory(path)) {
                    this.handleFolder(path, roots, files);
                }
            }

        }
    }

    private void search(Path root, ArrayList<Path> roots, ArrayList<Path> files) {
        Optional<Path> root_canonical_op = this.getCanonicalPath(root);
        if (root_canonical_op.isPresent()) {
            Path root_canonical = root_canonical_op.get();

            if (this.isExcludedDirectory(root_canonical)) {
                return;
            }

            Optional<DirectoryStream<Path>> entries_op = this.getDirectoryEntries(root_canonical);
            if (entries_op.isPresent()) {
                DirectoryStream<Path> entries = entries_op.get();
                this.walker(entries, roots, files);
            }
        }
    }
}
