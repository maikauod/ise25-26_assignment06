package de.seuhd.campuscoffee.domain.ports;

import de.seuhd.campuscoffee.domain.model.User;
import jakarta.annotation.Nullable;
import org.jspecify.annotations.NonNull;
import java.util.List;

public interface UserService {
    //DONE: Define user service interface
    void clear();


   @NonNull List<User> getAllUsers();

   @NonNull User getUserByID(@NonNull Long id);

    @NonNull User getByUsername(@NonNull String username);

    @NonNull User upsert(@NonNull User user);

    Void delete(@NonNull Long id);


}
