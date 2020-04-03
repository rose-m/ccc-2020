package io.coderose.ccc2020.utilities;

import javax.annotation.Nonnull;

public interface LineExtractor<T> {

    @Nonnull
    T extract(@Nonnull FileReader reader);

}
