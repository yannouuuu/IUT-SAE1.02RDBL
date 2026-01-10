import extensions.File;
import extensions.CSVFile;

class Main extends Program {

	final String CLEAR_SEQUENCE = "\033[H\033[2J";
	final char CSV_SEPARATOR = ';';
	final int ARGENT_DEPART = 150;
	final int GAIN_DEPART = 30;
	final int NB_COLONNES_SAVE = 14; // nombre de colonnes utilisées dans le fichier de sauvegarde CSV
	
	String ressourcesPrefix = "resources/";
	String questionsCsv;
	String cookiesCsv;
	String savesCsv;
	String logoAscii;
	String reglesTxt;
	String gameoverTxt;
	String bonneReponseTxt;
	String mauvaiseReponseTxt;
	String ecranBienvenueDir;

	// ============================== COULEURS ==============================
	//region COULEURS

	// succes, positif
	String vert(String texte) {
		return GREEN + texte + RESET;
	}

	// erreur, negatif
	String rouge(String texte) {
		return RED + texte + RESET;
	}

	// attention, neutre
	String jaune(String texte) {
		return YELLOW + texte + RESET;
	}

	// info, titres
	String cyan(String texte) {
		return CYAN + texte + RESET;
	}

	// special cookie
	String magenta(String texte) {
		return MAGENTA + texte + RESET;
	}

	String blanc(String texte) {
		return BRIGHT_WHITE + texte + RESET;
	}

	// valeurs importantes
	String or(String texte) {
		return GOLD + texte + RESET;
	}

	// Met un texte en gras
	String gras(String texte) {
		return BOLD + texte + RESET;
	}

	// Colore selon si la valeur est positive ou negative
	String couleurSelonSigne(int valeur, String texte) {
		String resultat = texte;
		if (valeur >= 0) {
			resultat = vert(texte);
		} else {
			resultat = rouge(texte);
		}
		return resultat;
	}

	//endregion

	// Point d'entree principal du programme
	void algorithm() {
		initialiserCheminsRessources();
		afficherEcranBienvenue(4);
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
		reglesTxt = ressourcesPrefix + "regles.txt";
		gameoverTxt = ressourcesPrefix + "gameover.txt";
		bonneReponseTxt = ressourcesPrefix + "bonnereponse.txt";
		mauvaiseReponseTxt = ressourcesPrefix + "mauvaisereponse.txt";
		ecranBienvenueDir = ressourcesPrefix + "ecranbienvenue/";
	}


	// ============================== AFFICHAGE ==============================
	//region AFFICHAGE

	// Retourne une chaine d'espaces de la longueur demandee
	String espaces(int n) {
		return repeter(" ", n);
	}

	// Affiche une ligne vide
	void nl() {
		println("");
	}

	// Affiche n lignes vides
	void nl(int n) {
		for (int i = 0; i < n; i++) nl();
	}

	// Affiche une option de bonus formatee
	void afficherOptionBonus(String lettre, String titre, String effet, String description) {
		println(espaces(2) + cyan(" " + lettre + ") ") + padDroit(titre, 40) + vert(effet));
		println(espaces(6) + jaune("> " + description));
		nl();
	}

