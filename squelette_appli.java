
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

            System.out.println("Choix de l'action a effectuer :"
                    + "\n\t-0 pour changer la fonction d'une cage."
                    + "\n\t-1 pour ajouter un animal."
                    + "\n\t-2 pour changer un animal de cage."
                    + "\n\t-Autre chose pour quitter le programme");
            int choix = LectureClavier.lireEntier("");
            switch (choix) {
                case 0:
                    updateCage();
                    break;
                case 1:
                    addAnimal();
                    break;
                case 2:
                    moveAnimal();
                    break;
                default:
                    break;
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
        } catch (SQLException s) {
            conn.close();
        }

    }

    public static void addAnimal() throws SQLException {
        System.out.println("MAJ de la cage");
        try {
            PreparedStatement updateFonctionPrepared = null;

            Statement stmt = conn.createStatement();

            System.out.println("Liste des cages");
            ResultSet rs = stmt.executeQuery("SELECT * FROM LesCages");
            while (rs.next()) {
                int numeroCage = rs.getInt("noCage");
                System.out.println(numeroCage + "");
            }

            System.out.println("Donnez le nom :");
            String nom = LectureClavier.lireChaine();;
            System.out.println("Donnez le sexe :");
            String sexe = LectureClavier.lireChaine();
            System.out.println("Donnez le type :");
            String type = LectureClavier.lireChaine();
            System.out.println("Donnez la fonction cage :");
            String fctCage = LectureClavier.lireChaine();
            System.out.println("Donnez le pays :");
            String pays = LectureClavier.lireChaine();
            System.out.println("Donnez l'annee de naissance :");
            int anNais = LectureClavier.lireEntier("");
            System.out.println("Donnez le no cage :");
            int noCage = LectureClavier.lireEntier("");
            System.out.println("Donnez le nombre de maladies :");
            int nbMal = LectureClavier.lireEntier("");

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
        } catch (SQLException s) {
            conn.close();
        }

    }

    public static void moveAnimal() throws SQLException {
        System.out.println("Mise a jour de la cage d'un animal");
        PreparedStatement updateFonctionPrepared = null;
        System.out.println("Donnez le nom :");
        String nom = LectureClavier.lireChaine();
        String fonction = "";
        String requete = "SELECT * FROM lesAnimaux where nomA ='" + nom + "'";
        System.out.println(requete);
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(requete);
            while (rs.next()) {
                fonction = rs.getString("fonction_Cage");

            }

            System.out.println("Liste des cages");
            rs = stmt.executeQuery("SELECT * FROM LesCages where fonction = '" + fonction + "'");

            int numeroCage;
            Collection<Integer> cagePossibles = new ArrayList<Integer>();
            while (rs.next()) {
                numeroCage = rs.getInt("noCage");
                cagePossibles.add(numeroCage);
                System.out.println(numeroCage);
            }

            System.out.println("Donnez le nouveau numero de cage :");
            int noCage = LectureClavier.lireEntier("");
            if (!cagePossibles.contains(noCage)) {
                System.out.println("Erreur : la cage " + noCage + " est incompatible.");
                return;
            }

            String updateFonction
                    = "UPDATE lesAnimaux SET noCage=? where nomA=?";

            updateFonctionPrepared = conn.prepareStatement(updateFonction);

            updateFonctionPrepared.setInt(1, noCage);
            updateFonctionPrepared.setString(2, nom);

            updateFonctionPrepared.executeUpdate();

            conn.commit();
        } catch (SQLException s) {
            conn.close();
        }
    }
}
