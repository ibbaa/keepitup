package net.ibbaa.keepitup.logging;

import java.util.List;

@FunctionalInterface
public interface IDumpSource {
    List<?> objectsToDump();
}
