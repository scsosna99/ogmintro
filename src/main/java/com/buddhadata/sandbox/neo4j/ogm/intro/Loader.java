/*
 * Copyright (c) 2018  Scott C. Sosna  ALL RIGHTS RESERVED
 */

package com.buddhadata.sandbox.neo4j.ogm.intro;

import com.buddhadata.sandbox.neo4j.ogm.intro.node.Person;
import com.buddhadata.sandbox.neo4j.ogm.intro.relationship.Married;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.transaction.Transaction;

import java.time.Year;
import java.util.List;

/**
 * OGM Intro for loading data
 */
public class Loader {

    /**
     * Session factory for connecting to Neo4j database
     */
    private final SessionFactory sessionFactory;

    //  Configuration info for connecting to the Neo4J database
    static private final String SERVER_URI = "bolt://localhost";
    static private final String SERVER_USERNAME = "neo4j";
    static private final String SERVER_PASSWORD = "password";


    /**
     * Constructor
     */
    public Loader() {
        //  Define session factory for connecting to Neo4j database
        Configuration configuration = new Configuration.Builder().uri(SERVER_URI).credentials(SERVER_USERNAME, SERVER_PASSWORD).build();
        sessionFactory = new SessionFactory(configuration, "com.buddhadata.sandbox.neo4j.ogm.intro.node", "com.buddhadata.sandbox.neo4j.ogm.intro.relationship");
    }


    /**
     * Main method for starting Java program
     * @param args command line arguments
     */
    public static void main (String[] args) {

        //  Create an instance of the class and process the file.
        new Loader().process();
    }


    /**
     * Process the file by reading lines one-by and break into constituent components.
     */
    private void process () {

        //  For demo purposes, create session and purge to cleanup whatever you have
        Session session = sessionFactory.openSession();
        session.purgeDatabase();

        //  All work done in single transaction.
        Transaction txn = session.beginTransaction();

        //  Create all persons.
        Person Carol = new Person ("Carol Maureen", 1945);
        Person Courtney = new Person ("Courtney Janice", 1945);
        Person Esme = new Person ("Esme Alexis", 1981);
        Person Gabe = new Person ("Gabriel Josiah", 1979);
        Person Gail = new Person ("Gail Ann", 1942);
        Person Jeremy = new Person ("Jeremy Douglas", 1969);
        Person Jesse = new Person ("Jesse Lucas", 1977);
        Person Kelly = new Person ("Kelly Leigh", 1977);
        Person Mike = new Person ("Michael Blevins", 1945);
        Person Scott = new Person ("Scott Christoper", 1965);
        Person Steve = new Person ("Steven Lester", 1950);
        Person Zane = new Person ("Michael Zane", 1973);

        //  Add children to each parent.
        List<Person> children = Carol.getChildren();
        children.add (Scott);
        children.add (Courtney);
        children.add (Jeremy);
        children.add (Jesse);
        children.add (Gabe);
        children.add (Esme);
        children = Mike.getChildren();
        children.add (Scott);
        children.add (Courtney);
        children.add (Jeremy);
        children.add (Zane);
        children.add (Kelly);
        children = Gail.getChildren();
        children.add (Zane);
        children.add (Kelly);
        children = Steve.getChildren();
        children.add (Jesse);
        children.add (Gabe);
        children.add (Esme);


        //  Save to database
        session.save (Carol);
        session.save (Courtney);
        session.save (Esme);
        session.save (Gabe);
        session.save (Gail);
        session.save (Jeremy);
        session.save (Jesse);
        session.save (Kelly);
        session.save (Mike);
        session.save (Scott);
        session.save (Steve);
        session.save (Zane);

        //  Create all marriages and save to database
        session.save (new Married(Carol, Mike, 1964, 1973));
        session.save (new Married(Gail, Mike, 1973, 1992));
        session.save (new Married(Carol, Steve, 1976, null));

        //  Commit the transaction.
        txn.commit();
    }
}
