
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

public class squelette_appli {

    static final String CONN_URL = "jdbc:oracle:thin:@im2ag-oracle.e.ujf-grenoble.fr:1521:im2ag";
    static final String USER = "gontardb";
    static final String PASSWD = "Pakedchip2";

    static Connection conn;

    public static void main(String args[]) {

        try {
            // Enregistrement du driver Oracle
            System.out.print("Loading Oracle driver... ");
            DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
            System.out.println("loaded");

            // Etablissement de la connection
            System.out.print("Connecting to the database... ");
            conn = DriverManager.getConnection(CONN_URL, USER, PASSWD);
            System.out.println("connected");

            // Desactivation de l'autocommit
            conn.setAutoCommit(false);
            System.out.println("Autocommit disabled");
            boolean cont = true;
            while (cont) {
                System.out.println("Choix de l'action a effectuer :"
                        + "\n\t-u pour changer la fonction d'une cage."
                        + "\n\t-a pour ajouter un animal."
                        + "\n\t-m pour changer un animal de cage."
                        + "\n\t-Autre chose pour quitter le programme");
                char choix = LectureClavier.lireChar("");
                switch (choix) {
                    case 'u':
                        updateCage();
                        break;
                    case 'a':
                        addAnimal();
                        break;
                    case 'm':
                        moveAnimal();
                        break;
                    default:
                        cont = false;
                        break;
                }
            }
            conn.close();
            System.out.println("bye.");
            // traitement d'exception
        } catch (SQLException e) {
            System.err.println("failed");
            System.out.println("Affichage de la pile d'erreur");
            e.printStackTrace(System.err);
            System.out.println("Affichage du message d'erreur");
            System.out.println(e.getMessage());
            System.out.println("Affichage du code d'erreur");
            System.out.println(e.getErrorCode());
        }
    }

    public static void updateCage() throws SQLException {

        System.out.println("MAJ de la cage");
        try {
            PreparedStatement updateFonctionPrepared = null;
            Statement stmt = conn.createStatement();
            System.out.println("Voici les cages disponibles :");
            ResultSet rs = stmt.executeQuery("SELECT * FROM LesCages");
            while (rs.next()) {
                int numeroCage = rs.getInt("noCage");
                String fonction = rs.getString("fonction");
                System.out.println(numeroCage + " " + fonction);
            }
            System.out.println("Donnez le numero de votre cage.");
            int cageModifier = LectureClavier.lireEntier("");

            System.out.println("Nommez la nouvelle cage svp.");
            String nouvelleFonction = LectureClavier.lireChaine();

            String updateFonction
                    = "update LesCages set fonction = ? where noCage = ?";

            updateFonctionPrepared = conn.prepareStatement(updateFonction);

            updateFonctionPrepared.setString(1, nouvelleFonction);
            updateFonctionPrepared.setInt(2, cageModifier);

            updateFonctionPrepared.executeUpdate();
            conn.commit();
            System.out.println("Mise à jour terminée.\n");
        } catch (SQLException s) {
            
            s.printStackTrace();
	    System.out.println(s.getMessage());
        }

    }

