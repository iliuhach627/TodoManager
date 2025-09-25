package org.shetsko.repository;

import org.shetsko.model.Task;
import org.shetsko.model.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    Optional<Task> findByCustomId(String customId);

    // Активные задачи - новые сверху
    @Query("SELECT t FROM Task t WHERE t.status != 'COMPLETED' ORDER BY t.createdAt DESC")
    List<Task> findActiveTasksOrderByCreatedAtDesc();

    // Выполненные задачи - новые сверху
    @Query("SELECT t FROM Task t WHERE t.status = 'COMPLETED' ORDER BY t.createdAt DESC")
    List<Task> findCompletedTasksOrderByCreatedAtDesc();

    // Все задачи - новые сверху
    @Query("SELECT t FROM Task t ORDER BY t.createdAt DESC")
    List<Task> findAllOrderByCreatedAtDesc();

    // Поиск с сортировкой - новые сверху
    @Query("SELECT DISTINCT t FROM Task t JOIN t.comments c WHERE LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(t.customId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(c.content) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY t.createdAt DESC")
    List<Task> findByKeywordOrderByCreatedAtDesc(@Param("keyword") String keyword);

    // Фильтр по тегам с сортировкой - новые сверху
    @Query("SELECT DISTINCT t FROM Task t JOIN t.tags tag WHERE tag.id IN :tagIds ORDER BY t.createdAt DESC")
    List<Task> findByTagsIdInOrderByCreatedAtDesc(@Param("tagIds") List<Long> tagIds);

    // Фильтр по дате с сортировкой - новые сверху
    @Query("SELECT t FROM Task t WHERE t.createdAt BETWEEN :start AND :end ORDER BY t.createdAt DESC")
    List<Task> findByCreatedAtBetweenOrderByCreatedAtDesc(@Param("start") LocalDateTime start,
                                                           @Param("end") LocalDateTime end);

    long countByCreatedAtBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1);

    @Query("SELECT t FROM Task t JOIN t.tags tag WHERE tag.id = :tagId")
    List<Task> findAllByTagsId(@Param("tagId") Long tagId);

    @Query("SELECT DISTINCT t FROM Task t JOIN t.tags tag WHERE tag.id IN :tagIds")
    List<Task> findByTagsIdIn(@Param("tagIds") List<Long> tagIds);

    @Query("SELECT DISTINCT t FROM Task t JOIN t.tags tag WHERE tag.id = :tagId")
    List<Task> findByTagsId(@Param("tagId") Long tagId);

    @Query("SELECT DISTINCT t FROM Task t JOIN t.tags tag WHERE LOWER(tag.name) LIKE LOWER(CONCAT('%', :tagName, '%'))")
    List<Task> findByTagName(@Param("tagName") String tagName);

    @Query("SELECT t FROM Task t WHERE t.customId != :excludeId ORDER BY t.createdAt DESC")
    List<Task> findAllExcludingTask(@Param("excludeId") String excludeId);

}