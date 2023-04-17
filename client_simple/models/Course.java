package models;

import java.io.Serializable;

public class Course implements Serializable {

    private String name; // Nom du cours
    private String code; // Numéro/code de cours
    private String session; // Session à laquelle il se déroule

    /**
     * Crée un cours en lui donnant ses trois caractéristiques en paramètre.
     *
     * @param name nom du cours
     * @param code code/numéro du cours
     * @param session session à laquelle il se déroule
     */
    public Course(String name, String code, String session) {
        this.name = name;
        this.code = code;
        this.session = session;
    }

    /**
     * @return le nom du cours
     */
    public String getName() {
        return name;
    }

    /**
     * Change le nom du cours
     *
     * @param name nouveau nom
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return le code du cours
     */
    public String getCode() {
        return code;
    }

    /**
     * Change le code du cours
     *
     * @param code nouveau code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return la session du cours
     */
    public String getSession() {
        return session;
    }

    /**
     * Change la session du cours
     *
     * @param session
     */
    public void setSession(String session) {
        this.session = session;
    }

    /**
     * @return une string avec le nom, le code et la session du cours
     */
    @Override
    public String toString() {
        return "Course{" +
                "name=" + name +
                ", code=" + code +
                ", session=" + session +
                '}';
    }
}