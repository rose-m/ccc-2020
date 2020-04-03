package io.coderose.ccc2020.challenges;

import io.coderose.ccc2020.utilities.FileReader;

import javax.annotation.Nonnull;

public abstract class AbstractChallenge implements Runnable {

    @Nonnull
    protected final FileReader newReader(@Nonnull String filename) {
        final String challenge = getClass().getSimpleName().toLowerCase();
        return FileReader.forResource(challenge, filename);
    }

}
