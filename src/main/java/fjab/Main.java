package fjab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
//        int[] arr = {1,5,3,4,2};
//        System.out.println(pairs(2, arr));

        List<Integer> arr = new ArrayList<>(Arrays.asList(0,0,2,0));
        System.out.println(balancedSums(arr));
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
}
