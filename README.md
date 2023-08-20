# File Search Java

## `➢` Information
A Java utility to search files with various filters such as:
* Exclusive Filenames
* Exclusive File Stems
* Exclusive Extensions
* Exclude Directories

The algorithm recursively searches through all of the directories from the specified root.

## `➢` Example Usage
```java
FileSearch fileSearch = new FileSearch();

// Set the root directory for the file search
String root = "./";
fileSearch.setRoot(root);

// Specify filenames to exclusively search for
String[] exclusiveFilenames = {"README"};
fileSearch.setExclusiveFilenames(exclusiveFilenames);

// Specify file stems to exclusively search for
String[] exclusiveFileStems = {"README"};
fileSearch.setExclusiveFileStems(exclusiveFileStems);

// Specify extensions to exclusively search for
String[] exclusiveExts = {"md"};
fileSearch.setExclusiveExtensions(exclusiveExts);

// Specify directories to exclude from the search
// This excludes the path and not the directory name
String[] excludeDirs = {"./excluded_dir"};
fileSearch.setExcludeDirectories(excludeDirs);

// Perform the file search and get the results
HashSet<Path> files = fileSearch.searchFiles();
```