	// Affiche une statistique formatee avec points de suite
	void afficherStat(String label, String valeur) {
		println(espaces(5) + padDroitPoints(label, 35) + " " + valeur);
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


	// Affiche les regles du jeu a l'ecran
	void afficherRegles() {
		effacerTerminal();
		afficherLogo();
		afficherFichierCouleur(reglesTxt, CYAN);
		attendreValidationUtilisateur();
	}

	// Affiche le message de fin de programme
	void afficherMessageSortie() {
		println(magenta("Merci d'avoir teste CookiesLand !"));
	}

	// Affiche une option du menu formate
	void afficherOption(String numero, String texte) {
		println(cyan(espaces(4) + padDroit(numero, 6)) + gras(blanc(texte)));
		nl();
	}

	// Affiche les options du menu principal
	void afficherMenuPrincipal() {
		int w = 55;
		String sep = repeter("─", w);
		
		println(magenta(espaces(4) + sep));
		nl();

		afficherOption("I.", "NOUVELLE PARTIE");
		afficherOption("II.", "CHARGER UNE PARTIE");
		afficherOption("III.", "RÈGLES DU JEU");
		afficherOption("IV.", "RÉINITIALISER");
		
		println(magenta(espaces(4) + sep));
		nl();
		println(rouge(espaces(4) + padDroit("V.", 6)) + gras(rouge("QUITTER")));
		nl();
	}

	// Affiche le logo ASCII du jeu
	void afficherLogo() {
		File logo = newFile(logoAscii);
		if (ready(logo)) {
			nl();
			afficherFichierCouleur(logoAscii, GOLD);
		} else {
			println(or("[[[COOKIESLAND]]]"));
		}
	}

	// Affiche le contenu d'un fichier texte
	void afficherFichier(String chemin) {
		File fichier = newFile(chemin);
		while (ready(fichier)) {
			println(readLine(fichier));
		}
	}

	// Affiche le contenu d'un fichier texte avec une couleur
	void afficherFichierCouleur(String chemin, String couleur) {
		File fichier = newFile(chemin);
		while (ready(fichier)) {
			println(couleur + readLine(fichier) + RESET);
		}
	}

	// Ajoute des espaces a droite pour atteindre la taille voulue
	String padDroit(String s, int n) {
		String res = s;
		while (length(res) < n) {
			res = res + " ";
		}
		return res;
	}

	// Ajoute des points a droite pour atteindre la taille voulue
	String padDroitPoints(String s, int n) {
		String res = s;
		while (length(res) < n) {
			res = res + ".";
		}
		return res;
	}

	// Centre un texte dans une largeur donnee en comblant avec des espaces
	String centrer(String s, int largeur) {
		if (length(s) >= largeur) {
			return s;
		}
		int espaces = largeur - length(s);
		int gauche = espaces / 2;
		String res = "";
		int i = 0;
		while (i < gauche) {
			res = res + " ";
			i = i + 1;
		}
		res = res + s;
		while (length(res) < largeur) {
			res = res + " ";
		}
		return res;
	}

	// Repete un caractere n fois
	String repeter(String c, int n) {
		String res = "";
		int i = 0;
		while (i < n) {
			res = res + c;
			i = i + 1;
		}
		return res;
	}

	// Affiche les informations du tour courant (jour, argent, stats, question)
	void afficherEcranTour(Partie partie, Question question ,CookieStat cookieStat) {
		effacerTerminal();
		
		CookieStat c = partie.cookie;
		int marge;

		if (c != null) {
			marge = c.prix - c.matiere;
		} else {
			marge = 0;
		}


		String indicateurMarge = marge >= 0 ? "▲" : "▼";
		String indicateurGain = partie.gainJour >= 0 ? "▲" : "▼";
		
		// === EN-TÊTE ===
		afficherLogo();
		println("");
		
		// === BARRE DE STATUT ===
		println(jaune("  📅 JOUR " + partie.jour + "   |   MODE: " + partie.difficulte));
		println(cyan("  ─────────────────────────────────────────────────────────────"));
		nl();
		
		// === FINANCES ===
		println(gras(or("  💰 FINANCES")));
		afficherStat("Argent disponible ", or(partie.argent + " €"));
		afficherStat("Gain du jour ", couleurSelonSigne(partie.gainJour, indicateurGain + " " + partie.gainJour + " €"));
		nl();
		
		// === COOKIE ===
		if (c != null) {
			println(gras(magenta("  🍪 " + c.nom.toUpperCase())));
			afficherStat("Cout matiere premiere ", rouge(c.matiere + " €"));
			afficherStat("Prix de vente ", vert(c.prix + " €"));
			afficherStat("Taxe ", jaune(c.taxe + " %"));
			afficherStat("Quantite en stock ", cyan(c.quantite + " unites"));
			afficherStat("Popularité ", or(c.popularite + " %"));
			afficherStat("Marge par cookie ", couleurSelonSigne(marge, indicateurMarge + " " + marge + " €"));
		}
		nl();
		
		// === QUESTION ===
		println(cyan("  ─────────────────────────────────────────────────────────────"));
		println(gras(jaune("  ❓ QUESTION")));
		println(cyan("  ─────────────────────────────────────────────────────────────"));
		nl();
		println(gras("     " + question.intitule));
		nl();
		println(espaces(5) + cyan("A)") + " " + question.propositions[0]);
		println(espaces(5) + cyan("B)") + " " + question.propositions[1]);
		println(espaces(5) + cyan("C)") + " " + question.propositions[2]);
		println(espaces(5) + cyan("D)") + " " + question.propositions[3]);
		print("\n\n\n\n");
	}

	// Demande a l'utilisateur de saisir sa reponse (A, B, C, D, S ou Q)
	String demanderReponse() {
		print(cyan("(Choisissez une reponse ") + vert("A/B/C/D") + cyan(", ") + jaune("S") + cyan(" pour Sauvegarder ou ") + rouge("Q") + cyan(" pour Quitter) > "));
		String saisie = readString();
		while (!estReponseValide(saisie) && !equals(majuscule(saisie), "Q") && !equals(majuscule(saisie), "S")) {
			print(rouge("Invalide. ") + cyan("(A/B/C/D/S/Q) > "));
			saisie = readString();
		}
		return majuscule(saisie);
	}


	// Affiche l'animation de bienvenue pendant un certain nombre de secondes
	void afficherEcranBienvenue(int dureeSecondes) {
		int nbImages = 3;
		int delaiMs = 300;
		int iterations = (dureeSecondes * 1000) / (nbImages * delaiMs);
		int frameActuelle = 1;
		
		for (int i = 0; i < iterations * nbImages; i++) {
			effacerTerminal();
			String cheminFrame = ecranBienvenueDir + "frame" + frameActuelle + ".txt";
			afficherFichierCouleur(cheminFrame, GOLD);
			sleep(delaiMs);
			
			frameActuelle = frameActuelle + 1;
			if (frameActuelle > nbImages) {
				frameActuelle = 1;
			}
		}
	}

	// Affiche si la reponse etait correcte ou non
	void afficherEcranResultat(boolean succes) {
		effacerTerminal();
		afficherLogo();
		
		int width = 70;
		String top = repeter("═", width);
		
		println("");
		if (succes) {
			println(vert("  ╔" + top + "╗"));
			println(vert("  ║" + centrer("", width) + "║"));
			println(vert("  ║" + centrer("O   R E P O N S E   C O R R E C T E   O", width) + "║"));
			println(vert("  ║" + centrer("", width) + "║"));
			println(vert("  ╚" + top + "╝"));
			println("");
			println(vert(centrer("Bravo ! Vous gagnez un bonus commercial.", width + 4)));
		} else {
			println(rouge("  ╔" + top + "╗"));
			println(rouge("  ║" + centrer("", width) + "║"));
			println(rouge("  ║" + centrer("X   M A U V A I S E   R E P O N S E   X", width) + "║"));
			println(rouge("  ║" + centrer("", width) + "║"));
			println(rouge("  ╚" + top + "╝"));
			println("");
			println(rouge(centrer("Dommage... Un malus va etre applique.", width + 4)));
		}
		println("");
		println(jaune("  " + repeter("─", width + 2)));
		println("");
		attendreValidationUtilisateur();
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
		println(rouge(gras("ATTENTION : Cela va effacer toutes les sauvegardes existantes.")));
		print(blanc("Etes-vous sur ? ") + cyan("(O/N) > "));
		String rep = readString();
		if (equals(majuscule(rep), "O")) {
			String[][] data = new String[0][NB_COLONNES_SAVE];
			saveCSV(data, savesCsv, CSV_SEPARATOR);
			println(vert("Fichier de sauvegarde reinitialise."));
		} else {
			println(jaune("Annule."));
		}
		attendreValidationUtilisateur();
	}

	//endregion

	// ============================== FONCTIONS PRINCIPALE ==============================
	//region FONCTIONS PRINCIPALE 

	// Initialise et lance une nouvelle partie avec les donnees par defaut
	void lancerNouvellePartie(){
		CookieStat[] cookies = chargerCookies();
		Question[] questions = chargerQuestions();
		
		if (length(cookies) == 0) {
			println("Impossible d'initialiser la partie (aucun cookie disponible).");
			attendreValidationUtilisateur();
		} else {
			Difficulte diff = afficherMenuDifficulte();
			int choix = afficherMenuSelectionCookie(cookies);
			if (choix > 0) {
				Partie partie = nouvellePartieAvecCookie(cookies[choix - 1]);
				partie.difficulte = diff;
				boucleJeu(partie, questions, partie.cookie);
			}
		}
	}
	
	// Choisit une question adaptee a la difficulté
	Question choisirQuestion(Question[] questions, Difficulte diff) {
		// Si mode NORMAL, on prend une question au hasard sans filtrer
		if (diff == Difficulte.NORMAL) {
			return questions[(int) (random() * length(questions))];
		}

		int maxEssais = 200;
		int essai = 0;
		while (essai < maxEssais) {
			Question q = questions[(int) (random() * length(questions))];
			// En mode "spécifique", on cherche une correspondance exacte
			if (q.niveau == diff) {
				return q;
			}
			essai = essai + 1;
		}
		return questions[(int) (random() * length(questions))];
	}
	
	// Boucle principale du jeu : gere les tours, les questions et les evenements
	void boucleJeu(Partie partie, Question[] questions, CookieStat cookiestat){
		boolean jeuEnCours = true;
		while (jeuEnCours) {
			Question question = choisirQuestion(questions, partie.difficulte);
			String bonneReponse = melangerPropositions(question);
			afficherEcranTour(partie, question, cookiestat);
	
			String reponse = demanderReponse();
			if (equals(reponse, "Q")){
				jeuEnCours = false;
			} else if (equals(reponse, "S")){
				sauvegarderPartie(partie);
				jeuEnCours = false;
			} else {
				boolean estCorrect = verifierReponse(reponse, bonneReponse);
				
				afficherEcranResultat(estCorrect);
				
				if (estCorrect) {
					traiterBonus(partie);
				} else {
					traiterMalus(partie);
				}
		
				calculerFinDeTour(partie);
				
				if (estEnFaillite(partie)) {
					afficherEcranGameOver(partie);
					jeuEnCours = false;
				} else {
					partie.jour = partie.jour + 1;
				}
			}
		}
	}

	// Verifie si le joueur a perdu (argent negatif ou nul)
	boolean estEnFaillite(Partie p) {
		return p.argent < 0;
	}

	// Affiche l'ecran de fin de partie
	void afficherEcranGameOver(Partie p) {
		effacerTerminal();
		afficherLogo();
		
		int width = 70;
		String top = repeter("═", width);
		
		println("");
		println(rouge("  ╔" + top + "╗"));
		println(rouge("  ║" + centrer("", width) + "║"));
		println(rouge("  ║" + centrer("#   G A M E   O V E R   #", width) + "║"));
		println(rouge("  ║" + centrer("", width) + "║"));
		println(rouge("  ╠" + top + "╣"));
		println(rouge("  ║" + centrer("", width) + "║"));
		println(rouge("  ║" + centrer(" VOTRE ENTREPRISE A FAIT FAILLITE ", width) + "║"));
		println(rouge("  ║" + centrer("", width) + "║"));
		println(rouge("  ╚" + top + "╝"));
		println("");
		
		String message = "Vous avez réussi à survivre pendant " + p.jour + " jours.";
		println(jaune(centrer(message, width + 4)));
		println("");
		println(rouge("  " + repeter("─", width + 2)));
		println("");
		attendreValidationUtilisateur();
	}

	// Applique un bonus choisi par le joueur
	void traiterBonus(Partie p) {
		effacerTerminal();
		afficherLogo();
		println("");

		int width = 70;
		String top = repeter("═", width);
		
		println(vert("  ╔" + top + "╗"));
		println(vert("  ║" + centrer("", width) + "║"));
		println(vert("  ║" + centrer("*  BONUS COMMERCIAL ACQUIS  *", width) + "║"));
		println(vert("  ║" + centrer("", width) + "║"));
		println(vert("  ╠" + top + "╣"));
		println(vert("  ║" + centrer("", width) + "║"));
		println(vert("  ║" + centrer("Selectionnez votre recompense strategique :", width) + "║"));
		println(vert("  ║" + centrer("", width) + "║"));
		println(vert("  ╚" + top + "╝"));
		nl();

		afficherOptionBonus("A", "Reduire cout matiere premiere", "[-5% cout]", "Achetez moins cher vos ingredients et augmentez vos marges.");
		afficherOptionBonus("B", "Augmenter prix de vente", "[+5% prix]", "Vendez vos cookies plus cher sans perdre de clients.");
		afficherOptionBonus("C", "Reduire les taxes", "[-1% taxes]", "Optimisation fiscale pour payer moins d'impots.");
		afficherOptionBonus("D", "Augmenter la popularite", "[+3% pop.]", "Lancez une campagne pub pour attirer les foules.");
		
		println(vert("  " + repeter("─", width + 2)));
		
		String choix = demanderReponseABC();
		
		CookieStat c = p.cookie;
		nl();
		if (equals(choix, "A")) {
			int ancien = c.matiere;
			c.matiere = (int)(c.matiere * 0.95);
			println(vert("  ✓ Cout matiere premiere : ") + rouge("" + ancien + " €") + "  →  " + vert("" + c.matiere + " €"));
		} else if (equals(choix, "B")) {
			int ancien = c.prix;
			c.prix = (int)(c.prix * 1.05);
			println(vert("  ✓ Prix de vente : ") + jaune("" + ancien + " €") + "  →  " + vert("" + c.prix + " €"));
		} else if (equals(choix, "C")) {
			int ancien = c.taxe;
			c.taxe = c.taxe - 1;
			if (c.taxe < 0) c.taxe = 0;
			println(vert("  ✓ Taxe : ") + jaune("" + ancien + " %") + "  →  " + vert("" + c.taxe + " %"));
		}else if (equals(choix,"D")){
			int ancien = c.popularite;
			c.popularite = c.popularite +3;
			println(vert("  ✓ Popularite : ") + jaune("" + ancien + " %") + "  →  " + vert("" + c.popularite + " %"));
		}else if( equals(choix, "motherlode")){
			int ancien = p.argent;
			p.argent = p.argent + 50000;
			println(gras("  ✓ Cheat : ") + "  →  " + vert("" + p.argent + " €"));

		}
		println("");
		c.quantite = c.quantite + c.popularite; 
		attendreValidationUtilisateur();
	}

		// Applique un malus aleatoire au joueur
	void traiterMalus(Partie p) {
		effacerTerminal();
		afficherLogo();
		int r = (int)(random() * 4);
		CookieStat c = p.cookie;
		
		println("");
		int width = 70;
		String top = repeter("═", width);
		
		println(rouge("  ╔" + top + "╗"));
		println(rouge("  ║" + centrer("", width) + "║"));
		println(rouge("  ║" + centrer("!   INCIDENT DE PRODUCTION   !", width) + "║"));
		println(rouge("  ║" + centrer("", width) + "║"));
		println(rouge("  ╚" + top + "╝"));
		nl();
		
		if (r == 0) {
			int ancien = c.matiere;
			c.matiere = (int)(c.matiere * 1.25);
			println(rouge(espaces(2) + "X Penurie d'ingredients !"));
			println(jaune(espaces(4) + "Les prix des matieres premieres explosent."));
			nl();
			println(blanc(espaces(4) + "Cout matiere : ") + jaune("" + ancien + " €") + "  ->  " + rouge("" + c.matiere + " €") + rouge(" (+25%)"));
		} else if (r == 1) {
			int ancien = c.prix;
			c.prix = (int)(c.prix * 0.85);
			println(rouge(espaces(2) + "X Guerre des prix !"));
			println(jaune(espaces(4) + "La concurrence vous oblige a baisser vos tarifs."));
			nl();
			println(blanc(espaces(4) + "Prix de vente : ") + jaune("" + ancien + " €") + "  ->  " + rouge("" + c.prix + " €") + rouge(" (-15%)"));
		} else if (r == 2){
			int ancien = c.taxe;
			c.taxe = (int)(c.taxe * 1.8);
			println(rouge(espaces(2) + "X Redressement fiscal !"));
			println(jaune(espaces(4) + "L'administration augmente vos taxes locales."));
			nl();
			println(blanc(espaces(4) + "Taxes : ") + jaune("" + ancien + " %") + "  ->  " + rouge("" + c.taxe + " %") + rouge(" (+80%)"));
		}else{
			int ancien = c.popularite;
			c.popularite = (int)(c.popularite / 1.4);
			println(rouge(espaces(2) + "X Scandale sanitaire !"));
			println(jaune(espaces(4) + "Un client a trouve un cheveu dans son cookie."));
			nl();
			println(blanc(espaces(4) + "Popularite : ") + jaune("" + ancien + " %") + "  ->  " + rouge("" + c.popularite + " %") + rouge(" (Baisse drastique)"));
		}
		
		nl();
		println(rouge(espaces(2) + repeter("─", width + 2)));
		nl();
		c.quantite = c.quantite - c.popularite;
		attendreValidationUtilisateur();
	}

	// Calcule les gains financiers a la fin du tour
	/*void calculerFinDeTour(Partie p) {
		int volume = 10000;
		CookieStat c = p.cookie;
		int margeUnitaire = c.prix - c.matiere;
		int gainBrut = margeUnitaire * volume;
		int montantTaxe = (int)(gainBrut * (c.taxe / 100.0));
		int gainNet = gainBrut - montantTaxe;
		
		p.gainJour = gainNet;
		p.argent = p.argent + gainNet;
	}
	*/
	void calculerFinDeTour(Partie p) {
		CookieStat c = p.cookie;
		int quantiteVendu = c.quantite;
		int gainParCookies = c.prix - c.matiere;
		int gainBrut = gainParCookies * quantiteVendu;
		int montantTaxe = (int)(gainBrut * (c.taxe / 100.0));
		int benefice = gainBrut - montantTaxe;
		int populariteDuCookie = c.popularite;

		p.gainJour = benefice;
		p.argent = p.argent + benefice;
	}

	//endregion

	// ============================== GESTION DES SAUVEGARDES ==============================
	//region GESTION DES SAUVEGARDES 

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
		
		saveCSV(data, savesCsv, CSV_SEPARATOR);
		println(vert("Partie '") + or(nomSauvegarde) + vert("' sauvegardee !"));
		attendreValidationUtilisateur();
	}

