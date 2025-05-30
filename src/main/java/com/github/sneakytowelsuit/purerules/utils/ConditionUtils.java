package com.github.sneakytowelsuit.purerules.utils;

import com.github.sneakytowelsuit.purerules.conditions.Condition;

import java.util.LinkedList;
import java.util.List;

public class ConditionUtils {

    public static <T extends Condition<?>> List<String> getIdPath(T condition, List<String> currentPath) {
        List<String> path;
        if (currentPath == null) {
           path = new LinkedList<>();
        }
        else {
            path = new LinkedList<>(currentPath);
        }
        path.add(condition.getId());
        return path;
    }
}
