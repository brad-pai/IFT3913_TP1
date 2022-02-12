public class FonctionsMetriques {

    private int cyc = 0;
    private String parentClasseNom;

    public FonctionsMetriques(String parentClasseNom) {
        this.parentClasseNom = parentClasseNom;
    }

    public int getCC() {
        return this.cyc;
    }
    public void setCC(int compCyc) {
        this.cyc = compCyc;

    }
    public String getParentClasseNom() {
        return parentClasseNom;
    }

}
