package de.ibba.keepitup.logging;

import java.util.List;

@FunctionalInterface
public interface IDumpSource {
    List<?> objectsToDump();
}
