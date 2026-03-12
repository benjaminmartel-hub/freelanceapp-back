package com.freelanceos.freelanceappback.infrastructure.persistence.adapter;

import com.freelanceos.freelanceappback.domain.model.auth.AuthProvider;
import com.freelanceos.freelanceappback.domain.ports.out.AuthAccountRepository;
import com.freelanceos.freelanceappback.infrastructure.persistence.entity.AuthAccountEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.sql.init.mode=never"
})
@Import(JpaAuthAccountRepositoryAdapter.class)
class JpaAuthAccountRepositoryAdapterTest {

    @Autowired
    private AuthAccountRepository authAccountRepository;

    @Test
    void saveAndFindByUsernameShouldReturnAccount() {
        AuthAccountEntity saved = authAccountRepository.save(
                new AuthAccountEntity(null, "demo", "hash", AuthProvider.LOCAL, null)
        );

        Optional<AuthAccountEntity> found = authAccountRepository.findByUsername("demo");

        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(saved.getId());
        assertThat(found.get().getUsername()).isEqualTo("demo");
    }

    @Test
    void findByProviderAndProviderUserIdShouldReturnAccount() {
        authAccountRepository.save(new AuthAccountEntity(null, "oauth-user", null, AuthProvider.GOOGLE, "google-123"));

        Optional<AuthAccountEntity> found = authAccountRepository.findByProviderAndProviderUserId(AuthProvider.GOOGLE, "google-123");

        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("oauth-user");
    }
}
