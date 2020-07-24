# Simple Java Backup Script

## Application Description 
This is a simple java-based command line backup script.

The main file handling libraries used are java 7's java.io.File, java 8's java.nio.Files.

## Implementation

The script first confirms all the directories on the local source and backup drive match, if not, the script mirrors the directory structure of the local source and creates an identically named directory on the backup drive. Then the script compares each file on the local source to the ones on the drive, marking them as more updated, new, or identical. New files are copied to the backup drive. Followed by the more recently modified files which will replace the old ones on the drive.

## Usage Instructions
1. Before build the application, you would want to change the file paths to suit your own usage
2. Run the following command to compile the java file
```bash
cd PATH_TO_JAVA_FILE
javac Backup.java
```
3. Run the following command to run the script
```bash 
java Backup
```

## Known Bugs
1. Comparing old files and new files result in a java error.

## Todos
1. Fix update part of the script
2. Make a javafx GUI version of this script that allows backup folder selection

## Version Log
1. Version 1.0
    - Initial commit to repo
