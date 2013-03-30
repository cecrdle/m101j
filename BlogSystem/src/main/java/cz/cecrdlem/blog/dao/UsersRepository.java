package cz.cecrdlem.blog.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import cz.cecrdlem.blog.to.User;

public interface UsersRepository extends PagingAndSortingRepository<User,String> {
}
