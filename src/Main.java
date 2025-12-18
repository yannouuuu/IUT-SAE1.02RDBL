import extensions.File;
import extensions.CSVFile;

class Main extends Program {

	final String CLEAR_SEQUENCE = "\033[H\033[2J";
	final char CSV_SEPARATOR = ',';
	final int ARGENT_DEPART = 200;
	final int GAIN_DEPART = 0;
	final int NB_COLONNES_SAVE = 10; // nombre de colonnes utilisées dans le fichier de sauvegarde CSV
	
	String ressourcesPrefix = "resources/";
	String questionsCsv;
	String cookiesCsv;
	String savesCsv;
	String logoAscii;

	// Point d'entree principal du programme
	void algorithm() {
		initialiserCheminsRessources();
		boucleMenuPrincipal();
	}

	// Configure les chemins vers les fichiers de ressources (CSV, texte)
	void initialiserCheminsRessources() {
		if (!repertoirePresentDansCourant("resources")) {
			ressourcesPrefix = "../resources/";
		}
		questionsCsv = ressourcesPrefix + "questions.csv";
		cookiesCsv = ressourcesPrefix + "cookies.csv";
		savesCsv = ressourcesPrefix + "saves.csv";
		logoAscii = ressourcesPrefix + "cookieslandascii.txt";
	}

	// Gere l'affichage et la navigation dans le menu principal
	void boucleMenuPrincipal() {
		boolean quitter = false;
		while (!quitter) {
			effacerTerminal();
			afficherLogo();
			afficherMenuPrincipal();
			int choix = lireEntierDansIntervalle(1, 5);
			OptionMenu option = optionDepuisChoix(choix);
			if (option == OptionMenu.NOUVELLE_PARTIE) {
				lancerNouvellePartie();
			} else if (option == OptionMenu.CHARGER) {
				chargerPartieDepuisFichier();
			} else if (option == OptionMenu.REGLES) {
				afficherRegles();
			} else if (option == OptionMenu.RESET) {
				initialiserSauvegardes();
			} else {
				quitter = true;
				afficherMessageSortie();
			}
		}
	}

	// Initialise et lance une nouvelle partie avec les donnees par defaut
	void lancerNouvellePartie(){
		CookieStat[] cookies = chargerCookies();
		Question[] questions = chargerQuestions();
		Partie partie = nouvellePartieInitiale(cookies);
		if (partie.cookie == null){
			println("Impossible d'initialiser la partie (aucun cookie disponible).");
			attendreValidationUtilisateur();
		} else {
			boucleJeu(partie, questions, partie.cookie);
		}
	}

	// Affiche les regles du jeu a l'ecran
	void afficherRegles() {
		effacerTerminal();
		println("=== REGLES DE COOKIESLAND ===");
		println("1. Repondez aux questions d'economie-gestion (4 choix possibles).");
		println("2. Une bonne reponse debloque un bonus (matiere, prix ou taxe).");
		println("3. Une mauvaise reponse applique un malus aleatoire.");
		println("4. Atteignez les paliers d'argent pour faire grandir votre empire.");
		println("5. Utilisez les ameliorations pour equilibrer production et fiscalite.");
		println("Retour au menu principal : appuyez sur ENTREE.");
		attendreValidationUtilisateur();
	}

	// Affiche le message de fin de programme
	void afficherMessageSortie() {
		println("Merci d'avoir teste CookiesLand !");
	}

	// Affiche les options du menu principal
	void afficherMenuPrincipal() {
		println("Choisissez une option :");
		println("1. Nouvelle partie");
		println("2. Charger une partie");
		println("3. Afficher les regles du jeu");
		println("4. Reinitialiser les sauvegardes");
		println("5. Quitter");
		print("> ");
	}

	// Affiche le logo ASCII du jeu
	void afficherLogo() {
		File logo = newFile(logoAscii);
		boolean affiche = false;
		while (ready(logo)) {
			println(readLine(logo));
			affiche = true;
		}
		if (!affiche) {
			println("[[[COOKIESLAND]]]");
		}
	}

	// Boucle principale du jeu : gere les tours, les questions et les evenements
	void boucleJeu(Partie partie, Question[] questions, CookieStat cookiestat){
		boolean jeuEnCours = true;
		while (jeuEnCours) {
			Question question = questions[(int) (random() * length(questions))];
			afficherEcranTour(partie, question, cookiestat);
	
			String reponse = demanderReponse();
			if (equals(reponse, "Q")){
				jeuEnCours = false;
			} else if (equals(reponse, "S")){
				sauvegarderPartie(partie);
				jeuEnCours = false;
			} else {
				boolean estCorrect = verifierReponse(reponse, question);
				
				afficherEcranResultat(estCorrect);
				
				if (estCorrect) {
					traiterBonus(partie);
				} else {
					traiterMalus(partie);
				}
				
				calculerFinDeTour(partie);
				
				if (partie.argent < 0) {
					effacerTerminal();
					println("Vous avez fait faillite !");
					jeuEnCours = false;
					attendreValidationUtilisateur();
				} else {
					partie.jour = partie.jour + 1;
				}
			}
		}
	}

