import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchEntry;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.utils.CodeGenerationUtils;
import com.github.javaparser.utils.SourceRoot;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {
    SourceRoot racineSource = new SourceRoot(
            CodeGenerationUtils.mavenModuleRoot(Parser.class).resolve("."));
    SourceRoot racineSourceNC = new SourceRoot(
            CodeGenerationUtils.mavenModuleRoot(Parser.class).resolve("."));

    private String cheminFicher;
    private CompilationUnit compilationU;
    private ArrayList<ClassesMetriques> classes = new ArrayList<>();

    /**
     * Constructor
     * @param cheminFicher chemin du fichier a parser
     */
    public Parser(String cheminFicher) {
        compilationU = racineSource.parse("", cheminFicher);
        this.cheminFicher = cheminFicher;
    }

    /**
     * Extrait les métriques classe_LOC
     *
     */
    public ArrayList<String[]> getclasse_LOC() {

        ArrayList<String[]> classLOC = new ArrayList<>();
        CompilationUnit cuNC = this.racineSourceNC.parse("", this.cheminFicher);
        effaceComment(cuNC);
        cuNC.accept(new GenericVisitorAdapter<Integer, ArrayList<String[]>>() {

            @Override
            public Integer visit(ClassOrInterfaceDeclaration n, ArrayList<String[]> arg) {
                String classeNom = n.getNameAsString();
                int cLOC = 0;
                String n_string = n.toString();
                String[] lines = n_string.split("\r\n|\r|\n");
                for (String line : lines) {
                    if (!line.equals("")) {
                        cLOC += 1;
                    }
                }
                String[] valeurs = { classeNom, String.valueOf(cLOC) };
                classLOC.add(valeurs);
                return super.visit(n, arg);
            }

            public Integer visit(EnumDeclaration n, ArrayList<String[]> arg) {
                String classeNom = n.getNameAsString();
                int cLOC = 0;
                String n_string = n.toString();
                String[] lignes = n_string.split("\r\n|\r|\n");
                for (String ligne : lignes) {
                    if (!ligne.equals("")) {
                        cLOC += 1;
                    }
                }
                String[] valeurs = { classeNom, String.valueOf(cLOC) };
                classLOC.add(valeurs);
                return super.visit(n, arg);
            }
        }, classLOC);
        return classLOC;
    }

    /**
     * Extrait les métriques classe_CLOC
     *
     */
    public ArrayList<String[]> getclasse_CLOC() {
        ArrayList<String[]> classCLOC = new ArrayList<>();

        Integer classeLignesComment = this.compilationU.accept(new GenericVisitorAdapter<Integer, ArrayList<String[]>>() {

            public Integer visit(ClassOrInterfaceDeclaration n, ArrayList<String[]> arg) {
                String classeNom = n.getNameAsString();
                int classeCLOC = 0;
                if (n.getComment().isPresent()) {
                    Comment comment = n.getComment().get();
                    classeCLOC += comment.getRange().get().getLineCount();
                }
                int cCLOC = classeCLOC;
                List<Comment> classeCIs = n.getAllContainedComments();
                for (Comment classeCI : classeCIs) {
                    int lineLength = classeCI.getRange().get().getLineCount();
                    cCLOC += lineLength;
                }
                String[] valeurs = { classeNom, String.valueOf(cCLOC) };
                classCLOC.add(valeurs);
                return super.visit(n, arg);
            }
            public Integer visit(EnumDeclaration n, ArrayList<String[]> arg) {
                String classeNom = n.getNameAsString();
                int classeCLOC = 0;
                if (n.getComment().isPresent()) {
                    Comment comment = n.getComment().get();
                    classeCLOC += comment.getRange().get().getLineCount();
                }
                int cCLOC = classeCLOC;
                List<Comment> classeCI = n.getAllContainedComments();

                for (int i = 0; i < classeCI.size(); i++) {
                    int lineLength = classeCI.get(i).getRange().get().getLineCount();
                    cCLOC += lineLength;
                }
                String[] valeurs = { classeNom, String.valueOf(cCLOC) };
                classCLOC.add(valeurs);
                return super.visit(n, arg);
            }
        }, classCLOC);
        return classCLOC;
    }

    /**
     * Genere tous les metriques pour les classes
     *
     * @throws IOException
     */
    public void generateurMetriques() throws IOException {
        ArrayList<String[]> getclasse_LOC = this.getclasse_LOC();
        ArrayList<String[]> getclasse_CLOC = this.getclasse_CLOC();
        ArrayList<ClassesMetriques> classes = new ArrayList<>();
        Map<String, Integer> WMCs = new HashMap<>();

        for (int i = 0; i < getclasse_LOC.size(); i++) {
            ClassesMetriques classeMetrique = new ClassesMetriques(getclasse_LOC.get(i)[0]);
            classeMetrique.setClasse_LOC(Integer.parseInt(getclasse_LOC.get(i)[1]));
            classeMetrique.setClasse_CLOC(Integer.parseInt(getclasse_CLOC.get(i)[1]));
            classeMetrique.setclasse_DC();
            classes.add(classeMetrique);
            WMCs.put(getclasse_LOC.get(i)[0], 0);
        }
        this.classes = classes;
        ArrayList<String[]> getfctLignes = this.getfctLignes();
        for (String[] fctL : getfctLignes) {
            FonctionsMetriques fonctionMetrique = new FonctionsMetriques(fctL[0]);
            fonctionMetrique.setCC(Integer.parseInt(fctL[1]));
            WMCs.put(fonctionMetrique.getParentClasseNom(), WMCs.get(fonctionMetrique.getParentClasseNom()) + fonctionMetrique.getCC());
        }
        for (ClassesMetriques cl : this.classes) {
            String classeNom = cl.getNom();
            cl.setWMC(WMCs.get(classeNom));
            cl.setclasse_BC();
        }
    }
    public ArrayList<ClassesMetriques> getClasses() {
        return classes;
    }

    /**
     * Extracts mLOC metrics from all classes/interfaces of the file
     *
     * @throws IOException
     */
    public ArrayList<String[]> getfctLignes() throws IOException {
        ArrayList<String[]> fctLignes = new ArrayList<String[]>();
        CompilationUnit cuNoC = this.racineSourceNC.parse("", this.cheminFicher);
        effaceComment(cuNoC);
        cuNoC.accept(new GenericVisitorAdapter<Integer, ArrayList<String[]>>() {
            @Override
            public Integer visit(MethodDeclaration n, ArrayList<String[]> arg) {
                String fctNom = n.getNameAsString() + "(";
                NodeList<Parameter> parametres = n.getParameters();
                for(int i=0; i<parametres.size(); i++) {
                    fctNom = fctNom.concat(parametres.get(i).getTypeAsString().replace(',', '-'));
                    if(i != parametres.size() - 1) {
                        fctNom = fctNom.concat("_");
                    }
                }
                String classeParent = "";
                int complexite = calculCyc(n);
                Node noeudPresent = n;
                while (noeudPresent.getParentNode().isPresent()) {
                    noeudPresent = noeudPresent.getParentNode().get();
                    if (noeudPresent instanceof ClassOrInterfaceDeclaration) {
                        classeParent = ((ClassOrInterfaceDeclaration) noeudPresent).getNameAsString();
                        break;
                    }
                    else if (noeudPresent instanceof EnumDeclaration) {
                        classeParent = ((EnumDeclaration) noeudPresent).getNameAsString();
                        break;
                    }
                }
                String[] valeurs = {classeParent, String.valueOf(complexite)};
                fctLignes.add(valeurs);
                return super.visit(n, arg);
            }

            @Override
            public Integer visit(ConstructorDeclaration CD, ArrayList<String[]> arg) {
                String fctNom = CD.getNameAsString() + "(";
                NodeList<Parameter> parametres = CD.getParameters();

                for(int i=0; i<parametres.size(); i++) {
                    fctNom = fctNom.concat(parametres.get(i).getTypeAsString().replace(',', '-'));
                    if(i != parametres.size() - 1) {
                        fctNom = fctNom.concat("_");
                    }
                }
                String parentClasseNom = "";
                int complexity = calculCyc(CD);
                Node noeudPresent = CD;
                while (noeudPresent.getParentNode().isPresent()) {
                    noeudPresent = noeudPresent.getParentNode().get();
                    if (noeudPresent instanceof ClassOrInterfaceDeclaration) {
                        parentClasseNom = ((ClassOrInterfaceDeclaration) noeudPresent).getNameAsString();
                        break;
                    }
                    else if (noeudPresent instanceof EnumDeclaration) {
                        parentClasseNom = ((EnumDeclaration) noeudPresent).getNameAsString();
                        break;
                    }
                }
                String[] valeurs = {parentClasseNom,String.valueOf(complexity)};
                fctLignes.add(valeurs);
                return super.visit(CD, arg);
            }
        }, fctLignes);
        return fctLignes;
    }

    /**
     * Efface les commentaires dans un noeud
     *
     * @param noeud fichier racine
     */
    static void effaceComment(Node noeud) {
        for (Comment enfant : noeud.getAllContainedComments()) {
            enfant.remove();
        }
    }

    /**
     * Calcule le parametre complexite pour methode
     *
     * @param methode: La methode pour calculer la complexite
     */
    public int calculCyc(MethodDeclaration methode) {
        Integer[] pred = new Integer [1];
        pred[0] = 0;
        methode.accept(new GenericVisitorAdapter<Void, Integer[]>() {
            public Void visit(TryStmt a, Integer[] arg) {
                arg[0] += 1;
                return super.visit(a, arg);
            }
            public Void visit(ForStmt a, Integer[] arg) {
                arg[0] += 1;
                return super.visit(a, arg);
            }
            public Void visit(IfStmt a, Integer[] arg) {
                arg[0] += 1;
                return super.visit(a, arg);
            }
            public Void visit(SwitchEntry a, Integer[] arg) {
                arg[0] += 1;
                return super.visit(a, arg);
            }
            public Void visit(WhileStmt a, Integer[] arg) {
                arg[0] += 1;
                return super.visit(a, arg);
            }
        }, pred);
        return pred[0] + 1;
    }

    /**
     * Calcule la complexite cyclomatique McCabe du parametre struct
     *
     * @param struct: Structure ou nous voulons calculer la complexite
     */
    public int calculCyc(ConstructorDeclaration struct) {
        Integer[] predicates = new Integer [1];
        predicates[0] = 0;
        struct.accept(new GenericVisitorAdapter<Void, Integer[]>() {
            public Void visit(TryStmt a, Integer[] arg) {
                arg[0] += 1;
                return super.visit(a, arg);
            }
            public Void visit(ForStmt a, Integer[] arg) {
                arg[0] += 1;
                return super.visit(a, arg);
            }
            public Void visit(IfStmt a, Integer[] arg) {
                arg[0] += 1;
                return super.visit(a, arg);
            }
            public Void visit(SwitchEntry a, Integer[] arg) {
                arg[0] += 1;
                return super.visit(a, arg);
            }
            public Void visit(WhileStmt a, Integer[] arg) {
                arg[0] += 1;
                return super.visit(a, arg);
            }
        }, predicates);
        return predicates[0] + 1;
    }
}