/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.altamira.erp.entity.dao;

import br.com.altamira.erp.entity.model.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Alessandro
 */
public class UserDao {
    @PersistenceContext(name = "persistence/altamira-bpm", unitName = "altamira-bpm-PU")
    private EntityManager entityManager;
    
    public List<User> list() {
        return (List<User>) entityManager
                .createNamedQuery("User.list", User.class)
                .getResultList();
    }

    public User find(String name) {
        return entityManager.find(User.class, name);
    }

    public User find(User user) {
        return find(user.getName());
    }

    public User create(User user) {
        entityManager.persist(user);

        return user;
    }

    public User update(User user) {
        return entityManager.merge(user);
    }

    public User remove(User user) {
        entityManager.remove(user);

        return user;
    }    
}
