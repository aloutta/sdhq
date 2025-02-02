package com.github.aloutta.backend.service;

import com.github.aloutta.backend.api.UserApiDelegate;
import com.github.aloutta.backend.jooq.tables.daos.UserDao;
import com.github.aloutta.backend.mapper.UserMapper;
import com.github.aloutta.backend.model.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserApiDelegate {

  private final UserDao userDao;
  private final UserMapper mapper;

  @Override
  public ResponseEntity<List<User>> listUsers() {

    var pojos = userDao.findAll();
    var models = mapper.toModelList(pojos);

    return ResponseEntity.ok(models);
  }
}
