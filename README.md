# File Search Java

## `➢` Information
A Java utility to search files with various filters such as:
* Exclusive Filenames
* Exclusive Extensions
* Exclude Directories

The algorithm recursively searches through all of the directories from the specified root.

## `➢` Example Usage
```java
FileSearch file_search = new FileSearch();

// Set the root directory for the file search
String root = "./";
file_search.setRoot(root);

// Below examples are optional

// Specify filenames to exclusively search for
String[] exclusive_filenames = {"README"};
file_search.setExclusiveFilenames(exclusive_filenames);

// Specify extensions to exclusively search for
String[] exclusive_exts = {"md"};
file_search.setExclusiveExtensions(exclusive_exts);

// Specify directories to exclude from the search
// This excludes the path and not the directory name
String[] exclude_dirs = {"./excluded_dir"};
file_search.setExcludeDirectories(exclude_dirs);

// Perform the file search and get the result
ArrayList<Path> files = file_search.searchFiles();
```
