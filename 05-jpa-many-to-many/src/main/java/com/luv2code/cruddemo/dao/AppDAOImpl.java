package com.luv2code.cruddemo.dao;

import com.luv2code.cruddemo.entity.Course;
import com.luv2code.cruddemo.entity.Instructor;
import com.luv2code.cruddemo.entity.InstructorDetail;
import com.luv2code.cruddemo.entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        // get the course
        List<Course> courses = tempInstructor.getCourses();
        // break association of all courses for the instructor
        for (Course tempCourse: courses) {
            tempCourse.setInstructor(null);
        }
        // delete the instructor
        entityManager.remove(tempInstructor);
    }
    @Override
    public InstructorDetail findInstructorDetailById(int theId) {
        return entityManager.find(InstructorDetail.class, theId);
    }

    @Override
    @Transactional
    public void deleteInstructorDetailById(int theId) {
        // retrieve instructor detail
        InstructorDetail tempInstructorDetail = entityManager.find(InstructorDetail.class, theId);
        // remove the associated object reference
        // break bi-directional link
        //
        tempInstructorDetail.getInstructor().setInstructorDetail(null);
        // delete the instructor detail
        entityManager.remove(tempInstructorDetail);
    }

    @Override
    public List<Course> findCoursesByInstructorId(int theId) {
        // use HQL to retrieve the courses of an instructor if the courses are lazy loading
        // create the query
        TypedQuery<Course> query = entityManager.createQuery(
                "FROM Course WHERE instructor.id = :data", Course.class);
        query.setParameter("data", theId);
        // execute the query and return the result;
        return query.getResultList();
    }

    @Override
    public Instructor findInstructorByIdJoinFetch(int theId) {
        // create query
        TypedQuery<Instructor> query = entityManager.createQuery(
                "SELECT i FROM Instructor i " +
                        "JOIN FETCH i.courses " +
                        "JOIN FETCH i.instructorDetail " +
                        "WHERE i.id = :data", Instructor.class);
        // set the parameter
        query.setParameter("data", theId);
        // execute the query and return the result
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void update(Instructor tempInstructor) {
        entityManager.merge(tempInstructor);
    }

    @Override
    @Transactional
    public void update(Course tempCourse) {
        entityManager.merge(tempCourse);
    }

    @Override
    public Course findCourseById(int theId) {
        return entityManager.find(Course.class, theId);
    }

    @Override
    @Transactional
    public void deleteCourseById(int theId) {
        // retrieve the course
        Course tempCourse = entityManager.find(Course.class, theId);
        // delete the course
        entityManager.remove(tempCourse);
    }

    @Override
    @Transactional
    public void save(Course theCourse) {
        entityManager.persist(theCourse);
    }

    @Override
    public Course findCourseAndReviewsByCourseId(int theId) {
        // create query
        TypedQuery<Course> query = entityManager.createQuery(
                "SELECT c FROM Course c " +
                        "JOIN FETCH c.reviews " +
                        "WHERE c.id=:data", Course.class);
        query.setParameter("data", theId);
        // execute query and return the result
        return query.getSingleResult();
    }

    @Override
    public Course findCourseAndStudentsByCourseId(int theId) {
        // create query
        TypedQuery<Course> query = entityManager.createQuery(
                "SELECT c FROM Course c JOIN FETCH c.students WHERE c.id=:data",
                Course.class);
        query.setParameter("data", theId);
        // execute query
        return query.getSingleResult();
    }

    @Override
    public Student findStudentAndCoursesByStudentId(int theId) {
        // create query
        TypedQuery<Student> query = entityManager.createQuery(
                "SELECT s FROM Student s JOIN FETCH s.courses WHERE s.id=:data",
                Student.class);
        query.setParameter("data", theId);
        // execute query
        return query.getSingleResult();
    }

    @Override
    @Transactional
    public void update(Student tempStudent) {
        entityManager.merge(tempStudent);
    }

    @Override
    @Transactional
    public void deleteStudentById(int theId) {
        // retrieve the student
        Student tempStudent = entityManager.find(Student.class, theId);
        if (tempStudent != null) {
            // get the courses
            List<Course> courses = tempStudent.getCourses();
            for (Course tempCourse: courses) {
                tempCourse.getStudents().remove(tempStudent);
            }
            entityManager.remove(tempStudent);
        }
    }
}
