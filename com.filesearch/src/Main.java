import java.util.ArrayList;
import java.nio.file.Path;


public class Main {
    public static void main(String[] args) {
        FileSearch file_search = new FileSearch();

        String root = "./";
        String[] exclusive_filenames= {};
        String[] exclusive_exts = {};
        String[] exclude_dirs = {};


        file_search.set_root(root);
        file_search.set_exclusive_filenames(exclusive_filenames);
        file_search.set_exclusive_extensions(exclusive_exts);
        file_search.set_exclude_directories(exclude_dirs);

        System.out.println("Searching.. ");
        ArrayList<Path> files = file_search.search_files();

        for (Path file : files) {
            System.out.println(file);
        }
    }
}