    public static void addAnimal() throws SQLException {
        System.out.println("Ajout d'un animal : par defaut son nombre de maladies sera mis a 0.");
        try {
            PreparedStatement updateFonctionPrepared = null;

            String selectFonction
                    = "SELECT distinct(noCage) FROM LesCages NATURAL JOIN LesAnimaux where fonction = ? and type_an = ? UNION (Select noCage from LesCages where fonction = ? and noCage not in (Select noCage from LesAnimaux))";

            System.out.println("Donnez la fonction cage :");
            String fctCage = LectureClavier.lireChaine();
            System.out.println("Donnez le type :");
            String type = LectureClavier.lireChaine();
            PreparedStatement stmt = conn.prepareStatement(selectFonction);
            stmt.setString(1, fctCage);
            stmt.setString(2, type);
            stmt.setString(3, fctCage);

            System.out.println("Liste des cages possibles : \n");
            ResultSet rs = stmt.executeQuery();
            Collection<Integer> cagePossibles = new ArrayList<Integer>();
            while (rs.next()) {
                int numeroCage = rs.getInt("noCage");
                cagePossibles.add(numeroCage);
                System.out.println(numeroCage + "");
            }
            if (cagePossibles.size() == 0) {
                System.out.println("Erreur : Aucune cage compatible.\n");
                return;
            }

            System.out.println("Donnez le no cage :");
            int noCage = LectureClavier.lireEntier("");
            if (!cagePossibles.contains(noCage)) {
                System.out.println("Erreur : la cage " + noCage + " est incompatible ou n'existe pas, mais on continue pour possiblement tester le trigger 2.\n");
                //return; Decommentez la si vous souhaitez que le programme s'arrete
            }
            System.out.println("Donnez le nom :");
            String nom = LectureClavier.lireChaine();;
            System.out.println("Donnez le sexe : male ou femelle");
            String sexe = LectureClavier.lireChaine();
            System.out.println("Donnez le pays :");
            String pays = LectureClavier.lireChaine();
            System.out.println("Donnez l'annee de naissance : au moins 1900 ");
            int anNais = LectureClavier.lireEntier("");
            int nbMal = 0;

            String updateFonction
                    = "INSERT into lesAnimaux values (?,?,?,?,?,?,?,?)";

            updateFonctionPrepared = conn.prepareStatement(updateFonction);

            updateFonctionPrepared.setString(1, nom);
            updateFonctionPrepared.setString(2, sexe);
            updateFonctionPrepared.setString(3, type);
            updateFonctionPrepared.setString(4, fctCage);
            updateFonctionPrepared.setString(5, pays);
            updateFonctionPrepared.setInt(6, anNais);
            updateFonctionPrepared.setInt(7, noCage);
            updateFonctionPrepared.setInt(8, nbMal);

            updateFonctionPrepared.executeUpdate();

            conn.commit();
            System.out.println("Ajout terminé.\n");
        } catch (SQLException s) {
          
            s.printStackTrace();
	    System.out.println(s.getMessage());
        }

    }

    public static void moveAnimal() throws SQLException {
        System.out.println("Mise a jour de la cage d'un animal");
        PreparedStatement updateFonctionPrepared = null;

        String fonction = "";
        String type = "";
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM lesAnimaux");
            System.out.println("Animaux disponibles : ");
            Collection<String> animauxPossibles = new ArrayList<String>();
            while (rs.next()) {
                System.out.println(rs.getString("nomA") + " " + rs.getString("fonction_Cage") + " " + rs.getString("type_an"));
                animauxPossibles.add(rs.getString("nomA"));
            }
            System.out.println("Donnez le nom :");
            String nom = LectureClavier.lireChaine();
            if(!animauxPossibles.contains(nom)){
                System.out.println("Erreur : Animal inconnu\n");
            }
            stmt = conn.createStatement();
            String requete = "SELECT * FROM lesAnimaux where nomA ='" + nom + "'";
            rs = stmt.executeQuery(requete);
            while (rs.next()) {
                fonction = rs.getString("fonction_Cage");
                type = rs.getString("type_an");
            }

            System.out.println("Liste des cages possibles : \n");
            rs = stmt.executeQuery("SELECT distinct(noCage) FROM LesCages NATURAL JOIN LesAnimaux where fonction = '" + fonction + "' AND type_an = '" + type + "' UNION (Select noCage from LesCages where fonction = '" + fonction + "' and noCage not in (Select noCage from LesAnimaux))");

            int numeroCage;
            Collection<Integer> cagePossibles = new ArrayList<Integer>();
            while (rs.next()) {
                numeroCage = rs.getInt("noCage");
                cagePossibles.add(numeroCage);
                System.out.println(numeroCage);
            }

            if (cagePossibles.size() == 0) {
                System.out.println("Erreur : Aucune cage compatible.\n");
                return;
            }

            System.out.println("Donnez le nouveau numero de cage :");
            int noCage = LectureClavier.lireEntier("");
            if (!cagePossibles.contains(noCage)) {
                System.out.println("Erreur : la cage " + noCage + " est incompatible ou n'existe pas, mais on continue pour possiblement tester le trigger 2.");
                //return; Décommenter la si vous souhaitez que le programme s'arrete
            }

            String updateFonction
                    = "UPDATE lesAnimaux SET noCage=? where nomA=?";

            updateFonctionPrepared = conn.prepareStatement(updateFonction);

            updateFonctionPrepared.setInt(1, noCage);
            updateFonctionPrepared.setString(2, nom);

            updateFonctionPrepared.executeUpdate();

            conn.commit();
            System.out.println("Fin de deplacement de cage._\n");
        } catch (SQLException s) {
            
            s.printStackTrace();
	    System.out.println(s.getMessage());
        }
    }
}
