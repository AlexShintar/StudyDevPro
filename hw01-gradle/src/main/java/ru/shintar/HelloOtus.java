package ru.shintar;

import com.google.common.base.Joiner;

public class HelloOtus {

    public static void main(String... args) {

        String[] words = {"Hello", "otus"};

        String message = Joiner.on(" ").join(words);

        System.out.println(message);
    }
}
