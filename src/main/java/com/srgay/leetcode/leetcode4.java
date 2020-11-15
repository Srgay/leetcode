package com.srgay.leetcode;

import java.util.Arrays;

public class leetcode4 {
    public static void main(String[] args) {
        new Solution4().findMedianSortedArrays(new int[]{1,2},new int[]{3,4});
    }
}
class Solution4 {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {
        int[] c= new int[nums1.length+nums2.length];
        System.arraycopy(nums1, 0, c, 0, nums1.length);
        System.arraycopy(nums2, 0, c, nums1.length, nums2.length);
        Arrays.sort(c);
        int n;
        if ((n=nums1.length+nums2.length)%2==0){
            return (double)(c[n]+c[n-1])/2;
        }else {
            return (double)c[n];
        }
    }
}
