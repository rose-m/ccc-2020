package io.coderose.ccc2020.challenges;

import io.coderose.ccc2020.utilities.FileReader;

public class Challenge1 extends AbstractChallenge {

    private FileReader reader;

    @Override
    public void run() {
        reader = newReader("xyz.txt");
    }

    public static void main(String[] args) {
        new Challenge1().run();
    }
}
