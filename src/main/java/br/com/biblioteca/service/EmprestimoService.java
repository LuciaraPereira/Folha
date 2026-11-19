
import java.time.LocalDate;
import java.util.List;

@Service
public class EmprestimoService {

    private final EmprestimoRepository emprestimos;
    private final LivroRepository livros;
    private final LeitorRepository leitores;

    public EmprestimoService(
            EmprestimoRepository emprestimos,
            LivroRepository livros,
            LeitorRepository leitores
    ) {
        this.emprestimos = emprestimos;
        this.livros = livros;
        this.leitores = leitores;
    }

    public List<Emprestimo> listar() {
        return emprestimos.findAll();
    }

    @Transactional
    public Emprestimo criar(Emprestimo emprestimo) {
        if (emprestimo.livro == null
                || emprestimo.livro.id == null
                || emprestimo.leitor == null
                || emprestimo.leitor.id == null
                || emprestimo.dataPrevista == null) {

            throw erro(
                    "Informe livro, leitor e previsão de devolução"
            );
        }

        Livro livro = livros.findById(emprestimo.livro.id)
                .orElseThrow(() -> erro("Livro não encontrado"));

        Leitor leitor = leitores.findById(emprestimo.leitor.id)
                .orElseThrow(() -> erro("Leitor não encontrado"));

        if (emprestimo.dataPrevista.isBefore(LocalDate.now())) {
            throw erro("A previsão não pode estar no passado");
        }

        long emprestimosAtivos =
                emprestimos.countByLivroIdAndDataDevolucaoIsNull(
                        livro.id
                );

        if (emprestimosAtivos >= livro.exemplares) {
            throw erro("Não há exemplares disponíveis");
        }

        emprestimo.id = null;
        emprestimo.livro = livro;
        emprestimo.leitor = leitor;
        emprestimo.dataEmprestimo = LocalDate.now();
        emprestimo.dataDevolucao = null;
        emprestimo.dataUltimoPagamento = null;
        emprestimo.multaPaga = false;

        return emprestimos.save(emprestimo);
    }

    @Transactional
    public Emprestimo devolver(Long id) {
        Emprestimo emprestimo = emprestimos.findById(id)
                .orElseThrow(
                        () -> erro("Empréstimo não encontrado")
                );

        if (emprestimo.dataDevolucao != null) {
            throw erro("Empréstimo já devolvido");
        }

        emprestimo.dataDevolucao = LocalDate.now();

        return emprestimos.save(emprestimo);
    }