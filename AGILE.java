/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agile;

/**
 *
 * @author Ronaldo
 */
public class AGILE {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String json = FileReader.loadFileIntoString("json/catalog.json", "utf-8");
        JSONObject commande = JSONObject.fromObject(json);
        
        String Porsche = FileReader.loadFileIntoString("json/catalog.json", "utf-8");
        JSONObject voitures = JSONObject.fromObject(json);
        
        
        
        
        
        String sexe = null;
        String vehicule = null;
        String date = null;
        String age = null;
        String ville = null;
        String burinage = null;
        double prixVoiture;
        boolean garage = false;
        boolean alarme = false;
        boolean cours = false;
        boolean premContrat = false;
        String xp = null;
        String experience = null;
        int experienceEntier;
        int dureeContrat;
                
        boolean assurable = true;
        int ageEntier;
        double options;
        int compteur = 0;
        int compteur2 = 0;
        double montantBase;
        double montant;
        
     
        sexe = commande.getString("sexe");
        vehicule = commande.getString("modele");
        date = commande.getString("date_de_naissance");
        xp = commande.getString("date_fin_cours_de_conduite");
        options = commande.getDouble("valeur_des_options");
        ville = commande.getString("ville");
        burinage = commande.getString("burinage");
        garage = commande.getBoolean("garage_interieur");
        alarme = commande.getBoolean("systeme_alarme");
        cours = commande.getBoolean("cours_de_conduite_reconnus_par_CAA");
        premContrat = commande.getBoolean("premier_contrat");
        prixVoiture = voitures.getDouble(vehicule);
        dureeContrat = commande.getInt("duree_contrat");
        
        
        for(int i=0; i<4 ; i++){
           compteur++;
           age = age + date.charAt(i);
            if(compteur == 3){
              ageEntier = Integer.parseInt(age); 
            }
        }
        
        for(int i=0; i<4 ; i++){
           compteur2++;
           experience = experience + xp.charAt(i);
            if(compteur2 == 3){
              experienceEntier = Integer.parseInt(experience); 
            }
        }
        
        if(dureeContrat == 3){
            prixVoiture = prixVoiture - (0.15*prixVoiture);
        }
        montantBase = 0.09 * prixVoiture;
        montant = montantBase + 0.1*options;
        
        if(ville.equals("Montréal") || ville.equals("Longueuil")){
            montant = montant + 200;
        }
        
        if(burinage.equals("Sherlock")){
            montant = montant - 250;
        }
        
        if(sexe.equals("F")){
            montant = montant - 1000;
        }
        
        if(garage){
            montant = montant - 500;
        }
        
        if(alarme){
            montant = montant - 500;
        }
        
        if(cours){
            montant = montant - 100;
        }
        
        if(garage){
            montant = montant - 500;
        }
        
        if (sexe.equals("M") && ageEntier < 35 ){
            montant = montant + 1000;
        }
        
        if(premContrat){
            montant = montant + 2000;
        }
        
        
        //commentaire pour verifier gitHUB
        
        
}
}