	// Sauvegarde l'etat actuel de la partie dans le fichier CSV
	void sauvegarderPartie(Partie p) {
		String nomSauvegarde = demanderNomSauvegarde();
		
		CSVFile csv = loadCSV(savesCsv, CSV_SEPARATOR);
		int existingRows = rowCount(csv);
		
		String[][] data;
		int indexTrouve = -1;
		int i = 0;
		while (i < existingRows) {
			if (equals(getCell(csv, i, 0), nomSauvegarde)) {
				indexTrouve = i;
			}
			i = i + 1;
		}
		
		if (indexTrouve != -1) {
			data = csvVersTableau(csv);
			remplirLigneSauvegarde(data, indexTrouve, nomSauvegarde, p);
		} else {
			data = new String[existingRows + 1][NB_COLONNES_SAVE];
			copierDonnees(csv, data);
			remplirLigneSauvegarde(data, existingRows, nomSauvegarde, p);
		}
		
		saveCSV(data, savesCsv);
		println("Partie '" + nomSauvegarde + "' sauvegardee !");
		attendreValidationUtilisateur();
	}

	// Demande a l'utilisateur de saisir un nom pour sa sauvegarde
	String demanderNomSauvegarde() {
		print("Entrez le nom de votre sauvegarde : ");
		return readString();
	}

	// Remplit une ligne du tableau de sauvegarde avec les donnees de la partie
	void remplirLigneSauvegarde(String[][] data, int ligne, String nom, Partie p) {
		data[ligne][0] = nom;
		data[ligne][1] = "" + p.jour;
		data[ligne][2] = "" + p.argent;
		data[ligne][3] = "" + p.gainJour;
		CookieStat c = p.cookie;
		if (c != null) {
			data[ligne][4] = c.id;
			data[ligne][5] = c.nom;
			data[ligne][6] = "" + c.matiere;
			data[ligne][7] = "" + c.prix;
			data[ligne][8] = "" + c.taxe;
		} else {
			data[ligne][4] = "NULL";
			data[ligne][5] = "";
			data[ligne][6] = "0";
			data[ligne][7] = "0";
			data[ligne][8] = "0";
		}
	}

	// Copie les donnees d'un fichier CSV vers un tableau de chaines
	void copierDonnees(CSVFile source, String[][] dest) {
		int rows = rowCount(source);
		int i = 0;
		while (i < rows) {
			int j = 0;
			while (j < NB_COLONNES_SAVE) {
				dest[i][j] = getCell(source, i, j);
				j = j + 1;
			}
			i = i + 1;
		}
	}
	
	// Convertit un fichier CSV en tableau de chaines
	String[][] csvVersTableau(CSVFile source) {
		int rows = rowCount(source);
		String[][] dest = new String[rows][NB_COLONNES_SAVE];
		copierDonnees(source, dest);
		return dest;
	}

	// Charge une partie existante depuis le fichier de sauvegarde
	void chargerPartieDepuisFichier() {
		CSVFile csv = loadCSV(savesCsv, CSV_SEPARATOR);
		int rows = rowCount(csv);
		if (rows == 0) {
			println("Aucune sauvegarde disponible.");
			attendreValidationUtilisateur();
		} else {
			println("Sauvegardes disponibles :");
			int i = 0;
			while (i < rows) {
				println((i + 1) + ". " + getCell(csv, i, 0) + " (Jour " + getCell(csv, i, 1) + ")");
				i = i + 1;
			}
			
			int choix = lireEntierDansIntervalle(1, rows);
			int index = choix - 1;
			
			Partie p = new Partie();
			p.jour = entierDepuisTexte(getCell(csv, index, 1));
			p.argent = entierDepuisTexte(getCell(csv, index, 2));
			p.gainJour = entierDepuisTexte(getCell(csv, index, 3));
			
			String cookieId = getCell(csv, index, 4);
			CookieStat c = new CookieStat();
			if (!equals(cookieId, "NULL")) {
				c.id = cookieId;
				c.nom = getCell(csv, index, 5);
				c.matiere = entierDepuisTexte(getCell(csv, index, 6));
				c.prix = entierDepuisTexte(getCell(csv, index, 7));
				c.taxe = entierDepuisTexte(getCell(csv, index, 8));
				c.quantite = entierDepuisTexte(getCell(csv, index, 9));
				p.cookie = c;

			} else {
				p.cookie = null;
			}			
			Question[] questions = chargerQuestions();
			boucleJeu(p, questions, c);
		}
	}

