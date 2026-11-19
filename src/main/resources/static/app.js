








































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

































































































































const campoQuantidade = configs.livros.fields.find( // obs: app.js linhas 979-1114 ao final do arquivo COMMIT 5 Vivian gomes
    campo => campo[0] === 'exemplares'
);

if (campoQuantidade) {
    campoQuantidade[1] = 'Quantidade total';
}


filtrarTabela = function (input) {
    const termo = input.value
        .trim()
        .toLocaleLowerCase('pt-BR');

    const termoSemPontuacao = termo.replace(/[.\s-]/g, '');

    const buscaNumerica =
        termoSemPontuacao.length > 0 &&
        /^[0-9x]+$/i.test(termoSemPontuacao);

    document.querySelectorAll('#content tbody tr')
        .forEach(linha => {
            const texto = linha.textContent
                .toLocaleLowerCase('pt-BR');

            const textoSemPontuacao =
                texto.replace(/[.\s-]/g, '');

            const encontrouTexto = texto.includes(termo);

            const encontrouNumero =
                buscaNumerica &&
                textoSemPontuacao.includes(termoSemPontuacao);

            linha.hidden = !(encontrouTexto || encontrouNumero);
        });
};


const listarOriginal = list;


list = async function (page) {
    await listarOriginal(page);

    if (page !== 'livros') {
        return;
    }

    const campoPesquisa = document.querySelector(
        '#content input[type="search"]'
    );

    if (campoPesquisa) {
        campoPesquisa.placeholder =
            'Pesquisar livro pelo título ou ISBN';

        campoPesquisa.setAttribute(
            'aria-label',
            'Pesquisar livro pelo título ou ISBN'
        );
    }

    const tabela = document.querySelector('#content table');

    if (!tabela) {
        return;
    }

    const emprestimos = await api('emprestimos');
    const livros = cache.livros || [];

    // A posição da quantidade segue a ordem dos campos de livros.
    const indiceQuantidade = configs.livros.fields.findIndex(
        campo => campo[0] === 'exemplares'
    );

    if (indiceQuantidade < 0) {
        return;
    }

    const cabecalhos = tabela.querySelectorAll('thead th');

    if (cabecalhos[indiceQuantidade]) {
        cabecalhos[indiceQuantidade].textContent =
            'QUANTIDADE DISPONÍVEL';
    }

    
    const cabecalhoStatus = document.createElement('th');
    cabecalhoStatus.textContent = 'STATUS';

    const cabecalhoAcoes = tabela.querySelector(
        'thead tr th:last-child'
    );

    cabecalhoAcoes.before(cabecalhoStatus);

    const linhas = tabela.querySelectorAll('tbody tr');

    linhas.forEach((linha, indice) => {
        const livro = livros[indice];

        if (!livro) {
            return;
        }

        const quantidadeEmprestada = emprestimos.filter(
            emprestimo =>
                !emprestimo.dataDevolucao &&
                emprestimo.livro?.id === livro.id
        ).length;

        const quantidadeDisponivel = Math.max(
            0,
            Number(livro.exemplares) - quantidadeEmprestada
        );

        const celulas = linha.querySelectorAll('td');

        if (celulas[indiceQuantidade]) {
            celulas[indiceQuantidade].textContent =
                quantidadeDisponivel;
        }

        const celulaStatus = document.createElement('td');

        celulaStatus.textContent = quantidadeDisponivel > 0
            ? 'Disponível'
            : 'Indisponível';

        const celulaAcoes = linha.querySelector('td:last-child');

        celulaAcoes.before(celulaStatus);
    });
};