package co.lecompany.app.data.service;

import co.lecompany.app.data.entity.SamplePerson;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SamplePersonRepository extends JpaRepository<SamplePerson, UUID> {

    Optional<SamplePerson> findByFirstName(String firstName);

    void deleteByEmail(String email);

    Optional<SamplePerson> findByEmail(String email);
}