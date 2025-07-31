package edu.uic.uic_shibboleth.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User {

    @Id
    private String uid;

    private String displayName;
    private String givenName;
    private String surname; // maps to sn
    private String mail;
    private String eduPersonPrincipalName;
    private String eduPersonPrimaryAffiliation;
    private String eduPersonScopedAffiliation;
    private Boolean iTrustSuppress;
    private String organizationName;
    private String iTrustHomeDeptCode;
    private String iTrustUIN;
    private String organizationalUnit;
    private String title;

    public User() {}

    // Constructor, getters and setters

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getGivenName() { return givenName; }
    public void setGivenName(String givenName) { this.givenName = givenName; }

    public String getSurname() { return surname; }
    public void setSurname(String surname) { this.surname = surname; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }

    public String getEduPersonPrincipalName() { return eduPersonPrincipalName; }
    public void setEduPersonPrincipalName(String eduPersonPrincipalName) { this.eduPersonPrincipalName = eduPersonPrincipalName; }

    public String getEduPersonPrimaryAffiliation() { return eduPersonPrimaryAffiliation; }
    public void setEduPersonPrimaryAffiliation(String eduPersonPrimaryAffiliation) { this.eduPersonPrimaryAffiliation = eduPersonPrimaryAffiliation; }

    public String getEduPersonScopedAffiliation() { return eduPersonScopedAffiliation; }
    public void setEduPersonScopedAffiliation(String eduPersonScopedAffiliation) { this.eduPersonScopedAffiliation = eduPersonScopedAffiliation; }

    public Boolean getITrustSuppress() { return iTrustSuppress; }
    public void setITrustSuppress(Boolean iTrustSuppress) { this.iTrustSuppress = iTrustSuppress; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getITrustHomeDeptCode() { return iTrustHomeDeptCode; }
    public void setITrustHomeDeptCode(String iTrustHomeDeptCode) { this.iTrustHomeDeptCode = iTrustHomeDeptCode; }

    public String getITrustUIN() { return iTrustUIN; }
    public void setITrustUIN(String iTrustUIN) { this.iTrustUIN = iTrustUIN; }

    public String getOrganizationalUnit() { return organizationalUnit; }
    public void setOrganizationalUnit(String organizationalUnit) { this.organizationalUnit = organizationalUnit; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}