package com.freelanceos.freelanceappback.service;

import com.freelanceos.freelanceappback.api.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class UserService {
    private final List<User> users = new ArrayList<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    public UserService() {
        users.add(new User(idSequence.incrementAndGet(), "Alice Martin", "alice@example.com"));
        users.add(new User(idSequence.incrementAndGet(), "Bob Dupont", "bob@example.com"));
    }

    public List<User> getAllUsers() {
        return users;
    }

    public Optional<User> getUserById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    public User createUser(User user) {
        User newUser = new User(idSequence.incrementAndGet(), user.getName(), user.getEmail());
        users.add(newUser);
        return newUser;
    }
}
