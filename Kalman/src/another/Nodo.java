package another;

public class Nodo {
    private int linha;
    private int coluna;
    private int dist;
    private int custo;
    boolean visited;
    private Nodo pred;
    
    Nodo(int l, int c, int dl, int dc){
        linha = l;
        coluna = c;
        pred = null;
        dist = Math.abs(l - dl) + Math.abs(c - dc);
        visited = false;
    }

    public int getLinha() {
        return linha;
    }

    public int getColuna() {
        return coluna;
    }
    
    public int getDist(){
        return dist;
    }
    
    public void setVisited(){
        visited = true;
    }

    public int getCusto() {
        return custo;
    }

    public void setCusto(int custo) {
        this.custo = custo;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) {
        this.visited = visited;
    }

    public Nodo getPred() {
        return pred;
    }

    public void setPred(Nodo pred) {
        this.pred = pred;
    }
    
    
}