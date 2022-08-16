package com.ptjp.application.data.service;

import com.ptjp.application.data.entity.Users;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    private final UsersRepository repository;

    @Autowired
    public UsersService(UsersRepository repository) {
        this.repository = repository;
    }

    public Optional<Users> get(UUID id) {
        return repository.findById(id);
    }

    public Users update(Users entity) {
        return repository.save(entity);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }

    public Page<Users> list(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public int count() {
        return (int) repository.count();
    }

}
