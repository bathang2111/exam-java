package co.lecompany.app.controllers;

import co.lecompany.app.data.entity.SamplePerson;
import co.lecompany.app.data.service.SamplePersonRepository;
import co.lecompany.app.exception.UserException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/users")
@Tag(name = "User Controller", description = "User Controller Description")
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    SamplePersonRepository repository;

//    @Operation(summary = "Get all user")
//    @GetMapping()
//    @Cacheable(value = "users")
//    public List<SamplePerson> getUsers() {
//        log.info("Get list users from database");
//        return repository.findAll();
//    }

    @Operation(summary = "Get user by firstName")
    @GetMapping("/{firstName}")
    @Cacheable(value = "users")
    public SamplePerson getUserByFirstName(@PathVariable(value = "firstName") String firstName) {
        log.info("Get user by firstNmae [{}]", firstName);
        var res = repository.findByFirstName(firstName)
                .orElseThrow(() -> new UserException(404, "User not found with firstName " + firstName));
        return res;
    }

    @Operation(summary = "Create new user")
    @PostMapping()
    @CacheEvict(value = "users", allEntries = true)
    public SamplePerson createUser(@RequestBody SamplePerson person) {
        log.info("Create new user with name: {} {}", person.getFirstName(), person.getLastName());
        var checkUser = repository.findByEmail(person.getEmail()).orElse(null);
        if (checkUser != null) {
            throw new UserException(400, String.format("Can not save user: Email [%s] already exists", person.getEmail()));
        }
        return repository.save(person);
    }

    @Operation(summary = "Update user by id")
    @PutMapping()
    @CacheEvict(value = "users", allEntries = true)
    public SamplePerson updateUser(@RequestBody SamplePerson person) {
        log.info("Update user with id: {}", person.getId());
        repository.findById(person.getId())
                .orElseThrow(() -> new UserException(404, "User not found with id " + person.getId()));
        repository.save(person);
        return person;
    }

    @Operation(summary = "Delete user by email")
    @DeleteMapping("/{email}")
    @Transactional
    @CacheEvict(value = "users", allEntries = true)
    public String deleteUser(@PathVariable(value = "email") String email) {
        log.info("Delete user by email: {} in db", email);
        var user = repository.findByEmail(email)
                .orElseThrow(() -> new UserException(404, "User not found with email: " + email));
        repository.deleteByEmail(email);
        return String.format("Delete user with email [%s] successfully", email);
    }

    @Operation(summary = "Get all user with pagination")
    @GetMapping()
    @Cacheable(value = "users", key = "#page")
    public Map<String, Object> getAllTutorials(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable paging = PageRequest.of(page, size);
        Page<SamplePerson> samplePeople = repository.findAll(paging);
        Map<String, Object> res = new HashMap<>();
        res.put("users", samplePeople.getContent());
        res.put("currentPage", samplePeople.getNumber());
        res.put("totalItems", samplePeople.getTotalElements());
        res.put("totalPages", samplePeople.getTotalPages());
        return res;
    }
}
