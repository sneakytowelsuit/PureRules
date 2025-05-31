package com.github.sneakytowelsuit.purerules.utils;

import com.github.sneakytowelsuit.purerules.conditions.Condition;

import java.util.ArrayList;
import java.util.List;

public class ConditionUtils {

    public static <T extends Condition<?>> List<String> getIdPath(T condition, List<String> currentPath) {
        List<String> path;
        if (currentPath == null) {
           path = new ArrayList<>();
        }
        else {
            path = new ArrayList<>(currentPath);
        }
        path.add(condition.getId());
        return path;
    }
}
