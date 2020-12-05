import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileInputStream;
import java.util.Scanner;
import java.lang.*;
import java.nio.charset.Charset;


public class IterativeClassifier {
    public static String nameBrief(String name) {
        if (name.contains(" ")) {
            return name.substring(0, name.indexOf(" "));
        } else {
            return name;
        }
    }
    public static void main(int iternumber, String loopfilename) throws IOException{
        double weight_u = 8; // The weight to neutraralize scores for users
        double weight_h = 20; // weight for the hashtags
        double liberalAdjustment = 1.0; //alpha value
        double beta = 0.5; // weight on the hashtaging behaviors
        double lastRoundWeight = 0; // the weight from the last iteration
        double credit_doubler = 1.5;
        boolean writeThisTime = true;
        
        //constants
        double theta_l = 0.25;  // retweeting credit threshold upperbound
        double theta_c = 0.75;  // retweeting credit threshold lowerbound
        double tau_l = 0.5; // hashtag credit threshold upperbound
        double tau_c = 0.5; // hashtag credit threshold lowerbound
        
        Scanner scan = new Scanner(System.in);
        int NUM_ITERATIONS = iternumber;
        String filePath = "./";
        
        String data_file = loopfilename;
        Path seTable = (new File(filePath + data_file)).toPath();
        
        Path liberalSeed = (new File(filePath + "lseed.txt")).toPath();
        Path conservativeSeed = (new File(filePath + "cseed.txt")).toPath();

        HashMap<String, Double> lastUserVals = new HashMap<String, Double>();  // to store the political scores of users
        HashMap<String, Double> workingUserVals = new HashMap<String, Double>(); // to store the current political credits
        HashMap<String, double[]> workingHashtagVals = new HashMap<String, double[]>(); // to store the hashtag credits

        HashMap<String, Integer> popularity = new HashMap<String, Integer>();

        ArrayList<String> seeds = new ArrayList<String>();

        String sourceUser = ""; // user name
        String userInProgress = "";
        String target = "";

        int nTweets = 0;

        double liberalPoints = 0;
        double conservePoints = 0;
        double hash_liberalPoints = 0;
        double hash_conservePoints = 0;
        double hash_totalPoints = 0;
        double convergenceMetric = 0;
        double average = 0;
        int n = 0;

        // add liberals seeds
        FileInputStream inputStream = null;
        Scanner sc = null;
        try {
            inputStream = new FileInputStream(liberalSeed.toString());
            sc = new Scanner(inputStream);
            while (sc.hasNextLine()) {
                String a = sc.nextLine();
                lastUserVals.put(a, 0.0);
                seeds.add(a);
            }
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }

        // add conservatives seeds
        inputStream = null;
        sc = null;
        try {
            inputStream = new FileInputStream(conservativeSeed.toString());
            sc = new Scanner(inputStream);
            while (sc.hasNextLine()) {
                String a = sc.nextLine();
                lastUserVals.put(a, 1.0);
                seeds.add(a);
            }
            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }

        inputStream = null;
        sc = null;
        try {

            inputStream = new FileInputStream(seTable.toString());
            sc = new Scanner(inputStream);
            while (sc.hasNextLine()) {
                String a = sc.nextLine();
                sourceUser = nameBrief(a.substring(0, a.indexOf(",")));

                if (!seeds.contains(sourceUser))
                    lastUserVals.put(sourceUser, null);
                sourceUser = a.substring(a.indexOf(",") + 1);

                if (sourceUser.indexOf(",") > 0) {
                    target = nameBrief(sourceUser.substring(0, sourceUser.indexOf(",")));
                }

                if (!seeds.contains(target)) {
                    lastUserVals.put(target, null);
                }
            }
            
            // note that Scanner suppresses exceptions
            if (sc.ioException() != null) {
                throw sc.ioException();
            }

        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (sc != null) {
                sc.close();
            }
        }

        System.out.println("========after initialization:");

        for (int i = 0; i < NUM_ITERATIONS; i++) { // number of iterations
            liberalPoints=0;
            conservePoints=0;
            hash_liberalPoints = 0;
            hash_conservePoints = 0;
            hash_totalPoints = 0;

            userInProgress = "";

            inputStream = null;
            sc = null;

            try {
                inputStream = new FileInputStream(seTable.toString());
                sc = new Scanner(inputStream);
                while (sc.hasNextLine()) {
                    String a = sc.nextLine();

                    if (a.indexOf("Source,Target,Weight")<0 && a.indexOf(",")>=0) {// if it is a data line
                        sourceUser = nameBrief(a.substring(0, a.indexOf(","))); // user name
                        target = a.substring(a.indexOf(",") + 1);
                        if (target.indexOf(",") > 0) {
                            nTweets = Integer.parseInt(target.substring(target.indexOf(",") + 1)); // number of times
                            target = nameBrief(target.substring(0, target.indexOf(","))); // target user name or hashtag
                        }
                        else {
                            nTweets = 0;
                        }

                        // if it is a hash line
                        if (a.contains("#")) {
                            if (!seeds.contains(target) && lastUserVals.get(sourceUser) != null) {
                                if (workingHashtagVals.get(target) != null) {
                                    hash_liberalPoints = workingHashtagVals.get(target)[0];
                                    hash_conservePoints = workingHashtagVals.get(target)[1];
                                    hash_totalPoints = workingHashtagVals.get(target)[2];
                                } else {
                                    hash_liberalPoints = 0;
                                    hash_conservePoints = 0;
                                    hash_totalPoints = 0;
                                }

                                if (lastUserVals.get(sourceUser) < 0.5) {
                                    hash_liberalPoints += lastUserVals.get(sourceUser) * nTweets * liberalAdjustment;
                                    hash_totalPoints += nTweets * liberalAdjustment;
                                } else {
                                    hash_conservePoints += lastUserVals.get(sourceUser) * nTweets;
                                    hash_totalPoints += nTweets;
                                }

                                workingHashtagVals.put(target, new double[]{hash_liberalPoints, hash_conservePoints, hash_totalPoints});
                            }
                        }

                        if (!seeds.contains(sourceUser)){
                            if (!sourceUser.equalsIgnoreCase(userInProgress)){
                                if (!seeds.contains(userInProgress) && (liberalPoints+conservePoints) > 0) { // if last user is not a seed user and got some points
                                    if(lastUserVals.get(userInProgress)!= null)
                                        workingUserVals.put(userInProgress, lastRoundWeight*lastUserVals.get(userInProgress)+(1-lastRoundWeight)*((conservePoints + weight_u) / ((liberalPoints+conservePoints) + (2 * weight_u))));
                                    else
                                        workingUserVals.put(userInProgress, ((conservePoints + weight_u) / (liberalPoints+conservePoints + (2 * weight_u))));
                                }

                                liberalPoints=0;
                                conservePoints=0;
                                userInProgress = sourceUser;

                            }
                            if (lastUserVals.get(target) != null) { // if the retweeted user has a score
                                if (lastUserVals.get(target) < theta_l) {
                                    liberalPoints += Math.abs(lastUserVals.get(target) - theta_l) * nTweets * liberalAdjustment * credit_doubler;
                                } else if (lastUserVals.get(target) >= theta_c){
                                    conservePoints += Math.abs(lastUserVals.get(target) - theta_c) * nTweets * credit_doubler;
                                }
                            }
                        }
                    }
                }
                if (sc.ioException() != null) {
                    throw sc.ioException();
                }
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (sc != null) {
                    sc.close();
                }
            }

            if (!seeds.contains(sourceUser) && (liberalPoints+conservePoints) > 0){
                if(lastUserVals.get(sourceUser)!= null)
                    workingUserVals.put(sourceUser, lastRoundWeight*lastUserVals.get(sourceUser)+(1-lastRoundWeight)*((conservePoints + weight_u) / ((liberalPoints+conservePoints) + (2 * weight_u))));
                else
                    workingUserVals.put(sourceUser, ((conservePoints + weight_u) / (liberalPoints+conservePoints + (2 * weight_u))));
            }
            for (String key : workingHashtagVals.keySet()) {
                workingUserVals.put(key, (workingHashtagVals.get(key)[0]+ workingHashtagVals.get(key)[1] + weight_h) / (workingHashtagVals.get(key)[2] + (2 * weight_h)));
            }
            workingHashtagVals.clear();

            convergenceMetric = 0;
            average = 0;
            n=0;
            for (String key : workingUserVals.keySet()) {
                if (workingUserVals.get(key) != null){
                    if(lastUserVals.get(key) != null) {
                        convergenceMetric += Math.abs(lastUserVals.get(key) - workingUserVals.get(key));
                    }else{
                        convergenceMetric += workingUserVals.get(key);
                    }
                    average += workingUserVals.get(key);
                    n++;
                }
                lastUserVals.put(key, workingUserVals.get(key));
            }
            convergenceMetric = convergenceMetric/n;
            average = average/n;

            System.out.println("average = " + average);
        }

        FileWriter w_u = new FileWriter(filePath + "Iterative-score-users.csv");
        FileWriter w_h = new FileWriter(filePath + "Iterative-score-hashtags.csv");
        for (String key : lastUserVals.keySet()) {
            if (lastUserVals.get(key) != null)
                if (key.contains("#"))
                    w_h.write(key + "," + lastUserVals.get(key) + System.lineSeparator());
                else
                    w_u.write(key + "," + lastUserVals.get(key) + System.lineSeparator());
        }
        
        w_u.close();
        w_h.close();

        w_u = new FileWriter(filePath + "popular.csv");
        
        for(String key : popularity.keySet()) {
            if(popularity.get(key)>500)
                w_u.write(key+ "," + popularity.get(key)+ System.lineSeparator());
        }
        
        w_u.close();

        if (writeThisTime) {
            w_u = new FileWriter(filePath + "IterativeLC-Conservative.csv");

            for (String key : lastUserVals.keySet()) {
                if (lastUserVals.get(key) != null) {
                    if (lastUserVals.get(key) > .80 && !seeds.contains(key))
                        w_u.write(key + "," + lastUserVals.get(key) + System.lineSeparator());
                }
            }

            w_u.close();

            w_u = new FileWriter(filePath + "IterativeLC-Liberal.csv");

            for (String key : lastUserVals.keySet()) {
                if (lastUserVals.get(key) != null) {
                    if (lastUserVals.get(key) < .20 && !seeds.contains(key))
                        w_u.write(key + "," + lastUserVals.get(key) + System.lineSeparator());
                }
            }

            w_u.close();
        }
    }
}
