/*
 * Copyright (c) 2018  Scott C. Sosna  ALL RIGHTS RESERVED
 */

package com.buddhadata.sandbox.neo4j.ogm.intro;

import com.buddhadata.sandbox.neo4j.ogm.intro.node.Person;
import com.buddhadata.sandbox.neo4j.ogm.intro.relationship.Married;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.cypher.BooleanOperator;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;
import org.neo4j.ogm.metadata.schema.Node;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.transaction.Transaction;

import java.time.Year;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * Method for doing work≥
     */
    private void process () {
        //  For demo purposes, create session and purge to cleanup whatever you have
        Session session = sessionFactory.openSession();
        session.purgeDatabase();

        //  Load the data via OGM
        load (session);

        //  OGM Filter, querying by birth year
        System.out.println ("Querying nodes by single OGM filter");
        queryByFilter (1977, session).forEach (one -> System.out.println (one.getName() + " was born in " + one.getBirthYear()));

        //  OGM multi-part filter, querying by birth year or name greater than the given letter.
        System.out.println ("Querying nodes by multiple OGM filters.");
        queryByMultipleFilters(1977, "M", session).forEach (one -> System.out.println (one.getName() + " was born in " + one.getBirthYear()));

        //  OGM query using Cypher, finding those married to the name passed in.
        System.out.println ("Querying nodes using a Cypher statement.");
        queryByCypher("Michael Blevins", session).forEach (one -> System.out.println (one.getName() + " at some point was married to Michael Blevins"));
    }


    /**
     * Load the data.
     */
    private void load (Session session) {

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

    /**
     * Example of querying using an OGM filter.
     * @param birthYear a person's birth year
     * @param session Neo4J session
     * @return collection of zero or more persons returned by the filter
     */
    private Iterable<Person> queryByFilter (int birthYear,
                                            Session session) {

        //  Create an OGM filter for the birthYear property.
        Filter filter = new Filter ("birthYear", ComparisonOperator.EQUALS, birthYear);

        //  Load all Persons with the given birth year.
        return session.loadAll (Person.class, filter);
    }

    /**
     * Create a composite filter for querying Neo4J via OGM
     * @param birthYear a person's birth year
     * @param startingLetter the letter for which a person's name is greater than
     * @param session Neo4J session
     * @return collection of zero or more persons returned by the filters
     */
    private Iterable<Person> queryByMultipleFilters (int birthYear,
                                                     String startingLetter,
                                                     Session session) {

        //  Filter either by the birth year or name greater than the starting letter
        Filters composite = new Filters();
        Filter filter = new Filter ("birthYear", ComparisonOperator.EQUALS, birthYear);
        composite.add(filter);
        filter = new Filter ("name", ComparisonOperator.GREATER_THAN, startingLetter);
        filter.setBooleanOperator(BooleanOperator.OR);
        composite.add(filter);

        //  Load all Persons which match the composite filter.
        return session.loadAll (Person.class, composite);
    }

    /**
     * Query Neo4J by providing a Cypher statement and parameters to plug in
     * @param marriedTo the destination node of the MARRIED relationship
     * @param session the Neo4J session
     * @return who's married to the name specified
     */
    private Iterable<Person> queryByCypher (String marriedTo,
                                            Session session) {

        //  Create/load a map to hold the parameter
        Map<String, Object> params = new HashMap<>(1);
        params.put ("name", marriedTo);

        //  Execute query and return the other side of the married relationship
        String cypher = "MATCH (w:Person)-[:MARRIED]->(h:Person {name:$name}) RETURN w";
        return session.query (Person.class, cypher, params);
    }
}
