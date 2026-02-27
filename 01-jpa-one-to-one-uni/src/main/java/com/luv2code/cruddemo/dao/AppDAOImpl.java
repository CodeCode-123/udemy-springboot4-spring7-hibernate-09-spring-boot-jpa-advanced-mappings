package com.luv2code.cruddemo.dao;

import com.luv2code.cruddemo.entity.Instructor;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public class AppDAOImpl implements AppDAO {
    // define field for entity manager
    private EntityManager entityManager;
    // inject entity manager using constructor injection
    @Autowired
    public AppDAOImpl(EntityManager theEntityManager) {
        this.entityManager = theEntityManager;
    }
    @Override
    @Transactional
    public void save(Instructor theInstructor) {
        // This will ALSO save the details object,
        // Because of CascadeType.ALL
        entityManager.persist(theInstructor);
    }
    @Override
    public Instructor findInstructorById(int theId) {
        // This will ALSO retrieve the instructor details object
        // Because of default behavior of @OneToOne fetch type is eager
        return entityManager.find(Instructor.class, theId);
    }
    @Override
    @Transactional
    public void deleteInstructorById(int theId) {
        // retrieve the instructor
        Instructor tempInstructor = entityManager.find(Instructor.class, theId);
        // delete the instructor
        if (tempInstructor != null) {
            entityManager.remove(tempInstructor);
        }
    }
}
