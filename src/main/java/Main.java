import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    public static String cheminRecherche;
    public static File fichier_classes = new File("classes.csv");
    public static File fichier_paquets = new File("paquets.csv");

    public static ArrayList<StructureFichier> paquets = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        fichier_classes.delete();
        fichier_paquets.delete();

        if(args.length == 0 || args[0] == null) {
            System.out.println("Entrer le chemin du fichier que vous vouliez analyser.");
            System.exit(1);
        }
        else {
            cheminRecherche =args[0];
        }
        File fichierRecherche = new File(cheminRecherche);
        StructureFichier init = new StructureFichier(fichierRecherche,null);
        paquets.add(init);
        rechercheTraverse(init);
        StructureFichier paquetPresent;
        for (StructureFichier paquet : paquets) {
            paquetPresent = paquet;
            ecriturePaquetDonnees(fichier_paquets, paquetPresent.getPaquetDirectory().getPath(), paquetPresent.getNom(), paquetPresent.getPaquet_LOC(), paquetPresent.getPaquet_CLOC(), paquetPresent.getPaquet_DC(), paquetPresent.getPaquet_BC(), paquetPresent.getWCP());
        }
        System.out.println("Réussi, vérifiez le dossier du projet pour csv.");
    }

    /**
     * Output des donnees pour les classes
     * @param fichierEcrire Le fichier output
     * @param chemin Chemin absolu
     * @param classeNom Nom de la classe
     * @param classe_LOC Nombre de lignes de code d'une classe
     * @param classe_CLOC Nombre de lignes de code d'une classe qui contiennet des commentaires
     * @param classe_DC Densite de commentaires pour une classe
     * @param classe_BC Degre selon lequel une classe est bien commentee
     * @param WMC "Weighted Methods per Class"
     * @throws IOException
     */

    public static void ecritureClassDonnees(File fichierEcrire, String chemin, String classeNom, int classe_LOC, int classe_CLOC, double classe_DC,double classe_BC , int WMC)
            throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(fichierEcrire, true));

        if (fichierEcrire.length() == 0)
            writer.append("chemin, class, classe_LOC, classe_CLOC, classe_DC, WMC, classe_BC\n");

        writer.append(chemin + ", " + classeNom + ", " + classe_LOC + ", " + classe_CLOC + ", " + classe_DC + ", " + WMC + ", " + classe_BC + "\n" );
        writer.close();
    }

    /**
     *Output des donnees pour les paquets
     * @param fichierEcrire Le fichier output
     * @param chemin Chemin absolu
     * @param paquetNom Nom du paquet
     * @param paquet_LOC Nombre de lignes de code d'un paquet
     * @param paquet_CLOC Nombre de lignes de code d'un paquet qui contiennet des commentaires
     * @param paquet_DC Densite de commentaires pour un paquet
     * @param paquet_BC Degre selon lequel un paquet est bien commentee
     * @param WCP "Weighted Methods per Package"
     * @throws IOException
     */
    public static void ecriturePaquetDonnees(File fichierEcrire, String chemin, String paquetNom, int paquet_LOC, int paquet_CLOC, double paquet_DC, double paquet_BC, int WCP) throws IOException {

        BufferedWriter writer = new BufferedWriter(new FileWriter(fichierEcrire, true));
        if (fichierEcrire.length() == 0)
            writer.append("chemin, paquet, paquet_LOC, paquet_CLOC, paquet_DC, WCP, paquet_BC\n");
        writer.append(chemin).append(", ").append(paquetNom).append(", ").append(String.valueOf(paquet_LOC)).append(", ").append(String.valueOf(paquet_CLOC)).append(", ").append(String.valueOf(paquet_DC)).append(", ").append(String.valueOf(WCP)).append(", ").append(String.valueOf(paquet_BC)).append("\n");
        writer.close();
    }

    /**
     * Rechercher dans chaque dossier et sous-dossier pour trouver les fichiers de classes java.
     * @param noeud dossier qui commence la traverse (racine)
     * @throws Exception
     */
    public static void rechercheTraverse(StructureFichier noeud) throws Exception{

        Parser parser;
        File racineFichier = noeud.getPaquetDirectory();
        String cheminRacine = racineFichier.getAbsolutePath();
        ClassesMetriques classePresent;

        if(cheminRacine.endsWith(".java")) {
            parser = new Parser(cheminRacine);
            parser.generateurMetriques();
            ArrayList<ClassesMetriques> classes = parser.getClasses();
            for (ClassesMetriques cl : classes) {
                StructureFichier nodeParentRecursive = noeud.getParent();
                classePresent = cl;
                while (nodeParentRecursive != null) {
                    nodeParentRecursive.setPaquet_LOC(nodeParentRecursive.getPaquet_LOC() + classePresent.getClasse_LOC());
                    nodeParentRecursive.setPaquet_CLOC(nodeParentRecursive.getPaquet_CLOC() + classePresent.getClasse_CLOC());
                    nodeParentRecursive.setPaquet_DC();
                    nodeParentRecursive.setWCP(nodeParentRecursive.getWCP() + classePresent.getWMC());
                    nodeParentRecursive.setPaquet_BC();
                    nodeParentRecursive = nodeParentRecursive.getParent();
                }
                ecritureClassDonnees(fichier_classes, racineFichier.getPath().replace(cheminRecherche, "."), classePresent.getNom(), classePresent.getClasse_LOC(), classePresent.getClasse_CLOC(), classePresent.getclasse_DC(), classePresent.getClasse_BC(), classePresent.getWMC());
            }
        }
        if(racineFichier.isDirectory()){
            String[] sousN = racineFichier.list();
            assert sousN != null;
            for(String fichierNom : sousN){
                File sousNoeud = new File(racineFichier, fichierNom);
                StructureFichier sousSF = new StructureFichier(sousNoeud,noeud);
                if (sousNoeud.isDirectory()){
                    paquets.add(sousSF);
                }
                rechercheTraverse(sousSF);
            }
        }
    }


}
