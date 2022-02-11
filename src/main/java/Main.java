import java.io.FileReader;

public class Main {

    public static void main(String[] args) {
            try {
                FileReader reader = new FileReader("src/FolderTest/Test.java");

                Parser parser = new Parser();
                int temp = parser.classLines(reader);
                System.out.println(temp);

                parser.folderContent();
            }catch(Exception ex){
                return;
            }
    }
}
