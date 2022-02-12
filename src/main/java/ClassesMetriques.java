public class ClassesMetriques {

    private String nom;
    private int WMC = 0;
    private int classe_LOC = 0;
    private int classe_CLOC = 0;
    private double classe_DC = 0;
    private double classe_BC = 0;

    public ClassesMetriques(String name) {
        this.nom = name;
    }

    public String getNom() {
        return nom;
    }

    public double getclasse_DC() {
        return classe_DC;
    }

    public void setclasse_DC(){
        if(this.classe_LOC != 0){
            this.classe_DC = ((double)this.classe_CLOC)/((double)this.classe_LOC);
        }
    }

    public int getClasse_CLOC() {
        return classe_CLOC;
    }

    public void setClasse_CLOC(int classe_CLOC) {
        this.classe_CLOC = classe_CLOC;
    }

    public int getClasse_LOC() {
        return classe_LOC;
    }

    public void setClasse_LOC(int classe_LOC) {
        this.classe_LOC = classe_LOC;
    }

    public int getWMC() {
        return WMC;
    }

    public void setWMC(int wMC) {
        this.WMC = wMC;
    }

    public double getClasse_BC() {
        return classe_BC;
    }

    public void setclasse_BC() {
        if(this.WMC == 0){
            this.classe_BC = this.classe_DC;
        } else{
            this.classe_BC = this.classe_DC/((double)this.WMC);
        }
    }
}
