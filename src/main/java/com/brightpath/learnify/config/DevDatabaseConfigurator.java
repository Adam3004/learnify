package com.brightpath.learnify.config;

import com.brightpath.learnify.domain.auth.AuthorizationService;
import com.brightpath.learnify.domain.note.NoteService;
import com.brightpath.learnify.domain.quiz.question.Question;
import com.brightpath.learnify.domain.quiz.question.QuestionService;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.domain.quiz.QuizService;
import com.brightpath.learnify.domain.workspace.WorkspaceService;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.workspace.Workspace;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DevDatabaseConfigurator {

    private String parseToString(List<String> strings) {
        return String.join("\u001F", strings);
    }

    @Bean
    CommandLineRunner commandLineRunner(
            WorkspaceService workspaceService,
            NoteService noteService,
            AuthorizationService authorizationService,
            QuizService quizService,
            QuestionService questionService
    ) {
        return args -> {
            User user = authorizationService.defaultUser();
            Workspace workspace1 = workspaceService.createWorkspace("Semestr 1");
            workspaceService.createWorkspace("Semestr 2");
            workspaceService.createWorkspace("Semestr 3");
            workspaceService.createWorkspace("Semestr 4");
            workspaceService.createWorkspace("Semestr 5");
            Workspace workspace6 = workspaceService.createWorkspace("Semestr 6");
            noteService.createNote(
                    "Systemy rozproszone",
                    "Notatki z wykładów i zajęć",
                    workspace6.id(),
                    user.id()
            );
            Quiz quiz = quizService.createQuiz(
                    "Systemy rozproszone",
                    "Pytania teoretyczne z wykładów i zajęć",
                    workspace6.id(),
                    user.id()
            ).get();
            questionService.createQuestions(
                    quiz.id(),
                    List.of(
                            new Question(
                                    null,
                                    "Co oznacza pojęcie 'przejrzystość lokalizacji' w systemach rozproszonych?",
                                    Question.QuestionType.SINGLE_CHOICE,
                                    quiz.id(),
                                    1,
                                    parseToString(
                                            List.of(
                                                    "Umożliwia administratorom zarządzanie fizyczną lokalizacją zasobów",
                                                    "Umożliwia użytkownikom dostęp do zasobów bez znajomości ich fizycznej lokalizacji",
                                                    "Poprawia wydajność systemu poprzez lokalizowanie danych blisko użytkowników",
                                                    "Pojawienie się redundantnych danych w różnych lokalizacjach"
                                            )
                                    ),
                                    parseToString(
                                            List.of(
                                                    "Przejrzystość lokalizacji odnosi się do użytkowników, a nie administratorów.",
                                                    "Tak",
                                                    "Poprawa wydajności poprzez lokalizowanie danych blisko użytkowników to kwestia optymalizacji, a nie przejrzystości lokalizacji.",
                                                    "Redundancja danych jest związana z replikacją i wysoką dostępnością, a nie przejrzystością lokalizacji."
                                            )
                                    ),
                                    "1"
                            ),
                            new Question(
                                    null,
                                    "Które z poniższych stwierdzeń jest prawdziwe w kontekście gRPC?",
                                    Question.QuestionType.SINGLE_CHOICE,
                                    quiz.id(),
                                    4,
                                    parseToString(
                                            List.of(
                                                    "Używa protokołu HTTP/1.1 do komunikacji",
                                                    "Umożliwia jedynie komunikację synchroniczną",
                                                    "Używa Protocol Buffers (ProtoBuf) jako domyślnego formatu serializacji",
                                                    "Nie wspiera komunikacji między różnymi językami programowania"
                                            )
                                    ),
                                    parseToString(
                                            List.of(
                                                    "gRPC używa protokołu HTTP/2, co pozwala na bardziej efektywną komunikację.",
                                                    "gRPC wspiera zarówno komunikację synchroniczną, jak i asynchroniczną.",
                                                    "Tak",
                                                    "gRPC jest zaprojektowane do wspierania komunikacji między różnymi językami programowania"
                                            )
                                    ),
                                    "2"
                            ),
                            new Question(
                                    null,
                                    "Jakie są korzyści z używania Apache Thrift?",
                                    Question.QuestionType.MULTIPLE_CHOICE,
                                    quiz.id(),
                                    3,
                                    parseToString(
                                            List.of(
                                                    "Wsparcie dla wielu języków programowania",
                                                    "Skomplikowana konfiguracja",
                                                    "Wysoka wydajność",
                                                    "Zgodność z protokołem HTTP",
                                                    "Generowanie kodu dla interfejsów"
                                            )
                                    ),
                                    parseToString(
                                            List.of(
                                                    "Tak",
                                                    "Apache Thrift jest znany z prostoty konfiguracji i łatwości użycia.",
                                                    "Tak",
                                                    "Apache Thrift nie jest specyficznie zaprojektowany do pracy z protokołem HTTP, chociaż może być używany z różnymi protokołami transportowymi.",
                                                    "Tak"
                                            )
                                    ),
                                    parseToString(List.of("true", "false", "true", "false", "true"))
                            ),
                            new Question(
                                    null,
                                    "Co to jest replikacja w systemach rozproszonych?",
                                    Question.QuestionType.SINGLE_CHOICE,
                                    quiz.id(),
                                    6,
                                    parseToString(
                                            List.of(
                                                    "Proces podziału danych na mniejsze części",
                                                    "Tworzenie wielu kopii danych w różnych miejscach",
                                                    "Technika łączenia różnych sieci",
                                                    "Metoda kompresji danych"
                                            )
                                    ),
                                    parseToString(
                                            List.of(
                                                    "Podział danych na mniejsze części to fragmentacja.",
                                                    "Tak",
                                                    "Łączenie różnych sieci to inter-networking.",
                                                    "Kompresja danych zmniejsza ich rozmiar, ale nie jest związana z replikacją."
                                            )
                                    ),
                                    "1"
                            ),
                            new Question(
                                    null,
                                    "Co oznacza pojęcie 'fault tolerance' w kontekście systemów rozproszonych?",
                                    Question.QuestionType.SINGLE_CHOICE,
                                    quiz.id(),
                                    8,
                                    parseToString(
                                            List.of(
                                                    "Zdolność systemu do automatycznego naprawiania błędów",
                                                    "Zdolność systemu do działania mimo wystąpienia błędów",
                                                    "Zdolność systemu do wykrywania błędów",
                                                    "Zdolność systemu do unikania błędów"
                                            )
                                    ),
                                    parseToString(
                                            List.of(
                                                    "Automatyczne naprawianie błędów to samonaprawa, nie fault tolerance.",
                                                    "Tak",
                                                    "Wykrywanie błędów to tylko część fault tolerance.",
                                                    "Unikanie błędów to zapobieganie błędom, nie fault tolerance."
                                            )
                                    ),
                                    "1"
                            )
                    )
            );
            System.out.println("Database initialized");
        };
    }
}
