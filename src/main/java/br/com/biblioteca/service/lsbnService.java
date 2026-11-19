package br.com.biblioteca.service;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import java.util.List;
import java.util.LinkedHashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class IsbnService {
    // A Open Library redireciona a consulta de ISBN para a página da edição.
    private final RestClient client = RestClient.builder()
            .baseUrl("https://openlibrary.org")
            .requestFactory(new JdkClientHttpRequestFactory(HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL)
                    .connectTimeout(Duration.ofSeconds(10)).build()))
            .build();

    public Map<?, ?> buscar(String isbn) {
        String limpo = isbn.replaceAll("[- ]", "");
        if (!limpo.matches("(?:[0-9]{13}|[0-9]{9}[0-9Xx])")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe um ISBN de 10 ou 13 caracteres.");
        }
        try {
            Map<?, ?> livro = client.get().uri("/isbn/{isbn}.json", limpo).retrieve().body(Map.class);
            if (livro == null || livro.get("title") == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Livro não encontrado na Open Library.");
            }
            Map<String, Object> resultado = new LinkedHashMap<>();
            resultado.put("title", livro.get("title"));
            resultado.put("editora", primeiroTexto(livro.get("publishers")));
            resultado.put("categoria", primeiroTexto(livro.get("subjects")));
            // Alguns dados vêm em documentos separados; ausência deles não impede a busca.
            Map<?, ?> autorRef = primeiroMapa(livro.get("authors"));
            Map<?, ?> autor = consultarReferencia(autorRef, "/authors/");
            resultado.put("autor", autor == null ? "" : String.valueOf(autor.get("name") == null ? "" : autor.get("name")));
            if (resultado.get("categoria").equals("")) {
                Map<?, ?> obra = consultarReferencia(primeiroMapa(livro.get("works")), "/works/");
                if (obra != null) resultado.put("categoria", primeiroTexto(obra.get("subjects")));
            }
            return resultado;
        } catch (HttpClientErrorException.NotFound ex) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ISBN não encontrado na Open Library.");
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Não foi possível consultar a Open Library. Verifique sua conexão e tente novamente.");
        }
    }

    private String primeiroTexto(Object valor) {
        if (!(valor instanceof List<?> lista) || lista.isEmpty() || !(lista.get(0) instanceof String)) return "";
        return ((String) lista.get(0)).trim();
    }

    private Map<?, ?> primeiroMapa(Object valor) {
        if (!(valor instanceof List<?> lista) || lista.isEmpty() || !(lista.get(0) instanceof Map<?, ?>)) return null;
        return (Map<?, ?>) lista.get(0);
    }

    private Map<?, ?> consultarReferencia(Map<?, ?> referencia, String prefixo) {
        if (referencia == null || !(referencia.get("key") instanceof String chave) || !chave.matches(prefixo + "[A-Za-z0-9]+"))
            return null;
        try {
            return client.get().uri(chave + ".json").retrieve().body(Map.class);
        } catch (RestClientException ex) {
            return null;
        }
    }
}