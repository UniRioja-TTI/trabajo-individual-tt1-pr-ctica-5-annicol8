package servicios;

import interfaces.InterfazContactoSim;
import modelo.DatosSolicitud;
import modelo.DatosSimulation;
import modelo.Entidad;
import modelo.Punto;
import modelo.SolicitudRequest;
import modelo.SolicitudResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ContactoSimService implements InterfazContactoSim {

    private List<Entidad> entidades;
    private final RestClient restClient;
    private static final String USUARIO = "ana";
    private static final String BASE_URL = "http://servicio-consumible:8080";

    public ContactoSimService() {
        this.restClient = RestClient.create(BASE_URL);

        entidades = new ArrayList<>();
        Entidad e1 = new Entidad();
        e1.setId(1); e1.setName("Elemento Alfa"); e1.setDescripcion("Primera entidad");
        Entidad e2 = new Entidad();
        e2.setId(2); e2.setName("Elemento Beta"); e2.setDescripcion("Segunda entidad");
        Entidad e3 = new Entidad();
        e3.setId(3); e3.setName("Elemento Gamma"); e3.setDescripcion("Tercera entidad");
        entidades.add(e1);
        entidades.add(e2);
        entidades.add(e3);

    }

    @Override
    public int solicitarSimulation(DatosSolicitud sol) {
        List<Integer> cantidades = new ArrayList<>(sol.getNums().values());
        List<String> nombres = entidades.stream()
                .map(Entidad::getName)
                .toList();

        SolicitudRequest request = new SolicitudRequest(cantidades, nombres);

        var response = restClient.post()
                .uri("/Solicitud/Solicitar?nombreUsuario=" + USUARIO)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(SolicitudResponse.class);

        if (response != null && response.isDone()) {
            return response.getTokenSolicitud();
        }
        return -1;
    }

    @Override
    public DatosSimulation descargarDatos(int ticket) {
        // Llamar al servicio
        var response = restClient.post()
                .uri("/Resultados?nombreUsuario=" + USUARIO + "&tok=" + ticket)
                .retrieve()
                .body(modelo.ResultsResponse.class);

        if (response == null || !response.isDone() || response.getData() == null) {
            return new DatosSimulation();
        }

        // Parsear el texto del servicio
        String[] lines = response.getData().split("\n");
        int anchoTablero = Integer.parseInt(lines[0].trim());

        Map<Integer, List<Punto>> puntos = new HashMap<>();
        int maxSegundos = 0;

        for (int i = 1; i < lines.length; i++) {
            String line = lines[i].trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split(",");
            if (parts.length == 4) {
                int tiempo = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                int x = Integer.parseInt(parts[2]);
                String color = parts[3];

                if (tiempo > maxSegundos) maxSegundos = tiempo;

                puntos.computeIfAbsent(tiempo, k -> new ArrayList<>());
                Punto p = new Punto();
                p.setY(y);
                p.setX(x);
                p.setColor(color);
                puntos.get(tiempo).add(p);
            }
        }

        DatosSimulation ds = new DatosSimulation();
        ds.setAnchoTablero(anchoTablero);
        ds.setMaxSegundos(maxSegundos);
        ds.setPuntos(puntos);
        return ds;
    }

    @Override
    public List<Entidad> getEntities() {
        return entidades;
    }

    @Override
    public boolean isValidEntityId(int id) {
        return entidades.stream().anyMatch(e -> e.getId() == id);
    }
}