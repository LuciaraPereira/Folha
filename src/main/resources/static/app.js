








































  livros: {   // obs:  campo de livros nas linhas 42-53  COMMIT 4 Vivian gomes
        titulo: 'Livros',
        singular: 'Livro',
        fields: [
            ['isbn', 'ISBN'],
            ['titulo', 'Título'],
            ['autor', 'Autor', 'select', 'autores'],
            ['categoria', 'Categoria', 'select', 'categorias'],
            ['editora', 'Editora', 'select', 'editoras'],
            ['exemplares', 'Quantidade de exemplares', 'number']
        ]
    },








































































































































































































































































































































































































































































































































































































































if (chave === 'isbn') {  // obs:  trechos do formulário para ISBN e sugestões nas linhas 670-685  COMMIT 4 Vivian gomes
                return `
                    <div class="inline">
                        <label>
                            ${label}
                            ${input}
                        </label>

                        <button
                            type="button"
                            onclick="buscarIsbn()"
                        >
                            Buscar ISBN
                        </button>
                    </div>
                `;
            }


























               if ( // obs:  sugestões nas linhas  713-738 COMMIT 4 Vivian gomes
                    tipo === 'select' &&
                    valor.startsWith('novo:')
                ) {
                    const nome = valor.slice(5);
                    const existentes = await api(referencia);

                    const encontrado = existentes.find(
                        registro =>
                            registro.nome
                                .trim()
                                .toLocaleLowerCase('pt-BR') ===
                            nome
                                .trim()
                                .toLocaleLowerCase('pt-BR')
                    );

                    const criado = encontrado || await api(
                        referencia,
                        {
                            method: 'POST',
                            body: JSON.stringify({ nome })
                        }
                    );

                    body[chave] = { id: criado.id };

































function selecionarSugestao(select, nome) {  // Linha 772 COMMIT 4 Vivian gomes
    if (!nome || !nome.trim()) {
        return false;
    }

    nome = nome.trim();

    for (const opcao of Array.from(select.options)) {
        if (opcao.value.startsWith('novo:')) {
            opcao.remove();
        }
    }

    const existente = Array.from(select.options).find(
        opcao =>
            opcao.value &&
            opcao.textContent
                .trim()
                .toLocaleLowerCase('pt-BR') ===
            nome.toLocaleLowerCase('pt-BR')
    );

    if (existente) {
        select.value = existente.value;

    } else {
        const opcao = new Option(
            nome + ' (cadastrar ao salvar)',
            'novo:' + nome
        );

        select.add(opcao);
        select.value = opcao.value;
    }

    return true;
}

async function buscarIsbn() {
    const form = document.querySelector('#form');

    try {
        const dados = await api(
            'isbn/' + encodeURIComponent(
                form.elements.isbn.value
            )
        );

        form.elements.titulo.value = dados.title || '';

        const faltantes = [];

        for (const [campo, rotulo] of [
            ['autor', 'autor'],
            ['editora', 'editora'],
            ['categoria', 'gênero']
        ]) {
            if (!selecionarSugestao(form.elements[campo], dados[campo])) {
                faltantes.push(rotulo);
            }
        }

        if (faltantes.length) {
            alert(
                'Título encontrado. A API não informou: ' +
                faltantes.join(', ') +
                '. Selecione entre os cadastrados.'
            );
        } else {
            alert(
                'Dados encontrados! Confira as sugestões antes de salvar.'
            );
        }

    } catch (erro) {
        alert(erro.message);
    }
} // Linha 849 COMMIT 4 Vivian gomes