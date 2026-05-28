package ui.javafx;

import model.Enrollment;
import model.Student;
import model.Subject;
import service.EnrollmentService;
import service.StudentService;
import service.SubjectService;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;

/**
 * View for Enrollment Management.
 *
 * Shows all enrollments in a table with colour-coded grade badges:
 *   - Green badge if the final grade is >= 5 (passed)
 *   - Red badge if the final grade is < 5 (failed)
 *
 * @author Fatima Roman
 * @version 1.0
 */
public class EnrollmentView {

    private Stage stage;
    private DashboardView dashboard;

    private EnrollmentService enrollmentService = new EnrollmentService();
    private StudentService    studentService    = new StudentService();
    private SubjectService    subjectService    = new SubjectService();

    // Assigned by buildTable() — do not call refreshTable() before buildContent()
    private TableView<Enrollment>       table;
    private ObservableList<Enrollment>  data;

    /**
     * Creates the Enrollment view.
     *
     * @param stage     the main window
     * @param dashboard reference to the dashboard
     */
    public EnrollmentView(Stage stage, DashboardView dashboard) {
        this.stage     = stage;
        this.dashboard = dashboard;
    }

    /**
     * Builds and shows the enrollment list on the main stage.
     */
    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle(MonsterHighStyles.SCENE_BG);
        root.setLeft(buildSidebar());
        root.setCenter(buildContent()); // assigns 'data' via buildTable()

        stage.setScene(new Scene(root, 1100, 700));
        stage.setTitle("Enrollment Management — Monster High");

