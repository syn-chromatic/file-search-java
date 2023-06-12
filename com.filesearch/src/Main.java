import java.util.ArrayList;
import java.nio.file.Path;


public class Main {
    public static void main(String[] args) {
        FileSearch file_search = new FileSearch();

        String root = "./";
        String[] exclusive_filenames= {};
        String[] exclusive_exts = {};
        String[] exclude_dirs = {};


        file_search.setRoot(root);
        file_search.setExclusiveFilenames(exclusive_filenames);
        file_search.setExclusiveExtensions(exclusive_exts);
        file_search.setExcludeDirectories(exclude_dirs);

        System.out.println("Searching.. ");
        ArrayList<Path> files = file_search.searchFiles();

        for (Path file : files) {
            System.out.println(file);
        }
    }
}