	// Demande a l'utilisateur de saisir un nom pour sa sauvegarde
	String demanderNomSauvegarde(){
		boolean valide = false; 
		print(cyan("Entrez le nom de votre sauvegarde : "));
		String rep = readString();
		while( valide != true){
			if( estAccepatable(rep) != true ){
				println(rouge("le nom doit faire moins de 15 caraxteres "));
				print(cyan("Entrez le nom de votre sauvegarde : "));
				rep = readString();
			}else
				valide = true ; 
		}
		return rep;
	}

	// Remplit une ligne du tableau de sauvegarde avec les donnees de la partie
	void remplirLigneSauvegarde(String[][] data, int ligne, String nom, Partie p) {
		data[ligne][0] = nom;
		data[ligne][1] = "" + p.jour;
		data[ligne][2] = "" + p.argent;
		data[ligne][3] = "" + p.gainJour;
		data[ligne][4] = "" + p.quantite;
		data[ligne][5] = "" + p.popularite;

		CookieStat c = p.cookie;
		if (c != null) {
			data[ligne][6] = c.id;
			data[ligne][7] = c.nom;
			data[ligne][8] = "" + c.matiere;
			data[ligne][9] = "" + c.prix;
			data[ligne][10] = "" + c.taxe;
			data[ligne][11] = "" + c.quantite;
			data[ligne][12] = "" + c.popularite;
		} else {
			data[ligne][6] = "NULL";
			data[ligne][7] = "";
			data[ligne][8] = "0";
			data[ligne][9] = "0";
			data[ligne][10] = "0";
			data[ligne][11] = "0";
			data[ligne][12] = "0";
		}
		
		if (p.difficulte != null) {
			data[ligne][13] = "" + p.difficulte;
		} else {
			data[ligne][13] = "MOYEN";
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
			println(jaune("Aucune sauvegarde disponible."));
			attendreValidationUtilisateur();
		} else {
			println(cyan("Sauvegardes disponibles :"));
			int i = 0;
			while (i < rows) {
				String nomSave = padDroit(getCell(csv, i, 0), 20);
				println("  " + cyan((i + 1) + ".") + " " + vert(nomSave) + " " + jaune("(Jour " + getCell(csv, i, 1) + ")"));
				i = i + 1;
			}
			println("");
			
			int choix = lireEntierDansIntervalle(0, rows);
			if (choix != 0) {
				int index = choix - 1;
				
				Partie p = new Partie();
				p.jour = entierDepuisTexte(getCell(csv, index, 1));
				p.argent = entierDepuisTexte(getCell(csv, index, 2));
				p.gainJour = entierDepuisTexte(getCell(csv, index, 3));
				p.quantite = entierDepuisTexte(getCell(csv, index, 4));
				p.popularite = entierDepuisTexte(getCell(csv, index, 5));
				
				String cookieId = getCell(csv, index, 6);
				CookieStat c = new CookieStat();
				if (!equals(cookieId, "NULL")) {
					c.id = cookieId;
					c.nom = getCell(csv, index, 7);
					c.matiere = entierDepuisTexte(getCell(csv, index, 8));
					c.prix = entierDepuisTexte(getCell(csv, index, 9));
					c.taxe = entierDepuisTexte(getCell(csv, index, 10));
					c.quantite = entierDepuisTexte(getCell(csv, index, 11));
					c.popularite = entierDepuisTexte(getCell(csv, index, 12));
					p.cookie = c;

				} else {
					p.cookie = null;
				}

				if (columnCount(csv) >= 14) {
					p.difficulte = niveauDepuisTexte(getCell(csv, index, 13));
				} else {
					p.difficulte = Difficulte.MOYEN;
				}

				Question[] questions = chargerQuestions();
				boucleJeu(p, questions, c); // Warning: c variable might be confusing here if p.cookie is set
			}
		}
	}

