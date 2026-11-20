let csrf = '';
let sessao = {};
let current = 'inicio';
let cache = {};

const configs = {
    autores: {
        titulo: 'Autores',
        singular: 'Autor',
        fields: [
            ['nome', 'Nome']
        ]
    },

    categorias: {
        titulo: 'Categorias',
        singular: 'Categoria',
        fields: [
            ['nome', 'Nome']
        ]
    },

    editoras: {
        titulo: 'Editoras',
        singular: 'Editora',
        fields: [
            ['nome', 'Nome']
        ]
    },

    leitores: {
        titulo: 'Leitores',
        singular: 'Leitor',
        fields: [
            ['nome', 'Nome'],
            ['cpf', 'CPF'],
            ['email', 'E-mail', 'email'],
            ['telefone', 'Telefone']
        ]
    },
























 usuarios: {
        titulo: 'Equipe',
        singular: 'Bibliotecário',
        fields: [
            ['nome', 'Nome'],
            ['login', 'Usuário'],
            ['senha', 'Senha', 'password']
        ]
    }
};

// Evita inserir texto de usuários como código HTML.
const esc = valor => String(valor ?? '').replace(
    /[&<>"']/g,
    caractere => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    }[caractere])
);

async function api(path, options = {}) {
    const resposta = await fetch('/api/' + path, {
        ...options,
        headers: {
            'Content-Type': 'application/json',
            'X-XSRF-TOKEN': csrf,
            ...options.headers
        }
    });

    if (!resposta.ok) {
        const dados = await resposta.json().catch(() => ({}));

        throw Error(
            dados.message ||
            'Não foi possível concluir. Verifique os dados e os vínculos deste registro.'
        );
    }

    if (
        resposta.status === 204 ||
        resposta.headers.get('content-length') === '0'
    ) {
        return null;
    }

    const texto = await resposta.text();

    return texto ? JSON.parse(texto) : null;
}

function notice(texto, erro = false) {
    const elemento = document.querySelector('#message');

    elemento.innerHTML = `
        <div class="notice ${erro ? 'error' : ''}">
            ${esc(texto)}
        </div>
    `;

    setTimeout(() => {
        elemento.innerHTML = '';
    }, 6000);
}

async function start() {
    const token = await fetch('/api/csrf')
        .then(resposta => resposta.json());

    csrf = token.token;
    sessao = await api('sessao');

    document.querySelector('#user').textContent =
        '◉  ' +
        sessao.login +
        (sessao.admin ? ' · Administrador' : ' · Bibliotecário');

    drawNav();
    await show('inicio');
}

function drawNav() {
    const itens = [
        ['inicio', '◫', 'Visão geral'],
        ['livros', '▤', 'Livros'],
        ['emprestimos', '⇄', 'Empréstimos'],
        ['multas', '◈', 'Multas'],
        ['leitores', '♙', 'Leitores'],
        ['autores', '✎', 'Autores'],
        ['categorias', '◇', 'Categorias'],
        ['editoras', '▧', 'Editoras']
    ];

    if (sessao.admin) {
        itens.push(['usuarios', '♧', 'Equipe']);
    }

    document.querySelector('#nav').innerHTML = itens.map(
        ([id, icone, nome]) => `
            <button
                class="${id === current ? 'active' : ''}"
                onclick="show('${id}')"
            >
                ${icone} &nbsp; ${nome}
            </button>
        `
    ).join('');
}

async function show(page) {
    current = page;
    drawNav();

    document.querySelector('#title').textContent =
        page === 'inicio'
            ? 'Visão geral'
            : page === 'multas'
                ? 'Multas'
                : configs[page].titulo;

    document.querySelector('#subtitle').textContent =
        page === 'inicio'
            ? 'Um bom dia começa com uma boa história.'
            : 'Tudo o que você precisa, em um só lugar.';

    try {
        if (page === 'inicio') {
            return await dashboard();
        }

        if (page === 'multas') {
            return await multas();
        }

        await list(page);

    } catch (erro) {
        notice(erro.message, true);
    }
}

