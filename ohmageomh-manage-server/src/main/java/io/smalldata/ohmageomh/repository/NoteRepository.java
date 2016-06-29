package io.smalldata.ohmageomh.repository;

import io.smalldata.ohmageomh.domain.Note;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Note entity.
 */
public interface NoteRepository extends JpaRepository<Note,Long> {

    @Query("select note from Note note where note.manager.login = ?#{principal.username}")
    List<Note> findByManagerIsCurrentUser();

}
