
// Anaëlle Morey Yann Renard

import extensions.File;
import extensions.CSVFile;
 
class Jeu extends Program {

    void jouer(){

        println("vouler vous jouer ? :");
        println(" oui = y");
        println(" non = n ");
        char repJ = readChar();

        while(repJ != 'y'){ // si le joueur a une autre reponse que oui(y)
            println("vouler vous jouer ? :");
            println(" oui = y");
            println(" non = n ");
            repJ = readChar();

        }
        intro(); //lancer l'introduction
        //clearScreen();
        affichage(); // affichage de l'interface
    }

    void intro(){
        // et je pense juste le mettre dans le code de jouer, ca va etre plus simple
        // a mettre ds une bdd je pense mais oklm"
        println("");
        println("Bienvenue a toi petit entrepreneur, que dirais tu de créer un empire de cookies ? ");
        println("voici quelque choses a savoir : ");  // explication rapide du fonctionnement du jeu
    }

    void affichage(){
        // idée : nv terminal pour que ce soit clean 
        println("");
        print("Argent : " /*+ argent*/);
        print("Gain de la journée :" /*+ gains*/);
        print(" Date : " /* + date */);
      /*   if( estTaxé == true){
            print( "Taxes :" + taxes);
        }
        if( aDesEmployer == true){
            print(" Nombre d'employers : " /* + nbrEmployer );
        }
        if( coutMatierePremier == true){
            print(" Coût des matières première : " /* + coutMatPrem );
        } */
       println();
    }

    void question(){
        
    }

    void algorithm(){

        //clearScreen();
        jouer();

    }
}