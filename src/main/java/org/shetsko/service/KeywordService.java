package org.shetsko.service;

import org.shetsko.model.Comment;
import org.shetsko.model.Keyword;
import org.shetsko.model.Task;
import org.shetsko.repository.KeywordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Transactional
public class KeywordService {
    private final KeywordRepository keywordRepository;
    private Map<String, Pattern> keywordPatterns = new HashMap<>();

    public KeywordService(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    @PostConstruct
    public void init() {
        System.out.println("Initializing KeywordService...");
        loadKeywordsFromDatabase();

        // Добавляем начальные ключевые слова если БД пустая
        if (keywordRepository.count() == 0) {
            System.out.println("Database is empty, adding initial keywords...");
            addInitialKeywords();
        } else {
            System.out.println("Loaded " + keywordRepository.count() + " keywords from database");
        }
    }

    private void loadKeywordsFromDatabase() {
        keywordPatterns.clear();
        try {
            List<Keyword> keywords = keywordRepository.findAllOrderByWord();
            for (Keyword keyword : keywords) {
                if (keyword.isValid()) {
                    String word = keyword.getWord().toLowerCase();
                    keywordPatterns.put(word, Pattern.compile("\\b" + Pattern.quote(word) + "\\b", Pattern.CASE_INSENSITIVE));
                }
            }
            System.out.println("Successfully loaded " + keywordPatterns.size() + " keywords into memory");
        } catch (Exception e) {
            System.err.println("Error loading keywords from database: " + e.getMessage());
        }
    }

    private void addInitialKeywords() {
        List<String> initialKeywords = Arrays.asList(
                "поставщик", "юрист", "финансы", "договор", "счет",
                "IT", "разработка", "срочно", "важно", "задача",
                "проект", "встреча", "отчет", "план", "бюджет",
                "клиент", "заказ", "оплата", "срок", "исполнитель"
        );

        int addedCount = 0;
        for (String keyword : initialKeywords) {
            if (addKeywordToDatabase(keyword)) {
                addedCount++;
            }
        }
        System.out.println("Added " + addedCount + " initial keywords to database");
        loadKeywordsFromDatabase();
    }

    private boolean addKeywordToDatabase(String keywordText) {
        try {
            if (keywordText != null && !keywordText.trim().isEmpty()) {
                String normalizedKeyword = keywordText.toLowerCase().trim();

                if (!keywordRepository.existsByWordIgnoreCase(normalizedKeyword)) {
                    Keyword keyword = new Keyword(normalizedKeyword);
                    if (keyword.isValid()) {
                        keywordRepository.save(keyword);
                        System.out.println("✓ Added keyword to database: '" + normalizedKeyword + "'");
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("✗ Error adding keyword '" + keywordText + "': " + e.getMessage());
        }
        return false;
    }

    public Set<String> findKeywordsInTask(Task task) {
        if (task == null) {
            return Collections.emptySet();
        }

        Set<String> foundKeywords = new HashSet<>();

        // Поиск в заголовке
        if (task.getTitle() != null) {
            foundKeywords.addAll(findKeywords(task.getTitle()));
        }

        // Поиск в описании
        if (task.getDescription() != null) {
            foundKeywords.addAll(findKeywords(task.getDescription()));
        }

        // Поиск в комментариях
        if (task.getComments() != null && !task.getComments().isEmpty()) {
            for (Comment comment : task.getComments()) {
                if (comment.getContent() != null) {
                    foundKeywords.addAll(findKeywords(comment.getContent()));
                }
            }
        }

        return foundKeywords;
    }

    public Map<String, List<String>> findKeywordsWithSource(Task task) {
        if (task == null) {
            return Collections.emptyMap();
        }

        Map<String, List<String>> keywordsWithSource = new HashMap<>();

        // Поиск в заголовке
        if (task.getTitle() != null) {
            Set<String> titleKeywords = findKeywords(task.getTitle());
            titleKeywords.forEach(keyword ->
                    keywordsWithSource.computeIfAbsent(keyword, k -> new ArrayList<>()).add("заголовок"));
        }

        // Поиск в описании
        if (task.getDescription() != null) {
            Set<String> descKeywords = findKeywords(task.getDescription());
            descKeywords.forEach(keyword ->
                    keywordsWithSource.computeIfAbsent(keyword, k -> new ArrayList<>()).add("описание"));
        }

        // Поиск в комментариях
        if (task.getComments() != null && !task.getComments().isEmpty()) {
            for (int i = 0; i < task.getComments().size(); i++) {
                Comment comment = task.getComments().get(i);
                if (comment.getContent() != null) {
                    Set<String> commentKeywords = findKeywords(comment.getContent());
                    int finalI = i;
                    commentKeywords.forEach(keyword ->
                            keywordsWithSource.computeIfAbsent(keyword, k -> new ArrayList<>())
                                    .add("комментарий " + (finalI + 1)));
                }
            }
        }

        return keywordsWithSource;
    }

    public Set<String> findKeywords(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.emptySet();
        }

        String lowerText = text.toLowerCase();
        return keywordPatterns.keySet().stream()
                .filter(keyword -> keywordPatterns.get(keyword).matcher(lowerText).find())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public List<String> highlightKeywords(String text) {
        if (text == null || text.isEmpty()) {
            return Collections.singletonList(text);
        }

        Set<String> foundKeywords = findKeywords(text);
        if (foundKeywords.isEmpty()) {
            return Collections.singletonList(text);
        }

        List<String> highlightedParts = new ArrayList<>();
        String remainingText = text;

        // Сортируем ключевые слова по длине (от длинных к коротким)
        List<String> sortedKeywords = foundKeywords.stream()
                .sorted((a, b) -> Integer.compare(b.length(), a.length()))
                .toList();

        for (String keyword : sortedKeywords) {
            Pattern pattern = Pattern.compile("(" + Pattern.quote(keyword) + ")", Pattern.CASE_INSENSITIVE);
            java.util.regex.Matcher matcher = pattern.matcher(remainingText);

            if (matcher.find()) {
                if (matcher.start() > 0) {
                    highlightedParts.add(remainingText.substring(0, matcher.start()));
                }

                highlightedParts.add("<span class='highlighted-keyword'>" + matcher.group(1) + "</span>");
                remainingText = remainingText.substring(matcher.end());

                // Перезапускаем для оставшегося текста
                matcher = pattern.matcher(remainingText);
            }
        }

        if (!remainingText.isEmpty()) {
            highlightedParts.add(remainingText);
        }

        return highlightedParts;
    }

    @Transactional
    public boolean addKeyword(String keywordText) {
        try {
            if (keywordText != null && !keywordText.trim().isEmpty()) {
                String normalizedKeyword = keywordText.toLowerCase().trim();

                if (!keywordRepository.existsByWordIgnoreCase(normalizedKeyword)) {
                    Keyword keyword = new Keyword(normalizedKeyword);
                    if (keyword.isValid()) {
                        keywordRepository.save(keyword);
                        keywordPatterns.put(normalizedKeyword,
                                Pattern.compile("\\b" + Pattern.quote(normalizedKeyword) + "\\b", Pattern.CASE_INSENSITIVE));
                        System.out.println("✓ Keyword added to database: '" + normalizedKeyword + "'");
                        return true;
                    }
                } else {
                    System.out.println("ℹ Keyword already exists: '" + normalizedKeyword + "'");
                }
            }
        } catch (Exception e) {
            System.err.println("✗ Error adding keyword '" + keywordText + "': " + e.getMessage());
        }
        return false;
    }

    @Transactional
    public boolean removeKeyword(String keywordText) {
        try {
            if (keywordText != null) {
                String normalizedKeyword = keywordText.toLowerCase().trim();
                Optional<Keyword> keywordOpt = keywordRepository.findByWordIgnoreCase(normalizedKeyword);

                if (keywordOpt.isPresent()) {
                    keywordRepository.delete(keywordOpt.get());
                    keywordPatterns.remove(normalizedKeyword);
                    System.out.println("✓ Keyword removed from database: '" + normalizedKeyword + "'");
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("✗ Error removing keyword '" + keywordText + "': " + e.getMessage());
        }
        return false;
    }

    public Set<String> getAllKeywords() {
        try {
            return keywordRepository.findAllOrderByWord().stream()
                    .map(Keyword::getWord)
                    .collect(Collectors.toCollection(LinkedHashSet::new));
        } catch (Exception e) {
            System.err.println("Error retrieving keywords: " + e.getMessage());
            return Collections.emptySet();
        }
    }

    public List<Keyword> getAllKeywordEntities() {
        try {
            return keywordRepository.findAllOrderByWord();
        } catch (Exception e) {
            System.err.println("Error retrieving keyword entities: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public boolean containsKeyword(String keywordText) {
        if (keywordText == null) return false;
        return keywordRepository.existsByWordIgnoreCase(keywordText.toLowerCase().trim());
    }

    public int getKeywordCount() {
        try {
            return (int) keywordRepository.count();
        } catch (Exception e) {
            return 0;
        }
    }

    public void reloadKeywords() {
        loadKeywordsFromDatabase();
    }
}