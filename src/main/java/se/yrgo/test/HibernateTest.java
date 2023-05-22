package se.yrgo.test;

import jakarta.persistence.*;

import se.yrgo.domain.Student;
import se.yrgo.domain.Subject;
import se.yrgo.domain.Tutor;

import java.util.List;

public class HibernateTest {
    public static EntityManagerFactory emf = Persistence.createEntityManagerFactory("databaseConfig");

    public static void main(String[] args) {
        setUpData();
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();


        // Task-1- Navigating across relationships (Using member of)
        //Subject science = em.find(Subject.class, 2);
        Subject science = em.createQuery("from Subject as subject where subject.subjectName = 'Science'", Subject.class).getSingleResult();
        Query query2 = em.createQuery("select tutor.teachingGroup from Tutor as tutor where tutor in (select tutor from Tutor tutor where :subject member of tutor.subjectsToTeach)");
        query2.setParameter("subject", science);
        List<Student> students = query2.getResultList();
        for (Student student : students) {
            System.out.println(student);
        }

        //Task-2- Report Query- Multiple fields (Use join)
        List<Object[]> results = em.createQuery("SELECT student.name AS student_name, tutor.name AS tutor_name from Tutor tutor JOIN tutor.teachingGroup as student").getResultList();

        for (Object[] obj : results) {
            System.out.println("studentName " + obj[0]);
            System.out.println("tutorName:" + obj[1]);
        }

        // Task-3-Report Query- Aggregation
        Double avgNumberOfSemesters = (Double) em.createQuery("select avg(subject.numberOfSemesters) from Subject subject").getSingleResult();
        System.out.println("The average legth of semester is " + avgNumberOfSemesters + " termin.");

        // Task-4-Query With Aggregation
        Integer maxSalary = (Integer) em.createQuery("select max(tutor.salary) from Tutor tutor").getSingleResult();
        System.out.println("Tutor with max salary is  " + maxSalary + " kr/month");

        // Task-5- NamedQuery
        TypedQuery<Tutor> query = em.createNamedQuery("findTutorsWithHighSalary", Tutor.class);
        List<Tutor> tutors = query.getResultList();
        for (Tutor tutor : tutors) {
            System.out.println(tutor);
        }

        tx.commit();
        em.close();
    }

    public static void setUpData() {
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();


        Subject mathematics = new Subject("Mathematics", 2);
        Subject science = new Subject("Science", 2);
        Subject programming = new Subject("Programming", 3);
        em.persist(mathematics);
        em.persist(science);
        em.persist(programming);

        Tutor t1 = new Tutor("ABC123", "Johan Smith", 40000);
        t1.addSubjectsToTeach(mathematics);
        t1.addSubjectsToTeach(science);


        Tutor t2 = new Tutor("DEF456", "Sara Svensson", 20000);
        t2.addSubjectsToTeach(mathematics);
        t2.addSubjectsToTeach(science);

        // This tutor is the only tutor who can teach History
        Tutor t3 = new Tutor("GHI678", "Karin Lindberg", 0);
        t3.addSubjectsToTeach(programming);

        em.persist(t1);
        em.persist(t2);
        em.persist(t3);


        t1.createStudentAndAddtoTeachingGroup("Jimi Hendriks", "1-HEN-2019", "Street 1", "city 2", "1212");
        t1.createStudentAndAddtoTeachingGroup("Bruce Lee", "2-LEE-2019", "Street 2", "city 2", "2323");
        t3.createStudentAndAddtoTeachingGroup("Roger Waters", "3-WAT-2018", "Street 3", "city 3", "34343");

        tx.commit();
        em.close();
    }


}
