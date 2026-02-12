package com.yawl.http.model;

import com.yawl.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record PathPattern(List<PathSegment> segments) {

    public static PathPattern parse(String path) {
        return splitInSegments(path)
                .map(segment -> {
                    if (segment.matches("\\{\\w+}")) {
                        var cleaned = segment.substring(1, segment.length() - 1);
                        return new PathSegment.Capture(cleaned);
                    }
                    return new PathSegment.Literal(segment);
                })
                .collect(Collectors.collectingAndThen(Collectors.toList(), PathPattern::new));
    }

    public boolean matches(String requestPath) {
        // split requestPath into segments
        var requestPathSplit = splitInSegments(requestPath).toList();

        // return false if segment count differs
        if (requestPathSplit.size() != segments.size()) {
            return false;
        }

        // compare segment-by-segment using PathSegment.matches()
        for (int i = 0; i < segments.size(); i++) {
            var segment = segments.get(i);
            var requestPathSegment = requestPathSplit.get(i);

            if (!segment.matches(requestPathSegment)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Collect Capture segment values into a map
     * e.g. "/users/42" against "/users/{id}" returns {"id": "42"}
     *
     * @param requestPath the path to capture the values
     * @return map of the capture parameters
     */
    public Map<String, String> extractPathVariables(String requestPath) {
        // split requestPath into segments
        var requestPathSplit = splitInSegments(requestPath).toList();

        // return empty if segment count differs
        if (requestPathSplit.size() != segments.size()) {
            return Map.of();
        }

        // capture all the values
        var pathVariables = new HashMap<String, String>(segments.size());
        for (int i = 0; i < segments.size(); i++) {
            var segment = segments.get(i);

            if (segment instanceof PathSegment.Capture(String name)) {
                pathVariables.put(name, requestPathSplit.get(i));
            }
        }

        return pathVariables;
    }

    public List<String> captureNames() {
        return segments.stream()
                .filter(segment -> segment instanceof PathSegment.Capture)
                .map(segment -> (PathSegment.Capture) segment)
                .map(PathSegment.Capture::name)
                .toList();
    }


    /**
     * Splits the path by "/" in segments.
     * This method removes empty segments to deal with the leading or trailing slash.
     * Also empty capture parameters will be deleted like this.
     *
     * @param requestPath the path to be split
     * @return a stream of non-empty path segments
     */
    private static Stream<String> splitInSegments(String requestPath) {
        //remove empty segment, like the leading one or in case of an empty path param
        // eg. /users//profile would become {"", "users", "", profile}
        return Pattern.compile("/").splitAsStream(requestPath)
                .filter(StringUtils::hasText);
    }
}
