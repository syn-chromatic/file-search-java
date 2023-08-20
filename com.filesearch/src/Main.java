import java.nio.file.Path;
import java.util.HashSet;


public class Main {
    public static void main(String[] args) {
        FileSearch fileSearch = new FileSearch();

        String root = "./";
        String[] exclusiveFilenames = {};
        String[] exclusiveFileStems = {};
        String[] exclusiveExts = {};
        String[] excludeDirs = {};


        fileSearch.setRoot(root);
        fileSearch.setExclusiveFilenames(exclusiveFilenames);
        fileSearch.setExclusiveFileStems(exclusiveFileStems);
        fileSearch.setExclusiveExtensions(exclusiveExts);
        fileSearch.setExcludeDirectories(excludeDirs);

        HashSet<Path> files = fileSearch.searchFiles();

        for (Path file : files) {
            System.out.println(file);
        }
    }
}