async function dashboard() {
    const [livros, leitores, emprestimos] = await Promise.all(
        ['livros', 'leitores', 'emprestimos'].map(
            endereco => api(endereco)
        )
    );

    const ativos = emprestimos.filter(
        emprestimo => !emprestimo.dataDevolucao
    );

    document.querySelector('#content').innerHTML = `
        <div class="hero">
            <div class="eyebrow">BEM-VINDO AO FOLHA</div>
            <h2>Um lugar para boas histórias.</h2>
            <p>
                Organize seu acervo, acompanhe empréstimos e mantenha
                sua biblioteca sempre em movimento.
            </p>
        </div>

        <div class="stats">
            <div class="stat">
                <span>▤ &nbsp; Títulos no acervo</span>
                <strong>${livros.length}</strong>
            </div>

            <div class="stat">
                <span>♙ &nbsp; Leitores cadastrados</span>
                <strong>${leitores.length}</strong>
            </div>

            <div class="stat">
                <span>⇄ &nbsp; Empréstimos ativos</span>
                <strong>${ativos.length}</strong>
            </div>
        </div>

        <div class="panel">
            <div class="panel-head">
                <div>
                    <h2>Em movimento</h2>
                    <p>Empréstimos ainda não devolvidos.</p>
                </div>

                <button
                    class="primary"
                    onclick="show('emprestimos')"
                >
                    Ver empréstimos →
                </button>
            </div>

            ${loanTable(ativos)}
        </div>
    `;
}

const dinheiro = valor => Number(valor || 0).toLocaleString(
    'pt-BR',
    {
        style: 'currency',
        currency: 'BRL'
    }
);

function filtrarTabela(input) {
    const termo = input.value
        .trim()
        .toLocaleLowerCase('pt-BR');

    document.querySelectorAll('#content tbody tr')
        .forEach(linha => {
            linha.hidden = !linha.textContent
                .toLocaleLowerCase('pt-BR')
                .includes(termo);
        });
}

function campoBusca(texto) {
    return `
        <input
            type="search"
            placeholder="${texto}"
            aria-label="${texto}"
            oninput="filtrarTabela(this)"
            style="margin:0 0 20px;max-width:380px"
        >
    `;
}
















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






























































































































































async function list(page) {
    const data = cache[page] = await api(page);
    const config = configs[page];

    let tabela;

    if (page === 'emprestimos') {
        tabela = loanTable(data);

    } else {
        let colunas = config.fields.filter(
            campo => campo[0] !== 'senha'
        );

        if (page === 'usuarios') {
            colunas = [...colunas, ['perfil', 'Perfil']];
        }

        tabela = data.length
            ? `
                <div class="table-wrap">
                    <table>
                        <thead>
                            <tr>
                                ${colunas.map(campo => `
                                    <th>
                                        ${esc(campo[1].toUpperCase())}
                                    </th>
                                `).join('')}

                                <th>AÇÕES</th>
                            </tr>
                        </thead>

                        <tbody>
                            ${data.map(item => `
                                <tr>
                                    ${colunas.map(campo => `
                                        <td>
                                            ${esc(
                                                typeof item[campo[0]] === 'object'
                                                    ? (
                                                        item[campo[0]]?.nome ||
                                                        item[campo[0]]?.titulo
                                                    )
                                                    : item[campo[0]]
                                            )}
                                        </td>
                                    `).join('')}

                                    <td>
                                        ${
                                            page !== 'usuarios' || item.perfil === 'BIBLIOTECARIO'
                                                ? `
                                                    <button
                                                        onclick="openForm('${page}', ${item.id})"
                                                    >
                                                        Editar
                                                    </button>

                                                    <button
                                                        onclick="removeItem('${page}', ${item.id})"
                                                    >
                                                        Excluir
                                                    </button>
                                                `
                                                : '—'
                                        }
                                    </td>
                                </tr>
                            `).join('')}
                        </tbody>
                    </table>
                </div>
            `
            : `
                <div class="empty">
                    Ainda não há registros por aqui.
                </div>
            `;
    }

    const mostrarBusca = [
        'livros',
        'emprestimos',
        'leitores'
    ].includes(page);

    const textoBusca = page === 'livros'
        ? 'Pesquisar livro pelo título'
        : 'Pesquisar pelo nome ou CPF do leitor';

    document.querySelector('#content').innerHTML = `
        <div class="panel">
            <div class="panel-head">
                <div>
                    <h2>${config.titulo}</h2>
                    <p>${data.length} registro(s) encontrado(s).</p>
                </div>

                <button
                    class="primary"
                    onclick="openForm('${page}')"
                >
                    + &nbsp; Novo ${config.singular.toLowerCase()}
                </button>
            </div>

            ${mostrarBusca ? campoBusca(textoBusca) : ''}

            ${tabela}
        </div>
    `;
}

