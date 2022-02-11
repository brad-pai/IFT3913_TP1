import com.github.javaparser.JavaParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Parser {

    /**
     * Counts the number of lines in a file
     *
     * @param input file
     * @return number of lines excluding empty lines
     * @throws IOException
     */
    public int classLines(FileReader input) throws IOException {
        BufferedReader br = new BufferedReader(input);
        int lines = 0;
        String currentLine;
        while ((currentLine = br.readLine()) != null){
            if(currentLine.length() > 0){
                lines++;
            }
        }
        input.close();
        return lines;
    }

    /**
     * List all files and directories in specified root directory
     */
    public void folderContent(){
        File folder = new File("src/FolderTest");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++){
            if (listOfFiles[i].isFile()){
                System.out.println("File " + listOfFiles[i].getName());
            }else if (listOfFiles[i].isDirectory()){
                System.out.println("Directory " + listOfFiles[i].getName());
            }
        }
    }
}

}