        refreshTable(); // safe now
    }

    private VBox buildSidebar() {
        VBox sidebar = new VBox(6);
        sidebar.setPrefWidth(220);
        sidebar.setPadding(new Insets(24, 12, 24, 12));
        sidebar.setStyle("-fx-background-color: #12122A;");

        Label skull = new Label("💀");
        skull.setStyle("-fx-font-size: 40px;");
        Label brand = new Label("Monster High");
        brand.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + MonsterHighStyles.PINK + ";");

        VBox logo = new VBox(4, skull, brand);
        logo.setAlignment(Pos.CENTER);
        logo.setPadding(new Insets(0, 0, 20, 0));

        Region divider = new Region();
        divider.setStyle(MonsterHighStyles.DIVIDER);

        Button btnDash     = dashboard.navButton("🏠  Dashboard",    false);
        Button btnStudents = dashboard.navButton("🧑‍🎓  Students",     false);
        Button btnTeachers = dashboard.navButton("🧙  Teachers",     false);
        Button btnSubjects = dashboard.navButton("📚  Subjects",     false);
        Button btnEnroll   = dashboard.navButton("📋  Enrollments",  true);  // active

        btnDash.setOnAction(e -> dashboard.show());
        btnStudents.setOnAction(e -> new StudentView(stage, dashboard).show());
        btnTeachers.setOnAction(e -> new TeacherView(stage, dashboard).show());
        btnSubjects.setOnAction(e -> new SubjectView(stage, dashboard).show());

        sidebar.getChildren().addAll(logo, divider,
            btnDash, btnStudents, btnTeachers, btnSubjects, btnEnroll);
        return sidebar;
    }

    /**
     * Builds the toolbar and table.
     * Calling this method initialises 'data' via buildTable().
     *
     * @return the VBox with the full content area
     */
    private VBox buildContent() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(28));

        Label title = new Label("📋 Enrollment Management");
        title.setStyle(MonsterHighStyles.TITLE);

        Button btnAdd = new Button("➕ Add Enrollment");
        btnAdd.setStyle(MonsterHighStyles.BTN_PRIMARY);
        btnAdd.setOnAction(e -> openAddDialog());

        Button btnRefresh = new Button("🔄 Refresh");
        btnRefresh.setStyle(MonsterHighStyles.BTN_SECONDARY);
        btnRefresh.setOnAction(e -> refreshTable());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox toolbar = new HBox(10, spacer, btnAdd, btnRefresh);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // buildTable() initialises 'data' — must come before refreshTable()
        table = buildTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        content.getChildren().addAll(title, toolbar, table);
        return content;
    }

    /**
     * Builds the TableView with grade-badge cells and assigns 'data'.
     *
     * @return the configured TableView
     */
    @SuppressWarnings("unchecked")
    private TableView<Enrollment> buildTable() {
        TableView<Enrollment> tv = new TableView<>();
        tv.setStyle(MonsterHighStyles.TABLE);
        tv.setPlaceholder(new Label("No enrollments registered 🕸️"));

        // ID column
        TableColumn<Enrollment, String> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(c ->
            new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        colId.setPrefWidth(50);

        // Student name (looked up from the enrollment object)
        TableColumn<Enrollment, String> colStudent = new TableColumn<>("Student");
        colStudent.setCellValueFactory(c -> {
            Student s = c.getValue().getStudent();
            return new SimpleStringProperty(s != null ? s.getName() + " " + s.getSurname() : "-");
        });

        // Subject name
        TableColumn<Enrollment, String> colSubject = new TableColumn<>("Subject");
        colSubject.setCellValueFactory(c -> {
            Subject s = c.getValue().getSubject();
            return new SimpleStringProperty(s != null ? s.getName() : "-");
        });

        // Grade columns — show "-" when the grade is 0 (not yet set)
        TableColumn<Enrollment, String> colG1 = new TableColumn<>("Grade 1");
        colG1.setCellValueFactory(c ->
            new SimpleStringProperty(formatGrade(c.getValue().getGrade1())));
        colG1.setPrefWidth(75);

        TableColumn<Enrollment, String> colG2 = new TableColumn<>("Grade 2");
        colG2.setCellValueFactory(c ->
            new SimpleStringProperty(formatGrade(c.getValue().getGrade2())));
        colG2.setPrefWidth(75);

        // Final grade — coloured badge: green if passed, red if failed
        TableColumn<Enrollment, String> colFinal = new TableColumn<>("Final");
        colFinal.setCellValueFactory(c ->
            new SimpleStringProperty(formatGrade(c.getValue().getFinalGrade())));
        colFinal.setPrefWidth(75);

        colFinal.setCellFactory(col -> new TableCell<Enrollment, String>() {
            @Override
            protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(val);
                try {
                    double grade = Double.parseDouble(val);
                    // Green badge for passed, red badge for failed
                    badge.setStyle(grade >= 5
                        ? MonsterHighStyles.BADGE_OK
                        : MonsterHighStyles.BADGE_FAIL);
                } catch (NumberFormatException ex) {
                    badge.setStyle(MonsterHighStyles.MUTED);
                }
                setGraphic(badge);
                setText(null);
            }
        });

        // Delete button per row
        TableColumn<Enrollment, Void> colActions = new TableColumn<>("Actions");
        colActions.setPrefWidth(100);
        colActions.setCellFactory(col -> new TableCell<Enrollment, Void>() {
            private final Button btnDelete = new Button("🗑️ Delete");

            {
                btnDelete.setStyle(MonsterHighStyles.BTN_DANGER);
                btnDelete.setOnAction(e ->
                    confirmDelete(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void v, boolean empty) {
                super.updateItem(v, empty);
                setGraphic(empty ? null : btnDelete);
            }
        });

        tv.getColumns().addAll(colId, colStudent, colSubject, colG1, colG2, colFinal, colActions);

        // Create and link the data list
        data = FXCollections.observableArrayList();
        tv.setItems(data);
        return tv;
    }

    /**
     * Opens a modal dialog to add a new enrollment.
     * The user selects a student, a subject and optionally enters two grades.
     */
    private void openAddDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(stage);
        dialog.setTitle("Add Enrollment");

        VBox form = new VBox(12);
        form.setPadding(new Insets(24));
        form.setStyle(MonsterHighStyles.SCENE_BG);
        form.setPrefWidth(420);

        Label heading = new Label("➕ New Enrollment");
        heading.setStyle(MonsterHighStyles.HEADING);

        // Student combo box
        List<Student> students = safeList(() -> studentService.findAll());
        ComboBox<Student> cbStudent = new ComboBox<>(FXCollections.observableArrayList(students));
        cbStudent.setStyle(MonsterHighStyles.COMBO);
        cbStudent.setMaxWidth(Double.MAX_VALUE);
        cbStudent.setConverter(new javafx.util.StringConverter<Student>() {
            @Override public String toString(Student s)   { return s == null ? "" : s.getName() + " " + s.getSurname(); }
            @Override public Student fromString(String s) { return null; }
        });

        // Subject combo box
        List<Subject> subjects = safeList(() -> subjectService.findAll());
        ComboBox<Subject> cbSubject = new ComboBox<>(FXCollections.observableArrayList(subjects));
        cbSubject.setStyle(MonsterHighStyles.COMBO);
        cbSubject.setMaxWidth(Double.MAX_VALUE);
        cbSubject.setConverter(new javafx.util.StringConverter<Subject>() {
            @Override public String toString(Subject s)   { return s == null ? "" : s.getName(); }
            @Override public Subject fromString(String s) { return null; }
        });

        // Grade fields — optional, default to 0
        TextField tfGrade1 = sf("Grade 1 (0–10)", "");
        TextField tfGrade2 = sf("Grade 2 (0–10)", "");

        Label lblError = new Label("");
        lblError.setStyle("-fx-text-fill: " + MonsterHighStyles.RED + ";");

        Button btnSave   = new Button("💾 Enroll");
        Button btnCancel = new Button("Cancel");
        btnSave.setStyle(MonsterHighStyles.BTN_PRIMARY);
        btnCancel.setStyle(MonsterHighStyles.BTN_GHOST);

        btnSave.setOnAction(e -> {
            try {
                Student st  = cbStudent.getValue();
                Subject sub = cbSubject.getValue();

                if (st == null || sub == null) {
                    throw new IllegalArgumentException("Please select a student and a subject.");
                }

                // Grades default to 0 if left blank
                double g1 = tfGrade1.getText().isBlank() ? 0 : Double.parseDouble(tfGrade1.getText().trim());
                double g2 = tfGrade2.getText().isBlank() ? 0 : Double.parseDouble(tfGrade2.getText().trim());

                enrollmentService.enroll(st.getId(), sub.getId(), g1, g2);
                dialog.close();
                refreshTable();

            } catch (NumberFormatException ex) {
                lblError.setText("Grades must be valid decimal numbers.");
            } catch (Exception ex) {
                lblError.setText("Error: " + ex.getMessage());
            }
        });

        btnCancel.setOnAction(e -> dialog.close());

        HBox btnRow = new HBox(10, btnSave, btnCancel);
        btnRow.setAlignment(Pos.CENTER_RIGHT);

        form.getChildren().addAll(heading,
            fr("Student", cbStudent),
            fr("Subject", cbSubject),
            fr("Grade 1", tfGrade1),
            fr("Grade 2", tfGrade2),
            lblError, btnRow);

        dialog.setScene(new Scene(form));
        dialog.showAndWait();
    }

    private void confirmDelete(Enrollment en) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete enrollment #" + en.getId() + "?", ButtonType.YES, ButtonType.NO);
        a.showAndWait().ifPresent(answer -> {
            if (answer == ButtonType.YES) {
                try {
                    enrollmentService.deleteEnrollment(en.getId());
                    refreshTable();
                } catch (Exception ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
                }
            }
        });
    }

    /**
     * Loads all enrollments from the service into the table.
     * Must only be called after buildTable() has initialised 'data'.
     */
    private void refreshTable() {
        try {
            data.setAll(enrollmentService.findAll());
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Could not load enrollments.").showAndWait();
        }
    }

    /**
     * Formats a grade for display: shows "-" when the value is 0 (not set yet).
     *
     * @param value the grade value
     * @return a formatted string like "7.5" or "-"
     */
    private String formatGrade(double value) {
        return value == 0 ? "-" : String.format("%.1f", value);
    }

    private TextField sf(String prompt, String val) {
        TextField tf = new TextField(val);
        tf.setPromptText(prompt);
        tf.setStyle(MonsterHighStyles.TEXT_FIELD);
        return tf;
    }

    private HBox fr(String label, javafx.scene.Node ctrl) {
        Label l = new Label(label + ":");
        l.setStyle(MonsterHighStyles.MUTED);
        l.setPrefWidth(100);
        HBox.setHgrow(ctrl, Priority.ALWAYS);
        HBox row = new HBox(10, l, ctrl);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private <T> List<T> safeList(java.util.concurrent.Callable<List<T>> supplier) {
        try { return supplier.call(); }
        catch (Exception ex) { return List.of(); }
    }
}