	// Affiche les informations du tour courant (jour, argent, stats, question)
	void afficherEcranTour(Partie partie, Question question ,CookieStat cookieStat) {
		effacerTerminal();
		afficherLogo();
		println("_______________________");
		println("Jour " + partie.jour + " - Argent : " + partie.argent + " euros - Gain de la journee : " + partie.gainJour + " euros - Nombre de cookies : " + cookieStat.quantite);
		println("_______________________");
		
		CookieStat c = partie.cookie;
		if (c != null) {
			println("Stats du cookie :");
			println(" - Matiere prem. : " + c.matiere);
			println(" - Prix de vente : " + c.prix);
			println(" - Taxe : " + c.taxe + "%");
			println(" - Quantité : " + c.quantite);
		}
		println("_______________________");
		
		println("Question : " + question.intitule);
		println("A. " + question.propositions[0]);
		println("B. " + question.propositions[1]);
		println("C. " + question.propositions[2]);
		println("D. " + question.propositions[3]);
		println("_______________________");
	}

	// Demande a l'utilisateur de saisir sa reponse (A, B, C, D, S ou Q)
	String demanderReponse() {
		print("(Choisissez une reponse A/B/C/D, S pour Sauvegarder ou Q pour Quitter) > ");
		String saisie = readString();
		while (!estReponseValide(saisie) && !equals(majuscule(saisie), "Q") && !equals(majuscule(saisie), "S")) {
			print("Invalide. (A/B/C/D/S/Q) > ");
			saisie = readString();
		}
		return majuscule(saisie);
	}

	// Verifie si la saisie correspond a une reponse valide (A, B, C ou D)
	boolean estReponseValide(String s) {
		boolean valide = false;
		if (length(s) == 1) {
			String l = majuscule(s);
			if (equals(l, "A") || equals(l, "B") || equals(l, "C") || equals(l, "D")) {
				valide = true;
			}
		}
		return valide;
	}

	// Compare la reponse de l'utilisateur avec la bonne reponse
	boolean verifierReponse(String saisie, Question question) {
		return equals(saisie, question.bonneReponse);
	}

	// Convertit une chaine en majuscule (pour les lettres a, b, c, d, q, s)
	String majuscule(String s) {
		String res = s;
		if (length(s) > 0) {
			String c = substring(s, 0, 1);
			if (equals(c, "a")) res = "A";
			else if (equals(c, "b")) res = "B";
			else if (equals(c, "c")) res = "C";
			else if (equals(c, "d")) res = "D";
			else if (equals(c, "q")) res = "Q";
			else if (equals(c, "s")) res = "S";
			else res = c;
		}
		return res;
	}

	// Affiche si la reponse etait correcte ou non
	void afficherEcranResultat(boolean succes) {
		effacerTerminal();
		if (succes) {
			println("          BONNE REPONSE !!!!!!!!          ");
		} else {
			println("          MAUVAISE REPONSE...          ");
		}
		println("");
		attendreValidationUtilisateur();
	}

	// Applique un bonus choisi par le joueur
	void traiterBonus(Partie p) {
		effacerTerminal();
		println("Que souhaitez vous ameliorer ??");
		println(" A. Matiere premiere (Cout -10%)");
		println(" B. Prix de vente (Prix +10%)");
		println(" C. Baisse des taxes (Taxe -2%)");
		
		String choix = demanderReponseABC();
		
		CookieStat c = p.cookie;
		if (equals(choix, "A")) {
			c.matiere = (int)(c.matiere * 0.9);
			println("Cout matiere premiere reduit !");
		} else if (equals(choix, "B")) {
			c.prix = (int)(c.prix * 1.1);
			println("Prix de vente augmente !");
		} else if (equals(choix, "C")) {
			c.taxe = c.taxe - 2;
			if (c.taxe < 0) c.taxe = 0;
			println("Taxes reduites !");
		}
		attendreValidationUtilisateur();
	}

	// Demande a l'utilisateur de choisir entre A, B ou C
	String demanderReponseABC() {
		print("(Choisissez A/B/C) > ");
		String s = readString();
		while (length(s) != 1 || (!equals(majuscule(s), "A") && !equals(majuscule(s), "B") && !equals(majuscule(s), "C"))) {
			print("Invalide. (A/B/C) > ");
			s = readString();
		}
		return majuscule(s);
	}

