import java.io.*;
public class StructureFichier {
    private StructureFichier parentDirectory;
    private File paquetDirectory;

    private int paquet_LOC = 0;
    private int paquet_CLOC = 0;
    private int WCP = 0;

    private double paquet_DC = 0;
    private double paquet_BC = 0;

    public StructureFichier(File paquetDirectory, StructureFichier parentDirectory){
        this.paquetDirectory =paquetDirectory;
        this.parentDirectory=parentDirectory;
    }

    public String getNom() {
        return paquetDirectory.getName();
    }
    public File getPaquetDirectory() {
        return paquetDirectory;
    }
    public StructureFichier getParent() {
        return parentDirectory;
    }

    public int getPaquet_LOC() {
        return paquet_LOC;
    }

    public void setPaquet_LOC(int paquet_LOC) {
        this.paquet_LOC = paquet_LOC;
    }

    public int getPaquet_CLOC() {
        return paquet_CLOC;
    }

    public void setPaquet_CLOC(int paquet_CLOC) {
        this.paquet_CLOC = paquet_CLOC;
    }

    public double getPaquet_DC() {
        return paquet_DC;
    }

    public void setPaquet_DC() {
        if(this.paquet_LOC != 0){
            this.paquet_DC = ((double)this.paquet_CLOC)/((double)this.paquet_LOC);
        }
    }

    public int getWCP() {
        return this.WCP;
    }

    public void setWCP(int WCP) {
        this.WCP = WCP;
    }

    public double getPaquet_BC() {
        return paquet_BC;
    }

    public void setPaquet_BC() {
        if(this.WCP !=0){
            this.paquet_BC = (this.paquet_DC)/((double)this.WCP);
        }
    }
}
