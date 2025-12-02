package de.seuhd.campuscoffee.domain.impl;

import de.seuhd.campuscoffee.domain.exceptions.DuplicationException;
import de.seuhd.campuscoffee.domain.model.User;
import de.seuhd.campuscoffee.domain.ports.UserDataService;
import de.seuhd.campuscoffee.domain.ports.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    // DONE: Implement user service
    private  final UserDataService userDataService;

    @Override
    public void clear() {
        log.warn("clearing user data");
        userDataService.clear();
    }

    @Override
    public @NonNull List<User> getAllUsers() {
        log.debug("retrieving al users");
        return userDataService.getAll();
    }

    @Override
    public @NonNull User getUserByID(@NonNull Long id) {
        log.debug("retrieving user by id: {}", id);
        return userDataService.getById(id);
    }

    @Override
    public @NonNull User getByUsername(@NonNull String username) {

        log.debug("retrieving user by username: {}", username);
        return userDataService.getByUsername(username);
    }

    @Override
    public @NonNull User upsert(@NonNull User user) {
        if(user.id() == null) {
            //new user
            log.info("creating new user: {}", user.username());
        }else{
            //update
            log.info("updating user: {}", user.id());
            //ensure user exists in db
            userDataService.getById(user.id());
            }
        return performUpsert(user);

    }
    private  @NonNull User performUpsert(@NonNull User user) {
        try {
            User upsertedUser = userDataService.upsert(user);
            log.info("upserted user: {}", upsertedUser.id());
            return upsertedUser;
        }catch (DuplicationException e){
       log.error("failed to upsert user: {}", user.id(), e);
            throw e;
        }
    }

    @Override
    public void delete(@NonNull Long id) {
        log.info("deleting user by id: {}", id);
        userDataService.delete(id);
        log.info("deleted user by id: {}", id);
    }




}
