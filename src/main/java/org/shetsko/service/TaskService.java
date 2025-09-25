package org.shetsko.service;

import org.shetsko.exception.ResourceNotFoundException;
import org.shetsko.model.*;
import org.shetsko.model.enums.TaskStatus;
import org.shetsko.repository.CommentRepository;
import org.shetsko.repository.TagRepository;
import org.shetsko.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TaskService {
    private final TaskRepository taskRepository;
    private final TagRepository tagRepository;
    private final CommentRepository commentRepository;
    private final TaskIdGenerator idGenerator;

    @Autowired
    public TaskService(TaskRepository taskRepository,
                       TagRepository tagRepository,
                       CommentRepository commentRepository,
                       TaskIdGenerator idGenerator) {
        this.taskRepository = taskRepository;
        this.tagRepository = tagRepository;
        this.commentRepository = commentRepository;
        this.idGenerator = idGenerator;
    }

    // Task operations
    @Transactional
    public Task createTask(Task task) {
        task.setCustomId(idGenerator.generateTaskId());
        task.setStatus(TaskStatus.NEW);
        return taskRepository.save(task);
    }


    public Optional<Task> getTask(String customId) {
        return taskRepository.findByCustomId(customId);
    }

    public Task getTaskByCustomId(String customId) {
        return taskRepository.findByCustomId(customId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", "customId", customId));
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAllOrderByCreatedAtDesc();
    }

    public List<Task> getActiveTasks() {
        return taskRepository.findActiveTasksOrderByCreatedAtDesc();
    }

    public List<Task> getCompletedTasks() {
        return taskRepository.findCompletedTasksOrderByCreatedAtDesc();
    }

    public Task updateTask(String customId, Task taskDetails) {
        Task task = getTaskByCustomId(customId);

        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setPriority(taskDetails.getPriority());
        task.setStatus(taskDetails.getStatus());

        return taskRepository.save(task);
    }

    public void deleteTask(String customId) {
        Task task = getTaskByCustomId(customId);
        taskRepository.delete(task);
    }

    @Transactional
    public void completeTask(Task task) {
        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    public List<Task> findTasksByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String searchTerm = keyword.toLowerCase().trim();
        return taskRepository.findByKeywordOrderByCreatedAtDesc(searchTerm);
    }

    public List<Task> findTasksByCreatedAndBetween(LocalDateTime start, LocalDateTime end) {
        return taskRepository.findByCreatedAtBetweenOrderByCreatedAtDesc(start, end);
    }

    // Tag operations
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public List<Task> findTasksByTagId(Long tagId) {
        if (tagId == null) {
            return Collections.emptyList();
        }

        return taskRepository.findByTagsId(tagId);
    }

    public List<Task> findTasksByTags(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Collections.emptyList();
        }

        return taskRepository.findByTagsIdInOrderByCreatedAtDesc(tagIds);
    }

    @Transactional
    public Tag createTag(Tag tag) {
        System.out.println("Saving tag to database: " + tag.getName());

        // Проверяем, существует ли тег с таким именем
        Optional<Tag> existingTag = tagRepository.findByName(tag.getName());
        if (existingTag.isPresent()) {
            System.out.println("Tag already exists: " + tag.getName());
            return existingTag.get();
        }

        // Сохраняем тег
        Tag savedTag = tagRepository.save(tag);
        System.out.println("Tag saved with ID: " + savedTag.getId());

        return savedTag;
    }

    public Tag updateTag(Long tagId, Tag tagDetails) {
        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));

        tag.setName(tagDetails.getName());
        tag.setColor(tagDetails.getColor());

        return tagRepository.save(tag);
    }

    @Transactional
    public void deleteTag(Long tagId) {
        // Удаляем связи тега с задачами
        List<Task> tasksWithTag = taskRepository.findAllByTagsId(tagId);
        tasksWithTag.forEach(task -> {
            task.getTags().removeIf(tag -> tag.getId().equals(tagId));
            taskRepository.save(task);
        });

        // Удаляем сам тег
        tagRepository.deleteById(tagId);
    }

    public Task addTag(String customId, Tag tag) {
        Task task = getTaskByCustomId(customId);
        Tag existingTag = tagRepository.findById(tag.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tag.getId()));

        task.getTags().add(existingTag);
        return taskRepository.save(task);
    }

    public void removeTag(String customId, Long tagId) {
        Task task = getTaskByCustomId(customId);
        task.getTags().removeIf(tag -> tag.getId().equals(tagId));
        taskRepository.save(task);
    }

    // Comment operations
    public Task addComment(String customId, Comment comment) {
        Task task = getTaskByCustomId(customId);

        comment.setTask(task);
        Comment savedComment = commentRepository.save(comment);
        task.getComments().add(savedComment);

        return taskRepository.save(task);
    }

    public Comment updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<Task> getReferencedTasks(String customId) {
        Task task = getTaskByCustomId(customId);
        return new ArrayList<>(task.getReferencedTasks());
    }

    public List<Task> getReferencingTasks(String customId) {
        Task task = getTaskByCustomId(customId);
        return new ArrayList<>(task.getReferencingTasks());
    }

    @Transactional
    public Task addReference(String customId, String referencedTaskId) {
        if (customId.equals(referencedTaskId)) {
            throw new IllegalArgumentException("Задача не может ссылаться на саму себя");
        }

        Task task = getTaskByCustomId(customId);
        Task referencedTask = getTaskByCustomId(referencedTaskId);

        // Проверяем, нет ли уже такой связи
        if (task.getReferencedTasks().contains(referencedTask)) {
            throw new IllegalArgumentException("Связь между задачами уже существует");
        }

        task.getReferencedTasks().add(referencedTask);
        return taskRepository.save(task);
    }

    @Transactional
    public void removeReference(String customId, String referencedTaskId) {
        Task task = getTaskByCustomId(customId);
        Task referencedTask = getTaskByCustomId(referencedTaskId);

        task.getReferencedTasks().remove(referencedTask);
        taskRepository.save(task);
    }

    public List<Task> findTasksForReference(String customId, String searchTerm) {
        List<Task> allTasks = taskRepository.findAllOrderByCreatedAtDesc();

        return allTasks.stream()
                .filter(task -> !task.getCustomId().equals(customId)) // Исключаем текущую задачу
                .filter(task -> task.getTitle().toLowerCase().contains(searchTerm.toLowerCase()) ||
                        task.getCustomId().contains(searchTerm))
                .collect(Collectors.toList());
    }

}