	//endregion

	// ============================== CONTROLE DE SAISIE ==============================
	//region CONTROLE DE SAISIE

	boolean estAccepatable( String s ){
		char lettre ='a';
		if(length(s) <= 15 && length(s) >= 2){
			for( int taille = 0 ; taille < length(s) ; taille++){
				lettre = charAt(s , taille );
				if(lettre == ';'){
					return false;
				}
			}
			return true;
		}
		return false;
	}

	// Verifie si la saisie correspond a une reponse valide (A, B, C ou D)
	boolean estReponseValide(String s) {
		boolean valide = false;
		if(equals(s,"motherlode")){
			valide = true;
			return valide; 
		}
		if (length(s) == 1) {
			String l = majuscule(s);
			if (equals(l, "A") || equals(l, "B") || equals(l, "C") || equals(l, "D")) {
				valide = true;
			}
		}
		return valide;
	}

	// Compare la reponse de l'utilisateur avec la bonne reponse
	boolean verifierReponse(String saisie, String bonneReponse) {
		return equals(saisie, bonneReponse);
	}

	// Melange les propositions d'une question et retourne la nouvelle lettre de la bonne reponse
	String melangerPropositions(Question question) {
		String[] props = question.propositions;
		int indiceBonne = indiceDepuisLettre(question.bonneReponse);
		String bonneReponseTexte = props[indiceBonne];
		
		// Melange
		for (int i = length(props) - 1; i > 0; i--) {
			int j = (int)(random() * (i + 1));
			String temp = props[i];
			props[i] = props[j];
			props[j] = temp;
		}
		
		// Trouve le nouvel indice de la bonne reponse
		int nouvelIndice = 0;
		for (int i = 0; i < length(props); i++) {
			if (equals(props[i], bonneReponseTexte)) {
				nouvelIndice = i;
			}
		}
		return lettreDepuisIndice(nouvelIndice);
	}

