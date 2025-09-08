package org.shetsko.repository;

import org.shetsko.model.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Optional<Keyword> findByWord(String word);

    boolean existsByWord(String word);

    List<Keyword> findByWordContainingIgnoreCase(String word);

    @Query("SELECT k FROM Keyword k WHERE LOWER(k.word) = LOWER(:word)")
    Optional<Keyword> findByWordIgnoreCase(@Param("word") String word);

    @Query("SELECT CASE WHEN COUNT(k) > 0 THEN true ELSE false END FROM Keyword k WHERE LOWER(k.word) = LOWER(:word)")
    boolean existsByWordIgnoreCase(@Param("word") String word);

    @Query("SELECT k FROM Keyword k ORDER BY k.word ASC")
    List<Keyword> findAllOrderByWord();
}