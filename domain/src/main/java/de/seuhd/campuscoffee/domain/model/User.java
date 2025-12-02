package de.seuhd.campuscoffee.domain.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * Domain module for storing User data in order to facilitate creation, updates, retrieval and deletion
 * field constraints are checked and validated (Bean validation) in the api layer
 *
 * @param id            user ID = 0 for new user before creation
 * @param createdAt     creation timestamp, automatically set upon creation
 * @param updatedAt     update timestamp, automatically set upon creation & update
 * @param username      login name, must only contain valid characters (@Pattern)
 * @param email         email address, has to be a valid address (@Email)
 * @param firstName     first name, 255 >= length >= 1
 * @param lastName      last name, 255 >= length >= 1
 */
@Builder(toBuilder = true)
public record User (
        //DONE: Implement user domain object
        @Nullable Long id,
        @Nullable LocalDateTime createdAt,
        @Nullable LocalDateTime updatedAt,
        @NonNull String username,
        @NonNull String emailAddress,
        @NonNull String firstName,
        @NonNull String lastName
        ) implements Serializable { // serializable to allow cloning (see TestFixtures class).
    @Serial
    private static final long serialVersionUID = 1L;
}
