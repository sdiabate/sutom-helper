package com.sdi.sutom.helper;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.*;

import static java.util.List.of;
import static java.util.Objects.requireNonNull;

public class SutomService {

    private Map<Integer, Map<Character, Set<String>>> dictionary;

    public void init() {
        // Build the dictionary. The words are grouped by size and by the first character to improve the performances
        dictionary = buildDictionary();
    }

    public void extractSingleWords() {
        try (final var lines = Files.lines(Paths.get(requireNonNull(getClass().getClassLoader().getResource("dictionary.csv")).toURI()))) {
            final var data = lines.filter(line -> !line.contains("['plural']"))
                    .map(line -> line.split(",")[1])
                    .filter(sentence -> !sentence.contains(" "))
                    .distinct()
                    .map(word -> Normalizer.normalize(word, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "").toUpperCase())
                    .toList();
            Files.write(Paths.get(requireNonNull(getClass().getClassLoader().getResource("dictionary.txt")).toURI()), data);
        } catch (final IOException | URISyntaxException e) {
            throw new ServiceException(e);
        }
    }

    public Iterator<List<String>> search(final SutomRequest request, final int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("Page size must be greater than 0");
        }

        final var result = search(request);

        return new Iterator<>() {
            private int cursor = 0;

            @Override
            public boolean hasNext() {
                return result.size() > cursor;
            }

            @Override
            public List<String> next() {
                final var page = result.stream()
                        .skip(cursor)
                        .limit(pageSize)
                        .toList();
                cursor += pageSize;
                return page;
            }
        };
    }

    public List<String> search(final SutomRequest request) {
        // Load the dictionary if not done
        if (dictionary == null) {
            init();
        }
        final var group = dictionary.get(request.pattern().length());
        if (group != null) {
            final var words = group.get(request.pattern().charAt(0));
            return words.stream()
                    .filter(word -> match(word, request))
                    .toList();
        }
        return of();
    }

    private boolean match(final String word, final SutomRequest request) {
        final var normalizedPattern = request.pattern().replace(".", "\\w");
        return word.matches(normalizedPattern)
                && matchInclusion(word, request.inclusions(), request.pattern())
                && matchExclusion(word, request.exclusions());
    }

    private boolean matchInclusion(final String word, final String inclusion, final String pattern) {
        if (inclusion != null) {
            final var chars = new char[word.length()];
            for (int i = 0; i < word.length(); i++) {
                chars[i] = pattern.charAt(i) == word.charAt(i) ? '.' : word.charAt(i);
            }
            final var newWord = new String(chars);
            return inclusion.chars()
                    .mapToObj(character -> String.valueOf((char) character))
                    .filter(character -> !character.isEmpty())
                    .allMatch(newWord::contains);
        }
        return true;
    }

    private boolean matchExclusion(final String word, final String exclusion) {
        if (exclusion != null) {
            return exclusion.chars()
                    .mapToObj(character -> String.valueOf((char) character))
                    .filter(character -> !character.isEmpty())
                    .noneMatch(word::contains);
        }
        return true;
    }

    private Map<Integer, Map<Character, Set<String>>> buildDictionary() {
        final Map<Integer, Map<Character, Set<String>>> dic = new HashMap<>();
        try (final var words = Files.lines(Paths.get(requireNonNull(getClass().getClassLoader().getResource("dictionary.txt")).toURI()))) {
            words.forEach(word -> dic.computeIfAbsent(word.length(), k -> new HashMap<>())
                    .computeIfAbsent(word.charAt(0), k -> new HashSet<>())
                    .add(word));
            return dic;
        } catch (final IOException | URISyntaxException e) {
            throw new ServiceException(e);
        }
    }
}
