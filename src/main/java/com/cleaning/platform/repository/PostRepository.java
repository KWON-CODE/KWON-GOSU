package com.cleaning.platform.repository;

import com.cleaning.platform.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import com.cleaning.platform.domain.PostCategory;

public interface PostRepository extends JpaRepository<Post, String> {


    @Query(value = "SELECT p FROM Post p JOIN FETCH p.users",
            countQuery = "SELECT COUNT(p) FROM Post p")
    @Override
    Page<Post> findAll(Pageable pageable);


    @Query(value = "SELECT p FROM Post p JOIN FETCH p.users WHERE p.title LIKE %:keyword%",
            countQuery = "SELECT COUNT(p) FROM Post p WHERE p.title LIKE %:keyword%")
    Page<Post> findByTitleContaining(@Param("keyword") String keyword, Pageable pageable);


    List<Post> findByTitleContaining(String keyword);


    List<Post> findByUsersEmailOrderByCreatedAtDesc(String email);


    @Query("SELECT p FROM Post p JOIN FETCH p.users LEFT JOIN FETCH p.comments c LEFT JOIN FETCH c.users WHERE p.id = :id")
    Optional<Post> findPostWithDetailsById(@Param("id") String postId);


    @Query(value = "SELECT p FROM Post p JOIN FETCH p.users WHERE p.category = :category",
            countQuery = "SELECT COUNT(p) FROM Post p WHERE p.category = :category")

    Page<Post> findByCategory(@Param("category") PostCategory category, Pageable pageable);

    @Query(value = "SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.images " +
            "WHERE (:category IS NULL OR p.category = :category) " +
            "AND (:keyword IS NULL OR p.title LIKE CONCAT('%', :keyword, '%') OR p.content LIKE CONCAT('%', :keyword, '%'))",
            countQuery = "SELECT COUNT(p) FROM Post p " +
                    "WHERE (:category IS NULL OR p.category = :category) " +
                    "AND (:keyword IS NULL OR p.title LIKE CONCAT('%', :keyword, '%') OR p.content LIKE CONCAT('%', :keyword, '%'))")
    Page<Post> findByCategoryAndKeyword(@Param("category") PostCategory category,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);


    @Query(value = "SELECT DISTINCT p FROM Post p " +
            "LEFT JOIN FETCH p.images " +
            "LEFT JOIN FETCH p.users " +
            "WHERE (:category IS NULL OR p.category = :category) " +
            "AND (:keyword IS NULL OR p.title LIKE CONCAT('%', :keyword, '%') OR p.content LIKE CONCAT('%', :keyword, '%'))",
            countQuery = "SELECT COUNT(p) FROM Post p " +
                    "WHERE (:category IS NULL OR p.category = :category) " +
                    "AND (:keyword IS NULL OR p.title LIKE CONCAT('%', :keyword, '%') OR p.content LIKE CONCAT('%', :keyword, '%'))")
    Page<Post> findByCategoryAndKeywordWithImages(@Param("category") PostCategory category,
                                                  @Param("keyword") String keyword,
                                                  Pageable pageable);

}