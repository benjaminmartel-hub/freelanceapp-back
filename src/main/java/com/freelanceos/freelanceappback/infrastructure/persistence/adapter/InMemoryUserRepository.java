package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final List<UserEntity> users = new ArrayList<>();
    private final AtomicLong idSequence = new AtomicLong(0);

    public InMemoryUserRepository() {
        users.add(new UserEntity(idSequence.incrementAndGet(), "Alice Martin", "alice@example.com"));
        users.add(new UserEntity(idSequence.incrementAndGet(), "Bob Dupont", "bob@example.com"));
    }

    @Override
    public List<UserEntity> findAll() {
        return new ArrayList<>(users);
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }

    @Override
    public UserEntity save(UserEntity user) {
        UserEntity userToSave = new UserEntity(
                idSequence.incrementAndGet(),
                user.getName(),
                user.getEmail()
        );
        users.add(userToSave);
        return userToSave;
    }

    @Override
    public Optional<UserEntity> update(Long id, UserEntity user) {
        for (int i = 0; i < users.size(); i++) {
            UserEntity existingUser = users.get(i);
            if (existingUser.getId().equals(id)) {
                UserEntity updatedUser = new UserEntity(id, user.getName(), user.getEmail());
                users.set(i, updatedUser);
                return Optional.of(updatedUser);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteById(Long id) {
        return users.removeIf(user -> user.getId().equals(id));
    }
}
