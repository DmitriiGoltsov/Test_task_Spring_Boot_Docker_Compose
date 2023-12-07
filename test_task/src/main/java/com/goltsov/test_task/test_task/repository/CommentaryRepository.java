package com.goltsov.test_task.test_task.repository;

import com.goltsov.test_task.test_task.model.Commentary;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentaryRepository extends JpaRepository<Commentary, Long> {
}
