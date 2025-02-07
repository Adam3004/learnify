package com.brightpath.learnify.config;

import com.brightpath.learnify.domain.auth.permission.PermissionLevel;
import com.brightpath.learnify.domain.note.Note;
import com.brightpath.learnify.domain.note.NoteService;
import com.brightpath.learnify.domain.note.NoteType;
import com.brightpath.learnify.domain.quiz.Quiz;
import com.brightpath.learnify.domain.quiz.QuizService;
import com.brightpath.learnify.domain.quiz.question.Question;
import com.brightpath.learnify.domain.quiz.question.QuestionService;
import com.brightpath.learnify.domain.quiz.question.QuestionType;
import com.brightpath.learnify.domain.user.User;
import com.brightpath.learnify.domain.user.UserService;
import com.brightpath.learnify.domain.workspace.Workspace;
import com.brightpath.learnify.domain.workspace.WorkspaceService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import java.util.List;
import java.util.UUID;

import static com.brightpath.learnify.domain.auth.permission.PermissionLevel.PRIVATE;

@Configuration
@Profile("dev")
public class DevDatabaseConfigurator {
    private static final PermissionLevel DEFAULT_PERMISSION_LEVEL = PRIVATE;

    private final UserService userService;
    private final String sampleUserId;

    public DevDatabaseConfigurator(
            @Value("${SAMPLE_USER_ID:}") String sampleUserId,
            UserService userService
    ) {
        this.userService = userService;
        this.sampleUserId = sampleUserId;
    }

    private String parseToString(List<String> strings) {
        return String.join("\u001F", strings);
    }

