package ru.cargaman.rbserver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.cargaman.rbserver.model.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
}
