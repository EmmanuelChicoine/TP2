package models;

import java.io.Serializable;

public class RegistrationForm implements Serializable {
    private String prenom; // Prénom de l'étudiant
    private String nom; // Nom de l'étudiant
    private String email; // Email de l'étudiant
    private String matricule; // Matricule de l'étudiant
    private Course course; // Cours auquel l'étudiant s'inscrit

    /**
     * Définit un formulaire. Toutes les caractéristiques sont données en paramètre.
     *
     * @param prenom prénom de l'étudiant
     * @param nom nom de fmaille de l'étudiant
     * @param email email de l'étudiant
     * @param matricule matricule de l'étudiant
     * @param course cours auquel l'étudiant s'inscrit
     */
    public RegistrationForm(String prenom, String nom, String email, String matricule, Course course) {
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.matricule = matricule;
        this.course = course;
    }

    /**
     * @return le prénom de l'étudiant
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * Change le prénom de l'étudiant
     *
     * @param prenom nouveau prénom
     */
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    /**
     * @return le nom de famille de l'étudiant
     */
    public String getNom() {
        return nom;
    }

    /**
     * Change le nom de famille de l'étudiant.
     *
     * @param nom nouveau nom de famille
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @return le email de l'étudiant
     */
    public String getEmail() {
        return email;
    }

    /**
     * Change le email de l'étudiant.
     *
     * @param email nouveau email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return le matricule de l'étudiant
     */
    public String getMatricule() {
        return matricule;
    }

    /**
     * Change le matricule de l'étudiant qui s'inscrit.
     *
     * @param matricule nouveau matricule
     */
    public void setMatricule(String matricule) {
        this.matricule = matricule;
    }

    /**
     * @return le cours du formulaire
     */
    public Course getCourse() {
        return course;
    }

    /**
     * Change le cours dans le formulaire.
     *
     * @param course nouveau cours
     */
    public void setCourse(Course course) {
        this.course = course;
    }

    /**
     * @return une string avec les information du formulaire : prénom, nom, email, matricule, cours
     */
    @Override
    public String toString() {
        return "InscriptionForm{" + "prenom='" + prenom + '\'' + ", nom='" + nom + '\'' + ", email='" + email + '\'' + ", matricule='" + matricule + '\'' + ", course='" + course + '\'' + '}';
    }
}