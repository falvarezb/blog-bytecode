package fjab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;

public class Main {

    static String readFile() throws Exception{
        try(BufferedReader br = new BufferedReader(new FileReader("src/main/resources/testCase"))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString();
        }
    }

    public static void main(String[] args) throws Exception{
//        int[] arr = {1,5,3,4,2};
//        System.out.println(pairs(2, arr));

//        List<Integer> arr = new ArrayList<>(Arrays.asList(0,0,2,0));
//        System.out.println(balancedSums(arr));

        //int[] arr = {1,1,0,0,0,1,1,1,1,1};
        //int[] arr = {0,1,1,1,1,0};
        //int[] arr = {0, 1, 0, 0, 0, 1, 1, 1, 1, 1};

        int[] arr = new int[100000];
        String[] n = readFile().split(",");
        for(int j=0; j<n.length-1; j++){
            try{
                arr[j] = Integer.parseInt(n[j]);
            }
            catch (Exception ex){
                System.out.println(j);
                throw ex;
            }
        }
        arr[99999] = 1;

        //int[] arr = Arrays.stream(readFile().split(",")).mapToInt(i -> Integer.parseInt(i)).toArray();
        System.out.println(pylons(4,arr));
    }

    static int pairs(int k, int[] arr) {

        Arrays.sort(arr);
        int counter = 0;
        for(int i=0; i<arr.length-1; i++){
            for(int j=i; j<arr.length; j++){
                if(arr[j] - arr[i] == k) {
                    counter++;
                    break;
                }
                else if(arr[j] - arr[i] > k) {
                    break;
                }
            }
        }
        return counter;
    }

    static String balancedSums(List<Integer> arr) {

        int total = arr.stream().mapToInt(i -> i).sum();
        int leftSum = 0;
        int rightSum = total;

        //first element
        rightSum -= arr.get(0);
        if(leftSum == rightSum) return "YES";

        for(int j=1; j<arr.size(); j++){
            leftSum += arr.get(j-1);
            rightSum -= arr.get(j);
            if(leftSum == rightSum) return "YES";
        }
        return "NO";
    }

    static int divisibleSumPairs(int n, int k, int[] ar) {

        int counter = 0;
        for(int i=0; i<n-1; i++){
            for(int j=i+1; j<n; j++){
                if((ar[i] + ar[j])%k == 0) counter++;
            }
        }
        return counter;
    }

    static int pylons(int k, int[] arr) {

        int lastPlantPosition = -k;
        int leftMostTownInTheDark = arr.length;
        int lastCandidateForPlant = arr.length;
        int counter = 0;

        for(int i=0; i<arr.length; i++){

            boolean isCurrentTownIlluminated = i - lastPlantPosition < k;
            boolean canBuildPlantInCurrentTown = arr[i] == 1;
            boolean canBuildPlantInPreviousTown = lastCandidateForPlant < i;
            boolean isLastTown = i == arr.length-1;

            if(!isCurrentTownIlluminated) { //current town is not illuminated
                if(i < leftMostTownInTheDark) //is current town the left most town in the dark
                    leftMostTownInTheDark = i;
            }

            int distanceToLeftMostTownInDark = i - leftMostTownInTheDark;

            if(canBuildPlantInCurrentTown && (distanceToLeftMostTownInDark == k-1 || isLastTown)){
                lastPlantPosition = i;
                leftMostTownInTheDark = lastPlantPosition + k;
                lastCandidateForPlant = arr.length;
                counter++;
                System.out.println(lastPlantPosition);
            }
            else if(distanceToLeftMostTownInDark == k-1 || isLastTown){
                if(canBuildPlantInPreviousTown){
                    lastPlantPosition = lastCandidateForPlant;
                    lastCandidateForPlant = arr.length;
                    leftMostTownInTheDark = lastPlantPosition + k;
                    counter++;
                    System.out.println(lastPlantPosition);
                }
                else if (!isCurrentTownIlluminated){
                    return -1;
                }
            }
            else if(canBuildPlantInCurrentTown){
                lastCandidateForPlant = i;
            }
        }
        return counter;
    }
}
