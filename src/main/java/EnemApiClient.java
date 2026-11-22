import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class EnemApiClient {

    private static final String BASE_URL = "https://api.enem.dev/v1";
    private static final int LIMIT = 50; //


    private static final int MAT_INICIO = 136;
    private static final int MAT_FIM = 180;

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        int ano = 2022;

        Map<Integer, JsonNode> questoes = new TreeMap<>();

        buscarPorPaginas(ano, questoes);
        buscarFaltantes(ano, questoes);

        System.out.println();
        System.out.println("Questões de Matemática do ENEM " + ano + ": " + questoes.size() + " de 45");

        List<Integer> faltando = new ArrayList<>();
        for (int i = MAT_INICIO; i <= MAT_FIM; i++) {
            if (!questoes.containsKey(i)) faltando.add(i);
        }
        if (!faltando.isEmpty()) {
            System.out.println("Não foi possível obter as questões: " + faltando);
        }
        System.out.println();

        for (JsonNode q : questoes.values()) {
            imprimir(q);
        }
    }

    private static void buscarPorPaginas(int ano, Map<Integer, JsonNode> destino) throws Exception {
        int offset = 0;

        while (true) {
            String url = BASE_URL + "/exams/" + ano + "/questions?limit=" + LIMIT + "&offset=" + offset;
            HttpResponse<String> response = get(url);

            if (response.statusCode() != 200) {
                throw new RuntimeException("Erro " + response.statusCode() + ": " + response.body());
            }

            JsonNode raiz = mapper.readTree(response.body());
            JsonNode questoes = raiz.path("questions");
            System.out.println("Página offset=" + offset + ": " + questoes.size() + " questões");

            for (JsonNode q : questoes) {
                int index = q.path("index").asInt();
                if (index >= MAT_INICIO && index <= MAT_FIM) {
                    destino.putIfAbsent(index, q);
                }
            }

            if (!raiz.path("metadata").path("hasMore").asBoolean(false)) {
                break;
            }
            offset += LIMIT;
        }
    }

    private static void buscarFaltantes(int ano, Map<Integer, JsonNode> destino) throws Exception {
        for (int i = MAT_INICIO; i <= MAT_FIM; i++) {
            if (destino.containsKey(i)) continue;

            HttpResponse<String> response = get(BASE_URL + "/exams/" + ano + "/questions/" + i);

            if (response.statusCode() == 429) {
                System.out.println("Rate limit atingido; parando as buscas individuais.");
                break;
            }
            if (response.statusCode() != 200) {
                System.out.println("Questão " + i + ": erro " + response.statusCode());
                continue;
            }

            JsonNode q = mapper.readTree(response.body());
            if (q.path("index").asInt() == i) {
                destino.put(i, q);
            }
            Thread.sleep(300);
        }
    }

    private static HttpResponse<String> get(String url) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private static void imprimir(JsonNode q) {
        System.out.println(q.path("title").asText());

        if (q.path("context").isTextual()) {
            System.out.println(q.path("context").asText());
        }
        if (q.path("alternativesIntroduction").isTextual()) {
            System.out.println(q.path("alternativesIntroduction").asText());
        }

        for (JsonNode alt : q.path("alternatives")) {
            String texto = alt.path("text").isTextual()
                    ? alt.path("text").asText()
                    : "(alternativa em imagem)";
            System.out.println("  " + alt.path("letter").asText() + ") " + texto);
        }
        System.out.println("Gabarito: " + q.path("correctAlternative").asText());
        System.out.println("----------------------------------------");
    }
}