    @Bean
    @Order(2)
    CommandLineRunner commandLineRunner(
            WorkspaceService workspaceService,
            NoteService noteService,
            QuizService quizService,
            QuestionService questionService
    ) {
        return args -> {
            User user;
            if (!sampleUserId.isEmpty()) {
                user = userService.getUserById(sampleUserId);
            } else {
                user = userService.createUser("sampleid", "testuser@gmail.com", "Test User");
            }
            Workspace workspace1 = workspaceService.createWorkspace("Semestr 1", user.id(), DEFAULT_PERMISSION_LEVEL, null);
            workspaceService.createWorkspace("Semestr 2", user.id(), DEFAULT_PERMISSION_LEVEL, null);
            workspaceService.createWorkspace("Semestr 3", user.id(), DEFAULT_PERMISSION_LEVEL, null);
            workspaceService.createWorkspace("Semestr 4", user.id(), DEFAULT_PERMISSION_LEVEL, null);
            workspaceService.createWorkspace("Semestr 5", user.id(), DEFAULT_PERMISSION_LEVEL, null);
            Workspace workspace6 = workspaceService.createWorkspace("Semestr 6", user.id(), DEFAULT_PERMISSION_LEVEL, null);
            Workspace systemyRozproszone = workspaceService.createWorkspace("Systemy rozproszone", user.id(), DEFAULT_PERMISSION_LEVEL, workspace6.id());
            noteService.createNote(
                    "Systemy rozproszone",
                    "Notatki z wykładów i zajęć",
                    systemyRozproszone.id(),
                    user.id(),
                    NoteType.BOARD,
                    DEFAULT_PERMISSION_LEVEL);
            Quiz quiz = quizService.createQuiz(
                    "Systemy rozproszone",
                    "Pytania teoretyczne z wykładów i zajęć",
                    systemyRozproszone.id(),
                    user.id(),
                    DEFAULT_PERMISSION_LEVEL
            ).get();
            Note note = noteService.createNote(
                    "Systemy rozproszone - lab 2",
                    "Notatki z laboratorium 2",
                    workspace6.id(),
                    user.id(),
                    NoteType.DOCUMENT,
                    DEFAULT_PERMISSION_LEVEL);
            noteService.updateDocumentNoteContentPage(note.id(), sampleUserId, 1, "{\"root\":{\"children\":[{\"children\":[],\"direction\":null,\"format\":\"\",\"indent\":0,\"type\":\"paragraph\",\"version\":1},{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"Systemy rozproszone lab 2\",\"type\":\"text\",\"version\":1}],\"direction\":\"ltr\",\"format\":\"center\",\"indent\":0,\"type\":\"heading\",\"version\":1,\"tag\":\"h1\"},{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"TCP/UDP\",\"type\":\"text\",\"version\":1}],\"direction\":\"ltr\",\"format\":\"start\",\"indent\":0,\"type\":\"heading\",\"version\":1,\"tag\":\"h1\"},{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"    TCP to protokół\",\"type\":\"text\",\"version\":1}],\"direction\":\"ltr\",\"format\":\"start\",\"indent\":0,\"type\":\"heading\",\"version\":1,\"tag\":\"h2\"},{\"children\":[{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"Połączeniowy\",\"type\":\"text\",\"version\":1}],\"direction\":\"ltr\",\"format\":\"start\",\"indent\":0,\"type\":\"listitem\",\"version\":1,\"value\":1},{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"Punkt-punkt\",\"type\":\"text\",\"version\":1}],\"direction\":\"ltr\",\"format\":\"start\",\"indent\":0,\"type\":\"listitem\",\"version\":1,\"value\":2},{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"Niezawodny\",\"type\":\"text\",\"version\":1}],\"direction\":\"ltr\",\"format\":\"start\",\"indent\":0,\"type\":\"listitem\",\"version\":1,\"value\":3},{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"Kontrola przepływu\",\"type\":\"text\",\"version\":1}],\"direction\":\"ltr\",\"format\":\"start\",\"indent\":0,\"type\":\"listitem\",\"version\":1,\"value\":4},{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"Strumieniowy\",\"type\":\"text\",\"version\":1}],\"direction\":\"ltr\",\"format\":\"start\",\"indent\":0,\"type\":\"listitem\",\"version\":1,\"value\":5},{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"Relatywne wolny (duży narzut)\",\"type\":\"text\",\"version\":1}],\"direction\":\"ltr\",\"format\":\"start\",\"indent\":0,\"type\":\"listitem\",\"version\":1,\"value\":6}],\"direction\":\"ltr\",\"format\":\"\",\"indent\":0,\"type\":\"list\",\"version\":1,\"listType\":\"bullet\",\"start\":1,\"tag\":\"ul\"},{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"    Sposób działania\",\"type\":\"text\",\"version\":1}],\"direction\":\"ltr\",\"format\":\"start\",\"indent\":0,\"type\":\"heading\",\"version\":1,\"tag\":\"h2\"},{\"children\":[{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"Nawiązanie połączenia (three-way-handshake)\",\"type\":\"text\",\"version\":1}],\"direction\":\"ltr\",\"format\":\"start\",\"indent\":0,\"type\":\"listitem\",\"version\":1,\"value\":1},{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"Transmisja Dane, które mają zostać przesłane są dzielone na segmenty. Wykorzystuje się potwierdzenia odbioru.\",\"type\":\"text\",\"version\":1}],\"direction\":\"ltr\",\"format\":\"start\",\"indent\":0,\"type\":\"listitem\",\"version\":1,\"value\":2},{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"Zamknięcie połączenia\",\"type\":\"text\",\"version\":1}],\"direction\":\"ltr\",\"format\":\"start\",\"indent\":0,\"type\":\"listitem\",\"version\":1,\"value\":3}],\"direction\":\"ltr\",\"format\":\"\",\"indent\":0,\"type\":\"list\",\"version\":1,\"listType\":\"number\",\"start\":1,\"tag\":\"ol\"},{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"     Zastosowania TCP\",\"type\":\"text\",\"version\":1}],\"direction\":\"ltr\",\"format\":\"start\",\"indent\":0,\"type\":\"heading\",\"version\":1,\"tag\":\"h2\"},{\"children\":[],\"direction\":\"ltr\",\"format\":\"\",\"indent\":0,\"type\":\"paragraph\",\"version\":1},{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"TCP jest stosowany w aplikacjach, które wymagają niezawodnej i uporządkowanej transmisji danych, takich jak komunikacja przez SSH, transfer danych przez FTP, przeglądarki internetowe - HTTP, protokoły pocztowe – SMTP.\",\"type\":\"text\",\"version\":1}],\"direction\":\"ltr\",\"format\":\"justify\",\"indent\":0,\"type\":\"paragraph\",\"version\":1},{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"     Serwer TCP (Java)\",\"type\":\"text\",\"version\":1}],\"direction\":\"ltr\",\"format\":\"\",\"indent\":0,\"type\":\"heading\",\"version\":1,\"tag\":\"h2\"},{\"children\":[{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"ServerSocket\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"class-name\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" serverSocket \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"=\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"operator\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"null\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"keyword\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\";\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"type\":\"linebreak\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"try\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"keyword\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"{\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"type\":\"linebreak\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"serverSocket \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"=\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"operator\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"new\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"keyword\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"ServerSocket\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"class-name\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"(\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"12345\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"number\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\")\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\";\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"type\":\"linebreak\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"while\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"keyword\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"(\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"true\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"boolean\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\")\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"{\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"type\":\"linebreak\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"Socket\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"class-name\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" clientSocket \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"=\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"operator\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" serverSocket\",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\".\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"accept\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"function\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"(\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\")\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\";\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"type\":\"linebreak\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"PrintWriter\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"class-name\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" out \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"=\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"operator\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"new\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"keyword\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"PrintWriter\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"class-name\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"(\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"clientSocket\",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\".\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"getOutputStream\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"function\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"(\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\")\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\",\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"true\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"boolean\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\")\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\";\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"type\":\"linebreak\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"BufferedReader\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"class-name\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" in \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"=\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"operator\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"new\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"keyword\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"BufferedReader\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"class-name\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"(\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"new\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"keyword\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"InputStreamReader\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"class-name\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"(\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"clientSocket\",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\".\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"getInputStream\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"function\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"(\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\")\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\")\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\")\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\";\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"type\":\"linebreak\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"String\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"class-name\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" msg \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"=\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"operator\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" in\",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\".\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"readLine\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"function\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"(\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\")\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\";\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"type\":\"linebreak\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"System\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"class-name\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\".\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"out\",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\".\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"println\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"function\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"(\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\\"received msg: \\\"\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"string\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"+\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"operator\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" msg\",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\")\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\";\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"type\":\"linebreak\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"out\",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\".\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"println\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"function\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"(\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\\"Pong\\\"\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"string\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\")\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\";\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"type\":\"linebreak\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"clientSocket\",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\".\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"close\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"function\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"(\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\")\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\";\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"type\":\"linebreak\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"}\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"type\":\"linebreak\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"}\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"catch\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"keyword\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"(\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"IOException\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"class-name\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" e\",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\")\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"{\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"type\":\"linebreak\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"e\",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\".\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"printStackTrace\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"function\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"(\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\")\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\";\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"type\":\"linebreak\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"}\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"finally\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"keyword\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"{\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"type\":\"linebreak\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"if\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"keyword\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"(\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"serverSocket \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"!=\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"operator\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"null\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"keyword\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\")\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"type\":\"linebreak\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":2,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"\\t\",\"type\":\"tab\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"serverSocket\",\"type\":\"code-highlight\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\".\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"close\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"function\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"(\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\")\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\";\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\" \",\"type\":\"code-highlight\",\"version\":1},{\"type\":\"linebreak\",\"version\":1},{\"detail\":0,\"format\":0,\"mode\":\"normal\",\"style\":\"\",\"text\":\"}\",\"type\":\"code-highlight\",\"version\":1,\"highlightType\":\"punctuation\"}],\"direction\":\"ltr\",\"format\":\"\",\"indent\":0,\"type\":\"code\",\"version\":1,\"language\":\"java\"}],\"direction\":\"ltr\",\"format\":\"\",\"indent\":0,\"type\":\"root\",\"version\":1}}", 1);
            questionService.createQuestions(
                    quiz.id(),
                    List.of(
                            new Question(
                                    UUID.randomUUID(),
                                    "Co oznacza pojęcie 'przejrzystość lokalizacji' w systemach rozproszonych?",
                                    QuestionType.SINGLE_CHOICE,
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
                                    UUID.randomUUID(),
                                    "Które z poniższych stwierdzeń jest prawdziwe w kontekście gRPC?",
                                    QuestionType.SINGLE_CHOICE,
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
                                    UUID.randomUUID(),
                                    "Jakie są korzyści z używania Apache Thrift?",
                                    QuestionType.MULTIPLE_CHOICE,
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
                                    UUID.randomUUID(),
                                    "Co to jest replikacja w systemach rozproszonych?",
                                    QuestionType.SINGLE_CHOICE,
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
                                    UUID.randomUUID(),
                                    "Co oznacza pojęcie 'fault tolerance' w kontekście systemów rozproszonych?",
                                    QuestionType.SINGLE_CHOICE,
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