	// Convertit une lettre (A, B, C, D) en indice (0, 1, 2, 3)
	int indiceDepuisLettre(String lettre) {
		int indice = 3;
		char L = charAt(lettre,0);
		if( L == 'A' || L == 'a'){
			indice = 0;
		} else if ( L == 'B' || L == 'b') {
			indice = 1;
		} else if ( L == 'C' || L == 'c') {
			indice = 2;
		}
		return indice;
	}

	// Convertit un indice (0, 1, 2, 3) en lettre (A, B, C, D)
	String lettreDepuisIndice(int indice) {
		String lettre = "D";
		if (indice == 0) {
			lettre = "A";
		} else if (indice == 1) {
			lettre = "B";
		} else if (indice == 2) {
			lettre = "C";
		}
		return lettre;
	}

	// Convertit une chaine en majuscule (pour les lettres a, b, c, d, q, s)
	String majuscule(String s) {
		String res = s;
	
		if (equals(res, "a")) res = "A";
		else if (equals(res, "b")) res = "B";
		else if (equals(res, "c")) res = "C";
		else if (equals(res, "d")) res = "D";
		else if (equals(res, "q")) res = "Q";
		else if (equals(res, "s")) res = "S";
		
		return res;
	}


	// Affiche le prompt de saisie standard
	void afficherPromptChoix() {
		print("\n" + blanc("Votre choix > "));
	}

