package com.github.aloutta.backend.mapper;

import java.util.List;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

  com.github.aloutta.backend.jooq.tables.pojos.User toPojo(
      com.github.aloutta.backend.model.User model);

  com.github.aloutta.backend.model.User toModel(
      com.github.aloutta.backend.jooq.tables.pojos.User pojo);

  default List<com.github.aloutta.backend.model.User> toModelList(
      List<com.github.aloutta.backend.jooq.tables.pojos.User> pojos) {
    return pojos.stream().map(this::toModel).toList();
  }
}
