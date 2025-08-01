package edu.uic.shibboleth.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "uid")
    private String uid; // oid:0.9.2342.19200300.100.1.1 - User's NetID

    // Core Attributes (Required)
    @Column(name = "display_name")
    private String displayName; // oid:2.16.840.1.113730.3.1.241 - Preferred name for display

    @Column(name = "edu_person_primary_affiliation")
    private String eduPersonPrimaryAffiliation; // oid:1.3.6.1.4.1.5923.1.1.1.5 - Primary relationship (student,
                                                // faculty, staff, etc.)

    @Column(name = "edu_person_principal_name")
    private String eduPersonPrincipalName; // oid:1.3.6.1.4.1.5923.1.1.1.6 - eduPerson per Internet2 and EDUCAUSE

    @Column(name = "edu_person_scoped_affiliation")
    private String eduPersonScopedAffiliation; // oid:1.3.6.1.4.1.5923.1.1.1.9 - Organization relationship with user

    @Column(name = "given_name")
    private String givenName; // oid:2.5.4.42 - Given name of a person

    @Column(name = "itrust_suppress")
    private Boolean iTrustSuppress; // oid:1.3.6.1.4.1.11483.101.3 - FERPA suppression election

    @Column(name = "mail")
    private String mail; // oid:0.9.2342.19200300.100.1.3 - Preferred email address

    @Column(name = "organization_name")
    private String organizationName; // oid:2.5.4.10 - Standard name of top-level organization

    @Column(name = "surname")
    private String surname; // oid:2.5.4.4 - Surname or family name (maps to sn)

    // Optional Attributes
    @Column(name = "itrust_home_dept_code")
    private String iTrustHomeDeptCode; // oid:1.3.6.1.4.1.11483.101.5 - Home department code in Banner format

    @Column(name = "itrust_uin")
    private String iTrustUIN; // oid:1.3.6.1.4.1.11483.101.4 - University ID number

    @Column(name = "organizational_unit")
    private String organizationalUnit; // oid:2.5.4.11 - Organizational Unit or primary department

    @Column(name = "title")
    private String title; // oid:2.5.4.12 - Job title of person

    // Default constructor
    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEduPersonPrimaryAffiliation() {
        return eduPersonPrimaryAffiliation;
    }

    public void setEduPersonPrimaryAffiliation(String eduPersonPrimaryAffiliation) {
        this.eduPersonPrimaryAffiliation = eduPersonPrimaryAffiliation;
    }

    public String getEduPersonPrincipalName() {
        return eduPersonPrincipalName;
    }

    public void setEduPersonPrincipalName(String eduPersonPrincipalName) {
        this.eduPersonPrincipalName = eduPersonPrincipalName;
    }

    public String getEduPersonScopedAffiliation() {
        return eduPersonScopedAffiliation;
    }

    public void setEduPersonScopedAffiliation(String eduPersonScopedAffiliation) {
        this.eduPersonScopedAffiliation = eduPersonScopedAffiliation;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public Boolean getITrustSuppress() {
        return iTrustSuppress;
    }

    public void setITrustSuppress(Boolean iTrustSuppress) {
        this.iTrustSuppress = iTrustSuppress;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getITrustHomeDeptCode() {
        return iTrustHomeDeptCode;
    }

    public void setITrustHomeDeptCode(String iTrustHomeDeptCode) {
        this.iTrustHomeDeptCode = iTrustHomeDeptCode;
    }

    public String getITrustUIN() {
        return iTrustUIN;
    }

    public void setITrustUIN(String iTrustUIN) {
        this.iTrustUIN = iTrustUIN;
    }

    public String getOrganizationalUnit() {
        return organizationalUnit;
    }

    public void setOrganizationalUnit(String organizationalUnit) {
        this.organizationalUnit = organizationalUnit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", displayName='" + displayName + '\'' +
                ", givenName='" + givenName + '\'' +
                ", surname='" + surname + '\'' +
                ", mail='" + mail + '\'' +
                ", eduPersonPrimaryAffiliation='" + eduPersonPrimaryAffiliation + '\'' +
                ", organizationName='" + organizationName + '\'' +
                ", organizationalUnit='" + organizationalUnit + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}