	// Lit un entier saisi par l'utilisateur en verifiant qu'il est dans l'intervalle
	int lireEntierDansIntervalle(int min, int max) {
		boolean valide = false;
		int resultat = min;
		afficherPromptChoix();
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
				print(rouge("Choix invalide, recommencez : "));
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
		if(length(caractere) == 1){
			char c = charAt(caractere, 0);
			if(c >= '0' && c <= '9'){
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
		char lettre = charAt(caractere, 0 );
		int valeur = (int)lettre - 48; // (0 = 48)

		// Si c'est un chiffre (0-9)
		if( valeur >= 0 && valeur <= 9) {
			return valeur; 
		// Si c'est une lettre majuscule (A=17-16=1, ... Z=42-16=26)
		} else if (valeur >= 17 && valeur <= 42) {
			return valeur - 16;
		} else {
			return 0;
		}
	}

	/* int chiffreDepuisTexte(String caractere) {
		String chiffres = "0123456789";
		int idx = 0;
		while (idx < length(chiffres)) {
			if (equals(substring(chiffres, idx, idx + 1), caractere)) {
				return idx;
			}
			idx = idx + 1;
		}
		return 9;
	}*/

	String demanderReponseABC() {
		print(cyan("(Choisissez ") + vert("A/B/C/D") + cyan(") > "));
		String s = readString();
		if( equals(s,"motherlode")){
			return s;
		}
		while (length(s) != 1 || (!equals(majuscule(s), "A") && !equals(majuscule(s), "B") && !equals(majuscule(s), "C") && !equals(majuscule(s), "D") && !equals(s,"motherlode"))) {
			print(rouge("Invalide. ") + cyan("(A/B/C/D) > "));
			s = readString();
		}
		return majuscule(s);
	}

	//endregion

	// ============================== SAUVEGARDE ==============================
	//region SAUVEGARDE

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
		cookie.popularite = entierDepuisTexte(getCell(table, ligne, 6));
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
		partie.quantite = 5;
		partie.popularite = 3;
		if (length(cookies) > 0) {
			partie.cookie = copierCookie(cookies[0]);
		} else {
			partie.cookie = null;
		}
		return partie;
	}

	// Cree une nouvelle partie avec un cookie specifique
	Partie nouvellePartieAvecCookie(CookieStat cookie) {
		Partie partie = new Partie();
		partie.jour = 1;
		partie.argent = ARGENT_DEPART;
		partie.gainJour = GAIN_DEPART;
		partie.quantite = 5;
		partie.popularite = 3;
		partie.cookie = copierCookie(cookie);
		return partie;
	}

	// Affiche le menu de selection de la difficulté
	Difficulte afficherMenuDifficulte() {
		effacerTerminal();
		afficherLogo();
		nl();
		
		int w = 55;
		String sep = repeter("─", w);
		
		println(cyan(espaces(4) + "╔" + sep + "╗"));
		println(cyan(espaces(4) + "║" + centrer("NIVEAU DE DIFFICULTE", w) + "║"));
		println(cyan(espaces(4) + "╚" + sep + "╝"));
		nl();
		
		afficherOption("1.", "FACILE    (Marché stable, clients dociles)");
		afficherOption("2.", "NORMAL    (Mélange aléatoire standard)");
		afficherOption("3.", "MOYEN     (La vraie vie d'entrepreneur)");
		afficherOption("4.", "DIFFICILE (Crise économique permanente)");
		
		nl();
		println(magenta(espaces(4) + sep));
		nl();
		
		int choix = lireEntierDansIntervalle(1, 4);
		if (choix == 1) return Difficulte.FACILE;
		else if (choix == 2) return Difficulte.NORMAL;
		else if (choix == 3) return Difficulte.MOYEN;
		else return Difficulte.DIFFICILE;
	}

	// Affiche le menu de selection de cookie et retourne le choix (0 = annuler)
	int afficherMenuSelectionCookie(CookieStat[] cookies) {
		effacerTerminal();
		afficherLogo();
		nl();
		
		// Affichage dynamique de l'entete
		int tableWidth = 74;
		String border = repeter("═", tableWidth - 2);
		
		println(cyan("  ╔" + border + "╗"));
		println(cyan("  ║" + centrer("SELECTION DU PRODUIT", tableWidth - 2) + "║"));
		println(cyan("  ╚" + border + "╝"));
		nl();
		println(blanc(espaces(4) + "Choisissez le cookie qui fera la réputation de votre entreprise :"));

		nl();
		afficherListeCookies(cookies);
		nl();
		int choix = lireEntierDansIntervalle(0, length(cookies));
		return choix;
	}

	// Affiche la liste des cookies avec leurs stats
	void afficherListeCookies(CookieStat[] cookies) {
		println(cyan("  ──────────────────────────────────────────────────────────────────────────"));
		println(cyan("  N°  " + padDroit("NOM", 20) + "  " + padDroit("COUT", 8) + " " + padDroit("PRIX", 8) + " " + padDroit("TAXE", 6) + " " + padDroit("POP.", 6) + "  MARGE"));
		int i = 0;
		while (i < length(cookies)) {
			afficherUnCookie(cookies[i], i + 1);
			i = i + 1;
		}
	}

	// Affiche un cookie avec son numero et ses stats (format compact)
	void afficherUnCookie(CookieStat c, int numero) {
		int marge = calculerMarge(c);
		String margeStr = formaterMarge(marge);
		String couleurMarge = couleurSelonSigne(marge, margeStr + "\u20ac/u");
		String numStr = "";
		if (numero < 10) {
			numStr = " " + numero;
		} else {
			numStr = "" + numero;
		}

		String nom = padDroit(c.nom, 20);
		String mat = padDroit("-" + c.matiere + "\u20ac", 8);
		String prix = padDroit("+" + c.prix + "\u20ac", 8);
		String taxe = padDroit("-" + c.taxe + "%", 6);
		String pop = padDroit("*" + c.popularite + "%", 6);

		println(espaces(2) + cyan(numStr + ".") + " " + nom + "  " + rouge(mat) + " " + vert(prix) + " " + jaune(taxe) + " " + cyan(pop)  + "  \u25b6 " + couleurMarge);
	}

	// Calcule la marge d'un cookie
	int calculerMarge(CookieStat c) {
		int marge = c.prix - c.matiere - (c.prix * c.taxe / 100);
		return marge;
	}

	// Formate la marge avec un + ou - devant
	String formaterMarge(int marge) {
		String resultat = "" + marge;
		if (marge >= 0) {
			resultat = "+" + marge;
		}
		return resultat;
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
		copie.popularite = source.popularite;
		return copie;
	}


	//endregion

	// ============================== AUTRE ==============================
	//region AUTRE

	Difficulte niveauDepuisTexte(String valeur) {
		if (equals(valeur, "FACILE")) {
			return Difficulte.FACILE;
		} else if (equals(valeur, "NORMAL")) {
			return Difficulte.NORMAL;
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
	//endregion

	// ============================== TESTS ==============================
	// region TESTS
	
	void test_majuscule(){
		assertEquals( "A" , majuscule("a"));
		assertEquals( "t" , majuscule("t"));
		assertEquals( "s s" , majuscule("s s"));
		assertEquals( "" , majuscule(""));
		assertEquals( "   " , majuscule("   "));
	}

	void test_estTexteNombre(){
		assertEquals( true , estTexteNombre("1234"));
		assertEquals( false , estTexteNombre("12a34"));
		assertEquals( true , estTexteNombre("-567"));
		assertEquals( false , estTexteNombre("-"));
		assertEquals( false , estTexteNombre(""));
	}

	void test_chargerCookies(){
		initialiserCheminsRessources();
		CookieStat[] cookies = chargerCookies();
		assertTrue( length(cookies) > 0 );
		assertEquals( "C001" , cookies[0].id);
		assertEquals( "Cookie Classique" , cookies[0].nom);
	}

	void test_chargerQuestions(){
		initialiserCheminsRessources();
		Question[] questions = chargerQuestions();
		assertTrue( length(questions) > 0 );
		assertEquals( "Q001" , questions[0].id);
		assertEquals( "Quel indicateur est liée à la rentabilée quotidienne de votre biscuiterie ?" , questions[0].intitule);
	}

	void test_chargerQuestionDepuisLigne(){
		initialiserCheminsRessources();
		CSVFile table = loadCSV(questionsCsv, CSV_SEPARATOR);
		Question q = creerQuestionDepuisLigne(table, 1);
		assertEquals( "Q001" , q.id);
		assertEquals( "Quel indicateur est liée à la rentabilée quotidienne de votre biscuiterie ?" , q.intitule);
		assertEquals( "B" , q.bonneReponse);
	}

	void test_calculerMarge(){
		CookieStat c = new CookieStat();
		c.matiere = 2;
		c.prix = 5;
		c.taxe = 10;
		int marge = calculerMarge(c);
		assertEquals( 3 , marge);
	}

	void test_formaterMarge(){
		assertEquals( "+5" , formaterMarge(5));
		assertEquals( "-3" , formaterMarge(-3));
		assertEquals( "+0" , formaterMarge(0));
	}

	void test_creerCokkieDepuisLigne(){
		initialiserCheminsRessources();
		CSVFile table = loadCSV(cookiesCsv, CSV_SEPARATOR);
		CookieStat c = creerCookieDepuisLigne(table, 1);
		assertEquals( "C001" , c.id);
		assertEquals( "Cookie Classique" , c.nom);
		assertEquals( 20 , c.matiere);
		assertEquals( 25 , c.prix);
		assertEquals( 15 , c.taxe);
		assertEquals( 15 , c.quantite);
		//assertEquals( 15 , c.quantite);

	}

	void test_copierCookie(){
		CookieStat source = new CookieStat();
		source.id = "C123";
		source.nom = "Cookie Test";
		source.matiere = 10;
		source.prix = 15;
		source.taxe = 5;
		source.quantite = 20;

		CookieStat copie = copierCookie(source);
		assertEquals( "C123" , copie.id);
		assertEquals( "Cookie Test" , copie.nom);
		assertEquals( 10 , copie.matiere);
		assertEquals( 15 , copie.prix);
		assertEquals( 5 , copie.taxe);
		assertEquals( 20 , copie.quantite);
	}

	void test_creerQuestionDepuisLigne(){
		initialiserCheminsRessources();
		CSVFile table = loadCSV(questionsCsv, CSV_SEPARATOR);
		Question q = creerQuestionDepuisLigne(table, 1);
		assertEquals( "Q001" , q.id);
		assertEquals( "Quel indicateur est liée à la rentabilée quotidienne de votre biscuiterie ?" , q.intitule);
		assertEquals( "B" , q.bonneReponse);
	}

	void test_nouvellePartieInitiale(){
		CookieStat[] cookies = new CookieStat[1];
		cookies[0] = new CookieStat();
		cookies[0].id = "C001";
		cookies[0].nom = "Cookie Test";
		cookies[0].matiere = 10;
		cookies[0].prix = 15;
		cookies[0].taxe = 5;
		cookies[0].quantite = 20;

		Partie p = nouvellePartieInitiale(cookies);
		assertEquals( 1 , p.jour);
		assertEquals( ARGENT_DEPART , p.argent);
		assertEquals( GAIN_DEPART , p.gainJour);
		assertEquals( 5 , p.quantite);
		assertEquals( "C001" , p.cookie.id);
	}

	void test_estReponseValide() {
        assertEquals(true,estReponseValide("A"));
        assertEquals(true,estReponseValide("B"));
        assertEquals(true,estReponseValide("c"));
        assertEquals(false,estReponseValide("Z"));
        assertEquals(false,estReponseValide("x"));
        assertEquals(false,estReponseValide("1"));
        assertEquals(false,estReponseValide("!"));
    }

	void test_verifierReponse(){
        assertEquals(true, verifierReponse("a","a"));
        assertEquals(false, verifierReponse("a","c"));
        assertEquals(false, verifierReponse("/","c"));
    }

	void test_indiceDepuisLettre(){
        assertEquals(0 , indiceDepuisLettre("a"));
        assertEquals(3 , indiceDepuisLettre("D"));
        assertEquals(3 , indiceDepuisLettre("Z"));
    }

	void test_lettreDepuisIndice(){
        assertEquals("A" , lettreDepuisIndice(0));
        assertEquals("C" , lettreDepuisIndice(2));
        assertEquals("D" , lettreDepuisIndice(199));
    }

    void test_estChiffre(){
        assertEquals(true , estChiffre("1"));
        assertEquals(true , estChiffre("5"));
        assertEquals(false , estChiffre("A"));
        assertEquals(false , estChiffre("b"));
    }

	void test_chiffreDepuisTexte(){
        assertEquals(0 , chiffreDepuisTexte("a"));
        assertEquals(5 , chiffreDepuisTexte("E"));
        assertEquals(1 , chiffreDepuisTexte("1"));
		assertEquals(6 , chiffreDepuisTexte("F"));
    }

	void test_entierDepuisTexte(){
        assertEquals(1 , entierDepuisTexte("1"));
        assertEquals(5 , entierDepuisTexte("5"));
        assertEquals(0 , entierDepuisTexte("a"));
    }

	
	void test_nouvellePartieAvecCookie(){
		Partie partie = new Partie();
		CookieStat cookie = new CookieStat();
		partie.jour = 1;
		partie.argent = 150;
		partie.gainJour = 30;
		partie.quantite = 6;
		partie.cookie = copierCookie(cookie);
		assertEquals( 1, nouvellePartieAvecCookie(cookie).jour);
		assertEquals( ARGENT_DEPART , nouvellePartieAvecCookie(cookie).argent);
	}

	void test_afficherMenuSelectionCookies(){
		initialiserCheminsRessources();
		CookieStat[] cookies = chargerCookies();
		int choix = afficherMenuSelectionCookie(cookies);
		assertTrue( choix >= 0 && choix <= length(cookies) );
	}
	
	void test_csvVersTableau(){
		initialiserCheminsRessources();
		CSVFile csv = loadCSV(savesCsv, CSV_SEPARATOR);
		String[][] data = csvVersTableau(csv);
		assertEquals( rowCount(csv) , length(data) );
		assertEquals( NB_COLONNES_SAVE , length(data[0]) );
	}

	void test_estAccepatable(){
		assertEquals( true , estAccepatable("oui"));
		assertEquals( true , estAccepatable("jsppas"));
		assertEquals( false , estAccepatable("n"));
		assertEquals( false , estAccepatable("nazertyuiopqsdfghjklmqsdfghjk"));
		assertEquals( false , estAccepatable("nrzet;rt"));
	}

	void test_padDroit() {
		assertEquals("test      ", padDroit("test", 10));
		assertEquals("test", padDroit("test", 4));
		assertEquals("test", padDroit("test", 3)); // Ne tronque pas
	}

	void test_padDroitPoints() {
		assertEquals("test......", padDroitPoints("test", 10));
		assertEquals("test", padDroitPoints("test", 4));
	}

	void test_centrer() {
		assertEquals("   test   ", centrer("test", 10)); // 10-4=6 => 3 espaces de chaque côté
		assertEquals("  test   ", centrer("test", 9));   // 9-4=5  => 2 espaces à gauche, 3 à droite
		assertEquals("test", centrer("test", 4));
		assertEquals("test", centrer("test", 2));
	}

	void test_repeter() {
		assertEquals("abcabc", repeter("abc", 2));
		assertEquals("", repeter("abc", 0));
		assertEquals("aaaaa", repeter("a", 5));
	}

	void test_niveauDepuisTexte() {
		assertTrue(Difficulte.FACILE == niveauDepuisTexte("FACILE"));
		assertTrue(Difficulte.NORMAL == niveauDepuisTexte("NORMAL"));
		assertTrue(Difficulte.MOYEN == niveauDepuisTexte("MOYEN"));
		assertTrue(Difficulte.DIFFICILE == niveauDepuisTexte("DIFFICILE"));
		assertTrue(Difficulte.DIFFICILE == niveauDepuisTexte("INCONNU"));
	}

	void test_choisirQuestion() {
		Question q1 = new Question(); q1.niveau = Difficulte.FACILE;
		Question q2 = new Question(); q2.niveau = Difficulte.MOYEN;
		Question[] questions = new Question[]{q1, q2};

		Question r1 = choisirQuestion(questions, Difficulte.NORMAL);
		assertTrue(r1 == q1 || r1 == q2);

		Question r2 = choisirQuestion(questions, Difficulte.FACILE);
		assertTrue(Difficulte.FACILE == r2.niveau);

		Question r3 = choisirQuestion(questions, Difficulte.MOYEN);
		assertTrue(Difficulte.MOYEN == r3.niveau);
	}

	// endregion

}

