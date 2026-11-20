
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

    @Transactional
    public Emprestimo pagarMulta(Long id) {
        Emprestimo emprestimo = emprestimos.findById(id)
                .orElseThrow(
                        () -> erro("Empréstimo não encontrado")
                );

        if (emprestimo.getValorMulta().signum() <= 0) {
            throw erro(
                    "Este empréstimo não possui multa pendente"
            );
        }

        // Se o livro já foi devolvido, a multa termina na data
        // da devolução. Caso contrário, termina no dia de hoje.
        emprestimo.dataUltimoPagamento =
                emprestimo.dataDevolucao == null
                        ? LocalDate.now()
                        : emprestimo.dataDevolucao;

        emprestimo.multaPaga = true;

        return emprestimos.save(emprestimo);
    }

    @Transactional
    public Emprestimo renovar(Long id, LocalDate novaData) {
        Emprestimo emprestimo = emprestimos.findById(id)
                .orElseThrow(
                        () -> erro("Empréstimo não encontrado")
                );

        if (emprestimo.dataDevolucao != null) {
            throw erro(
                    "Não é possível renovar um empréstimo devolvido"
            );
        }

        if (emprestimo.getValorMulta().signum() > 0) {
            throw erro("Pague a multa pendente antes de renovar");
        }

        if (novaData == null
                || !novaData.isAfter(LocalDate.now())) {
            throw erro(
                    "A nova data deve ser posterior a hoje"
            );
        }

        if (!novaData.isAfter(emprestimo.dataPrevista)) {
            throw erro(
                    "A nova data deve ser posterior ao prazo atual"
            );
        }

        emprestimo.dataPrevista = novaData;
        emprestimo.dataUltimoPagamento = null;
        emprestimo.multaPaga = false;

        return emprestimos.save(emprestimo);
    }

    private ResponseStatusException erro(String mensagem) {
        return new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                mensagem
        );
    }
}