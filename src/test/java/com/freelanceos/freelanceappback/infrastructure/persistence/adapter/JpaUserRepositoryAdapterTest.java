package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.ports.out.UserRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
@Import(JpaUserRepositoryAdapter.class)
class JpaUserRepositoryAdapterTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void saveAndFindAllShouldPersistUsers() {
        userRepository.save(new UserEntity(null, "Alice", "alice@example.com"));
        userRepository.save(new UserEntity(null, "Bob", "bob@example.com"));

        List<UserEntity> users = userRepository.findAll();

        assertThat(users).hasSize(2);
        assertThat(users).extracting(UserEntity::getId).allMatch(id -> id != null);
    }

    @Test
    void findByIdShouldReturnUserWhenExisting() {
        UserEntity saved = userRepository.save(new UserEntity(null, "Alice", "alice@example.com"));

        Optional<UserEntity> result = userRepository.findById(saved.getId());

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(saved.getId());
        assertThat(result.get().getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void findByIdShouldReturnEmptyWhenMissing() {
        Optional<UserEntity> result = userRepository.findById(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void updateShouldReturnUpdatedUserWhenExisting() {
        UserEntity created = userRepository.save(new UserEntity(null, "Alice", "alice@example.com"));
        UserEntity update = new UserEntity(null, "Alice Updated", "alice.updated@example.com");

        Optional<UserEntity> updated = userRepository.update(created.getId(), update);

        assertThat(updated).isPresent();
        assertThat(updated.get().getName()).isEqualTo("Alice Updated");
        assertThat(updated.get().getEmail()).isEqualTo("alice.updated@example.com");
    }

    @Test
    void updateShouldReturnEmptyWhenMissing() {
        Optional<UserEntity> updated = userRepository.update(999L, new UserEntity(null, "Ghost", "ghost@example.com"));
        assertThat(updated).isEmpty();
    }

    @Test
    void deleteByIdShouldReturnTrueWhenDeletedAndFalseWhenMissing() {
        UserEntity created = userRepository.save(new UserEntity(null, "Alice", "alice@example.com"));

        boolean deleted = userRepository.deleteById(created.getId());
        boolean deletedAgain = userRepository.deleteById(created.getId());

        assertThat(deleted).isTrue();
        assertThat(deletedAgain).isFalse();
    }
}
