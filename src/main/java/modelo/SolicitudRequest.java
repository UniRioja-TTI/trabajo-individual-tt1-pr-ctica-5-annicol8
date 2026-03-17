package modelo;

import java.util.List;

public class SolicitudRequest {
    private List<Integer> cantidadesIniciales;
    private List<String> nombreEntidades;

    public SolicitudRequest(List<Integer> cantidadesIniciales, List<String> nombreEntidades) {
        this.cantidadesIniciales = cantidadesIniciales;
        this.nombreEntidades = nombreEntidades;
    }

    public List<Integer> getCantidadesIniciales() { return cantidadesIniciales; }
    public void setCantidadesIniciales(List<Integer> cantidadesIniciales) { this.cantidadesIniciales = cantidadesIniciales; }

    public List<String> getNombreEntidades() { return nombreEntidades; }
    public void setNombreEntidades(List<String> nombreEntidades) { this.nombreEntidades = nombreEntidades; }
}