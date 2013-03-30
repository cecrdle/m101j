package cz.cecrdlem.blog.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import cz.cecrdlem.blog.to.Session;

public interface SessionsRepository extends PagingAndSortingRepository<Session,String> {
	
    public Session findByUsername(String username);

}
