package com.spring.cloud.moudle.study.demo.sortDemo;

/**
 * 冒泡排序
 *
 * @author wangmj
 * @since 2018/12/26
 */
public class BubbleSortDemo {


    public static void main(String[] args) {
        int arr[] = CommonSort.arr;
        for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr.length - 1 - i; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j + 1];
                    arr[j + 1] = arr[j];
                    arr[j] = temp;
                }
            }
        }

        CommonSort.printer(arr);

    }
}
