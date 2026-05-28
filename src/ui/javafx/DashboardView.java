package ui.javafx;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import service.StudentService;
import service.TeacherService;
import service.SubjectService;
import service.EnrollmentService;

/**
 * Main dashboard window of the Monster High Institute Manager.
 *
 * Shows a sidebar for navigation and four summary cards
 * (students, teachers, subjects, enrollments) in the center.
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class DashboardView {

    /** The main JavaFX window. */
    private Stage stage;
    private StudentService    studentService    = new StudentService();
    private TeacherService    teacherService    = new TeacherService();
    private SubjectService    subjectService    = new SubjectService();
    private EnrollmentService enrollmentService = new EnrollmentService();

    /**
     * Creates the dashboard linked to the given stage.
     *
     * @param stage the primary stage
     */
    public DashboardView(Stage stage) {
        this.stage = stage;
    }

    /**
     * Builds and shows the dashboard scene.
     */
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle(MonsterHighStyles.SCENE_BG);

        // Left = sidebar, Center = main content
        root.setLeft(buildSidebar());
        root.setCenter(buildMain());

        Scene scene = new Scene(root, 1100, 700);
        stage.setTitle("Monster High Institute Manager");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Builds the left navigation sidebar.
     *
     * @return the VBox containing logo + nav buttons
     */
    private VBox buildSidebar() {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(220);
        sidebar.setPadding(new Insets(24, 12, 24, 12));
        sidebar.setStyle("-fx-background-color: #12122A;");

        Label skull    = new Label("💀");
        skull.setStyle("-fx-font-size: 40px;");

        Label brand    = new Label("Monster High");
        brand.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + MonsterHighStyles.PINK + ";");

        Label subtitle = new Label("Institute Manager");
        subtitle.setStyle(MonsterHighStyles.MUTED);

        VBox logoBox = new VBox(2, skull, brand, subtitle);
        logoBox.setAlignment(Pos.CENTER);
        logoBox.setPadding(new Insets(0, 0, 20, 0));

        // Thin pink divider line
        Region divider = new Region();
        divider.setStyle(MonsterHighStyles.DIVIDER);
        divider.setPrefHeight(2);

        // "true" = this button is the active/selected one
        Button btnDash     = navButton("🏠  Dashboard",    true);
        Button btnStudents = navButton("🧟  Students",     false);
        Button btnTeachers = navButton("🧙  Teachers",     false);
        Button btnSubjects = navButton("📚  Subjects",     false);
        Button btnEnroll   = navButton("📋  Enrollments",  false);

        // Each button opens its own view
        btnStudents.setOnAction(e -> new StudentView(stage, this).show());
        btnTeachers.setOnAction(e -> new TeacherView(stage, this).show());
        btnSubjects.setOnAction(e -> new SubjectView(stage, this).show());
        btnEnroll.setOnAction(e -> new EnrollmentView(stage, this).show());

        // Spacer pushes the version label to the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        Label version = new Label("v1.0 · IES Nervion 2025-26");
        version.setStyle(MonsterHighStyles.MUTED + " -fx-font-size: 10px;");

        sidebar.getChildren().addAll(
            logoBox, divider,
            btnDash, btnStudents, btnTeachers, btnSubjects, btnEnroll,
            spacer, version
        );
        return sidebar;
    }

    /**
     * Creates a sidebar navigation button.
     * Package-private so other views can reuse it to build their own sidebars.
     *
     * @param text   button label (may include emoji)
     * @param active whether this is the currently selected section
     * @return the configured Button
     */
    Button navButton(String text, boolean active) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle(active ? MonsterHighStyles.NAV_BTN_ACTIVE : MonsterHighStyles.NAV_BTN);
        return btn;
    }

    /**
     * Builds the central area with the title, KPI cards and quick-action buttons.
     *
     * @return the VBox with all the dashboard content
     */
    private VBox buildMain() {
        VBox main = new VBox(24);
        main.setPadding(new Insets(32));

        // Title
        Label title    = new Label("Institute Dashboard");
        title.setStyle(MonsterHighStyles.TITLE);

        Label subtitle = new Label("Welcome back! Here is today's overview.");
        subtitle.setStyle(MonsterHighStyles.MUTED);

        int totalStudents    = safeCount(() -> studentService.findAll().size());
        int totalTeachers    = safeCount(() -> teacherService.findAll().size());
        int totalSubjects    = safeCount(() -> subjectService.findAll().size());
        int totalEnrollments = safeCount(() -> enrollmentService.findAll().size());

        HBox cardRow = new HBox(16,
            kpiCard("🧟 Students",    String.valueOf(totalStudents),    MonsterHighStyles.PINK),
            kpiCard("🧙 Teachers",    String.valueOf(totalTeachers),    MonsterHighStyles.PURPLE),
            kpiCard("📚 Subjects",    String.valueOf(totalSubjects),    MonsterHighStyles.GREEN),
            kpiCard("📋 Enrollments", String.valueOf(totalEnrollments), "#FF9800")
        );
        cardRow.setAlignment(Pos.CENTER_LEFT);

        Label quickLabel = new Label("Quick Actions");
        quickLabel.setStyle(MonsterHighStyles.HEADING);

        Button btnAddStudent  = quickBtn("➕ Add Student",  MonsterHighStyles.PINK,
            () -> new StudentView(stage, this).showAddDialog());
        Button btnAddTeacher  = quickBtn("➕ Add Teacher",  MonsterHighStyles.PURPLE,
            () -> new TeacherView(stage, this).showAddDialog());
        Button btnAddSubject  = quickBtn("➕ Add Subject",  MonsterHighStyles.GREEN,
            () -> new SubjectView(stage, this).showAddDialog());

        HBox quickRow = new HBox(12, btnAddStudent, btnAddTeacher, btnAddSubject);

        main.getChildren().addAll(title, subtitle, cardRow, quickLabel, quickRow);
        return main;
    }

    /**
     * Creates a KPI summary card.
     *
     * @param label  what the number represents
     * @param value  the number to display
     * @param accent border and number colour (hex)
     * @return a styled VBox card
     */
    private VBox kpiCard(String label, String value, String accent) {
        Label lbl = new Label(label);
        lbl.setStyle(MonsterHighStyles.BODY);

        Label val = new Label(value);
        val.setStyle("-fx-font-size: 40px; -fx-font-weight: bold; -fx-text-fill: " + accent + ";");

        VBox card = new VBox(8, lbl, val);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setPrefWidth(200);
        card.setStyle(MonsterHighStyles.CARD
            + " -fx-border-color: " + accent + "; -fx-border-radius: 12; -fx-border-width: 1.5;");
        return card;
    }

    /**
     * Creates a coloured quick-action button.
     *
     * @param text   button label
     * @param color  background colour
     * @param action what to do when clicked
     * @return the configured Button
     */
    private Button quickBtn(String text, String color, Runnable action) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; " +
            "-fx-font-size: 13px; -fx-font-weight: bold; " +
            "-fx-background-radius: 8; -fx-cursor: hand; -fx-padding: 10 20;");
        btn.setOnAction(e -> action.run());
        return btn;
    }

    /**
     * Runs a count supplier safely — returns 0 if anything goes wrong.
     * This prevents the dashboard from crashing when the database is empty
     * or a service throws an exception.
     *
     * @param supplier the callable that returns the count
     * @return the count, or 0 on any error
     */
    private int safeCount(java.util.concurrent.Callable<Integer> supplier) {
        try {
            return supplier.call();
        } catch (Exception ex) {
            return 0;
        }
    }
}
