package org.shetsko.controller;

import lombok.RequiredArgsConstructor;
import org.shetsko.exception.ResourceNotFoundException;
import org.shetsko.model.*;
import org.shetsko.model.enums.TaskStatus;
import org.shetsko.service.KeywordService;
import org.shetsko.service.TaskService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final KeywordService keywordService;

    @GetMapping
    public String getAllTasks(Model model) {
        // Получаем активные и завершенные задачи
        List<Task> activeTasks = taskService.getActiveTasks();
        List<Task> completedTasks = taskService.getCompletedTasks();

        // Добавляем в модель
        model.addAttribute("activeTasks", activeTasks);
        model.addAttribute("completedTasks", completedTasks);
        model.addAttribute("keywordService", keywordService);

        // Добавляем пустые объекты для форм
        model.addAttribute("newTask", new Task());
        model.addAttribute("newTag", new Tag());

        // Получаем все теги
        model.addAttribute("allTags", taskService.getAllTags());

        return "tasks/list";
    }

    @PostMapping("/create")
    public String createTask(@ModelAttribute("newTask") Task newTask,
                             @RequestParam(value = "tagIds", required = false) List<Long> tagIds) {
        Task createdTask = taskService.createTask(newTask);

        if (tagIds != null && !tagIds.isEmpty()) {
            tagIds.forEach(tagId -> taskService.addTag(createdTask.getCustomId(), new Tag(tagId, null, null)));
        }

        return "redirect:/tasks";
    }

    @GetMapping("/{customId}")
    public String getTask(@PathVariable String customId, Model model) {
        Task task = taskService.getTask(customId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // Ищем ключевые слова во всех полях задачи
        Map<String, List<String>> keywordsWithSource = keywordService.findKeywordsWithSource(task);

        model.addAttribute("task", task);
        model.addAttribute("newComment", new Comment());
        model.addAttribute("keywords", keywordsWithSource.keySet());
        model.addAttribute("keywordsWithSource", keywordsWithSource);
        model.addAttribute("allTags", taskService.getAllTags());

        return "tasks/view";
    }

    @PostMapping("/{customId}/comments")
    public String addComment(@PathVariable String customId,
                             @ModelAttribute Comment newComment) {
        taskService.addComment(customId, newComment);
        return "redirect:/tasks/" + customId;
    }

    @PostMapping("/{customId}/comments/{commentId}")
    public String updateComment(@PathVariable String customId,
                                @PathVariable Long commentId,
                                @RequestParam String content) {
        taskService.updateComment(commentId, content);
        return "redirect:/tasks/" + customId;
    }

    @PostMapping("/{customId}/comments/{commentId}/delete")
    public String deleteComment(@PathVariable String customId,
                                @PathVariable Long commentId) {
        taskService.deleteComment(commentId);
        return "redirect:/tasks/" + customId;
    }

    @PostMapping("/{customId}/complete")
    public String completeTask(@PathVariable String customId) {
        Task task = taskService.getTaskByCustomId(customId);
        taskService.completeTask(task);
        return "redirect:/tasks";
    }

    @PostMapping("/tags/create")
    public String createTag(@ModelAttribute("newTag") Tag newTag) {
        System.out.println("Creating tag: " + newTag.getName() + ", color: " + newTag.getColor());

        if (newTag.getColor() == null || newTag.getColor().isEmpty()) {
            newTag.setColor(String.format("#%06x", (int) (Math.random() * 0x1000000)));
        }

        try {
            taskService.createTag(newTag);
            System.out.println("Tag created successfully");
        } catch (Exception e) {
            System.out.println("Error creating tag: " + e.getMessage());
            e.printStackTrace();
        }

        return "redirect:/tasks";
    }

    @PostMapping("/tags/delete/{id}")
    public String deleteTag(@PathVariable Long id) {
        taskService.deleteTag(id);
        return "redirect:/tasks";
    }

    @PostMapping("/{customId}/tags")
    public String addTagToTask(@PathVariable String customId,
                               @RequestParam Long tagId) {
        taskService.addTag(customId, new Tag(tagId, null, null));
        return "redirect:/tasks/" + customId;
    }

    @PostMapping("/{customId}/tags/{tagId}/remove")
    public String removeTagFromTask(@PathVariable String customId,
                                    @PathVariable Long tagId) {
        taskService.removeTag(customId, tagId);
        return "redirect:/tasks/" + customId;
    }

    @GetMapping("/search")
    public String searchTasks(@RequestParam(required = false) String keyword,
                              @RequestParam(required = false) List<Long> tagIds,
                              Model model) {

        List<Task> foundTasks = new ArrayList<>();

        // Поиск по ключевому слову
        if (keyword != null && !keyword.trim().isEmpty()) {
            foundTasks = taskService.findTasksByKeyword(keyword.trim());
        }

        // Фильтр по тегам
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Task> taggedTasks = taskService.findTasksByTags(tagIds);

            if (foundTasks.isEmpty()) {
                foundTasks = taggedTasks;
            } else {
                // Пересечение результатов поиска и фильтра по тегам
                foundTasks = foundTasks.stream()
                        .filter(taggedTasks::contains)
                        .collect(Collectors.toList());
            }
        }

        // Если нет параметров поиска - показываем все задачи
        if ((keyword == null || keyword.trim().isEmpty()) &&
                (tagIds == null || tagIds.isEmpty())) {
            foundTasks = taskService.getAllTasks();
        }

        // Разделяем на активные и выполненные
        List<Task> activeFoundTasks = foundTasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.COMPLETED)
                .collect(Collectors.toList());

        List<Task> completedFoundTasks = foundTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .collect(Collectors.toList());

        model.addAttribute("activeTasks", activeFoundTasks);
        model.addAttribute("completedTasks", completedFoundTasks);
        model.addAttribute("keywordService", keywordService);
        model.addAttribute("newTask", new Task());
        model.addAttribute("newTag", new Tag());
        model.addAttribute("allTags", taskService.getAllTags());
        model.addAttribute("searchKeyword", keyword);
        model.addAttribute("selectedTagIds", tagIds != null ? tagIds : Collections.emptyList());
        model.addAttribute("searchResultsCount", foundTasks.size());
        model.addAttribute("activeResultsCount", activeFoundTasks.size());
        model.addAttribute("completedResultsCount", completedFoundTasks.size());

        return "tasks/list";
    }

    @GetMapping("/filter/tag/{tagId}")
    public String filterTasksByTag(@PathVariable Long tagId, Model model) {
        List<Task> foundTasks = taskService.findTasksByTagId(tagId);

        // Разделяем на активные и выполненные
        List<Task> activeFoundTasks = foundTasks.stream()
                .filter(task -> task.getStatus() != TaskStatus.COMPLETED)
                .collect(Collectors.toList());

        List<Task> completedFoundTasks = foundTasks.stream()
                .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
                .collect(Collectors.toList());

        model.addAttribute("activeTasks", activeFoundTasks);
        model.addAttribute("completedTasks", completedFoundTasks);
        model.addAttribute("keywordService", keywordService);
        model.addAttribute("newTask", new Task());
        model.addAttribute("newTag", new Tag());
        model.addAttribute("allTags", taskService.getAllTags());
        model.addAttribute("selectedTagIds", Collections.singletonList(tagId));
        model.addAttribute("searchResultsCount", foundTasks.size());
        model.addAttribute("activeResultsCount", activeFoundTasks.size());
        model.addAttribute("completedResultsCount", completedFoundTasks.size());

        return "tasks/list";
    }

    @GetMapping("/keywords")
    public String manageKeywords(Model model) {
        model.addAttribute("keywords", keywordService.getAllKeywords());
        model.addAttribute("newKeyword", "");
        return "tasks/keywords";
    }

    @PostMapping("/keywords/add")
    public String addKeyword(@RequestParam String newKeyword) {
        keywordService.addKeyword(newKeyword);
        return "redirect:/tasks/keywords";
    }

    @PostMapping("/keywords/delete")
    public String deleteKeyword(@RequestParam String keyword) {
        keywordService.removeKeyword(keyword);
        return "redirect:/tasks/keywords";
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }
}