	// Applique un malus aleatoire au joueur
	void traiterMalus(Partie p) {
		effacerTerminal();
		int r = (int)(random() * 3);
		println("Votre malus est :");
		CookieStat c = p.cookie;
		if (r == 0) {
			println("Augmentation du cout matiere premiere (+15%)");
			c.matiere = (int)(c.matiere * 1.15);
		} else if (r == 1) {
			println("Baisse du prix de vente (-10%)");
			c.prix = (int)(c.prix * 0.9);
		} else {
			println("Augmentation des taxes (+5%)");
			c.taxe = c.taxe + 5;
		}
		attendreValidationUtilisateur();
	}

	// Calcule les gains financiers a la fin du tour
	void calculerFinDeTour(Partie p) {
		int volume = 1000;
		CookieStat c = p.cookie;
		int margeUnitaire = c.prix - c.matiere;
		int gainBrut = margeUnitaire * volume;
		int montantTaxe = (int)(gainBrut * (c.taxe / 100.0));
		int gainNet = gainBrut - montantTaxe;
		
		p.gainJour = gainNet;
		p.argent = p.argent + gainNet;
	}

	// Charge la liste des cookies depuis le fichier CSV
	CookieStat[] chargerCookies() {
		CSVFile table = loadCSV(cookiesCsv, CSV_SEPARATOR);
		int lignes = rowCount(table);
		if (lignes <= 1) {
			return new CookieStat[0];
		}
		
		CookieStat[] cookies = new CookieStat[lignes - 1];
		int idx = 1;
		int pos = 0;
		while (idx < lignes) {
			cookies[pos] = creerCookieDepuisLigne(table, idx);
			idx = idx + 1;
			pos = pos + 1;
		}
		return cookies;
	}

	// Cree une structure CookieStat a partir d'une ligne du CSV
	CookieStat creerCookieDepuisLigne(CSVFile table, int ligne) {
		CookieStat cookie = new CookieStat();
		cookie.id = getCell(table, ligne, 0);
		cookie.nom = getCell(table, ligne, 1);
		cookie.matiere = entierDepuisTexte(getCell(table, ligne, 2));
		cookie.prix = entierDepuisTexte(getCell(table, ligne, 3));
		cookie.taxe = entierDepuisTexte(getCell(table, ligne, 4));
		cookie.quantite = entierDepuisTexte(getCell(table, ligne, 5));
		return cookie;
	}

	// Charge la liste des questions depuis le fichier CSV
	Question[] chargerQuestions() {
		CSVFile table = loadCSV(questionsCsv, CSV_SEPARATOR);
		int lignes = rowCount(table);
		if (lignes <= 1) {
			return new Question[0];
		}
		Question[] questions = new Question[lignes - 1];
		int idx = 1;
		int pos = 0;
		while (idx < lignes) {
			questions[pos] = creerQuestionDepuisLigne(table, idx);
			idx = idx + 1;
			pos = pos + 1;
		}
		return questions;
	}

	// Cree une structure Question a partir d'une ligne du CSV
	Question creerQuestionDepuisLigne(CSVFile table, int ligne) {
		Question question = new Question();
		question.id = getCell(table, ligne, 0);
		question.intitule = getCell(table, ligne, 1);
		question.propositions = new String[4];
		question.propositions[0] = getCell(table, ligne, 2);
		question.propositions[1] = getCell(table, ligne, 3);
		question.propositions[2] = getCell(table, ligne, 4);
		question.propositions[3] = getCell(table, ligne, 5);
		question.bonneReponse = getCell(table, ligne, 6);
		question.niveau = niveauDepuisTexte(getCell(table, ligne, 7));
		return question;
	}

	// Initialise une nouvelle partie avec les valeurs de depart
	Partie nouvellePartieInitiale(CookieStat[] cookies) {
		Partie partie = new Partie();
		partie.jour = 1;
		partie.argent = ARGENT_DEPART;
		partie.gainJour = GAIN_DEPART;
		if (length(cookies) > 0) {
			partie.cookie = copierCookie(cookies[0]);
		} else {
			partie.cookie = null;
		}
		return partie;
	}

	// Cree une copie d'une structure CookieStat
	CookieStat copierCookie(CookieStat source) {
		if (source == null) {
			return null;
		}
		CookieStat copie = new CookieStat();
		copie.id = source.id;
		copie.nom = source.nom;
		copie.matiere = source.matiere;
		copie.prix = source.prix;
		copie.taxe = source.taxe;
		copie.quantite = source.quantite;
		return copie;
	}

