package com.buddhadata.sandbox.neo4j.ogm.intro.relationship;

import com.buddhadata.sandbox.neo4j.ogm.intro.node.Person;
import org.neo4j.ogm.annotation.*;

/**
 * A Neo4J relationship representing a marriage
 */
@RelationshipEntity(type = "MARRIED")
public class Married {

    /**
     * Internal Neo4J id of the node
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * If divorced, what year was the divorce finalized
     */
    private Integer yearDivorced;

    /**
     * the year married
     */
    private Integer yearMarried;

    /**
     * the wife in the marriage
     */
    @StartNode
    private Person wife;

    /**
     * the husband in the marriage
     */
    @EndNode
    private Person husband;

    /**
     * Constructor
     */
    public Married () {}

    /**
     * Constructor
     * @param wife the wife in the marriage
     * @param husband the husband in the marriage
     * @param yearMarried the year married
     * @param yearDivorced the year divorced
     */
    public Married (Person wife,
                    Person husband,
                    int yearMarried,
                    Integer yearDivorced) {
        this.wife = wife;
        this.husband = husband;
        this.yearMarried = yearMarried;
        if (yearDivorced != null) {
            this.yearDivorced = yearDivorced;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYearDivorced() {
        return yearDivorced;
    }

    public void setYearDivorced(Integer yearDivorced) {
        this.yearDivorced = yearDivorced;
    }

    public Integer getYearMarried() {
        return yearMarried;
    }

    public void setYearMarried(Integer yearMarried) {
        this.yearMarried = yearMarried;
    }

    public Person getWife() {
        return wife;
    }

    public void setWife(Person wife) {
        this.wife = wife;
    }

    public Person getHusband() {
        return husband;
    }

    public void setHusband(Person husband) {
        this.husband = husband;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Married married = (Married) o;

        if (yearDivorced != null ? !yearDivorced.equals(married.yearDivorced) : married.yearDivorced != null)
            return false;
        if (!yearMarried.equals(married.yearMarried)) return false;
        if (!wife.equals(married.wife)) return false;
        return husband.equals(married.husband);

    }

    @Override
    public int hashCode() {
        int result = yearDivorced != null ? yearDivorced.hashCode() : 0;
        result = 31 * result + yearMarried.hashCode();
        result = 31 * result + wife.hashCode();
        result = 31 * result + husband.hashCode();
        return result;
    }
}
