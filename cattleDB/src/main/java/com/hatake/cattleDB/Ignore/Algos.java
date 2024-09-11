package com.hatake.cattleDB.Ignore;

import java.util.HashMap;

public class Algos {
    public static void main(String[] args) {
            // Number of men and women
            int n = 3;

            // Men's preference lists
            int[][] menPreferences = {
                    {1, 2, 0}, // m1's preferences
                    {0, 1, 2}, // m2's preferences
                    {0, 2, 1}  // m3's preferences
            };

            // Women's preference lists
            int[][] womenPreferences = {
                    {2, 0, 1}, // w1's preferences
                    {0, 1, 2}, // w2's preferences
                    {1, 2, 0}  // w3's preferences
            };

            // Find and print stable matches
            stableMatching(n, menPreferences, womenPreferences);
//            printMatches(matches);





    }


    private static void stableMatching(
            int k, int[][] men, int[][] women) {

        var menMap = new HashMap<Integer, Integer>();
        var womenMap = new HashMap<Integer, Integer>();
        int n = 1 ;
        int w = 1;
        while ( n <= k){
            while ( w <= k){
                menMap.put(n,men[n][w]);
                w = w + 1;
            }
            n = n + 1;
        }

        System.out.println(menMap);
    }
}
