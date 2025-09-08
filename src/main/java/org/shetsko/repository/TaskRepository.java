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

    List<Task> findByStatus(TaskStatus status);
    List<Task> findByStatusNot(TaskStatus status);

    @Query("SELECT t FROM Task t WHERE t.createdAt BETWEEN :start AND :end")
    List<Task> findByCreatedAtBetween(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

//    @Query("SELECT t FROM Task t JOIN t.tags tag WHERE tag.name = :tagName")
//    List<Task> findByTagName(@Param("tagName") String tagName);

    @Query("SELECT t FROM Task t WHERE LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Task> findByKeyword(@Param("keyword") String keyword);

    long countByCreatedAtBetween(LocalDateTime localDateTime, LocalDateTime localDateTime1);

    @Query("SELECT t FROM Task t JOIN t.tags tag WHERE tag.id = :tagId")
    List<Task> findAllByTagsId(@Param("tagId") Long tagId);

    @Query("SELECT DISTINCT t FROM Task t JOIN t.tags tag WHERE tag.id IN :tagIds")
    List<Task> findByTagsIdIn(@Param("tagIds") List<Long> tagIds);

    @Query("SELECT DISTINCT t FROM Task t JOIN t.tags tag WHERE tag.id = :tagId")
    List<Task> findByTagsId(@Param("tagId") Long tagId);

    @Query("SELECT DISTINCT t FROM Task t JOIN t.tags tag WHERE LOWER(tag.name) LIKE LOWER(CONCAT('%', :tagName, '%'))")
    List<Task> findByTagName(@Param("tagName") String tagName);
}