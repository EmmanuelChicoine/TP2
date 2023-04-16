package server;

import java.io.Serializable;

public class Command implements Serializable {
    private String commande; // Ce que le client doit transmettre au serveur

    public Command(String commande) {
        this.commande = commande;
    }

    public String getCommande() {
        return commande;
    }
}
