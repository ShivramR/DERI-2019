
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.*;
import java.util.List;
import java.io.FileReader;
import java.nio.file.*;
import java.lang.*;

public class Loop_run {
    public static void main(String[] args) throws IOException {
        int num_loop;
        int start_year;
        int num_iterations;
        List array = new ArrayList();
        String base_path = new File("").getAbsolutePath();

        Path config = new File("config.txt").toPath();
        FileInputStream inputStream = new FileInputStream(config.toString());
        Scanner sc = new Scanner(inputStream);
        for(int h=0; sc.hasNextLine(); h++){
            String a = sc.nextLine();
            array.add(a);
        }

        inputStream.close();
        sc.close();

        ProcessBuilder q = new ProcessBuilder(new String[] {"python", base_path.concat("/data_fixer.py")});

        Process proc = q.start();

        try {
            Thread.sleep(45000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        num_loop = Integer.parseInt(array.get(0).toString());
        start_year = Integer.parseInt(array.get(1).toString());
        num_iterations = Integer.parseInt(array.get(2).toString());

        System.out.println("Number of years: " + num_loop);
        System.out.println("Start year: " + start_year);

        for (int i = 0; i < num_loop; i++) {
            System.out.println(start_year+i);
            String subdirectory = String.valueOf(start_year+i);
            File file = new File(base_path, subdirectory);
            file.mkdir();
            try {
                IterativeClassifier.main(num_iterations, array.get(i+3).toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Path path1 = Paths.get("Iterative-score-users.csv");
            Path path1final = Paths.get(subdirectory.concat("/Iterative-score-users.csv"));

            Path path2 = Paths.get("Iterative-score-hashtags.csv");
            Path path2final = Paths.get(subdirectory.concat("/Iterative-score-hashtags.csv"));

            Path path3 = Paths.get("IterativeLC-Conservative.csv");
            Path path3final = Paths.get(subdirectory.concat("/IterativeLC-Conservative.csv"));

            Path path4 = Paths.get("IterativeLC-Liberal.csv");
            Path path4final = Paths.get(subdirectory.concat("/IterativeLC-Liberal.csv"));

            Path path5 = Paths.get("popular.csv");
            Path path5final = Paths.get(subdirectory.concat("/popular.csv"));

            try {
                Files.move(path1, path1final, StandardCopyOption.REPLACE_EXISTING);
                Files.move(path2, path2final, StandardCopyOption.REPLACE_EXISTING);
                Files.move(path3, path3final, StandardCopyOption.REPLACE_EXISTING);
                Files.move(path4, path4final, StandardCopyOption.REPLACE_EXISTING);
                Files.move(path5, path5final, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {}
        }

        ProcessBuilder p = new ProcessBuilder(new String[] {"python", base_path.concat("/common.py")});

        Process proc = p.start();
    }
}
