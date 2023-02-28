package com.epiphany.context;

import java.util.Optional;

public interface Context {

    <Type> Optional<Type> get(final Class<Type> type);

}
