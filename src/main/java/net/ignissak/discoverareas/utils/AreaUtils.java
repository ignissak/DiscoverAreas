package net.ignissak.discoverareas.utils;

import net.ignissak.discoverareas.DiscoverAreasPlugin;

import java.util.Optional;
import java.util.stream.Stream;

public class AreaUtils {

    public static int getNextAreaId() {
        final Stream<Integer> integerStream = DiscoverAreasPlugin.getAreasFile().getKeys(false).stream().sorted().map(Integer::parseInt);
        final Optional<Integer> first = integerStream.skip(integerStream.count() - 1).findFirst();
        return first.map(integer -> integer + 1).orElse(0);
    }
}