	// Lit un entier saisi par l'utilisateur en verifiant qu'il est dans l'intervalle
	int lireEntierDansIntervalle(int min, int max) {
		boolean valide = false;
		int resultat = min;
		while (!valide) {
			String entree = readString();
			if (estTexteNombre(entree)) {
				int valeur = entierDepuisTexte(entree);
				if (valeur >= min && valeur <= max) {
					resultat = valeur;
					valide = true;
				}
			}
			if (!valide) {
				print("Choix invalide, recommencez : ");
			}
		}
		return resultat;
	}

	// Verifie si une chaine de caracteres represente un nombre entier
	boolean estTexteNombre(String valeur) {
		if (length(valeur) == 0) {
			return false;
		}
		int idx = 0;
		if (equals(substring(valeur, 0, 1), "-")) {
			if (length(valeur) == 1) {
				return false;
			}
			idx = 1;
		}
		while (idx < length(valeur)) {
			String caractere = substring(valeur, idx, idx + 1);
			if (!estChiffre(caractere)) {
				return false;
			}
			idx = idx + 1;
		}
		return true;
	}

	// Verifie si un caractere est un chiffre
	//return equals(caractere, "0") || equals(caractere, "1") || equals(caractere, "2") || equals(caractere, "3") || equals(caractere, "4") || equals(caractere, "5") || equals(caractere, "6") || equals(caractere, "7") || equals(caractere, "8") || equals(caractere, "9");
	boolean estChiffre(String caractere){
		if(length(caractere) <=1 and length(caractere)>0){
			if(caractere >'9' and caractere < '0'){
				return true;
			}
		}
		return false;
	}

	// Convertit une chaine de caracteres en entier
	int entierDepuisTexte(String valeur) {
		int signe = 1;
		int idx = 0;
		if (length(valeur) > 0 && equals(substring(valeur, 0, 1), "-")) {
			signe = -1;
			idx = 1;
		}
		int resultat = 0;
		while (idx < length(valeur)) {
			int chiffre = chiffreDepuisTexte(substring(valeur, idx, idx + 1));
			resultat = resultat * 10 + chiffre;
			idx = idx + 1;
		}
		return resultat * signe;
	}

	// Convertit un caractere chiffre en sa valeur entiere
	int chiffreDepuisTexte(String caractere) {
		String chiffres = "0123456789";
		int idx = 0;
		while (idx < length(chiffres)) {
			if (equals(substring(chiffres, idx, idx + 1), caractere)) {
				return idx;
			}
			idx = idx + 1;
		}
		return 9;
	}

	// Met le programme en pause jusqu'a ce que l'utilisateur appuie sur Entree
	void attendreValidationUtilisateur() {
		println("Appuyez sur ENTREE pour continuer...");
		readString();
	}

	// Efface le contenu du terminal
	void effacerTerminal() {
		println(CLEAR_SEQUENCE);
	}

	// Verifie si un repertoire existe dans le dossier courant
	boolean repertoirePresentDansCourant(String nom) {
		String[] fichiers = getAllFilesFromCurrentDirectory();
		int idx = 0;
		while (idx < length(fichiers)) {
			if (equals(fichiers[idx], nom)) {
				return true;
			}
			idx = idx + 1;
		}
		return false;
	}

	// Reinitialise le fichier de sauvegardes (efface tout)
	void initialiserSauvegardes() {
		println("ATTENTION : Cela va effacer toutes les sauvegardes existantes.");
		print("Etes-vous sur ? (O/N) > ");
		String rep = readString();
		if (equals(majuscule(rep), "O")) {
			String[][] data = new String[0][NB_COLONNES_SAVE];
			saveCSV(data, savesCsv);
			println("Fichier de sauvegarde reinitialise.");
		} else {
			println("Annule.");
		}
		attendreValidationUtilisateur();
	}

	Difficulte niveauDepuisTexte(String valeur) {
		if (equals(valeur, "FACILE")) {
			return Difficulte.FACILE;
		} else if (equals(valeur, "MOYEN")) {
			return Difficulte.MOYEN;
		}
		return Difficulte.DIFFICILE;
	}

	OptionMenu optionDepuisChoix(int choix) {
		if (choix == 1) {
			return OptionMenu.NOUVELLE_PARTIE;
		} else if (choix == 2) {
			return OptionMenu.CHARGER;
		} else if (choix == 3) {
			return OptionMenu.REGLES;
		} else if (choix == 4) {
			return OptionMenu.RESET;
		}
		return OptionMenu.QUITTER;
	}
}