async function openForm(page, id) {
    const config = configs[page];

    const item = (cache[page] || [])
        .find(registro => registro.id === id) || {};

    const referencias = {};

    for (const campo of config.fields) {
        if (campo[2] === 'select') {
            referencias[campo[3]] = await api(campo[3]);
        }
    }

    document.querySelector('#modal-title').textContent =
        (id ? 'Editar ' : 'Novo ') +
        config.singular.toLowerCase();

    const form = document.querySelector('#form');

    form.innerHTML = config.fields.map(
        ([chave, label, tipo, referencia]) => {
            let input;

            if (tipo === 'select') {
                input = `
                    <select name="${chave}" required>
                        <option value="">Selecione</option>

                        ${referencias[referencia].map(registro => `
                            <option
                                value="${registro.id}"
                                ${
                                    item[chave]?.id === registro.id
                                        ? 'selected'
                                        : ''
                                }
                            >
                                ${esc(
                                    (registro.nome || registro.titulo) +
                                    (
                                        referencia === 'leitores'
                                            ? ' · CPF: ' +
                                                (
                                                    registro.cpf ||
                                                    'não informado'
                                                )
                                            : ''
                                    )
                                )}
                            </option>
                        `).join('')}
                    </select>
                `;

            } else {
                input = `
                    <input
                        name="${chave}"
                        type="${tipo || 'text'}"
                        value="${esc(
                            item[chave] ??
                            (chave === 'exemplares' ? 1 : '')
                        )}"
                        ${tipo === 'number' ? 'min="1"' : ''}
                        ${page === 'usuarios' && id && chave === 'senha' ? '' : 'required'}
                    >
                `;
            }



















           return `
                <label>
                    ${label}
                    ${input}
                </label>
            `;
        }
    ).join('') + `
        <button type="submit">
            Salvar registro →
        </button>
    `;

    form.onsubmit = async evento => {
        evento.preventDefault();

        const body = {};

        try {
            for (
                const [chave, label, tipo, referencia]
                of config.fields
            ) {
                const valor = form.elements[chave].value;




























        else if (tipo === 'select') {
                            body[chave] = { id: Number(valor) };

                        } else if (tipo === 'number') {
                            body[chave] = Number(valor);

                        } else {
                            body[chave] = valor;
                        }
                    }

                    await api(
                        page + (id ? '/' + id : ''),
                        {
                            method: id ? 'PUT' : 'POST',
                            body: JSON.stringify(body)
                        }
                    );

                    document.querySelector('#modal').close();

                    notice('Registro salvo com sucesso.');
                    await show(page);

                } catch (erro) {
                    alert(erro.message);
                }
            };

            document.querySelector('#modal').showModal();
        }
















































































async function removeItem(page, id) {
    if (!confirm('Excluir este registro?')) {
        return;
    }

    try {
        await api(page + '/' + id, {
            method: 'DELETE'
        });

        notice('Registro excluído.');
        await show(page);

    } catch (erro) {
        notice(erro.message, true);
    }
}


























































































document.querySelector('#logout').onclick = async () => {
    const form = document.createElement('form');

    form.method = 'POST';
    form.action = '/logout';

    form.innerHTML = `
        <input
            type="hidden"
            name="_csrf"
            value="${esc(csrf)}"
        >
    `;

    document.body.appendChild(form);
    form.submit();
};

start().catch(erro => notice(erro.message, true));















































































































































































































































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