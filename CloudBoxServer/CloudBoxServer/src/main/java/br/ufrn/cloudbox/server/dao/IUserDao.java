package br.ufrn.cloudbox.server.dao;

import br.ufrn.cloudbox.model.User;

public interface IUserDao {

	public Long register(User user);
	public User findByEmail(String email);
	public User findByEmailAndPassword(String email, String password);
	
}
